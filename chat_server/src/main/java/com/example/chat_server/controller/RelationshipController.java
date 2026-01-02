package com.example.chat_server.controller;

import com.example.chat_server.dto.response.RelationshipResponse;
import com.example.chat_server.security.MyUserDetails;
import com.example.chat_server.service.RelationshipService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relationships")
public class RelationshipController {
    
    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService){ this.relationshipService = relationshipService; }
    
    @GetMapping("/list")
    public List<RelationshipResponse> getRelationlist(@AuthenticationPrincipal UserDetails userDetails){
        // Ep kieu ve MyUserDetails
        MyUserDetails myUser = (MyUserDetails) userDetails;

        Long id = myUser.getId();

        return relationshipService.getRelationship(id);
    }
}
