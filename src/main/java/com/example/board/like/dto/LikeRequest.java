package com.example.board.like.dto;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
        @NotNull Long postId,     // 요청에서 postId 받기
        @NotNull Boolean like     // true: 좋아요, false: 취소
) {}