package com.example.board.like.domain;

import com.example.board.post.domain.Post;
import com.example.board.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_user", columnNames = {"post_id","user_id"}))
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected PostLike() {}
    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public Long getId() { return id; }
    public Post getPost() { return post; }
    public User getUser() { return user; }
}