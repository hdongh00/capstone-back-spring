package com.website.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessage {
    // 채팅방의 고유 아이디입니다.
    private ObjectId convId;
    // 채팅 메세지입니다
    private String content;
    //메세지 전송 시간입니다
    private LocalDateTime createAt;
    //메세지 전송을 보낸 사람입니다.
    private String role;

    public ChatMessage(String convId, String content) {
        this.convId = new ObjectId(convId);
        this.content = content;
        this.createAt = LocalDateTime.now();
        this.role = "user";
    }
    public ChatMessage(ObjectId convId, String content, String role) {
        this.convId = convId;
        this.content = content;
        this.createAt = LocalDateTime.now();
        this.role = role;
    }
}
