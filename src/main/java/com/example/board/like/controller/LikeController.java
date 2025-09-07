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

    // 좋아요 토글
    @PutMapping("/{postId}/like")
    public ResponseEntity<Void> like(
            @PathVariable Long postId,
            @RequestBody @Valid LikeRequest req
    ) {
        // ⚡ userId는 SecurityContextHolder 또는 @AuthenticationPrincipal에서 가져오기
        Long userId = getCurrentUserId();
        likeService.toggleLike(postId, userId, req.like());
        return ResponseEntity.ok().build();
    }

    // 좋아요 여부 / 카운트 조회
    @GetMapping("/{postId}/like")
    public ResponseEntity<LikeStatusResponse> likeStatus(
            @PathVariable Long postId
    ) {
        Long userId = getCurrentUserId();
        boolean liked = likeService.isLiked(postId, userId);
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(new LikeStatusResponse(liked, count));
    }

    public record LikeStatusResponse(boolean liked, long count) {}

   
    }
}
