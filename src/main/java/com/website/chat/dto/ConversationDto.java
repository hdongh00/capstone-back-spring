package com.website.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter@Setter
public class ConversationDto {
    private String id;
    private Long userCode;
    private LocalDateTime date;
    private String ai;
    private String status;
}
