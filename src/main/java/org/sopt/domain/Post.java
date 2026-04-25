package org.sopt.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {}

    public Post(String title, String content, User user, LocalDateTime createdAt, boolean isAnonymous, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = createdAt;
        this.isAnonymous = isAnonymous;
        this.boardType = boardType;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isAnonymous() { return isAnonymous; }
    public BoardType getBoardType() { return boardType; }
    public User getUser() { return user; }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
