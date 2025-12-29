package org.example.Entity;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="users")
@Schema(description = "Модель данных юзера")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @Schema(description = "Уникальный идентификатор юзера",
            example = "123")
    private Integer userId;

    @Column(name = "user_name", nullable = false)
    @Schema(description = "Имя юзера",
            example = "Матвей",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Column(name = "user_email", nullable = false, unique = true)
    @Schema(description = "Почта юзера",
            example = "test@gmail.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @Column(name = "user_age", nullable = false)
    @Schema(description = "Возраст юзера",
            example = "30",
            minimum = "0", maximum = "80",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userAge;

    @Column(name = "user_created_at", nullable = false)
    @Schema(description = "Дата создания юзера",
            example = "2022-07-01T15:00:00+01")
    private String userCreatedAt;

    public User() {}

    public User(String userName, Integer userAge, String userEmail, String userCreatedAt) {
        this.userName = userName;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userCreatedAt = userCreatedAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public String getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(String userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', age=%d, email='%s', createdAt='%s'}",
                userId, userName, userAge, userEmail, userCreatedAt);
    }
}