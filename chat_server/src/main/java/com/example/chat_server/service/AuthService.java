package com.example.chat_server.service;

import com.example.chat_server.dto.request.AuthRequest;
import com.example.chat_server.entity.User;
import com.example.chat_server.repository.UserRepo;
import com.example.chat_server.security.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Service
public class AuthService{

    private UserRepo userRepo;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    AuthService(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    public void register(AuthRequest authRequest) {
        if (userRepo.findByUserName(authRequest.getUserName()).isPresent()){
            throw new RuntimeException("Username da ton tai");
        }
        User user = new User();
        user.setUserName(authRequest.getUserName());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setUserFullName(authRequest.getUserName());
        
        userRepo.save(user);
    }

    public String login(AuthRequest authRequest) {
        // Xac thuc username/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
        );
        
        if (authentication.isAuthenticated()){
            return jwtUtil.genarateToken(authRequest.getUserName());
        }
        else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
    
    
}