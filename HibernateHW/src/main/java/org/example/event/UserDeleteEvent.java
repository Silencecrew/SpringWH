package org.example.event;

public class UserDeleteEvent {

    private Integer userId;
    private String userEmail;

    public UserDeleteEvent() {
    }

    public UserDeleteEvent( Integer userId, String userEmail) {
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
