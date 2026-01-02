package com.example.chat_client;

import com.example.chat_client.ui.LoginTUI;

public class ChatClientApplication {
    public static void main(String[] args) {
        try {
            // Launch login TUI directly
            new LoginTUI().run();
            // new RegisterTUI().run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}