package org.example.Dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDto extends RepresentationModel<UserDto> {
    private int id;
    private String name;
    private int age;
    private String email;
    private String createdAt;

    public UserDto(int id, String name, int age, String email, String createdAt) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.createdAt = createdAt;
    }

    public UserDto() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', age=%d, email='%s', createdAt='%s'}",
                id, name, age, email, createdAt);
    }
}
