package com.example.board.like.service;

import com.example.board.like.dto.LikeRequest;
import com.example.board.like.dto.LikeResponse;
import com.example.board.like.domain.PostLike;
import com.example.board.like.repository.PostLikeRepository;
import com.example.board.post.domain.Post;
import com.example.board.post.repository.PostRepository;
import com.example.board.user.domain.User;
import com.example.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponse like(Long postId, LikeRequest req) {
        if (req == null || req.postId() == null || req.like() == null) {
            throw new IllegalArgumentException("USER_ID_OR_LIKE_IS_NULL");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("POST_NOT_FOUND"));
        User user = userRepository.findByIdAndIsDeletedFalse(req.postId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        boolean already = postLikeRepository.existsByPost_IdAndUser_Id(postId, user.getId());

        if (req.like()) {
            if (!already) {
                postLikeRepository.save(new PostLike(post, user));
            }
        } else {
            if (already) {
                postLikeRepository.deleteByPost_IdAndUser_Id(postId, user.getId());
            }
        }

        long cnt = postLikeRepository.countByPost_Id(postId);
        return new LikeResponse(cnt, req.like());
    }
}