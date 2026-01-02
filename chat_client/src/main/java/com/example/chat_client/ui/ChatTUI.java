// FILE: project2/chat_client/src/main/java/com/example/chat_client/ui/ChatTUI.java

// Author: vamamurin

/*
tree:
        ChatTUI
        ├── [FIELDS - private]
        │   ├── Screen screen
        │   ├── TextGraphics graphics
        │   ├── TerminalSize termSize
        │   │
        │   ├── ApiClient apiClient
        │   ├── String userName
        │   ├── String friendName
        │   ├── List<ChatMessage> messageHistory
        │   ├── StringBuilder inputBuffer
        │   ├── boolean running
        │   │
        │   ├── TextColor BG_COLOR
        │   ├── TextColor TEXT_PRIMARY
        │   ├── TextColor ACCENT_CYAN
        │   ├── TextColor ACCENT_PINK
        │   ├── TextColor ACCENT_GREEN
        │   └── TextColor BORDER_COLOR
        │
        ├── [PUBLIC CONSTRUCTOR]
        │   └── ChatTUI(ApiClient apiClient, String userName, String friendName)
        │
        ├── [PUBLIC METHOD]
        │   └── run() → Main method
        │
        ├── [PRIVATE METHODS - LOGIC]
        │   ├── fetchMessages() → Poll API, update tin nhắn mới
        │   │
        │   ├── handleInput(KeyStroke key) → Xử lý phím 
        │   │
        │   └── sendMessage() → Gửi tin nhắn + update UI (optimistic update)
        │
        ├── [PRIVATE METHODS - RENDERING]
        │   ├── drawBaseInterface() → Vẽ layout tĩnh: nền, sidebar, statusbar, khung chat, input
        │   │
        │   ├── drawChatWindow() → Vẽ nội dung tin nhắn
        │   │
        │   ├── drawInputArea() → Vẽ ô nhập tin nhắn 
        │   │
        │   ├── drawStatusBar(int x, int width) → Vẽ bar trên cùng (status / title) 
        │   │
        │   ├── drawSidebar(int x, int y, int width, int height) → Vẽ thanh sidebar bên trái
        │   │
        │   └── drawBox(int x, int y, int width, int height, String title, TextColor titleColor) → Vẽ khung viền
        │
        └── END
*/



package com.example.chat_client.ui;

import com.example.chat_client.controller.ClientController; 
import com.example.chat_client.dto.request.UserRequest; 
import com.example.chat_client.model.ChatMessage;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatTUI {
    private Screen screen;
    private TextGraphics graphics;
    private TerminalSize termsize;

    // --- Ở ĐÂY: Dùng Controller ---
    private final ClientController controller; 
    
    private final String userName;
    private String friendName;
    private volatile List<ChatMessage> messageHistory = new ArrayList<>();
    private List<User> relationshipList = new ArrayList<>();
    private StringBuilder inputBuffer = new StringBuilder();
    private boolean running = true;

    // Colors
    // private final TextColor BG_COLOR = new TextColor.RGB(20, 20, 30);
    // private final TextColor TEXT_PRIMARY = TextColor.ANSI.WHITE;
    // private final TextColor ACCENT_CYAN = new TextColor.RGB(0, 255, 255);
    // private final TextColor ACCENT_PINK = new TextColor.RGB(255, 0, 128);
    // private final TextColor ACCENT_GREEN = new TextColor.RGB(0, 255, 128);
    private final TextColor BODER_COLOR = new TextColor.RGB(100, 100, 150);

    private final TextColor BG_COLOR = new TextColor.RGB(15, 15, 25);
    private final TextColor BG_DARKER = new TextColor.RGB(10, 10, 18);
    private final TextColor TEXT_PRIMARY = new TextColor.RGB(230, 230, 240);
    private final TextColor TEXT_SECONDARY = new TextColor.RGB(150, 150, 170);
    private final TextColor TEXT_MUTED = new TextColor.RGB(100, 100, 120);
    
    // Accent colors
    private final TextColor ACCENT_CYAN = new TextColor.RGB(0, 230, 255);
    private final TextColor ACCENT_PINK = new TextColor.RGB(255, 75, 145);
    private final TextColor ACCENT_PURPLE = new TextColor.RGB(180, 100, 255);
    private final TextColor ACCENT_GREEN = new TextColor.RGB(50, 255, 150);
    private final TextColor ACCENT_ORANGE = new TextColor.RGB(255, 165, 80);
    
    // UI Element colors
    // private final TextColor BORDER_COLOR = new TextColor.RGB(80, 80, 120);
    // private final TextColor BORDER_HIGHLIGHT = new TextColor.RGB(120, 120, 180);
    // private final TextColor STATUS_BAR_BG = new TextColor.RGB(25, 25, 40);
    // private final TextColor BUBBLE_MY_BG = new TextColor.RGB(60, 40, 100);
    // private final TextColor BUBBLE_FRIEND_BG = new TextColor.RGB(30, 30, 45);

    // --- CONSTRUCTOR ---
    public ChatTUI(String userName, String friendName) throws IOException {
        this.controller = new ClientController(); // Khởi tạo Controller
        this.userName = userName;
        this.friendName = friendName;

        // Init Lanterna (Giữ nguyên)
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        // Terminal term = terminalFactory.createTerminal(); 
        terminalFactory.setPreferTerminalEmulator(false);
        terminalFactory.setForceAWTOverSwing(false);
        Terminal term = null;
        try {
            term = terminalFactory.createTerminal();
        } catch (IOException e) {
            // Nếu vẫn lỗi, thử ép dùng Text terminal thuần túy
            term = terminalFactory.createTerminalEmulator();
        }
        screen = new TerminalScreen(term);
        screen.startScreen();
        screen.setCursorPosition(null);
        graphics = screen.newTextGraphics();
        termsize = screen.getTerminalSize();

        // Load danh sách relationship ngay khi mở Chat
        try {
            this.relationshipList = controller.getRelationList(userName);
        } catch (Exception e) {
            this.relationshipList = new ArrayList<>(); // Tránh null pointer
        }
    }

    public void run() throws IOException{
        drawBaseInterface();
        screen.refresh();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchMessages, 0, 2, TimeUnit.SECONDS);

        while (running) {
            KeyStroke key = screen.pollInput();
            if (key != null){
                handleInput(key);
                drawInputArea();
                screen.refresh();
            }

            TerminalSize newSize = screen.doResizeIfNecessary();
            if (newSize != null){
                termsize = newSize;
                drawBaseInterface();
                drawChatWindow();
                screen.refresh();
            }
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }

        scheduler.shutdown();
        screen.stopScreen();
        // System.exit(0); // Thoát hẳn
    }

    private void fetchMessages(){
        try{
            // Gọi qua Controller
            List<ChatMessage> newMsg = controller.getConversation(friendName);

            boolean needUpdate = false;
            if (newMsg.size() != messageHistory.size()){
                needUpdate = true;
            } else if (!newMsg.isEmpty() && !messageHistory.isEmpty()) {
                // Check tạm bằng nội dung tin cuối (tốt nhất là check ID)
                Long lastOld = messageHistory.get(messageHistory.size() - 1).getId();
                Long lastNew = newMsg.get(newMsg.size() - 1).getId();
                if (!lastOld.equals(lastNew)) needUpdate = true;
            }

            if (needUpdate){
                messageHistory = newMsg;
                drawChatWindow(); 
                screen.refresh();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleInput(KeyStroke key){
        if (key.getKeyType() == KeyType.Character){
            inputBuffer.append(key.getCharacter());
        }
        else if (key.getKeyType() == KeyType.Backspace){
            if (inputBuffer.length() > 0){
                inputBuffer.setLength(inputBuffer.length() - 1);
            }
        }
        else if (key.getKeyType() == KeyType.Enter){
            sendMessage();
        }
        else if (key.getKeyType() == KeyType.ArrowUp) {
            switchChatPartner(-1); // Lùi lại 1 người
        }
        else if (key.getKeyType() == KeyType.ArrowDown) {
            switchChatPartner(1);  // Tiến tới 1 người
        }
        else if (key.getKeyType() == KeyType.F2) {
            try {
                // Tạm dừng ChatTUI, mở FriendTUI
                // Truyền controller hiện tại để không phải login lại socket
                new RelationshipTUI(userName, controller).run();
                
                // Khi FriendTUI đóng (ESC), code sẽ chạy tiếp xuống dưới
                screen.clear(); // Xóa màn hình cũ
                termsize = screen.getTerminalSize(); // Cập nhật lại size phòng khi resize bên kia
                drawBaseInterface(); // Vẽ lại giao diện Chat
                fetchMessages(); // Cập nhật tin nhắn mới
                screen.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (key.getKeyType() == KeyType.Escape){
            running = false;
        }
    }

    // --- SỬA HÀM SEND ---
    private void sendMessage(){
        String content = inputBuffer.toString().trim();
        if(!content.isEmpty()){
            // Tạo UserRequest để gửi đi
            UserRequest receiverReq = new UserRequest(friendName);
            
            // Gọi Controller
            controller.sendMessage(content, receiverReq);
            
            inputBuffer.setLength(0);
            
            // Fetch lại luôn cho mượt
            fetchMessages();
        }
    }

    
    
    // Ham ve khung hinh ban dau (Ham nay chi duoc goi ban dau hoac khi resize)
    private void drawBaseInterface(){
        graphics.setBackgroundColor(BG_COLOR);
        graphics.fill(' ');
        
        // Lay chieu dai va chieu rong tu TerminalSize termsize
        int width = termsize.getColumns();
        int height = termsize.getRows();
        
        // Kich thuoc cua vung hien thi user
        int sideBarWidth = width/4;
        
        
        
        // status bar
        drawStatusBar(0, width);
        
        // side bar
        drawSideBar(0, 1, sideBarWidth, height - 1);
        
        // Ve khung cho chat window va input area
        drawChatWindow();
        drawInputArea();
        

    }

    private void drawChatWindow() {
        List<ChatMessage> snapshotMessages = this.messageHistory;
        // --- 1. SETUP KÍCH THƯỚC ---
        int termWidth = termsize.getColumns();
        int termHeight = termsize.getRows();
        int sideBarWidth = termWidth / 4;

        int x = sideBarWidth + 1;
        int y = 1;
        int width = termWidth - sideBarWidth - 1;
        int height = (int)((termHeight - 1) * 0.8) - 1; 

        // Vùng vẽ an toàn (Padding trên dưới 1 tí cho đẹp)
        int drawAreaTop = y + 1;
        int drawAreaHeight = height - 2; 

        // Xóa nền & Vẽ khung
        graphics.setBackgroundColor(BG_DARKER);
        for (int i = y + 1; i < y + height - 1; i++) {
            graphics.drawLine(x + 1, i, x + width - 2, i, ' ');
        }
        drawBox(x, y, width, height, "CHAT BOX", ACCENT_PURPLE);

        // --- 2. BƯỚC TÍNH TOÁN ---
        
        List<ChatMessage> visibleMessages = new ArrayList<>();
        int currentUsedHeight = 0;
        int maxBubbleWidth = (int)(width * 0.6);

        for (int i = snapshotMessages.size() - 1; i >= 0; i--) {
            ChatMessage msg = snapshotMessages.get(i);
            int msgHeight = calculateMessageHeight(msg, maxBubbleWidth);
            
            if (currentUsedHeight + msgHeight <= drawAreaHeight) {
                visibleMessages.add(0, msg);
                currentUsedHeight += msgHeight;
            } else {
                break; 
            }
        }

        // --- 3. BƯỚC VẼ (RENDER TOP-DOWN) ---
        // Tính tọa độ Y bắt đầu.
        // Nếu tin nhắn ít (chưa full màn hình), thì đẩy nó xuống đáy.
        // startY = (Đáy vùng vẽ) - (Tổng chiều cao các tin đã chọn)
        int drawAreaBottom = drawAreaTop + drawAreaHeight - 1;
        int currentY = drawAreaBottom - currentUsedHeight + 1;

        for (ChatMessage msg : visibleMessages) {
            boolean isMe = msg.getSender().getUserName().equals(userName);
            String content = msg.getContent();
            String timeStr = (msg.getCreatedAt() != null) 
                 ? msg.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")) : "Now";

            // Tách dòng
            List<String> lines = wrapText(content, maxBubbleWidth);
            
            // Tính độ rộng khung
            int longestLineLen = 0;
            for (String line : lines) if (line.length() > longestLineLen) longestLineLen = line.length();
            int boxInnerWidth = longestLineLen + 2;
            String horizontalLine = "─".repeat(boxInnerWidth);
            
            // --- VẼ TỪ TRÊN XUỐNG (Y tăng dần) ---

            if (!isMe) {
                // --- NGƯỜI KHÁC ---
                int startX = x + 2;
                TextColor bubbleColor = TEXT_PRIMARY;

                // 1. Viền trên
                graphics.setForegroundColor(bubbleColor);
                graphics.putString(startX, currentY, "╭" + horizontalLine + "╮");
                currentY++;

                // 2. Nội dung
                for (String line : lines) {
                    String padded = line + " ".repeat(longestLineLen - line.length());
                    graphics.putString(startX, currentY, "│ " + padded + " │");
                    currentY++;
                }

                // 3. Viền dưới
                graphics.putString(startX, currentY, "╰" + horizontalLine + "╯");
                currentY++;

                // 4. Time
                graphics.setBackgroundColor(BG_COLOR);
                graphics.setForegroundColor(TEXT_MUTED);
                graphics.putString(startX + 1, currentY, timeStr);
                currentY++; // Xuống dòng cho tin tiếp theo

            } else {
                // --- BẢN THÂN ---
                int startX = (x + width - 1) - (boxInnerWidth + 2);
                TextColor bubbleColor = ACCENT_CYAN;

                // 1. Viền trên
                graphics.setForegroundColor(bubbleColor);
                graphics.putString(startX, currentY, "╭" + horizontalLine + "╮");
                currentY++;

                // 2. Nội dung
                for (String line : lines) {
                    String padded = line + " ".repeat(longestLineLen - line.length());
                    graphics.putString(startX, currentY, "│ " + padded + " │");
                    currentY++;
                }

                // 3. Viền dưới
                graphics.putString(startX, currentY, "╰" + horizontalLine + "╯");
                currentY++;

                // 4. Time
                graphics.setBackgroundColor(BG_COLOR);
                graphics.setForegroundColor(TEXT_MUTED);
                int timeX = (x + width - 1) - timeStr.length() - 1;
                graphics.putString(timeX, currentY, timeStr);
                currentY++;
            }

            // Cộng thêm 1 dòng khoảng cách giữa các tin nhắn
            currentY++; 
        }

        graphics.setBackgroundColor(BG_COLOR);
    }
    
    // Tính chiều cao thực tế của 1 tin nhắn (bao gồm viền, time, nội dung đã wrap)
    private int calculateMessageHeight(ChatMessage msg, int maxWidth) {
        String content = msg.getContent();
        // Dùng hàm wrapText mày đã có
        List<String> lines = wrapText(content, maxWidth);
        
        // Công thức: 1 dòng Time + 1 dòng Viền dưới + N dòng Text + 1 dòng Viền trên
        // + 1 dòng khoảng cách giữa các tin nhắn
        return 1 + 1 + lines.size() + 1 + 1; 
    }
    
    // Ham ve INPUT BOX
    private void drawInputArea(){

        int termWidth = termsize.getColumns();
        int termHeight = termsize.getRows();
        int sideBarWidth = termWidth/4;
        int chatWindowHeight = (int)((termHeight - 1) * 0.8);

        // x, y, width, height
        int x = sideBarWidth + 1;
        int y = chatWindowHeight + 1;
        int width = termWidth - sideBarWidth - 1;
        int height = termHeight - y;

        drawBox(x, y, width, height, "Input Box", ACCENT_PURPLE);
        

        // Ve con tro
        graphics.setForegroundColor(ACCENT_PINK);
        graphics.putString(x + 2, y + 2, ">");

        // Xoa dong cu
        graphics.setBackgroundColor(BG_COLOR);
        graphics.drawLine(x + 4, y + 2, x + width - 2, y + 2, ' ');

        // Ve text dang go
        graphics.setForegroundColor(TEXT_PRIMARY);
        graphics.putString(x+4, y+2, inputBuffer.toString() + "|");
    }

    // Ham ve status bar
    private void drawStatusBar(int x, int width){
        graphics.setBackgroundColor(new TextColor.RGB(30, 30, 45));
        graphics.drawLine(0, 0, width, 0, ' '); // Xóa sạch bar cũ
        
        // Vẽ status
        graphics.setForegroundColor(ACCENT_CYAN);
        String status = " CHAT TUI v1.0 | Connected as: " + userName;
        graphics.putString(1, 0, status);

        graphics.setBackgroundColor(BG_COLOR);
    }


    // private void drawSideBar(int x, int y, int width, int height){
    //     drawBox(x, y, width, height, "MENU", ACCENT_PURPLE);

    //     // --- SECTION 1: MY INFO ---
    //     graphics.setForegroundColor(ACCENT_GREEN);
    //     String myNameDisplay = userName;
    //     if (myNameDisplay.length() > width - 3) myNameDisplay = myNameDisplay.substring(0, width - 3);
    //     graphics.putString(x + 2, y + 2, myNameDisplay);
        
    //     graphics.setForegroundColor(TEXT_SECONDARY);
    //     graphics.putString(x + 2, y + 3, "─".repeat(width - 4));

    //     // --- SECTION 2: NAVIGATION ---
    //     graphics.setForegroundColor(ACCENT_ORANGE); 
    //     graphics.putString(x + 2, y + 5, "[F2] Manage Rel.");
        
    //     // --- SECTION 3: LIST RELATIONSHIPS ---
    //     graphics.setForegroundColor(TEXT_MUTED);
    //     graphics.putString(x + 2, y + 7, "CONTACTS:");

    //     int listY = y + 8; // Bắt đầu vẽ từ dòng thứ 8

    //     if (relationshipList == null || relationshipList.isEmpty()) {
    //         graphics.setForegroundColor(TEXT_SECONDARY);
    //         graphics.putString(x + 2, listY, "(Empty)");
    //     } else {
    //         for (User user : relationshipList) {
    //             // 1. Kiểm tra bounds: Nếu vẽ quá chiều cao sidebar thì dừng lại (tránh vỡ khung)
    //             if (listY >= y + height - 1) break; 

    //             String name = user.getUserName();
    //             // 2. Kiểm tra xem có phải người đang chat không
    //             boolean isCurrentChat = name.equals(friendName);

    //             String displayLine;
    //             if (isCurrentChat) {
    //                 // -> HIGHLIGHT: Màu sáng (Cyan), có mũi tên
    //                 graphics.setForegroundColor(ACCENT_CYAN);
    //                 displayLine = "➜ " + name;
    //             } else {
    //                 // -> NORMAL: Màu tối hơn (Text Primary), lùi đầu dòng
    //                 graphics.setForegroundColor(TEXT_PRIMARY);
    //                 displayLine = "  " + name;
    //             }

    //             // 3. Cắt chuỗi nếu tên quá dài so với độ rộng cột
    //             if (displayLine.length() > width - 4) {
    //                 displayLine = displayLine.substring(0, width - 4) + "…";
    //             }

    //             graphics.putString(x + 2, listY, displayLine);
    //             listY++; 
    //         }
    //     }
    // }
    private void drawSideBar(int x, int y, int width, int height) {
    drawBox(x, y, width, height, "MENU", ACCENT_PURPLE);
    
    int currentY = y + 2;
    
    // ═══════════════════════════════════════
    // USER INFO
    // ═══════════════════════════════════════
    graphics.setForegroundColor(ACCENT_GREEN);
    graphics.putString(x + 2, currentY, "◉ " + userName);
    currentY += 2;
    
    // Separator
    graphics.setForegroundColor(TEXT_SECONDARY);
    graphics.putString(x + 2, currentY, "─".repeat(width - 4));
    currentY += 2;
    
    // ═══════════════════════════════════════
    // QUICK ACTION
    // ═══════════════════════════════════════
    graphics.setForegroundColor(ACCENT_ORANGE);
    graphics.putString(x + 2, currentY, "[F2]");
    graphics.setForegroundColor(TEXT_PRIMARY);
    graphics.putString(x + 7, currentY, "Manage Relations");
    currentY += 2;
    
    // Separator
    graphics.setForegroundColor(TEXT_SECONDARY);
    graphics.putString(x + 2, currentY, "─".repeat(width - 4));
    currentY += 2;
    
    // ═══════════════════════════════════════
    // CONTACTS HEADER
    // ═══════════════════════════════════════
    graphics.setForegroundColor(ACCENT_CYAN);
    graphics.putString(x + 2, currentY, "CONTACTS");
    graphics.setForegroundColor(TEXT_MUTED);
    graphics.putString(x + width - 9, currentY, "↑↓ nav");
    currentY += 2;
    
    // ═══════════════════════════════════════
    // CONTACTS LIST
    // ═══════════════════════════════════════
    int listY = currentY;
    
    if (relationshipList == null || relationshipList.isEmpty()) {
        graphics.setForegroundColor(TEXT_SECONDARY);
        graphics.putString(x + 2, listY, "(Empty)");
    } else {
        for (User user : relationshipList) {
            if (listY >= y + height - 2) break;
            
            String name = user.getUserName();
            boolean isCurrentChat = name.equals(friendName);
            
            if (isCurrentChat) {
                // Active chat - highlighted
                graphics.setBackgroundColor(new TextColor.RGB(40, 40, 60));
                graphics.setForegroundColor(ACCENT_CYAN);
                String displayLine = "▶ " + name;
                if (displayLine.length() > width - 4) {
                    displayLine = displayLine.substring(0, width - 7) + "...";
                }
                // Pad line to fill width
                displayLine = displayLine + " ".repeat(Math.max(0, width - 4 - displayLine.length()));
                graphics.putString(x + 2, listY, displayLine);
                graphics.setBackgroundColor(BG_COLOR);
            } else {
                // Regular contact
                graphics.setForegroundColor(TEXT_PRIMARY);
                String displayLine = "  " + name;
                if (displayLine.length() > width - 4) {
                    displayLine = displayLine.substring(0, width - 7) + "...";
                }
                graphics.putString(x + 2, listY, displayLine);
            }
            
            listY++;
        }
    }
    
    // ═══════════════════════════════════════
    // FOOTER
    // ═══════════════════════════════════════
    graphics.setForegroundColor(ACCENT_GREEN);
    graphics.putString(x + 2, y + height - 2, "● Online");
}

    private void drawBox(int x, int y, int width, int height, String title, TextColor titleColor){
        // Set mau cua duong vien
        graphics.setForegroundColor(BODER_COLOR); // #646496

        // Top border
        graphics.putString(x, y, "╭");
        for (int i = 1; i < width - 1; i++){
            graphics.putString(x+i, y, "─");
        }
        graphics.putString(x + width - 1, y, "╮");

        // title
        // set mau cua title
        graphics.setForegroundColor(titleColor);
        graphics.setBackgroundColor(BG_COLOR);
        String titleStr = " " + title + " ";
        graphics.putString(x+2, y, titleStr);
        graphics.setBackgroundColor(BG_COLOR);

        // two sides
        graphics.setForegroundColor(BODER_COLOR);
        for (int i = 1; i < height - 1; i++){
            graphics.putString(x, y+i, "│");
            graphics.putString(x+width-1, y+i, "│");
        }
        
        // Bottom border
        graphics.putString(x, y+height-1, "╰");
        for (int i = 1; i < width -1; i++){
            graphics.putString(x+i, y+height-1, "─");
        }
        graphics.putString(x+width-1, y+height-1, "╯");
    }

    // Hàm tách chuỗi thành nhiều dòng (Word Wrap)
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            // Nếu từ quá dài hơn cả max width thì buộc phải cắt (force split)
            if (word.length() > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                }
                // Cắt từ dài ngoằng thành nhiều khúc
                while (word.length() > maxWidth) {
                    lines.add(word.substring(0, maxWidth));
                    word = word.substring(maxWidth);
                }
                currentLine.append(word).append(" ");
            } 
            // Logic ghép từ bình thường
            else if (currentLine.length() + word.length() <= maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString().trim());
                currentLine.setLength(0);
                currentLine.append(word).append(" ");
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }
        return lines;
    }
 
    private void switchChatPartner(int offset) {
        if (relationshipList == null || relationshipList.isEmpty()) return;

        // 1. Tìm vị trí (index) của người đang chat hiện tại trong danh sách
        int currentIndex = -1;
        for (int i = 0; i < relationshipList.size(); i++) {
            if (relationshipList.get(i).getUserName().equals(friendName)) {
                currentIndex = i;
                break;
            }
        }

        // Nếu không tìm thấy (lỗi lạ) thì gán về 0
        if (currentIndex == -1) currentIndex = 0;

        // 2. Tính index mới (Có xử lý vòng lặp: Đang ở đầu bấm Lên -> Về cuối)
        int newIndex = currentIndex + offset;
        
        if (newIndex < 0) {
            newIndex = relationshipList.size() - 1; // Vòng về cuối
        } else if (newIndex >= relationshipList.size()) {
            newIndex = 0; // Vòng về đầu
        }

        // 3. Cập nhật friendName
        friendName = relationshipList.get(newIndex).getUserName();

        // 4. Reset trạng thái chat để load người mới
        messageHistory.clear(); // Xóa tin nhắn cũ của người trước
        // drawChatWindow(); // Xóa màn hình chat ngay lập tức cho đỡ nhầm (Optional)
        
        // 5. Cập nhật UI (Highlight người mới ở Sidebar) và Fetch tin nhắn mới
        drawBaseInterface(); // Vẽ lại sidebar highlight
        fetchMessages();     // Kéo tin nhắn của người mới về ngay
        
        try {
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}