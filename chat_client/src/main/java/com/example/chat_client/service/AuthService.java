package com.example.chat_client.service;

import com.example.chat_client.api.AuthClient;
import com.example.chat_client.core.UserSession;
import com.example.chat_client.dto.request.AuthRequest;

public class AuthService {
    private AuthClient authClient = new AuthClient();

    public boolean login(String userName, String password){
        try {
            // Goi API
            String token = authClient.login(new AuthRequest(userName, password));

            // Luu vao phien hoat dong
            UserSession.getInstance().setToken(token); // Luu token vao instance do
            UserSession.getInstance().setUserName(userName); // Luu userName vao instance do luon
            return true;
        } catch (Exception e) {
            System.out.println(">> Login error: " + e.getMessage());
            return false;
        }
    }

    public boolean register(String userName, String password){
        try {
            return authClient.register(new AuthRequest(userName, password));
        } catch (Exception e) {
            System.err.println(">> Sign up error: " + e.getMessage());
            return false;
        }
    }
}
