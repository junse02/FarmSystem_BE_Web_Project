package com.example.board.like.service;

import com.example.board.like.domain.PostLike;
import com.example.board.like.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;

    /** 좋아요 상태 조회 */
    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    /** 좋아요 수 조회 */
    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    /** like=true면 좋아요, false면 취소 (멱등 동작) */
    @Transactional
    public boolean toggleLike(Long postId, Long userId, boolean like) {
        boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, userId);

        if (like) {
            if (!exists) postLikeRepository.save(new PostLike(postId, userId));
            return true;
        } else {
            if (exists) postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            return false;
        }
    }
}
