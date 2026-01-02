package com.example.chat_server.security;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.chat_server.repository.UserRepo;
import com.example.chat_server.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // Tim user trong database
        User user = userRepo.findByUserName(userName)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));

        // Chuyen doi User thanh UserDetails cua spring security 
        return new MyUserDetails(
            user.getUserName(),
            user.getPassword(),
            Collections.emptyList(), // Authorities
            user.getId(),            // <--- TRUYỀN ID VÀO ĐÂY
            user.getUserFullName()   // Truyền thêm fullname nếu thích
        );
    }

}
