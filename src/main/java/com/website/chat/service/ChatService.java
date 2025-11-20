package com.website.chat.service;

import com.website.chat.dto.AI;
import com.website.chat.dto.ChatMessage;
import com.website.chat.dto.ConversationDto;
import com.website.common.exception.InvalidEnumValueException;
import com.website.common.exception.NotAllowException;
import com.website.entity.Conversation;
import com.website.entity.Message;
import com.website.repository.ConversationRepository;
import com.website.repository.MessageRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ChatService {
    private final AIService aiService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatService(AIService aiService, ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.aiService = aiService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * 대화 방 생성하는 메서드
     * @param userCode JWT 토큰 분해 유저 코드
     * @param ai 사용할 AI 종류 enum 있음
     */
    public void createConversation(Long userCode, String ai){
        try {
            AI a = AI.valueOf(ai.toUpperCase());
            Conversation conv = new Conversation(
                    userCode,
                    LocalDateTime.now(),
                    a.name().toLowerCase(),
                    "active"
            );
            conversationRepository.save(conv);
        } catch (IllegalArgumentException e){
            throw new InvalidEnumValueException("AI", ai, Arrays.toString(AI.values()));
        }
    }

    /**
     * 대화 방 조회하는 메서드
     * @param userCode JWT 토큰 분해 유저 코드
     * @return 대화방 dto 객체 배열
     */
    public List<ConversationDto> getConversations(Long userCode){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        List<Conversation> conversations = conversationRepository.findAllByUserCodeAndStatusAndDateBetween(userCode,"active", start, end);
        return conversations.stream().map(i ->new ConversationDto(i.get_id().toHexString(), i.getUserCode(), i.getDate(), i.getAi(), i.getStatus())).toList();
    }

    /**
     * 채팅방 삭제하기
     * @param roomId 삭제할 대화 ObjectId 코드
     * @param userCode JWT 토큰 분해 유저 코드
     */
    public void deleteConversation(String roomId, Long userCode){
        Conversation conversation = conversationRepository.findById(new ObjectId(roomId)).orElseThrow();
        if(conversation.getUserCode().equals(userCode)){
            conversation.setStatus("deactivate");
            conversationRepository.save(conversation);
        } else {
            throw new NotAllowException();
        }
    }

    /**
     * 채팅 50개씩 가져오기
     * @param userCode JWT 토큰 분해 유저 코드
     * @param roomId 가져올 대화방 oBjectId
     * @param page 몇 번째 페이지인지
     * @return Message 엔티티 페이지 객체로 그대로 반환
     */
    public Page<Message> getMessages(Long userCode, String roomId, int page) {
        Pageable pageable = PageRequest.of(page, 50, Sort.by("createdAt").ascending());
        return messageRepository.findByConvIdAndUserCode(new ObjectId(roomId), userCode, pageable);
    }
    // 안녕?
    // 답변
    // 내가 방금 뭐라고 했게?
    // 방금 안녕이라고 하셨습니다.
    // 뭐해?
    // 방금 안녕, 뭐해? 라고 하셨습니다.

    public ChatMessage processAndGetAIResponse(ChatMessage msg, Long userCode) {
        ObjectId convId = msg.getConvId();
        String content = msg.getContent();
        //DB 에서 채팅방의 최근 대화 기록 조회
        List<Message> messageHistories = messageRepository.findTop10ByUserCodeAndConvIdOrderByCreatedAtDesc(userCode, convId);
        // 유저 메세지 저장
        messageRepository.save(new Message(convId, content,"user",userCode));
        // Gemini API를 호출해서 AI의 답변을 받아옴
        String aiResponseText = aiService.getAIResponse(messageHistories, content);
        // AI의 답변으로 챗봇 메시지를 생성하여 MongoDB에 저장
        Message botMessage = new Message(convId, aiResponseText, "bot", userCode);
        messageRepository.save(botMessage);
        return new ChatMessage(convId, aiResponseText, "bot");
    }
}
