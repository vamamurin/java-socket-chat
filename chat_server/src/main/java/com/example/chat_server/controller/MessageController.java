package com.example.chat_server.controller;

import java.util.List;

import com.example.chat_server.dto.request.MessageRequest;
import com.example.chat_server.dto.response.MessageResponse;
import com.example.chat_server.service.MessageService;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;




@RestController
@RequestMapping("/messages")
public class MessageController{

    private final MessageService service;
    // private final Us

    public MessageController(MessageService service){ this.service = service; }

    //CREATE
    @PostMapping
    public MessageResponse create(@RequestBody MessageRequest request, @AuthenticationPrincipal UserDetails userDetails){
        return service.create(request, userDetails.getUsername());
    }

    //READ ALL
    @GetMapping("/conversation")
    public List<MessageResponse> getConversation(
        @RequestParam String partner, 
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ){
        return service.getConversation(userDetails.getUsername(), partner, page, size);
    }
    
}