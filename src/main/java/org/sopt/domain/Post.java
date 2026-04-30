package org.sopt.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Post() {}

    public Post(String title, String content, User user, boolean isAnonymous, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.boardType = boardType;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isAnonymous() { return isAnonymous; }
    public BoardType getBoardType() { return boardType; }
    public User getUser() { return user; }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
