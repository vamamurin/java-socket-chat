package com.example.chat_server.dto.response;

import com.example.chat_server.enums.RelationshipLevel;
import java.time.LocalDateTime;

public class RelationshipResponse {
    private Long id;              // ID của mối quan hệ
    private UserResponse friend;  // Thông tin người bạn
    private RelationshipLevel level;
    private LocalDateTime createdAt;

    public RelationshipResponse(Long id, UserResponse friend, RelationshipLevel level, LocalDateTime createdAt) {
        this.id = id;
        this.friend = friend;
        this.level = level;
        this.createdAt = createdAt;
    }
    
    // Getter Setter (Bắt buộc phải có để Jackson đọc)
    public Long getId() { return id; }
    public UserResponse getFriend() { return friend; }
    public RelationshipLevel getLevel() { return level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}