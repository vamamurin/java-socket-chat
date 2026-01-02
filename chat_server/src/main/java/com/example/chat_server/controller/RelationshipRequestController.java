package com.example.chat_server.controller;

// import com.example.chat_server.dto.response.UserResponse;
import com.example.chat_server.service.RelationshipRequestService;
// import com.example.chat_server.service.RelationshipService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipRequestController {

    private final RelationshipRequestService relationshipRequestService;

    public RelationshipRequestController(RelationshipRequestService relationshipRequestService) {
        this.relationshipRequestService = relationshipRequestService;
    }

    // 1. POST GUI LOI MOI
    @PostMapping("/request/{targetUsername}")
    public ResponseEntity<String> sendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetUsername) {
        relationshipRequestService.sendRequest(userDetails.getUsername(), targetUsername);
        return ResponseEntity.ok("Request sent");
    }

    // 2. POST chap nhan loi moi
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptRequest(@PathVariable Long requestId, @AuthenticationPrincipal UserDetails userDetails) {
        relationshipRequestService.acceptRequest(requestId, userDetails.getUsername());
        return ResponseEntity.ok("Accepted");
    }
}
