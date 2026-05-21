# DB 명세

## ERD

```
users          ||--o{ post           : "작성"
users          ||--o{ likes          : "누름"
users          ||--o{ refresh_tokens : "보유"
post           ||--o{ likes          : "받음"
```

---

## 테이블 명세

### users

사용자 계정 정보를 저장하는 테이블. 게시글 작성자 및 좋아요 행위자의 기준 엔티티.

**테이블 이름:** `users`

| Key | Name | Type | Constraint | Description | Example |
|-----|------|------|------------|-------------|---------|
| PK | id | BIGINT | NOT NULL | 사용자 식별자 (AUTO_INCREMENT) | 1 |
|  | nickname | VARCHAR(255) | NULL | 닉네임 | 에이솝트 |
|  | email | VARCHAR(255) | NULL | 이메일 주소 | sopt@example.com |
|  | password | VARCHAR(255) | NULL | BCrypt 로 해시된 비밀번호. OAuth 가입자는 NULL | $2a$10$abc... |
|  | provider | VARCHAR(255) | NOT NULL | 가입 방식 (`LOCAL` / `KAKAO`) | LOCAL |
|  | provider_id | VARCHAR(255) | NULL | OAuth 제공자 내부 사용자 ID. LOCAL 가입자는 NULL | 3829012345 |

**Example Row**

| id | nickname | email | password | provider | provider_id |
|----|----------|-------|----------|----------|-------------|
| 1 | 에이솝트 | sopt@example.com | $2a$10$abc... | LOCAL | NULL |
| 2 | 진수 | jsshin@kakao.com | NULL | KAKAO | 3829012345 |

---

### post

자유게시판에 작성된 게시글을 저장하는 테이블. 소프트 딜리트 방식으로 삭제를 처리하며, `deleted_at IS NULL` 조건이 자동 적용된다.

**테이블 이름:** `post`

| Key | Name | Type | Constraint | Description | Example |
|-----|------|------|------------|-------------|---------|
| PK | id | BIGINT | NOT NULL | 게시글 식별자 (AUTO_INCREMENT) | 1 |
|  | title | VARCHAR(255) | NOT NULL | 제목 (최대 50자, 서버 검증) | 주 3시간 강의 4번 빠지면 결석 아님? |
|  | content | VARCHAR(255) | NULL | 본문 (선택 입력) | 맞지? |
|  | is_anonymous | BIT(1) | NOT NULL | 익명 여부 (기본값 true) | 1 |
|  | board_type | VARCHAR(255) | NULL | 게시판 종류 (`FREE` / `HOT` / `SECRET`) | FREE |
| FK | user_id | BIGINT | NULL | 작성자 (`users.id` 참조) | 1 |
|  | created_at | DATETIME(6) | NULL | 생성일시 (JPA Auditing 자동 관리) | 2025-03-27 15:22:00.000000 |
|  | updated_at | DATETIME(6) | NULL | 수정일시 (JPA Auditing 자동 관리) | 2025-03-27 15:30:00.000000 |
|  | deleted_at | DATETIME(6) | NULL | 삭제일시 (소프트 딜리트, NULL이면 미삭제) | NULL |

> 소프트 딜리트: `DELETE` 호출 시 실제 행 삭제 없이 `deleted_at`에 시각을 기록. 조회 시 `deleted_at IS NULL` 조건이 자동 적용됨.

**Example Row**

| id | title | content | is_anonymous | board_type | user_id | created_at | updated_at | deleted_at |
|----|-------|---------|--------------|------------|---------|------------|------------|------------|
| 1 | 주 3시간 강의 4번 빠지면 결석 아님? | 맞지? | 1 | FREE | 1 | 2025-03-27 15:22:00.000000 | 2025-03-27 15:22:00.000000 | NULL |

---

### likes

사용자가 게시글에 누른 좋아요(공감)를 저장하는 테이블. 동일 사용자가 동일 게시글에 중복 좋아요를 방지하는 유니크 제약이 걸려 있으며, 낙관적 락을 위한 `version` 컬럼을 포함한다.

**테이블 이름:** `likes`

| Key | Name | Type | Constraint | Description | Example |
|-----|------|------|------------|-------------|---------|
| PK | id | BIGINT | NOT NULL | 좋아요 식별자 (AUTO_INCREMENT) | 1 |
| FK | user_id | BIGINT | NOT NULL | 좋아요를 누른 사용자 (`users.id` 참조) | 1 |
| FK | post_id | BIGINT | NOT NULL | 좋아요 대상 게시글 (`post.id` 참조) | 1 |
|  | version | BIGINT | NOT NULL | 낙관적 락용 버전 (`@Version` 자동 관리) | 0 |
|  | created_at | DATETIME(6) | NULL | 생성일시 (JPA Auditing 자동 관리) | 2025-03-27 16:00:00.000000 |
|  | updated_at | DATETIME(6) | NULL | 수정일시 (JPA Auditing 자동 관리) | 2025-03-27 16:00:00.000000 |

> UNIQUE 제약: `(user_id, post_id)` — 같은 사용자가 같은 게시글에 중복 좋아요 불가

**Example Row**

| id | user_id | post_id | version | created_at | updated_at |
|----|---------|---------|---------|------------|------------|
| 1 | 1 | 1 | 0 | 2025-03-27 16:00:00.000000 | 2025-03-27 16:00:00.000000 |

---

### refresh_tokens

사용자별 Refresh Token 을 저장하는 테이블. 로그인 시 발급되고, 토큰 재발급(Rotate) 시 새 토큰으로 교체되며, 만료 시각이 지나면 무효 처리된다. 동일 사용자가 새로 로그인하면 기존 행을 삭제 후 재등록한다.

**테이블 이름:** `refresh_tokens`

| Key | Name | Type | Constraint | Description | Example |
|-----|------|------|------------|-------------|---------|
| PK | id | BIGINT | NOT NULL | Refresh Token 식별자 (AUTO_INCREMENT) | 1 |
| FK | user_id | BIGINT | NOT NULL | 토큰 소유자 (`users.id` 논리적 참조, FK 제약은 미설정) | 1 |
|  | token | VARCHAR(512) | NOT NULL, UNIQUE | Refresh Token (JWT 문자열) | eyJhbGciOiJIUzI1NiJ9... |
|  | expires_at | DATETIME(6) | NOT NULL | 만료 일시 (발급 시각 + 2주) | 2026-06-04 20:00:00.000000 |

> Rotate 전략: 토큰 재발급 시 `token`/`expires_at` 을 새 값으로 갱신 (행 삭제·재생성 X)

**Example Row**

| id | user_id | token | expires_at |
|----|---------|-------|------------|
| 1 | 1 | eyJhbGciOiJIUzI1NiJ9... | 2026-06-04 20:00:00.000000 |

---

## 테이블 관계

| 관계 | 설명 |
|------|------|
| users : post = 1 : N | 한 사용자가 여러 게시글을 작성할 수 있음 |
| users : likes = 1 : N | 한 사용자가 여러 게시글에 좋아요를 누를 수 있음 |
| users : refresh_tokens = 1 : N | 한 사용자가 여러 Refresh Token 을 가질 수 있음 (실제로는 로그인마다 기존 행 삭제 후 1건만 유지) |
| post : likes = 1 : N | 한 게시글에 여러 사용자가 좋아요를 누를 수 있음 |
| users ↔ post (N:M) | `likes` 테이블이 중간 테이블 역할 |
