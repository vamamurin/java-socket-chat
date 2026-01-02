package com.example.chat_client.ui;

import com.example.chat_client.controller.ClientController;
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

public class RegisterTUI {
    private Screen screen;
    private TextGraphics graphics;
    private TerminalSize termsize;
    private final ClientController controller;

    // Form fields
    private StringBuilder usernameBuffer = new StringBuilder();
    private StringBuilder passwordBuffer = new StringBuilder();
    private StringBuilder confirmPasswordBuffer = new StringBuilder();
    private int currentField = 0; // 0=username, 1=password, 2=confirm password
    private String errorMessage = "";
    private String successMessage = "";
    private boolean isRegistered = false;
    private boolean running = true;

    // Colors (matching ChatTUI and LoginTUI)
    private final TextColor BG_COLOR = new TextColor.RGB(20, 20, 30);
    private final TextColor TEXT_PRIMARY = TextColor.ANSI.WHITE;
    private final TextColor ACCENT_CYAN = new TextColor.RGB(0, 255, 255);
    private final TextColor ACCENT_PINK = new TextColor.RGB(255, 0, 128);
    private final TextColor ACCENT_GREEN = new TextColor.RGB(0, 255, 128);
    private final TextColor ACCENT_PURPLE = new TextColor.RGB(180, 100, 255);
    private final TextColor BORDER_COLOR = new TextColor.RGB(100, 100, 150);
    private final TextColor ERROR_COLOR = new TextColor.RGB(255, 50, 50);
    private final TextColor SUCCESS_COLOR = new TextColor.RGB(0, 255, 128);

    public RegisterTUI() throws IOException {
        this.controller = new ClientController();

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal term = terminalFactory.createTerminal();
        screen = new TerminalScreen(term);
        screen.startScreen();
        screen.setCursorPosition(null);
        graphics = screen.newTextGraphics();
        termsize = screen.getTerminalSize();
    }

    public void run() throws IOException {
        while (running) {
            drawRegisterInterface();
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
        
        // If registration successful, launch login screen
        if (isRegistered) {
            new LoginTUI().run();
        }
    }

    private void handleInput(KeyStroke key) {
        // Clear success message on any input
        if (!successMessage.isEmpty() && key.getKeyType() != KeyType.Escape) {
            successMessage = "";
        }

        if (key.getKeyType() == KeyType.Character) {
            getCurrentBuffer().append(key.getCharacter());
            errorMessage = "";
        }
        else if (key.getKeyType() == KeyType.Backspace) {
            StringBuilder buffer = getCurrentBuffer();
            if (buffer.length() > 0) {
                buffer.setLength(buffer.length() - 1);
            }
        }
        else if (key.getKeyType() == KeyType.Tab || key.getKeyType() == KeyType.ArrowDown) {
            currentField = (currentField + 1) % 3;
            errorMessage = "";
        }
        else if (key.getKeyType() == KeyType.ArrowUp) {
            currentField = (currentField - 1 + 3) % 3;
            errorMessage = "";
        }
        else if (key.getKeyType() == KeyType.Enter) {
            if (currentField < 2) {
                currentField++;
            } else {
                attemptRegister();
            }
        }
        else if (key.getKeyType() == KeyType.Escape) {
            if (!successMessage.isEmpty()) {
                // If showing success message, ESC goes to login
                isRegistered = true;
            }
            running = false;
        }
    }

    private StringBuilder getCurrentBuffer() {
        switch (currentField) {
            case 0: return usernameBuffer;
            case 1: return passwordBuffer;
            case 2: return confirmPasswordBuffer;
            default: return usernameBuffer;
        }
    }

    private void attemptRegister() {
        String user = usernameBuffer.toString().trim();
        String pass = passwordBuffer.toString().trim();
        String confirmPass = confirmPasswordBuffer.toString().trim();

        // Validation
        if (user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            errorMessage = "All fields are required!";
            return;
        }

        if (user.length() < 3) {
            errorMessage = "Username must be at least 3 characters!";
            currentField = 0;
            return;
        }

        if (pass.length() < 6) {
            errorMessage = "Password must be at least 6 characters!";
            currentField = 1;
            return;
        }

        if (!pass.equals(confirmPass)) {
            errorMessage = "Passwords do not match!";
            confirmPasswordBuffer.setLength(0);
            currentField = 2;
            return;
        }

        // Attempt registration
        try {
            if (controller.register(user, pass)) {
                successMessage = "Registration successful! Press ESC to login.";
                isRegistered = true;
                // Clear password fields for security
                passwordBuffer.setLength(0);
                confirmPasswordBuffer.setLength(0);
                currentField = 0;
            } else {
                errorMessage = "Registration failed! Username may already exist.";
                passwordBuffer.setLength(0);
                confirmPasswordBuffer.setLength(0);
                currentField = 1;
            }
        } catch (Exception e) {
            errorMessage = "Connection error: " + e.getMessage();
        }
    }

    private void drawRegisterInterface() {
        // Clear screen
        graphics.setBackgroundColor(BG_COLOR);
        graphics.fill(' ');

        int width = termsize.getColumns();
        int height = termsize.getRows();

        // Draw status bar
        drawStatusBar(width);

        // Calculate centered register box
        int boxWidth = Math.min(60, width - 4);
        int boxHeight = 20;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;

        // Draw main register box
        drawBox(boxX, boxY, boxWidth, boxHeight, "REGISTER", ACCENT_PURPLE);

        // Draw ASCII art logo
        drawLogo(boxX, boxY + 2, boxWidth);

        // Draw form fields
        int formStartY = boxY + 7;
        drawFormField(boxX + 2, formStartY, boxWidth - 4, "Username", usernameBuffer.toString(), currentField == 0);
        drawFormField(boxX + 2, formStartY + 3, boxWidth - 4, "Password", maskPassword(passwordBuffer.toString()), currentField == 1);
        drawFormField(boxX + 2, formStartY + 6, boxWidth - 4, "Confirm Password", maskPassword(confirmPasswordBuffer.toString()), currentField == 2);

        // Draw error or success message
        if (!errorMessage.isEmpty()) {
            graphics.setForegroundColor(ERROR_COLOR);
            String error = "✗ " + errorMessage;
            int errorX = boxX + (boxWidth - error.length()) / 2;
            graphics.putString(errorX, formStartY + 9, error);
        } else if (!successMessage.isEmpty()) {
            graphics.setForegroundColor(SUCCESS_COLOR);
            String success = "✓ " + successMessage;
            int successX = boxX + (boxWidth - success.length()) / 2;
            graphics.putString(successX, formStartY + 9, success);
        }

        // Draw instructions
        graphics.setForegroundColor(new TextColor.RGB(150, 150, 150));
        String[] instructions = {
            "TAB/↑↓: Navigate | ENTER: Submit | ESC: " + (successMessage.isEmpty() ? "Cancel" : "Login")
        };
        int instrY = boxY + boxHeight + 1;
        for (String instr : instructions) {
            int instrX = (width - instr.length()) / 2;
            graphics.putString(instrX, instrY, instr);
        }

        // Draw link to login
        if (successMessage.isEmpty()) {
            graphics.setForegroundColor(ACCENT_CYAN);
            String loginLink = "Already have an account? Press ESC to cancel";
            int linkX = (width - loginLink.length()) / 2;
            graphics.putString(linkX, instrY + 1, loginLink);
        }
    }

    private void drawFormField(int x, int y, int width, String label, String value, boolean active) {
        // Draw label
        graphics.setForegroundColor(active ? ACCENT_PINK : ACCENT_GREEN);
        graphics.putString(x, y, label + ":");

        // Draw input box
        int inputY = y + 1;
        graphics.setForegroundColor(active ? ACCENT_CYAN : BORDER_COLOR);
        
        // Top border
        graphics.putString(x, inputY, "┌");
        for (int i = 1; i < width - 1; i++) {
            graphics.putString(x + i, inputY, "─");
        }
        graphics.putString(x + width - 1, inputY, "┐");

        // Content
        graphics.setForegroundColor(TEXT_PRIMARY);
        graphics.setBackgroundColor(BG_COLOR);
        String displayValue = value;
        if (displayValue.length() > width - 4) {
            displayValue = displayValue.substring(0, width - 4);
        }
        graphics.putString(x + 2, inputY + 1, displayValue + (active ? "█" : " "));
        
        // Clear rest of line
        for (int i = x + 2 + displayValue.length() + 1; i < x + width - 1; i++) {
            graphics.putString(i, inputY + 1, " ");
        }

        // Bottom border
        graphics.setForegroundColor(active ? ACCENT_CYAN : BORDER_COLOR);
        graphics.putString(x, inputY + 2, "└");
        for (int i = 1; i < width - 1; i++) {
            graphics.putString(x + i, inputY + 2, "─");
        }
        graphics.putString(x + width - 1, inputY + 2, "┘");
    }

    private void drawLogo(int x, int y, int width) {
        String[] logo = {
            "✨ CREATE ACCOUNT ✨",
            "━━━━━━━━━━━━━━━━━━━"
        };
        
        graphics.setForegroundColor(ACCENT_PURPLE);
        for (int i = 0; i < logo.length; i++) {
            int logoX = x + (width - logo[i].length()) / 2;
            graphics.putString(logoX, y + i, logo[i]);
        }
    }

    private void drawStatusBar(int width) {
        graphics.setForegroundColor(ACCENT_PURPLE);
        graphics.setBackgroundColor(new TextColor.RGB(30, 30, 45));
        
        String status = " 🆕 NEW USER REGISTRATION | SECURE SIGNUP ";
        int padding = (width - status.length()) / 2;
        
        for (int i = 0; i < width; i++) {
            graphics.putString(i, 0, " ");
        }
        graphics.putString(padding, 0, status);
        graphics.setBackgroundColor(BG_COLOR);
    }

    private void drawBox(int x, int y, int width, int height, String title, TextColor titleColor) {
        graphics.setForegroundColor(BORDER_COLOR);

        // Top border
        graphics.putString(x, y, "╭");
        for (int i = 1; i < width - 1; i++) {
            graphics.putString(x + i, y, "─");
        }
        graphics.putString(x + width - 1, y, "╮");

        // Title
        graphics.setForegroundColor(titleColor);
        String titleStr = " " + title + " ";
        graphics.putString(x + 2, y, titleStr);

        // Sides
        graphics.setForegroundColor(BORDER_COLOR);
        for (int i = 1; i < height - 1; i++) {
            graphics.putString(x, y + i, "│");
            graphics.putString(x + width - 1, y + i, "│");
        }

        // Bottom border
        graphics.putString(x, y + height - 1, "╰");
        for (int i = 1; i < width - 1; i++) {
            graphics.putString(x + i, y + height - 1, "─");
        }
        graphics.putString(x + width - 1, y + height - 1, "╯");
    }

    private String maskPassword(String password) {
        return "●".repeat(password.length());
    }
}