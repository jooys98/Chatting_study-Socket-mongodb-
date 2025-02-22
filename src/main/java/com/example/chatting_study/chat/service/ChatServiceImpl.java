package com.example.chatting_study.chat.service;

import com.example.chatting_study.chat.document.ChatMessage;
import com.example.chatting_study.chat.dto.ChatHistoryDTO;
import com.example.chatting_study.chat.dto.ChatMessageDTO;
import com.example.chatting_study.chat.dto.ChatRoomDTO;
import com.example.chatting_study.chat.entity.ChatRoom;
import com.example.chatting_study.chat.repository.ChatMessageRepository;
import com.example.chatting_study.chat.repository.ChatRoomRepository;
import com.example.chatting_study.exception.NotAccessChatRoom;
import com.example.chatting_study.member.entity.Member;
import com.example.chatting_study.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {


    private final MongoTemplate mongoTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    @Override
    public ChatMessageDTO saveMessage(ChatMessageDTO chatMessageDTO, String email) {
        log.info("chatMessageDTO: {}", chatMessageDTO);
        //shop 조회
        //구매자
        Member member = getMember(email);
        Member sender = getMember(chatMessageDTO.getSender());
        return this.saveMongoAndReturnChatDTO(chatMessageDTO, member ,sender);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatHistoryDTO> getChattingHistory(String email, Long roomId) {
        Member member = getMember(email);
        //채팅방이 있는지 확인
        ChatRoom chatRoom = getChatroom(roomId);

        // 파라미터로 온 이메일이 채팅방의 참여자 인지 확인
        //shop 의 셀러이거나 , 구매자의 이메일 둘 다 아니라면 ?
        if (!chatRoom.getSender().equals(member) && !chatRoom.getMember().equals(member)) {
            throw new NotAccessChatRoom("해당 채팅방의 참여자가 아닙니다.");
        }
        return getChatMessage(chatRoom);
    }


    @Override
    public ChatRoomDTO createChatRoom(ChatRoomDTO chatRoomDTO, String email) {
        log.info("createChatRoom : {}", chatRoomDTO);
        Member member = getMember(email);
        // shop 조회
        Member senderMember = getMember(chatRoomDTO.getSender());
        ChatRoom chatRoom = createChatRoomEntity(member, senderMember, null);
        //새로운 chat room 생성 + 저장
        //대화 상대방
        String sender = this.getRoomMembers(chatRoom.getId()).stream()
                .filter(roomMember -> !roomMember.equals(member.getEmail()))
                .findFirst()
                .orElse(null);



        chatRoomRepository.save(chatRoom);
        //다시 dto로 변환하여 리턴
        return this.convertToChatRoomDTO(chatRoom, sender, null);

    }

    @Override
    public Long deleteChatRoom(String email, Long roomId) {
        Member member = getMember(email);
        chatRoomRepository.deleteByRoomIdAndEmail(member.getEmail(), roomId);
        return roomId;
    }

    @Override
    public Set<String> getRoomMembers(Long roomId) {
        return chatRoomRepository.findByChatMembers(roomId);
    }


    @Override
    public List<ChatRoomDTO> findAllChatRooms(String email) {
        log.info("findAllChatRooms : {}", email);
        Member member = getMember(email);

        //로그인한 회원의 메세지 룸 가져오기
        List<ChatRoom> chatRoomList = chatRoomRepository.findByMemberOrSender(member.getEmail());


        List<Long> roomIds = chatRoomList.stream().map(ChatRoom::getId).toList();
        //메세지룸의 룸 아이디들을 가져오기
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagesByRoomIds(roomIds);
        //룸 아이디들로 마지막 메세지 찾기
//아이디 리스트 들 + 메세지 리스트 하나씩 빼서 convertToChatRoomDTO로 변환 시키고 가시 list 처리
        Map<Long, ChatMessage> lastMessageMap = lastMessages.stream()
                .filter(msg -> msg.getRoomId() != null)
                .collect(Collectors.toMap(
                        ChatMessage::getRoomId,
                        message -> message,
                        (existing, replacement) -> replacement  // 혹시 중복이 있을 경우 처리
                ));

        return chatRoomList.stream().map(chatRoom -> {
            String sender = this.getRoomMembers(chatRoom.getId()).stream()
                    .filter(roomMember -> !roomMember.equals(member.getEmail()))
                    .findFirst()
                    .orElse(null);
            ChatMessage lastMessage = lastMessageMap.get(chatRoom.getId());
            return convertToChatRoomDTO(chatRoom, sender,
                    lastMessage != null ? lastMessage.getMessage() : null);
        }).toList();
    }


    private List<ChatHistoryDTO> getChatMessage(ChatRoom chatRoom) {


        //roomId 로 mongo db 메세지 내역을 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderBySendTimeAsc(chatRoom.getId());

        if (chatMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return chatMessages.stream()
                .map(chatMessage -> ChatHistoryDTO.builder()
                        .roomId(chatRoom.getId())
                        .email(chatMessage.getEmail())
                        .message(chatMessage.getMessage())
                        .lastMessageTime(LocalDateTime.from(chatMessage.getSendTime()))
                        .build())
                .toList();
    }


    private ChatMessageDTO saveMongoAndReturnChatDTO(ChatMessageDTO chatMessageDTO, Member member, Member sender) {
        //채팅룸의 아이디로 엔티티 조회
        ChatRoom chatRoom = getChatroom(chatMessageDTO.getRoomId());
        // document 변환
        ChatMessage chatMessage = this.convertToDocument(chatMessageDTO, member, sender, chatRoom.getId());
        //mongodb 에 저장된 document
        mongoTemplate.save(chatMessage);
        //저장된 document 를 다시 dto 로 변환하여 전달
        return this.convertToDTO(chatMessage, sender.getEmail());
    }


    private Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("Member with email " + email + " not found"));
    }

    private ChatRoom getChatroom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 아이디의 채팅룸은 존재하지 않습니다 "));
    }
}
