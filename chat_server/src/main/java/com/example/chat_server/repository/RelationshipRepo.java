package com.example.chat_server.repository;

import com.example.chat_server.entity.Relationship;
import com.example.chat_server.entity.User;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepo extends JpaRepository<Relationship, Long> {
    Optional<Relationship> findByUser1AndUser2(User user1, User user2);

    @Query("""
        SELECT r
        FROM Relationship r
        WHERE r.user1.id = :userId OR r.user2.id = :userId
        """)
    List<Relationship> findAllByUserId(@Param("userId") Long userId);
    // Service need this
    // User u1 = me.getId() < other.getId() ? me : other;
    // User u2 = me.getId() < other.getId() ? other : me;

    // Optional<Relationship> rel =
    //     relationshipRepo.findByUser1AndUser2(u1, u2);

}
