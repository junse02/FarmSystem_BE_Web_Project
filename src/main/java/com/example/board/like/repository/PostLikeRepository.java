package com.example.board.like.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostLikeRepository {

    private final JdbcTemplate jdbc;

    public boolean exists(Long postId, Long userId) {
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM post_like WHERE post_id = ? AND user_id = ?)",
                Boolean.class, postId, userId
        );
        return exists != null && exists;
    }

    // 좋아요 추가 (중복 무시)
    public void insert(Long postId, Long userId) {
        jdbc.update(
                "INSERT INTO post_like (post_id, user_id) VALUES (?, ?) ON CONFLICT (post_id, user_id) DO NOTHING",
                postId, userId
        );
    }

    // 좋아요 취소
    public int delete(Long postId, Long userId) {
        return jdbc.update(
                "DELETE FROM post_like WHERE post_id = ? AND user_id = ?",
                postId, userId
        );
    }

    public long countByPost(Long postId) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM post_like WHERE post_id = ?",
                Long.class, postId
        );
        return n == null ? 0L : n;
    }
}
