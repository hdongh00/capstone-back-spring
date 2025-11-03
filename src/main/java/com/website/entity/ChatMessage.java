package com.website.entity;

import jakarta.persistence.Id;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 이 엔티티는 MongoDB 전용 엔티티입니다.
 */
@Document(collection = "chat_messages")
@Getter
@Setter
public class ChatMessage {
    @Id
    private Long userCode; //고유 Id

    private String chatRoomId; //어떤 대화방인지 구분

    private String senderName;

    private String message;

    private LocalDateTime timestamp;
}
