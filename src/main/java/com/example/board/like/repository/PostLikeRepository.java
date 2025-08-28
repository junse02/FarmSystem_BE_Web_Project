package com.example.board.like.repository;

import com.example.board.like.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
    long countByPost_Id(Long postId);
    @Transactional
    void deleteByPost_IdAndUser_Id(Long postId, Long userId);
}
