package org.sopt.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.sopt.domain.Post;
import org.sopt.domain.QPost;
import org.sopt.domain.QUser;

import java.util.List;

public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Post> searchPosts(String title, String nickname) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(
                        titleContains(title),
                        nicknameContains(nickname)
                )
                .fetch();
    }

    private BooleanExpression titleContains(String title) {
        return (title != null && !title.isBlank()) ? QPost.post.title.contains(title) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return (nickname != null && !nickname.isBlank()) ? QUser.user.nickname.contains(nickname) : null;
    }
}
