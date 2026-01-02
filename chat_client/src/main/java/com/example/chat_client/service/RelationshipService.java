package com.example.chat_client.service;

import com.example.chat_client.api.RelationshipClient;
import com.example.chat_client.dto.response.RelationshipResponse;
import com.example.chat_client.dto.response.UserResponse;
import com.example.chat_client.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RelationshipService {

    private final RelationshipClient relationshipClient = new RelationshipClient();

    public RelationshipService(){}

    // Lấy danh sách user là bạn bè
    public List<User> getMyFriends() {
        List<RelationshipResponse> responses = relationshipClient.getFriendList();

        if (responses == null) return new ArrayList<>(); // Defend null response

        return responses.stream()
                .map(rel -> mapToUser(rel.getFriend()))
                .filter(user -> user != null) // <--- QUAN TRỌNG: Lọc bỏ user null
                .collect(Collectors.toList());
    }

    private User mapToUser(UserResponse dto) {
        if (dto == null) return null;
        User user = new User(dto.getUserName());
        user.setId(dto.getId());
        // user.setFullName(dto.getFullName());
        return user;
    }
}