package com.website.board.controller;

import com.website.board.dto.CommentDto;
import com.website.board.dto.ForumDto;
import com.website.board.service.BoardService;
import com.website.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> createForum(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody ForumDto.CreateRequest request
    ) {
        Long id = boardService.createForumPost(user.getUserCode(), request);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<ForumDto.Response>> getForums(
            @RequestParam(value = "sort", required = false) String sort
    ) {
        if (sort == null || sort.isBlank()) {
            return ResponseEntity.ok(boardService.getAllForums());
        }
        return ResponseEntity.ok(boardService.getAllForumsSorted(sort));
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<ForumDto.Response> getForumDetail(@PathVariable Long forumId) {
        return ResponseEntity.ok(boardService.getForumDetail(forumId));
    }

    @PostMapping("/{forumId}/like")
    public ResponseEntity<Void> toggleLike(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long forumId
    ) {
        boardService.toggleLike(forumId, user.getUserCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{forumId}/comments")
    public ResponseEntity<Void> addComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long forumId,
            @RequestBody CommentDto.CreateRequest request
    ) {
        boardService.addComment(user.getUserCode(), forumId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{forumId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long forumId) {
        return ResponseEntity.ok(boardService.getComments(forumId));
    }
}
