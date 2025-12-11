package com.website.chat.service;

import com.website.chat.dto.*;
import com.website.common.exception.InvalidEnumValueException;
import com.website.common.exception.NotAllowException;
import com.website.entity.AnalysisResult;
import com.website.entity.Conversation;
import com.website.entity.Message;
import com.website.repository.AnalysisResultRepository;
import com.website.repository.ConversationRepository;
import com.website.repository.MessageRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository, AnalysisResultRepository analysisResultRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    public List<AnalysisResult> getMonthlyAnalysis(Long userCode, int year, int month){
        //해당 월 시작일 계산
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);

        //해당 월 마지막 계싼
        LocalDateTime end = start.plusMonths(1);

        return analysisResultRepository.findAllByUserCodeAndCreateAtBetween(userCode, start, end);
    }

    /**
     * 사용자의 전체 감정 기록을 분석하여 3가지 성장 지표(긍정성, 안정도, 자신감)를 계산
     * 단순 산술 평균 대신, 멘탈 케어의 특성을 반영하여 '최근 상태'와 '변화 추세'에 가중치를 두는 로직을 적용
     * 긍정성 (Positivity): 지수 가중 이동 평균(EMA)을 적용하여, 과거보다 최근의 기분이 점수에 더 크게 반영 (Alpha = 0.2)
     * 안정도 (Stability): 가중 평균 기준의 표준편차를 역산하여 계산, 감정 기복이 적고 꾸준할수록 높은 점수를 받음
     * 자신감 (Confidence): 긍정성과 안정도의 조화를 기본으로 하되, 최근 3건의 데이터가 '상승세'일 경우 성장 보너스(+10점)를 부여
     * @param userCode 조회할 유저 식별자
     * @return 0~100 사이의 퍼센트 값을 담은 GrowthDto
     */

    // ---성장지표 (전체 기간 분석)---
    public GrowthDto getGrowth(Long userCode){
        //모든 기록 가져오기
        List<AnalysisResult> allResults = analysisResultRepository.findAllByUserCode(userCode);

        if(allResults.isEmpty()) return new GrowthDto(0, 0, 0);

        //최신순 정렬
        allResults.sort((a, b) -> a.getCreateAt().compareTo(b.getCreateAt()));

        //--표준편차가 아닌 가중치 방식--
        double weightedSum = 0;
        double weightTotal = 0;
        double alpha = 0.2; //가중치 계수

        //긍정성 ( 처음에 10점이하면 10배를 해서 가져옴)
        double firstScore = allResults.get(0).getEmotionScore();
        double currentScore = firstScore <= 5 ? firstScore * 20 : firstScore;

        for(AnalysisResult result : allResults){
            //10점이면 100점으로
            double score = result.getEmotionScore() <= 5 ? result.getEmotionScore() * 20 : result.getEmotionScore();

            //EMA 공식
            currentScore = (1 - alpha) * currentScore + alpha * score;
        }
        int positivity = (int)currentScore;

        //안정성
        double varianceSum = 0;
        double weightedMean = currentScore;

        //최근 데이터일수록 변동성에 더 큰 페널티
        double Penalty = 0;
        double weight = 1.0;

        for(int i = 0; i < allResults.size(); i++){
            AnalysisResult r = allResults.get(i);
            double score = r.getEmotionScore() <= 5 ? r.getEmotionScore() * 20 : r.getEmotionScore();

            //평균과의 차이(절대값)
            double diff = Math.abs(score - weightedMean);

            //최근일수록 weight가 커징
            weight = 1.0 + (i * 0.1);
            Penalty += diff * weight;
        }

        double avgPenalty = Penalty / allResults.size();

        //페널티가 0이면 안정도 100, 페널티가 20점 정도면 안정도 60점대로
        int stability =Math.max(0, 100 - (int) (avgPenalty * 1.5));

        //자신감 - 성장세가 있으면 보너스
        //기본 계산은(긍정성 + 안정도) / 2
        //최근 3개 데이터 확인
        int trendBonus = 0;
        int size = allResults.size();
        if(size >= 3){
            double last = allResults.get(size - 1).getEmotionScore();
            double prev = allResults.get(size - 3).getEmotionScore();
            if(last > prev) trendBonus = 10;
        }

        int confidence = ((positivity + stability) / 2) + trendBonus;
        confidence = Math.min(100, confidence);

        return new GrowthDto(stability, confidence, positivity);
    }

    //---감정 변화 요약--
    public EmotionDto getEmotion(Long userCode, int year, int month){
        List<AnalysisResult> monthlyData = getMonthlyAnalysis(userCode, year, month);

        if(monthlyData.isEmpty()){
            return new EmotionDto(0, "-", "-");
        }

        //기록 횟수
        int count = monthlyData.size();

        //가장 많은 감정 찾기
        String topEmotion = findMostFrequentEmotion(monthlyData);

        //변동성 계산(표준편차 이용)
        String volatility = calculateVolatility(monthlyData);

        return new EmotionDto(count, topEmotion, volatility);
    }

    //가장 많은 감정 찾기
    private String findMostFrequentEmotion(List<AnalysisResult> data){
        return data.stream()
                .map(AnalysisResult::getEmotionName)
                .reduce((a,b) -> a) //마지막꺼 리턴
                .orElse("모름");
    }

    //변동성 계산
    private String calculateVolatility(List<AnalysisResult> data){
        if(data.size() < 2) return "낮음";

        //표준쳔차 계산
        double sum = 0;
        for(AnalysisResult r : data)sum += r.getEmotionScore();
        double mean = sum / data.size();

        double standardDeviation = 0;
        for(AnalysisResult r : data){
            standardDeviation += Math.pow(r.getEmotionScore()-mean, 2);
        }
        double stdDev = Math.sqrt(standardDeviation/data.size());

        if(stdDev < 10) return "안정적";
        if(stdDev < 20) return "중간";
        return "불안정";
    }

    /**
     * 대화 방 생성하는 메서드
     * @param userCode JWT 토큰 분해 유저 코드
     * @param ai 사용할 AI 종류 enum 있음
     */
    @Transactional
    public boolean createConversation(Long userCode, String ai){
        try {
            AI a = AI.valueOf(ai.toUpperCase());
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = start.plusDays(1).minusNanos(1);
            Boolean isConv = conversationRepository.existsByUserCodeAndDateBetween(userCode, start, end);
            if(isConv){
                return false;
            } else {
                Conversation c = conversationRepository.save(
                        new Conversation(
                                userCode, LocalDateTime.now(), a.name().toLowerCase(),"active"
                        )
                );
                conversationRepository.save(c);
                return true;
            }
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
     * 채팅 20개씩 가져오기
     * @param userCode JWT 토큰 분해 유저 코드
     * @param roomId 가져올 대화방 oBjectId
     * @param page 몇 번째 페이지인지
     * @return Message 엔티티 페이지 객체로 그대로 반환
     */
    public Page<Message> getMessages(Long userCode, String roomId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return messageRepository.findByConvIdAndUserCode(new ObjectId(roomId), userCode, pageable);
    }
}
