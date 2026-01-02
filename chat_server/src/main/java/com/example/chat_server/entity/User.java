package com.example.chat_server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false) // Cot username trong data table
    private String userName;
    private String password;
    private String userFullName;

    @CreationTimestamp
    private LocalDateTime createdAt;


    // Constructor
    public User(){}

    public User(Long id, String userName, String password, String userFullName) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userFullName = userFullName;
    }

    // Getter
    public Long getId(){ return id; }
    public String getUserName(){ return userName; }
    public String getPassword(){ return password; }
    public String getUserFullName(){ return userFullName; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    // Setter
    public void setId(Long id){ this.id = id; }
    public void setUserName(String userName){ this.userName = userName; }
    public void setUserFullName(String userFullName){ this.userFullName = userFullName; }
    public void setPassword(String password){ this.password = password; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }


}
