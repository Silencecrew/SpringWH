package org.example.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    @Column(name = "user_age", nullable = false)
    private int userAge;
    @Column(name = "user_created_at", nullable = false)
    private String userCreatedAt;

    public User() {}
    public User(String userName, int userAge, String userEmail, String userCreatedAt) {
        this.userName = userName;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userCreatedAt = userCreatedAt;
    }
    @Id
    @GeneratedValue
    public int getUserId() {
        return userId;
    }
    @Id
    @GeneratedValue
    public void setUserId(int userId) {
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

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
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
