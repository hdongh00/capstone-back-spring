package com.website.chat.service;

import com.website.entity.ChatMessage;
import com.website.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getAIResponse(List<Message> history, String newMessage){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> contents = new ArrayList<>();
        for (Message msg : history) {
            String role = msg.getRole().equals("user") ? "user" : "model";
            contents.add(Map.of("role", role, "parts", List.of(Map.of("text", msg.getContent()))));
        }
        // 마지막 사용자 메시지 추가
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", newMessage))));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", contents);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try{
            Map<String, Object> response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, requestEntity, Map.class);

            // 복잡한 응답 구조 속에서 실제 텍스트 답변만 추출
            // 이 경로는 Gemini API의 공식 응답 형식에 따름
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if(candidates == null || candidates.isEmpty()){
                return "AI로부터 응답을 받지 못했습니다.";
            }
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if(content == null) {
                return "AI 응답 내용이 비어있습니다.";
            }
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if(parts == null || parts.isEmpty()){
                return "AI 응답 텍스트가 비어있습니다.";
            }
            return (String) parts.get(0).get("text");
        }catch (Exception e){
            e.printStackTrace();
            return "죄송해요, 지금은 AI와 연결할 수 없어요. 잠시 후 다시 시도해주세요.";
        }
    }
}
