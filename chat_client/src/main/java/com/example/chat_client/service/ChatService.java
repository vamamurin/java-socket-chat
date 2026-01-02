package com.example.chat_client.service;


import com.example.chat_client.api.MessageClient;
import com.example.chat_client.dto.request.MessageRequest;
import com.example.chat_client.dto.response.MessageResponse;
import com.example.chat_client.dto.request.UserRequest;
import com.example.chat_client.model.ChatMessage;
import com.example.chat_client.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatService {
    private final MessageClient messageClient = new MessageClient();

    // Gui tin nhan
    public ChatMessage sendMessage(String content, UserRequest receiver){
        try {
            MessageRequest req = new MessageRequest(content, receiver);

            MessageResponse response = messageClient.sendMessage(req);

            if (response != null){
                return mapToModel(response);
            }

        } catch (Exception e) {
            System.err.println("Send message error: " + e.getMessage());
        }
        return null;
    }

    public List<ChatMessage> loadConversation(String partner){
        try {
            List<MessageResponse> response = messageClient.getConversation(partner, 0, 20);

            return response.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error: Cannot load message: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private ChatMessage mapToModel(MessageResponse response){
        ChatMessage msg = new ChatMessage(new User(response.getSender().getUserName()),
                                response.getContent(),
                                new User(response.getReceiver().getUserName()));
        msg.setId(response.getId());
        msg.setCreatedAt(response.getCreatedAt());

        return msg;
    }
}
