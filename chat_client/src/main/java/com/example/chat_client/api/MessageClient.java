package com.example.chat_client.api;

import com.example.chat_client.core.BaseClient;
import com.example.chat_client.dto.request.MessageRequest;
import com.example.chat_client.dto.response.MessageResponse;
import com.fasterxml.jackson.core.type.TypeReference;


import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class MessageClient extends BaseClient {
    // POST MESSAGE
    public MessageResponse sendMessage(MessageRequest req) throws Exception { 
        try{
            // MessageRequest msgReq = new MessageRequest(new UserRequest(sender), content, new UserRequest(receiver));
            String json = mapper.writeValueAsString(req);

            HttpRequest reqest = getRequestBuilder("/messages")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = httpClient.send(reqest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200){
                return mapper.readValue(response.body(), MessageResponse.class);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // GET MESSAGE
    public List<MessageResponse> getConversation(String partner, int page, int size) throws Exception {
        String url = String.format("/messages/conversation?partner=%s&&page=%d&size=%d", partner, page, size);

        HttpRequest request = getRequestBuilder(url)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200){
            return mapper.readValue(response.body(), new TypeReference<List<MessageResponse>>(){});
        }
        System.err.println("Error GET msg: " + response.statusCode() + " " + response.body());
        return new ArrayList<>();
    }

}




