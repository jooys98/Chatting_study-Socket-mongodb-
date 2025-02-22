package com.example.chatting_study.chat.document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chatMessages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {


    @Id
    private String id; // mongodb 에선 string 타입의 id 사용

    //MySQL의 ChatRoom 테이블의 ID를 참조
    private Long roomId;

    // 메시지 보낸 사람의 Member ID
    private String email;
    private String sender;
    private String message;     // 채팅 내용
    private LocalDateTime sendTime;
}
