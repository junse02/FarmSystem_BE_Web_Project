package com.example.board.like.controller;

import com.example.board.like.dto.LikeRequest;
import com.example.board.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PutMapping("/{postId}/like")
    public ResponseEntity<Void> like(
            @PathVariable Long postId,
            @RequestHeader(name = "X-User-Id") Long userId,  // 임시: 헤더로 사용자 식별
            @RequestBody @Valid LikeRequest req
    ) {
        likeService.toggleLike(postId, userId, req.like());
        return ResponseEntity.ok().build();
    }

    // (선택) 현재 좋아요 여부/카운트 조회 API
    @GetMapping("/{postId}/like")
    public ResponseEntity<LikeStatusResponse> likeStatus(
            @PathVariable Long postId,
            @RequestHeader(name = "X-User-Id") Long userId
    ) {
        boolean liked = likeService.isLiked(postId, userId);
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(new LikeStatusResponse(liked, count));
    }

    public record LikeStatusResponse(boolean liked, long count) {}
}
