package com.example.chat_server.entity;

import com.example.chat_server.enums.RelationshipRequestStatus;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "relationship_requests",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_user_id", "to_user_id"})
    }
)
public class RelationshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipRequestStatus status; // PENDING / ACCEPTED / REJECTED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // CONSTRUCTOR
    public RelationshipRequest(){}
    
    public RelationshipRequest(User fromUser, User toUser, RelationshipRequestStatus status){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    
    // GETTER-SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getFromUser() { return fromUser; }
    public void setFromUser(User fromUser) { this.fromUser = fromUser; }

    public User getToUser() { return toUser; }
    public void setToUser(User toUser) { this.toUser = toUser; }

    public RelationshipRequestStatus getStatus() { return status; }
    public void setStatus(RelationshipRequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

