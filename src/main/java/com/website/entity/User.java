package com.website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class User { //User 테이블
    @Id //PK설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private Long userCode;
    @Column(name = "nickname", unique = true)
    private String nickname;
    @Column
    private String email;
    @Column(name = "chat_bot")
    private String chatBot;
    @Column
    private String bio;
    @Column
    private String city;
    @Column
    private String region;
    @Column
    private boolean enable;
    @Column
    private String role;
    @Column(name = "profile_img")
    private String profileImage;
    @Column(name = "name")
    private String name;
    @Column(name = "oauth_provider")
    private String oauthProvider;
    @Column(name = "oauth_id")
    private Long oauthId;
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
}
