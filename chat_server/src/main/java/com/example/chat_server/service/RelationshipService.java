package com.example.chat_server.service;

import com.example.chat_server.entity.User;
import com.example.chat_server.enums.RelationshipLevel;
import com.example.chat_server.repository.RelationshipRepo;
import com.example.chat_server.dto.response.RelationshipResponse;
import com.example.chat_server.dto.response.UserResponse;
import com.example.chat_server.entity.Relationship;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RelationshipService {

    private final RelationshipRepo relationshipRepo;


    public RelationshipService(RelationshipRepo relationshipRepo){
        this.relationshipRepo = relationshipRepo;
    }

    // Domain service (bussiness core)
    // Tao moi quan he
    public void createRelationship(User user1, User user2){
        User u1 = (user1.getId() < user2.getId()) ? user1 : user2;
        User u2 = (user1.getId() < user2.getId()) ? user2 : user1;

        // Kiem tra xem da ton tai quan he chua
        if (relationshipRepo.findByUser1AndUser2(u1, u2).isPresent()) {
            return;
        }
        Relationship rel = new Relationship(u1, u2, RelationshipLevel.CONNECTED);
        relationshipRepo.save(rel);
    }
    
    public void upgradeLevel(User user1, User user2, RelationshipLevel relationshipLevel){}

    public void remove(User user1, User user2){}

    public List<RelationshipResponse> getRelationship(Long Id){
        List<Relationship> relationships = relationshipRepo.findAllByUserId(Id);

        // Gọi hàm map riêng biệt
        return relationships.stream()
                .map(rel -> mapToResponse(rel, Id))
                .toList();
    }

    private RelationshipResponse mapToResponse(Relationship rel, Long myId) {
        // 1. Logic tìm "Thằng kia" (Friend)
        // Nếu user1 là tao (myId) -> thì user2 là bạn. Ngược lại user1 là bạn.
        User friend = (rel.getuser1().getId().equals(myId)) ? rel.getuser2() : rel.getuser1();

        // 2. Map User -> UserResponse DTO
        UserResponse friendResponse = new UserResponse(
            friend.getId(),
            friend.getUserName(),
            friend.getUserFullName()
        );

        // 3. Map Relationship -> RelationshipResponse DTO
        return new RelationshipResponse(
            rel.getId(),
            friendResponse,
            rel.getStatus(),
            rel.getCreatedAt() // Vẫn cần truyền vào đây (xem giải thích bên dưới)
        );
    }
}
