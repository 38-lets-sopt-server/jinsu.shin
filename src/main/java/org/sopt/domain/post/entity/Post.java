package org.sopt.domain.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.sopt.domain.user.entity.User;
import org.sopt.global.persistence.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseEntity {

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

    public Post(String title, String content, User user, boolean isAnonymous, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.boardType = boardType;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
