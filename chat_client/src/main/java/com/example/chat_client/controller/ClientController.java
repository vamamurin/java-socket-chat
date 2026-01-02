package com.example.chat_client.controller;

import com.example.chat_client.dto.request.UserRequest;
import com.example.chat_client.model.ChatMessage;
import com.example.chat_client.model.User;
import com.example.chat_client.service.AuthService;
import com.example.chat_client.service.ChatService;
import com.example.chat_client.service.RelationshipService;

import java.util.List;

public class ClientController {
    private final AuthService authService = new AuthService();
    private final ChatService chatService = new ChatService();
    private final RelationshipService relationshipService = new RelationshipService();

    // -- AUTH --
    public boolean login(String userName, String password){
        return authService.login(userName, password);
    }

    public boolean register(String userName, String password){
        return authService.register(userName, password);
    }

    // -- CHAT --
    public ChatMessage sendMessage(String content, UserRequest receiver){
        return chatService.sendMessage(content, receiver);
    }

    public List<ChatMessage> getConversation(String partner){
        return chatService.loadConversation(partner);
    }

    public List<User> getRelationList(String username) {
        return relationshipService.getMyFriends();
    }

    public boolean sendFriendRequest(String sender, String receiver) {
        // Logic gửi request kết bạn
        return true; // Stub
    }
    
}
