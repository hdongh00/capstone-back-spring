package com.website.chat.controller;

import com.website.chat.dto.ChatMessage;
import com.website.chat.dto.ConversationDto;
import com.website.chat.service.ChatService;
import com.website.entity.Message;
import com.website.user.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService){
        this.chatService = chatService;
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
    @PostMapping("/messages")
    public ResponseEntity<ChatMessage> sendChatMessage(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ChatMessage msg){
        ChatMessage response = chatService.processAndGetAIResponse(msg, user.getUserCode());
        return ResponseEntity.ok().body(response);
    }
}
