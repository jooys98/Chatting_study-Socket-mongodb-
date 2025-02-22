package com.example.chatting_study.exception;

public class NotAccessChatRoom extends RuntimeException {
    public NotAccessChatRoom(String message) {
        super(message);
    }
}
