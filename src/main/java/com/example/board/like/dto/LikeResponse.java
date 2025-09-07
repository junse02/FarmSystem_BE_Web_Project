package com.example.board.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeResponse {
    private final long likes;   // 게시글 총 좋아요 수
    private final boolean liked; // 내가 좋아요 했는지
}
