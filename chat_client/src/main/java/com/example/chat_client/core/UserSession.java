package com.example.chat_client.core;

public class UserSession{

    private static final UserSession INSTANCE = new UserSession();
    
    // Tao san instance duy nhat
    private String token;

    // Luu them cai nay de do phai truyen qua lai
    private String userName;

    // private constructor de khong ai new duoc
    private UserSession(){}

    public static UserSession getInstance(){
        return INSTANCE;
    }

    // GETTER
    public String getToken(){ return token; }
    public String getUserName(){ return userName; }
    // SETTER
    public void setToken(String token){ this.token = token; }
    public void setUserName(String userName){ this.userName = userName;}

    public boolean isLoggedIn(){
        return (token != null && !token.isEmpty());
    }

    // Ham dang xuat (Xoa sach du lieu)
    public void clear(){
        this.token = null;
        this.userName = null;
    }
}