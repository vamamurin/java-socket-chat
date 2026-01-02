package com.example.chat_server.repository;

import com.example.chat_server.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>{
    Optional<User> findByUserName(String userName);
}