package com.example.chat_server.repository;

import com.example.chat_server.entity.Message;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    
    // Tìm tin nhắn dựa trên username (String) của sender và receiver
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.userName = :user1 AND m.receiver.userName = :user2) OR " +
           "(m.sender.userName = :user2 AND m.receiver.userName = :user1) ")
        //    "ORDER BY m.createdAt ASC")
    List<Message> findChatHistory(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);
}