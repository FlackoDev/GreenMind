package com.example.greenmind.resource.ai;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;
    public static final int TYPE_LOADING = 3;

    private String text;
    private int type;

    public ChatMessage(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() { return text; }
    public int getType() { return type; }
}