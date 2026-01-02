package com.example.chat_server.entity;

import com.example.chat_server.enums.RelationshipLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
@Entity
@Table(
    name="relationships",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user1", "user2"})
    }
)
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user1", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2", nullable = false)
    private User user2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipLevel level;

    private LocalDateTime createdAt;

    //CONSTRUCTOR
    public Relationship(){}

    public Relationship(User sourcUser, User user2, RelationshipLevel level){
        this.user1 = sourcUser;
        this.user2 = user2;
        this.level = level;
        this.createdAt = LocalDateTime.now();
    }

    //GETTER-SETTER
    // --- Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getuser1() { return user1; }
    public void setuser1(User user1) { this.user1 = user1; }

    public User getuser2() { return user2; }
    public void setuser2(User user2) { this.user2 = user2; }

    public RelationshipLevel getStatus() { return level; }
    public void setStatus(RelationshipLevel level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = LocalDateTime.now(); }
}
