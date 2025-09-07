package com.example.board.like.controller;

import com.example.board.common.response.ApiResponse;
import com.example.board.like.dto.LikeRequest;
import com.example.board.like.dto.LikeResponse;
import com.example.board.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts") // 최종 경로: /posts/{postId}/like
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /** 좋아요 토글 (멱등) — Body: { "like": true } */
    @PutMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> like(
            @PathVariable Long postId,
            @RequestBody @Valid LikeRequest req,
            @AuthenticationPrincipal Long userId   // JwtAuthFilter가 principal을 Long userId로 세팅해야 함
    ) {
        boolean liked = likeService.toggleLike(postId, userId, req.like());
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(ApiResponse.success(new LikeResponse(count, liked)));
    }

    /** 현재 좋아요 상태/카운트 조회 */
    @GetMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> likeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId
    ) {
        boolean liked = likeService.isLiked(postId, userId);
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(ApiResponse.success(new LikeResponse(count, liked)));
    }
}
