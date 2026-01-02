package com.example.chat_client.model;

public class User {
    private Long id;
    private String userName;
    private String userFullName;

    // Constructor rỗng cho Jackson
    public User() {}

    public User(String userName) {
        this.userName = userName;
        // this.userFullName = userFullName;
    }

    // Getter Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullname) { this.userFullName = userFullname; }
}