package org.example.Service;

import org.example.Dto.CreateUserRequest;
import org.example.Dto.UpdateUserRequest;
import org.example.Dto.UserDto;
import org.example.Entity.User;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserRequest request);
    UserDto getUserById(int id);
    List<UserDto> getAllUsers();
    UserDto updateUser(int id, UpdateUserRequest request);
    void deleteUser(int id);

    void printAllUsers();
    boolean validateEmail(String email);
    boolean validateAge(int age);
}

