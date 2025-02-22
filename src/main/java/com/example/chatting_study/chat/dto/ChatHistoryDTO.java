package com.example.chatting_study.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatHistoryDTO {
    private Long roomId;
    private String email;
    private Long shopId;
    private String shopImage;
    private String message;     // 채팅 내용


    private LocalDateTime lastMessageTime;

}
