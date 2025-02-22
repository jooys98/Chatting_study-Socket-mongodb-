package com.example.chatting_study.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "member")
public class Member {

    @Id
    private String email;
    private String nickName;
    private Long birthday;
    private String profileImage;
    private String password;
    private String phone;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_role_list", joinColumns = @JoinColumn(name = "email"))
    @Column(name = "role") // 해당 memberRoleList 를 저장할 컬럼명을 지정
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();
}
