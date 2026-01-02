package com.example.chat_server.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_msg_sender", columnList = "sender_id"),     // Tăng tốc tìm theo người gửi
    @Index(name = "idx_msg_receiver", columnList = "receiver_id"), // Tăng tốc tìm theo người nhận
    @Index(name = "idx_msg_created_at", columnList = "createdAt")  // Tăng tốc sắp xếp thời gian (Pagination)
})
public class Message{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // CONSTRUCTOR
    public Message(){}
    public Message(User sender, User receiver, String content){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    // Getter Setter
    public Long getId(){ return id; }
    public User getSender(){ return sender; }
    public User getReceiver(){ return receiver; }
    public String getContent(){ return content; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    
    public void setId(Long id){ this.id = id; }
    public void setSender(User sender){ this.sender = sender; }
    public void setReceiver(User receiver){ this.receiver = receiver; }
    public void setContent(String content){ this.content = content; }


}