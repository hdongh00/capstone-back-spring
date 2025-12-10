package com.website.board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "forum")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_code")
    private Long id;

    @Column(name = "user_code", nullable = false)
    private Long userCode;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "VARCHAR(3000)")
    private String content;

    @ElementCollection
    @CollectionTable(name = "forum_recommend", joinColumns = @JoinColumn(name = "forum_code"))
    @Column(name = "user_code")
    private Set<Long> likedUserCodes = new HashSet<>();

    // @Column(name = "analysis_code")
    // private Long analysisCode;

    @Column(name = "is_delete")
    private boolean isDeleted;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Forum(Long userCode, String title, String content, Long analysisCode) {
        this.userCode = userCode;
        this.title = title;
        this.content = content;
        // this.analysisCode = analysisCode;
        this.isDeleted = false;
        this.likedUserCodes = new HashSet<>();
    }

    public void toggleLike(Long userCode) {
        if (likedUserCodes.contains(userCode)) {
            likedUserCodes.remove(userCode);
        } else {
            likedUserCodes.add(userCode);
        }
    }

    public void delete() {
        this.isDeleted = true;
    }
}
