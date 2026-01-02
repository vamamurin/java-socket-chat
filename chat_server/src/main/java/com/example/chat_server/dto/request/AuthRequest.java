package com.example.chat_server.dto.request;

public class AuthRequest {
    private String userName;
    private String password;
    private String userFullName;
    
    // Constructor rỗng (Bắt buộc cho Jackson)
    public AuthRequest() {} 
    
    // Getter Setter
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserFullName(){ return userFullName; }
    public void setUserFullName(String userFullName){ this.userFullName = userFullName; }
}