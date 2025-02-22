package com.example.chatting_study.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageDTO {

    private Long roomId;
    private String sender;
    private String email;
    private Long shopId;
    private String message;

    private LocalDateTime sendTime;
}
