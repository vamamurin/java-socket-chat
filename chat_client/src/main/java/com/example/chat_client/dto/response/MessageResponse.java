package com.example.chat_client.dto.response;

import java.time.LocalDateTime;

public class MessageResponse {
    private Long id;
    private UserResponse sender;
    private UserResponse receiver;
    private String content;

    private LocalDateTime createdAt;

    public MessageResponse(){}

    public MessageResponse(Long id, LocalDateTime createdAt, UserResponse sender, UserResponse receiver, String content){
        this.id = id;
        this.createdAt = createdAt;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    // GETTER
    public Long getId(){ return id; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public UserResponse getSender(){ return sender; }
    public UserResponse getReceiver(){ return receiver; }
    public String getContent(){ return content; }

    // SETTER
    public void setId(Long id){ this.id = id;}
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt;}
    public void setSender(UserResponse sender){ this.sender = sender;}
    public void setReceiver(UserResponse receiver){ this.receiver = receiver;}
    public void setContent(String content){ this.content = content;}
}
