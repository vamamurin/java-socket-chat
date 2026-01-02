package com.example.chat_server.repository;

import com.example.chat_server.entity.User;
import com.example.chat_server.enums.RelationshipRequestStatus;
import com.example.chat_server.entity.RelationshipRequest;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRequestRepo
        extends JpaRepository<RelationshipRequest, Long> {

    List<RelationshipRequest> findByToUserAndStatus(
        User toUser,
        RelationshipRequestStatus status
    );

    Optional<RelationshipRequest> findByFromUserAndToUser(
        User fromUser,
        User toUser
    );
}

