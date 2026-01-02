package com.example.chat_server.service;

import com.example.chat_server.entity.RelationshipRequest;
import com.example.chat_server.entity.User;
import com.example.chat_server.repository.RelationshipRepo;
import com.example.chat_server.repository.RelationshipRequestRepo;
import com.example.chat_server.repository.UserRepo;
import com.example.chat_server.enums.RelationshipRequestStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelationshipRequestService {

    private UserRepo userRepo;
    
    private RelationshipRepo relationshipRepo;

    private RelationshipRequestRepo relationshipRequestRepo;

    private RelationshipService relationshipService;

    public RelationshipRequestService(UserRepo userRepo, RelationshipRepo relationshipRepo, RelationshipRequestRepo relationshipRequestRepo, RelationshipService relationshipService){
        this.userRepo = userRepo;
        this.relationshipRepo = relationshipRepo;
        this.relationshipRequestRepo = relationshipRequestRepo;
        this.relationshipService = relationshipService;
    }
    

    // Application service (API facing)
    public void sendRequest(String fromUser, String toUser){
        User from = userRepo.findByUserName(fromUser).orElseThrow();

        User to = userRepo.findByUserName(toUser).orElseThrow();

        // Khong duoc gui RelationshipRequest toi chinh ban than
        if(from.getId().equals(to.getId())){
            throw new RuntimeException("Cannot connect to yourself");
        }

        // Dat u1 = id be hon
        // Dat u2 = id lon hon
        User u1 = (from.getId() < to.getId()) ? from : to;
        User u2 = (from.getId() < to.getId()) ? to : from;

        // Kiem tra da co quan he chua
        if (relationshipRepo.findByUser1AndUser2(u1, u2).isPresent()){
            throw new RuntimeException("Already connected");
        }

        // Kiem tra da gui request chua
        if (relationshipRequestRepo.findByFromUserAndToUser(from, to).isPresent()){
            throw new RuntimeException("Already request");
        }
        // Kiem tra ca chieu con lai
        if (relationshipRequestRepo.findByFromUserAndToUser(to, from).isPresent()){
            throw new RuntimeException("Already request");
        }

        RelationshipRequest req = new RelationshipRequest(from, to, RelationshipRequestStatus.PENDING);
        relationshipRequestRepo.save(req);
    }

    @Transactional  // Dam bao tinh toan ven du lieu 
    public void acceptRequest(Long requestId, String userName){
        RelationshipRequest req = relationshipRequestRepo.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found")); // Tim request trong database
        
        if (!req.getToUser().getUserName().equals(userName)) {
            throw new RuntimeException("You are not allowed to accept this request");
        }

        // Neu la accept thi coi nhu la da thanh cong
        if (req.getStatus() == RelationshipRequestStatus.ACCEPTED) {
            return;
        }

        // Request chi co the chap nhan neu chua bi tu choi hay dong y hay bi huy
        if (req.getStatus() != RelationshipRequestStatus.PENDING) {
            throw new RuntimeException("Request is not pending");
        }

        req.setStatus(RelationshipRequestStatus.ACCEPTED);
        relationshipRequestRepo.save(req);
        relationshipService.createRelationship(req.getFromUser(), req.getToUser());
    }

    public void rejectRequest(Long requestId){}

    public void cancelRequest(Long requestId){}
}
