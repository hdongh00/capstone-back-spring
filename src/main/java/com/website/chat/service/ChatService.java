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
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
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
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").ascending());
        return messageRepository.findByConvIdAndUserCode(new ObjectId(roomId), userCode, pageable);
    }
}
