package com.website.user.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter@ToString
public class UserProfileDto {
    private String name;
    private String email;
    private LocalDate joinDate;
    private String bio;
    private String location;
    private String[] emotionTag;
    private String profileImage;

    private Integer conversationCount;
    private Integer monthCount;
}
