package com.example.chat_server.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        
        // 1. Lay header Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        // 2. Kiem tra xem header co bat dau bang "Bearer " khong
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7); // Cat bo chu "Bearer "
            userName = jwtUtil.extractUserName(token);  // Lay userName tu token
        }
        
        // 3. Neu co username ma chua xac thuc (securityContext rong)
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            // 4. Neu token hop ly -> set thong tin xac thuc cho spring security
            if(jwtUtil.validateToken(token, userDetails.getUsername())){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Dong dau da xac thuc 
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Cho di tiep vao controller hoac filter ke tiep
        filterChain.doFilter(request, response);
    }


}