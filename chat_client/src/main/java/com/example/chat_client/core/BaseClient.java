package com.example.chat_client.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class BaseClient {
    protected final String BASE_URL = "http://localhost:8080";
    protected final HttpClient httpClient;
    protected final ObjectMapper mapper;

    public BaseClient(){
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    protected HttpRequest.Builder getRequestBuilder(String uri){
        var builder = HttpRequest.newBuilder().uri(URI.create(BASE_URL+uri));
        if(UserSession.getInstance().isLoggedIn()){
            builder.header("Authorization", "Bearer " + UserSession.getInstance().getToken());
        }
        return builder.header("Content-Type", "application/json");
    }
}
