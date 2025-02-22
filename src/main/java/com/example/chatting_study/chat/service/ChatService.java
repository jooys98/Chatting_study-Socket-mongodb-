package com.example.chatting_study.chat.service;

import com.example.chatting_study.chat.document.ChatMessage;
import com.example.chatting_study.chat.dto.ChatHistoryDTO;
import com.example.chatting_study.chat.dto.ChatMessageDTO;
import com.example.chatting_study.chat.dto.ChatRoomDTO;
import com.example.chatting_study.chat.entity.ChatRoom;
import com.example.chatting_study.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ChatService {
    ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email);

    List<ChatHistoryDTO> getChattingHistory(String email, Long roomId);

    ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email);

    Long deleteChatRoom(String email, Long roomId);

    Set<String> getRoomMembers(Long roomId);

    List<ChatRoomDTO> findAllChatRooms(String email);



    default ChatMessage convertToDocument(ChatMessageDTO chatMessageDTO, Member member,Member sender, Long chatRoomId) {
        return ChatMessage.builder()
                .roomId(chatRoomId)
                .email(member.getEmail())
                .sender(sender.getEmail())
                .message(chatMessageDTO.getMessage())
                .sendTime(chatMessageDTO.getSendTime())
                .build();

    }

    //클라이언트에 보낼 채팅 메세지 기록
    default ChatMessageDTO convertToDTO(ChatMessage chatMessage,String sender) {
        return ChatMessageDTO.builder()
                .roomId(chatMessage.getRoomId())
                .sender(sender)
                .email(chatMessage.getEmail())
                .message(chatMessage.getMessage())
                .sendTime((chatMessage.getSendTime()))
                .build();
    }

    default ChatRoom createChatRoomEntity(Member member, Member sender, Long chatRoomId) {
        return ChatRoom.builder()
                .id(chatRoomId) //null 이면 자동으로 생성된다
                .sender(sender)
                .member(member)
                .lastMessageTime(LocalDateTime.now())
                .build();
    }

    default ChatRoomDTO convertToChatRoomDTO(ChatRoom chatRoom, String sender, String message) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .sender(sender)
                .lastMessage(message == null ? null : message)
                .lastMessageTime(chatRoom.getLastMessageTime())
                .build();
    }




}
