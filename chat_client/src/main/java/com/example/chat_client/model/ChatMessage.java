package com.example.chat_client.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatMessage {
    private Long id;
    private User sender;
    private String content;
    private User receiver;

    @JsonProperty("timeStamp")
    private LocalDateTime createdAt;
    
    public ChatMessage(){}

    public ChatMessage(User sender, String content, User receiver){
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
    }

    // Getter/setter
    public Long getId(){ return id; }
    public User getSender(){ return sender; }
    public String getContent(){ return content; }
    public User getReceiver(){ return receiver; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    public void setId(Long id){ this.id = id; }
    public void setSender(User sender){ this.sender = sender; }
    public void setContent(String content){ this.content = content; }
    public void setReceiver(User receiver){ this.receiver = receiver; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }
}
