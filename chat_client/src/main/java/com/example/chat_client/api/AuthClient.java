package com.example.chat_client.api;

import com.example.chat_client.core.BaseClient;
import com.example.chat_client.dto.request.AuthRequest;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthClient extends BaseClient {
    public String login(AuthRequest req) throws Exception {
        String json = mapper.writeValueAsString(req);
        
        // Goi builder tu class cha (BaseClient), URL noi them /auth/login
        HttpRequest request = getRequestBuilder("/auth/login")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200){
            return response.body();
        }
        else {
            throw new RuntimeException("Login Failed: " + response.statusCode());
        }
    }

    public boolean register(AuthRequest req) throws Exception {
        String json = mapper.writeValueAsString(req);
        // Goi builder tu cha, URL noi them /auth/register
        HttpRequest request = getRequestBuilder("/auth/register")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return (response.statusCode() == 200);
    }
}
