package com.example.chat_server.dto.request;


public class MessageRequest {
    private UserRequest receiver;
    private String content;

    public MessageRequest(){}

    public MessageRequest(UserRequest receiver, String content){
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
