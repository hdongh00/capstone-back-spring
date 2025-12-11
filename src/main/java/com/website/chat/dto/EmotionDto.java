package com.website.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class EmotionDto {
    private int count; //이번 달 기록 횟수
    private String topEmotion; //가장 많은 감정
    private String volatility; //감정 변동성
}
