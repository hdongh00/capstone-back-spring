package com.website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_result")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_code")
    private Long analysisCode;
    @Column(name = "emotion_score", nullable = false)
    private Float emotionScore;
    @Column(name = "emotion_name", nullable = false)
    private String emotionName;
    @Column(name = "summary", nullable = false)
    private String summary;
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;
    @Column(name = "user_code", nullable = false)
    private Long userCode;
}
