package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto createdUser = userService.createUser(request);

        EntityModel<UserDto> resource = EntityModel.of(createdUser);

        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(createdUser.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .updateUser(createdUser.getId(), null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class)
                .deleteUser(createdUser.getId())).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withRel("users"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(linkTo(methodOn(UserController.class)
                        .getUserById(createdUser.getId())).toUri())
                .body(resource);
    }
    @Operation(
            summary = "Получить юзера по ID",
            description = "Возвращает объект юзера, если он найден в системе."
    )
    @ApiResponse(responseCode = "200", description = "Юзер найден")
    @ApiResponse(responseCode = "404", description = "Юзер не найден")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable int id) {
        UserDto user = userService.getUserById(id);

        EntityModel<UserDto> resource = EntityModel.of(user);

        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(id)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .updateUser(id, null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class)
                .deleteUser(id)).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withRel("users"));
        resource.add(linkTo(methodOn(UserController.class)
                .createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(resource);
    }
    @Operation(
            summary = "Получить список юзеров",
            description = "Возвращает лист юзеров."
    )
    @ApiResponse(responseCode = "200", description = "Юзеры найдены")
    @ApiResponse(responseCode = "404", description = "Юзеры не найдены")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        List<EntityModel<UserDto>> userResources = users.stream()
                .map(user -> {
                    EntityModel<UserDto> resource = EntityModel.of(user);
                    resource.add(linkTo(methodOn(UserController.class)
                            .getUserById(user.getId())).withSelfRel());
                    resource.add(linkTo(methodOn(UserController.class)
                            .updateUser(user.getId(), null)).withRel("update"));
                    resource.add(linkTo(methodOn(UserController.class)
                            .deleteUser(user.getId())).withRel("delete"));
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(userResources);

        collection.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class)
                .createUser(null)).withRel("create-user"));

        return ResponseEntity.ok(collection);
    }
    @Operation(
            summary = "Обновить данные юзера",
            description = "Возвращает обновленного юзера."
    )
    @ApiResponse(responseCode = "201", description = "Операция обновления пользователя прошла успешно")
    @ApiResponse(responseCode = "406", description = "Операция обновления пользователя прошла не успешно")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @PathVariable int id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserDto updatedUser = userService.updateUser(id, request);

        EntityModel<UserDto> resource = EntityModel.of(updatedUser);

        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(id)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .updateUser(id, null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class)
                .deleteUser(id)).withRel("delete"));
        resource.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withRel("users"));

        return ResponseEntity.ok(resource);
    }
    @Operation(
            summary = "Удалить юзера",
            description = "Удаляет юзера по его id."
    )
    @ApiResponse(responseCode = "200", description = "Пользователь удален")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity
                .noContent()
                .header("Link",
                        linkTo(methodOn(UserController.class)
                                .getAllUsers()).withRel("users").toString())
                .build();
    }
}