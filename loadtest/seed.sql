SET SESSION cte_max_recursion_depth = 1000000;

INSERT INTO users (nickname, email, password, provider, provider_id)
VALUES ('loadtester', 'loadtester@sopt.org', '$2a$10$0123456789012345678901uPzGZ2eDcN6r1mJ0Q1aQ8e5xF6wq2', 'LOCAL', NULL);

SET @uid = LAST_INSERT_ID();

INSERT INTO post (title, content, is_anonymous, board_type, user_id, deleted_at, created_at, updated_at)
WITH RECURSIVE seq (n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 5000
)
SELECT
    CONCAT('부하테스트 게시글 ', n),
    CONCAT('부하 테스트용 본문입니다. seq=', n),
    1,
    'FREE',
    @uid,
    NULL,
    NOW(),
    NOW()
FROM seq;

SELECT COUNT(*) AS seeded_posts, MIN(id) AS min_id, MAX(id) AS max_id
FROM post
WHERE user_id = @uid;