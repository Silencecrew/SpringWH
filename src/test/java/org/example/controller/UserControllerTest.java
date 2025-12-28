package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        String createdAt = LocalDateTime.now().format(DATE_FORMATTER);
        userDto = new UserDto(1, "Test User", 25, "test@example.com", createdAt);

        createUserRequest = new CreateUserRequest("Test User", 25, "test@example.com");
        updateUserRequest = new UpdateUserRequest("Updated User", 30, "updated@example.com");
    }

    @Test
    void createUser_ValidRequest_ShouldReturnCreated() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(userService).createUser(any(CreateUserRequest.class));
    }
    @Test
    void getUserById_ExistingUser_ShouldReturnOk() throws Exception {
        when(userService.getUserById(1)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService).getUserById(1);
    }

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        UserDto userDto2 = new UserDto(2, "Test User2", 30, "test2@example.com",
                LocalDateTime.now().format(DATE_FORMATTER));
        List<UserDto> users = Arrays.asList(userDto, userDto2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test User2")));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_EmptyList_ShouldReturnEmptyArray() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService).getAllUsers();
    }

    @Test
    void updateUser_ValidRequest_ShouldReturnOk() throws Exception {
        UserDto updatedUser = new UserDto(1, "Updated User", 30, "updated@example.com",
                LocalDateTime.now().format(DATE_FORMATTER));

        when(userService.updateUser(eq(1), any(UpdateUserRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.age", is(30)))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService).updateUser(eq(1), any(UpdateUserRequest.class));
    }

    @Test
    void deleteUser_ExistingUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/api/users/{id}", 1))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

    @Test
    void updateUser_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{invalid json";

        mockMvc.perform(put("/api/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyInt(), any(UpdateUserRequest.class));
    }

    @Test
    void getUserById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/{id}", "not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/users/{id}", "not-a-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", "not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithNullValues_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest requestWithNulls = new CreateUserRequest(null, 25, null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithNulls)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }
}