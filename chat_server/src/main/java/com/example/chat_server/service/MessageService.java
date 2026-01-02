package com.example.chat_server.service;

import com.example.chat_server.dto.request.MessageRequest;
import com.example.chat_server.dto.response.MessageResponse;
import com.example.chat_server.dto.response.UserResponse;
import com.example.chat_server.entity.Message;
import com.example.chat_server.entity.User;
import com.example.chat_server.service.MessageService;
import com.example.chat_server.repository.MessageRepo;
import com.example.chat_server.repository.UserRepo;


import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Collections;

@Service 
public class MessageService{
    private final MessageRepo msgRepo;
    private final UserRepo userRepo;

    public MessageService(MessageRepo msgRepo, UserRepo userRepo){
        this.msgRepo = msgRepo;
        this.userRepo = userRepo;
    }

    public MessageResponse create(MessageRequest request, String senderUserName){
        User sender = userRepo.findByUserName(senderUserName)
            .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi gui (sender): " + senderUserName));

        User receiver = userRepo.findByUserName(request.getReceiver().getUserName())
            .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi nhan (receiver): " + request.getReceiver().getUserName()));

        Message msgCreate = new Message(sender, receiver, request.getContent());
        Message msgSave = msgRepo.save(msgCreate);

        return new MessageResponse(
            msgSave.getId(),
            msgSave.getCreatedAt(),
            new UserResponse(msgSave.getSender().getId(), msgSave.getSender().getUserName(), msgSave.getSender().getUserFullName()),
            new UserResponse(msgSave.getReceiver().getId(), msgSave.getReceiver().getUserName(), msgSave.getReceiver().getUserFullName()),
            msgSave.getContent()
        );
    }

    public List<MessageResponse> getConversation(String user1, String user2, int page, int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<Message> messages = msgRepo.findChatHistory(user1, user2, pageable);

        Collections.reverse(messages);

        return messages.stream().map(this::mapToResponse).toList();
    }

    private MessageResponse mapToResponse(Message msg) {
        return new MessageResponse(
            msg.getId(),
            msg.getCreatedAt(),
            new UserResponse(msg.getSender().getId(), msg.getSender().getUserName(), msg.getSender().getUserFullName()),
            new UserResponse(msg.getReceiver().getId(), msg.getReceiver().getUserName(), msg.getReceiver().getUserFullName()),
            msg.getContent()
        );
    }
}