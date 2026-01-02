package com.example.chat_client.api;

import com.example.chat_client.core.BaseClient;
import com.example.chat_client.dto.response.RelationshipResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RelationshipClient extends BaseClient {

    // Gọi vào RelationshipController bên Server
    // URL: /relationships/list
    public List<RelationshipResponse> getFriendList() {
        try {
            HttpRequest request = getRequestBuilder("/relationships/list")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<RelationshipResponse>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}