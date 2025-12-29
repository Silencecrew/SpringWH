package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API",
        description = "API для управления данными о юзерах")
public class UserController {

    private final UserService userService;




    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }
    @Operation(summary = "Создать нового юзера")
    @ApiResponse(responseCode = "201", description = "Юзер успешно создан")
    @PostMapping("api/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {

        UserDto createdUser = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @Operation(
            summary = "Получить юзера по ID",
            description = "Возвращает объект юзера, если он найден в системе."
    )
    @ApiResponse(responseCode = "200", description = "Юзер найден")
    @ApiResponse(responseCode = "404", description = "Юзер не найден")
    @GetMapping("api/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @Operation(
            summary = "Получить список юзеров",
            description = "Возвращает лист юзеров."
    )
    @ApiResponse(responseCode = "200", description = "Юзеры найдены")
    @ApiResponse(responseCode = "404", description = "Юзеры не найдены")
    @GetMapping("api/api/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @Operation(
            summary = "Обновить данные юзера",
            description = "Возвращает обновленного юзера."
    )
    @ApiResponse(responseCode = "201", description = "Операция обновления пользователя прошла успешно")
    @ApiResponse(responseCode = "406", description = "Операция обновления пользователя прошла не успешно")
    @PutMapping("api/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int id, @Valid @RequestBody UpdateUserRequest request) {
        UserDto updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
    @Operation(
            summary = "Удалить юзера",
            description = "Удаляет юзера по его id."
    )
    @ApiResponse(responseCode = "200", description = "Пользователь удален")
    @DeleteMapping("api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}