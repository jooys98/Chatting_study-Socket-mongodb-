package com.example.chatting_study.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String sender; //먼저 보낸사람
    private Long shopId;
    private String shopImage;
    private String shopName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
