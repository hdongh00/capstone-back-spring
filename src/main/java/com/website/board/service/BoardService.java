package com.website.board.service;

import com.website.board.domain.Comment;
import com.website.board.domain.Forum;
import com.website.board.dto.CommentDto;
import com.website.board.dto.ForumDto;
import com.website.board.repository.CommentRepository;
import com.website.board.repository.ForumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final ForumRepository forumRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long createForumPost(ForumDto.CreateRequest request) {
        Forum forum = Forum.builder()
                .userCode(request.getUserCode())
                .title(request.getTitle())
                .content(request.getContent())
                .analysisCode(null)
                .build();
        return forumRepository.save(forum).getId();
    }

    @Transactional(readOnly = true)
    public List<ForumDto.Response> getAllForums() {
        return forumRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(forum -> ForumDto.Response.from(
                        forum,
                        commentRepository.countByForumId(forum.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ForumDto.Response> getAllForumsSorted(String sort) {
        List<Forum> forums;
        if (sort == null || sort.isBlank()) {
            forums = forumRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
        } else if ("likes".equalsIgnoreCase(sort)) {
            forums = forumRepository.findAllByIsDeletedFalse();
            forums.sort((a, b) -> {
                int sizeA = a.getLikedUserCodes() != null ? a.getLikedUserCodes().size() : 0;
                int sizeB = b.getLikedUserCodes() != null ? b.getLikedUserCodes().size() : 0;
                return Integer.compare(sizeB, sizeA);
            });
        } else if ("comments".equalsIgnoreCase(sort)) {
            forums = forumRepository.findAllByIsDeletedFalse();
            forums.sort((a, b) -> {
                long countA = commentRepository.countByForumId(a.getId());
                long countB = commentRepository.countByForumId(b.getId());
                return Long.compare(countB, countA);
            });
        } else {
            forums = forumRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();
        }

        return forums.stream()
                .map(forum -> ForumDto.Response.from(
                        forum,
                        commentRepository.countByForumId(forum.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ForumDto.Response getForumDetail(Long forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        long commentCount = commentRepository.countByForumId(forumId);
        return ForumDto.Response.from(forum, commentCount);
    }

    @Transactional
    public void toggleLike(Long forumId, Long userCode) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        forum.toggleLike(userCode);
        forumRepository.save(forum);
    }

    @Transactional
    public void addComment(CommentDto.CreateRequest request) {
        Forum forum = forumRepository.findById(request.getForumId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .forumId(request.getForumId())
                .userCode(request.getUserCode())
                .content(request.getContent())
                .build();
        commentRepository.save(comment);
        forumRepository.save(forum);
    }

    @Transactional(readOnly = true)
    public List<CommentDto.Response> getComments(Long forumId) {
        return commentRepository.findAllByForumIdOrderByCreatedAtAsc(forumId)
                .stream()
                .map(CommentDto.Response::from)
                .collect(Collectors.toList());
    }
}
