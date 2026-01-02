// FILE: project2/chat_client/src/main/java/com/example/chat_client/ui/RelationshipTUI.java
package com.example.chat_client.ui;

import com.example.chat_client.controller.ClientController;
import com.example.chat_client.model.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelationshipTUI {
    private Screen screen;
    private TextGraphics graphics;
    private TerminalSize termsize;
    private final ClientController controller;
    private final String currentUserName;

    // Data
    private List<User> friendList = new ArrayList<>();
    private StringBuilder addFriendBuffer = new StringBuilder();
    private String statusMessage = "";
    private boolean isError = false;
    private boolean running = true;

    // Colors
    private final TextColor BG_COLOR = new TextColor.RGB(20, 20, 30);
    private final TextColor TEXT_PRIMARY = new TextColor.RGB(230, 230, 240);
    private final TextColor ACCENT_CYAN = new TextColor.RGB(0, 230, 255);
    private final TextColor ACCENT_GREEN = new TextColor.RGB(50, 255, 150);
    private final TextColor ACCENT_ORANGE = new TextColor.RGB(255, 165, 80);
    private final TextColor BORDER_COLOR = new TextColor.RGB(80, 80, 120);
    private final TextColor ERROR_COLOR = new TextColor.RGB(255, 80, 80);

    // --- ĐỔI TÊN CONSTRUCTOR ---
    public RelationshipTUI(String currentUserName, ClientController controller) throws IOException {
        this.currentUserName = currentUserName;
        this.controller = (controller != null) ? controller : new ClientController();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal term = terminalFactory.createTerminal();
        screen = new TerminalScreen(term);
        screen.startScreen();
        screen.setCursorPosition(null);
        graphics = screen.newTextGraphics();
        termsize = screen.getTerminalSize();

        fetchRelationshipList();
    }

    public void run() throws IOException {
        while (running) {
            drawInterface();
            screen.refresh();

            KeyStroke key = screen.pollInput();
            if (key != null) {
                handleInput(key);
            }
            
            TerminalSize newSize = screen.doResizeIfNecessary();
            if (newSize != null) {
                termsize = newSize;
            }

            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
        screen.stopScreen();
    }

    private void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            running = false; 
        } else if (key.getKeyType() == KeyType.Character) {
            addFriendBuffer.append(key.getCharacter());
        } else if (key.getKeyType() == KeyType.Backspace) {
            if (addFriendBuffer.length() > 0) addFriendBuffer.setLength(addFriendBuffer.length() - 1);
        } else if (key.getKeyType() == KeyType.Enter) {
            sendFriendRequest();
        } 
    }

    // --- LOGIC ---
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void fetchRelationshipList() {
        try {
            // Log thử xem có chạy vào đây không
            // System.err.println("DEBUG: Dang lay danh sach ban be...");
            
            List<User> friends = controller.getRelationList(currentUserName);
        
            if (friends != null) {
                // System.err.println("DEBUG: Lay duoc " + friends.size() + " ban.");
                friendList.clear();
                friendList.addAll(friends);
            } else {
                // System.err.println("DEBUG: List ban be bi NULL");
            }
        } catch (Exception e) {
            statusMessage = "Error loading list: " + e.getMessage();
            e.printStackTrace(); // Quan trọng: In lỗi ra để đọc
        }
    }

    private void sendFriendRequest() {
        String targetUser = addFriendBuffer.toString().trim();
        if (targetUser.isEmpty()) return;

        try {
            // TODO: Gọi API gửi kết bạn
            boolean success = true; 

            if (success) {
                statusMessage = "Request sent to " + targetUser + "!";
                isError = false ;
                addFriendBuffer.setLength(0); 
            } else {
                statusMessage = "User not found.";
                isError = true;
            }
        } catch (Exception e) {
            statusMessage = "Error: " + e.getMessage();
            isError = true;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // --- RENDERING ---

    private void drawInterface() {
        graphics.setBackgroundColor(BG_COLOR);
        graphics.fill(' ');

        int width = termsize.getColumns();
        int height = termsize.getRows();

        drawStatusBar(width);

        int splitX = (int)(width * 0.6);

        // --- LEFT: LIST ---
        drawBox(0, 1, splitX, height - 1, "FRIEND LIST", ACCENT_CYAN);
        
        int listY = 3;
        graphics.setForegroundColor(TEXT_PRIMARY);
        for (User friend : friendList) {
            if (listY >= height - 2) break;
            graphics.putString(5, listY, friend.getUserName());
            listY += 2; 
        }

        // --- RIGHT: ADD FRIEND ---
        drawBox(splitX, 1, width - splitX, height - 1, "ADD FRIEND", ACCENT_ORANGE);
        
        int formX = splitX + 2;
        int formY = 4;
        
        graphics.putString(formX, formY, "Enter Username:");
        
        graphics.setForegroundColor(ACCENT_ORANGE);
        graphics.putString(formX, formY + 1, "┌" + "─".repeat(width - splitX - 6) + "┐");
        graphics.putString(formX, formY + 2, "│ " + addFriendBuffer.toString() + "_"); 
        graphics.putString(formX, formY + 3, "└" + "─".repeat(width - splitX - 6) + "┘");

        graphics.setForegroundColor(new TextColor.RGB(150, 150, 150));
        graphics.putString(formX, formY + 5, "[ENTER] to Send Request");

        if (!statusMessage.isEmpty()) {
            graphics.setForegroundColor(isError ? ERROR_COLOR : ACCENT_GREEN);
            graphics.putString(formX, formY + 7, (isError ? "✗ " : "✓ ") + statusMessage);
        }
    }

    private void drawStatusBar(int width) {
        graphics.setForegroundColor(ACCENT_ORANGE);
        graphics.setBackgroundColor(new TextColor.RGB(40, 30, 20));
        // Đổi title status bar
        String status = " RELATIONSHIP MANAGER | [ESC] Back to Chat ";
        int padding = (width - status.length()) / 2;
        for (int i = 0; i < width; i++) graphics.putString(i, 0, " ");
        graphics.putString(padding, 0, status);
        graphics.setBackgroundColor(BG_COLOR);
    }

    private void drawBox(int x, int y, int width, int height, String title, TextColor color) {
        graphics.setForegroundColor(BORDER_COLOR);
        graphics.putString(x, y, "╭");
        for (int i = 1; i < width - 1; i++) graphics.putString(x + i, y, "─");
        graphics.putString(x + width - 1, y, "╮");
        
        for (int i = 1; i < height - 1; i++) {
            graphics.putString(x, y + i, "│");
            graphics.putString(x + width - 1, y + i, "│");
        }
        
        graphics.putString(x, y + height - 1, "╰");
        for (int i = 1; i < width - 1; i++) graphics.putString(x + i, y + height - 1, "─");
        graphics.putString(x + width - 1, y + height - 1, "╯");

        graphics.setForegroundColor(color);
        graphics.putString(x + 2, y, " " + title + " ");
    }
}