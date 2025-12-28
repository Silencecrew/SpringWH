package org.example.event;

public class UserCreateEvent {

    private Integer userId;
    private String userEmail;

    public UserCreateEvent() {
    }

    public UserCreateEvent( Integer userId, String userEmail) {
        this.userId=userId;
        this.userEmail = userEmail;
    }
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
