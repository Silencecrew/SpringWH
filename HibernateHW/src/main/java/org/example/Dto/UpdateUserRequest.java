package org.example.Dto;

import javax.validation.constraints.*;

public class UpdateUserRequest {
    private String name;

    @Min(value = 0, message = "Возраст должен быть не меньше 0")
    @Max(value = 80, message = "Возраст должен быть не больше 80")
    private Integer age;

    @Email(message = "Некорректный формат email")
    private String email;

    public UpdateUserRequest(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public UpdateUserRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}