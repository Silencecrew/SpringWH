package org.example.Dto;

import javax.validation.constraints.*;


public class CreateUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст должен быть не меньше 0")
    @Max(value = 80, message = "Возраст должен быть не больше 80")
    private int age;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    public CreateUserRequest(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public CreateUserRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
