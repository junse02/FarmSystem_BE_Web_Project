package com.example.board.like.service;

import com.example.board.like.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository likeRepository;

    @Transactional
    public void toggleLike(Long postId, Long userId, boolean like) {
        if (like) {
            if (!likeRepository.exists(postId, userId)) {
                likeRepository.insert(postId, userId);
            }
        } else {
            likeRepository.delete(postId, userId);
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return likeRepository.exists(postId, userId);
    }

    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return likeRepository.countByPost(postId);
    }
}
