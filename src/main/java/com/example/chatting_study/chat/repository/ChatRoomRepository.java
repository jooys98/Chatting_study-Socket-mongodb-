package com.example.chatting_study.chat.repository;

import com.example.chatting_study.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("select e from ChatRoom e where e.member.email=:email")
    List<ChatRoom> findByEmail(@Param("email") String email);


    @Query("SELECT c FROM ChatRoom c WHERE c.member.email = :email OR c.sender.email = :email")
    List<ChatRoom> findByMemberOrSender(@Param("email") String email);


    @Query("SELECT r.member.email FROM ChatRoom r WHERE r.id = :roomId " +
            "UNION " +
            "SELECT r.sender.email FROM ChatRoom r  WHERE r.id = :roomId")
    Set<String> findByChatMembers(@Param("roomId") Long roomId);

    @Modifying
    @Query("delete ChatRoom c where c.member.email =:email and c.id =:roomId")
    void deleteByRoomIdAndEmail(@Param("email") String email, @Param("roomId") Long roomId);

}
