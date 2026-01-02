package com.example.chat_client.dto.request;

public class MessageRequest {
    public UserRequest receiver;
    public String content;

    public MessageRequest(){}

    public MessageRequest(String content, UserRequest receiver) {
        this.receiver = receiver;
        this.content = content;
    }

    // GETTER
    public UserRequest getReceiver(){ return receiver; }
    public String getContent(){ return content; }

    // SETTER
    public void setReceiver(UserRequest receiver){ this.receiver = receiver;}
    public void setContent(String content){ this.content = content;}
}