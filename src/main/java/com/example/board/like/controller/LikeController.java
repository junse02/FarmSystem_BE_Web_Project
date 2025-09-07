package com.example.board.like.controller;

import com.example.board.like.dto.LikeRequest;
import com.example.board.like.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider; // 이미 있다면 주입, 없으면 아래 대체 구현 사용

    // 좋아요 토글: Swagger에선 postId만 노출
    @PutMapping("/{postId}/like")
    public ResponseEntity<Void> like(@PathVariable Long postId,
                                     @RequestBody @Valid LikeRequest req,
                                     HttpServletRequest httpReq) {
        Long userId = resolveUserId(httpReq); // 내부에서 헤더/토큰 읽기
        likeService.toggleLike(postId, userId, req.like());
        return ResponseEntity.ok().build();
    }

    // 좋아요 상태/카운트 조회: Swagger에선 postId만 노출
    @GetMapping("/{postId}/like")
    public ResponseEntity<LikeStatusResponse> likeStatus(@PathVariable Long postId,
                                                         HttpServletRequest httpReq) {
        Long userId = resolveUserId(httpReq);
        boolean liked = likeService.isLiked(postId, userId);
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(new LikeStatusResponse(liked, count));
    }

    // --- 내부 헬퍼들 ---

    // Authorization: Bearer <JWT> 에서 userId 추출 (없으면 X-User-Id 헤더 fallback)
    private Long resolveUserId(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            Long uid = jwtTokenProvider.getUserId(token);
            if (uid != null) return uid;
        }

        String raw = req.getHeader("X-User-Id");
        if (raw != null && !raw.isBlank()) {
            try { return Long.valueOf(raw); } catch (NumberFormatException ignored) {}
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
    }

    public record LikeStatusResponse(boolean liked, long count) {}
}
