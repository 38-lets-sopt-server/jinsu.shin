package org.sopt.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.sopt.domain.post.entity.Post;
import org.sopt.domain.post.entity.QPost;
import org.sopt.domain.user.entity.QUser;

import java.util.List;

public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Post> searchFeed(String title, String nickname, Long cursor, int limit) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(
                        titleContains(title),
                        nicknameContains(nickname),
                        idLessThan(cursor)
                )
                .orderBy(post.id.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression idLessThan(Long cursor) {
        return (cursor != null) ? QPost.post.id.lt(cursor) : null;
    }

    private BooleanExpression titleContains(String title) {
        return (title != null && !title.isBlank()) ? QPost.post.title.contains(title) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return (nickname != null && !nickname.isBlank()) ? QUser.user.nickname.contains(nickname) : null;
    }
}