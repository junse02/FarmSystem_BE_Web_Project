package com.example.board.like.controller;

import com.example.board.common.response.ApiResponse;
import com.example.board.like.dto.LikeRequest;
import com.example.board.like.dto.LikeResponse;
import com.example.board.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<?> like(@PathVariable Long postId,
                                  @RequestBody @Valid LikeRequest req) {
        try {
            LikeResponse res = likeService.like(postId, req);
            return ResponseEntity.ok(ApiResponse.success(res));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        }
    }
}