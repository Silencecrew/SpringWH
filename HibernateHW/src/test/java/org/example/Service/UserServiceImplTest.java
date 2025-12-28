package org.example.Service;

import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Entity.User;
import org.example.Exception.DuplicateEmailException;
import org.example.Exception.UserNotFoundException;
import org.example.Exception.ValidationException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        String createdAt = LocalDateTime.now().format(DATE_FORMATTER);
        testUser = new User("test", 30, "test@test.com", createdAt);
        testUser.setUserId(1);

        createRequest = new CreateUserRequest();
        createRequest.setName("test");
        createRequest.setAge(20);
        createRequest.setEmail("test@test.com");

        updateRequest = new UpdateUserRequest();
    }

    @Test
    void createUser_ValidRequest_ShouldReturnUserDto() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("test@test.com", result.getEmail());
        assertNotNull(result.getCreatedAt());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("test", savedUser.getUserName());
        assertEquals(20, savedUser.getUserAge());
        assertEquals("test@test.com", savedUser.getUserEmail());
        assertNotNull(savedUser.getUserCreatedAt());

        verify(userRepository).existsByEmail("test@test.com");
    }

    @Test
    void createUser_DuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository).existsByEmail("test@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidEmail_ShouldThrowValidationException() {
        createRequest.setEmail("invalid-email");

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InvalidAge_ShouldThrowValidationException() {
        createRequest.setAge(100);

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUserDto() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("test@test.com", result.getEmail());

        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(999));

        verify(userRepository).findById(999);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        User user2 = new User("test2", 25, "test2@test.com",
                LocalDateTime.now().format(DATE_FORMATTER));
        user2.setUserId(2);

        List<User> users = Arrays.asList(testUser, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getName());
        assertEquals("test2", result.get(1).getName());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_ValidUpdate_ShouldReturnUpdatedUserDto() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        updateRequest.setName("new name");
        updateRequest.setAge(20);
        updateRequest.setEmail("new@email.com");

        UserDto result = userService.updateUser(1, updateRequest);

        assertNotNull(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertEquals("new name", updatedUser.getUserName());
        assertEquals(20, updatedUser.getUserAge());
        assertEquals("new@email.com", updatedUser.getUserEmail());
        assertEquals(1, updatedUser.getUserId());
    }

    @Test
    void updateUser_OnlyNameUpdate_ShouldUpdateOnlyName() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        updateRequest.setName("name");

        UserDto result = userService.updateUser(1, updateRequest);

        assertNotNull(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertEquals("name", updatedUser.getUserName());
        assertEquals(0, updatedUser.getUserAge());
        assertEquals("test@test.com", updatedUser.getUserEmail());
        assertEquals(1, updatedUser.getUserId());
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(999, updateRequest));

        verify(userRepository).findById(999);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_DuplicateEmail_ShouldThrowException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        updateRequest.setEmail("existing@email.com");

        assertThrows(DuplicateEmailException.class,
                () -> userService.updateUser(1, updateRequest));

        verify(userRepository).existsByEmail("existing@email.com");
        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    void updateUser_SameEmail_ShouldAllowUpdate() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        updateRequest.setEmail("test@test.com");
        updateRequest.setName("Новое Имя");

        UserDto result = userService.updateUser(1, updateRequest);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userRepository, never()).existsByEmail("test@test.com");
    }

    @Test
    void deleteUser_ExistingUser_ShouldCallDelete() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUser(1);

        verify(userRepository).existsById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        when(userRepository.existsById(999)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(999));

        verify(userRepository).existsById(999);
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void validateEmail_ValidEmail_ShouldReturnTrue() {
        assertTrue(userService.validateEmail("test@example.com"));
        assertTrue(userService.validateEmail("user@test.ru"));
    }

    @Test
    void validateEmail_InvalidEmail_ShouldReturnFalse() {
        assertFalse(userService.validateEmail("email@"));
        assertFalse(userService.validateEmail(""));
        assertFalse(userService.validateEmail(null));
    }

    @Test
    void validateAge_ValidAge_ShouldReturnTrue() {
        assertTrue(userService.validateAge(0));
        assertTrue(userService.validateAge(25));
        assertTrue(userService.validateAge(80));
    }

    @Test
    void printAllUsers_ShouldNotThrowException() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        assertDoesNotThrow(() -> userService.printAllUsers());

        verify(userRepository).findAll();
    }

    @Test
    void printAllUsers_EmptyList_ShouldNotThrowException() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> userService.printAllUsers());

        verify(userRepository).findAll();
    }

    @Test
    void createUser_EmptyName_ShouldThrowValidationException() {
        createRequest.setName("");

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullName_ShouldThrowValidationException() {
        createRequest.setName(null);

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyEmail_ShouldThrowValidationException() {
        createRequest.setEmail("");

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullEmail_ShouldThrowValidationException() {
        createRequest.setEmail(null);

        assertThrows(ValidationException.class,
                () -> userService.createUser(createRequest));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void constructor_WithRepository_ShouldWork() {
        UserServiceImpl service = new UserServiceImpl(userRepository);
        assertNotNull(service);
    }

    @Test
    void updateUser_InvalidAgeInUpdate_ShouldThrowValidationException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        updateRequest.setAge(100);

        assertThrows(ValidationException.class,
                () -> userService.updateUser(1, updateRequest));

        verify(userRepository).findById(1);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_InvalidEmailInUpdate_ShouldThrowValidationException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        updateRequest.setEmail("invalid-email");

        assertThrows(ValidationException.class,
                () -> userService.updateUser(1, updateRequest));

        verify(userRepository).findById(1);
        verify(userRepository, never()).save(any(User.class));
    }
}