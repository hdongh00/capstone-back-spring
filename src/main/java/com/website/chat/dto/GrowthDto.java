package com.website.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class GrowthDto {
    private int stability; //감정 안정도
    private int confidence; //자신감
    private int positivity; //긍정성
}
