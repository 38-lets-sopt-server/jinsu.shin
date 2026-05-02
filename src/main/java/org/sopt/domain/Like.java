package org.sopt.domain;

import jakarta.persistence.*;

@Entity(name = "PostLike")
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Version
    private Long version;

    protected Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Post getPost() { return post; }
}
