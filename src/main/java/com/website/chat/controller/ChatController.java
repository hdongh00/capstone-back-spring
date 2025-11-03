package com.website.chat.controller;

import com.website.chat.dto.AI;
import com.website.chat.dto.ConversationDto;
import com.website.chat.service.ChatService;
import com.website.common.exception.InvalidEnumValueException;
import com.website.entity.ChatMessage;
import com.website.entity.Conversation;
import com.website.entity.Message;
import com.website.user.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendChatMessage(@RequestBody ChatMessage message){

        //토큰에서 사용자 이름 추출
//        Long userCode = user.getUserCode();
//        message.setUserCode(userCode);

        ChatMessage msg = chatService.processAndGetAIResponse(message);

        return ResponseEntity.ok(msg);
    }
    // 사용자가 채팅 페이지에 진입했을 때 최근 채팅내역 20개를 가져와서 사용자에게 전달하는 API
    // 사용자 데이터 send 예시 : 사용자정보, 현재 페이지 수
    // 1페이지를 달라 : 최근 20개
    // 2페이지를 달라 : 최근 20개 건너뛰고 20개 (페이징)
    // 사용자가 스크롤을 올리면 그 뒤에 20개를 가져오는 식
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String chatRoomId){
        return ResponseEntity.ok(chatService.getMessages(chatRoomId));
    }
    @PostMapping("/conversations")
    public ResponseEntity<String> createConversation(@AuthenticationPrincipal CustomUserDetails user, @RequestBody Map<String,String> body){
        chatService.createConversation(user.getUserCode(), body.get("ai"));
        return ResponseEntity.status(HttpStatus.CREATED).body("새로운 채팅방이 생성되었습니다!");
    }
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(@AuthenticationPrincipal CustomUserDetails user){
        List<ConversationDto> conversations = chatService.getConversations(user.getUserCode());
        return ResponseEntity.ok().body(conversations);
    }
    @DeleteMapping("/conversations")
    public ResponseEntity<String> deleteConversation(@AuthenticationPrincipal CustomUserDetails user, @RequestParam String roomId){
        chatService.deleteConversation(roomId, user.getUserCode());
        return ResponseEntity.ok().body("채팅방이 삭제되었습니다.");
    }
    @GetMapping("/messages")
    public ResponseEntity<Page<Message>> getMessage(@AuthenticationPrincipal CustomUserDetails user, @RequestParam String roomId, @RequestParam int page){
        Page<Message> messages = chatService.getMessages(user.getUserCode(), roomId, page);
        return ResponseEntity.ok().body(messages);
    }
}
