package com.website.repository;

import com.website.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findAllByUserCodeAndCreateAtBetween(Long userCode, LocalDateTime start, LocalDateTime end);

    List<AnalysisResult> findAllByUserCode(Long userCode);
}
