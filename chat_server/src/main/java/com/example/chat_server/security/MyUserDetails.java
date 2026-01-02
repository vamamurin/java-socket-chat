package com.example.chat_server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MyUserDetails extends User {
    
    // Thêm field ID vào đây
    private final Long id;
    private final String fullName; // Thích thì thêm cả full name để dùng cho tiện

    public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, String fullName) {
        // Gọi constructor của thằng cha (Spring User)
        super(username, password, authorities);
        this.id = id;
        this.fullName = fullName;
    }

    // Getter cho ID
    public Long getId() {
        return id;
    }
    
    public String getFullName() {
        return fullName;
    }
}