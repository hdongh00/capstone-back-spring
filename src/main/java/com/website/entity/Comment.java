package com.website.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_code")
    private Long commentCode;
    @Column(nullable = false)
    private String content;
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;
    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete;

    @JoinColumn(name = "comment_code_ref")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment ref;

    @JoinColumn(name = "forum_code", nullable = false)
    @ManyToOne
    @JsonIgnore
    private Forum forum;
    @JoinColumn(name = "user_code", nullable = false)
    @ManyToOne
    @JsonIgnore
    private User user;
}
