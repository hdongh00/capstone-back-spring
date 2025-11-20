package com.website.chat.controller;

import com.website.chat.service.ChatService;
import com.website.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class WebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    public WebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = chatMessage.getChatRoomId();
        String sessionId = headerAccessor.getSessionId();
        log.info(">>>> '{}' 채팅방에 사용자 입장 (세션 ID: {}). 첫 메시지 전송 시작...", chatRoomId, sessionId);

        //헤더에 사용자 정보 추가
        Principal principal = headerAccessor.getUser();
        String senderName = "익명"; // 기본값
        if (principal != null) {
            senderName = principal.getName();
            log.info(">>>> 입장 사용자 이름: {}", senderName);
        } else {
            //JWT 토큰 없이 테스트하는 경우 임시 이름 사용
            senderName = "나";
            log.info(">>>> 입장 사용자 임시 이름: '나'");
        }

        // 챗봇의 첫 메시지 생성
        ChatMessage firstMessage = new ChatMessage();
        firstMessage.setChatRoomId(chatRoomId);
        firstMessage.setSenderName("챗봇");
        firstMessage.setMessage("오늘 하루는 어땠어요?");
        firstMessage.setTimestamp(LocalDateTime.now());

        // 첫 메시지를 DB에 저장
//        chatService.saveBotMessage(firstMessage);
        log.info(">>>> DB에 첫 메시지 저장 완료.");

        messagingTemplate.convertAndSend(
                "/topic/" + chatRoomId,   // 구독 중인 토픽으로 보냄
                firstMessage
        );
        log.info(">>>> '{}' 채팅방 사용자({})에게 첫 메시지 전송 완료.", chatRoomId, sessionId);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        Principal principal = headerAccessor.getUser();
        if (principal != null) {
            chatMessage.setSenderName(principal.getName());
        } else {
            // JWT 토큰 없이 테스트하는 경우 임시 이름 사용
            if (chatMessage.getSenderName() == null) {
                chatMessage.setSenderName("나");
            }
        }

        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChatRoomId(), chatMessage);

//        ChatMessage msg = chatService.processAndGetAIResponse(chatMessage, user.getUserCode());

//        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChatRoomId(), msg);

    }

}
