# API 명세서

> Base URL: `/api/v1`
> 모든 응답은 공통 응답 포맷 `ApiResponseBody` 로 감싸진다. 인증이 필요한 API 는 `Authorization: Bearer <accessToken>` 헤더를 함께 보낸다.

## 공통 응답 포맷

### 성공 응답
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": { }
}
```

### 실패 응답
```json
{
  "success": false,
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "code": "POS_404_001",
  "meta": {
    "path": "/api/v1/posts/999",
    "timestamp": 1748841600000
  }
}
```

| 필드 | 설명 |
|------|------|
| `success` | 성공 여부 |
| `status` | HTTP 상태 코드 |
| `message` | 응답 메시지 (SuccessCode/ErrorCode 의 message) |
| `data` | 성공 시 응답 데이터 (null 가능, null 이면 직렬화에서 제외) |
| `code` | 실패 시 에러 코드 (`{DOMAIN}_{STATUS}_{SEQ}`) |
| `meta.path` | 실패 시 요청 경로 |
| `meta.timestamp` | 실패 시 응답 시각 (Unix epoch millisecond) |

### Access Token 만료 응답

만료된 Access Token 으로 보호 API 호출 시 401 + 응답 헤더 `Token-Expired: true` + `ATH_401_006`. 클라이언트는 이를 감지해 토큰 재발급(`POST /api/v1/auth/reissue`) 호출 후 원래 요청을 재시도한다.

---

## 1. 회원가입

**POST /api/v1/users**

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Content-Type | String | Y | 요청 본문 형식 | application/json |

### Response Header
없음

### Request Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| nickname | String | Y | 닉네임 | "진수" |
| email | String | Y | 이메일 | "jsshin@test.com" |
| password | String | Y | 비밀번호 (BCrypt 해시로 저장) | "pw1234" |

### Request Example
```json
{
  "nickname": "진수",
  "email": "jsshin@test.com",
  "password": "pw1234"
}
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 201 |
| message | String | Y | 결과 메시지 | "리소스가 생성되었습니다." |
| data.id | Long | Y | 생성된 사용자 ID | 1 |
| data.nickname | String | Y | 닉네임 | "진수" |
| data.email | String | Y | 이메일 | "jsshin@test.com" |

### Success Response Example
**201 Created**
```json
{
  "success": true,
  "status": 201,
  "message": "리소스가 생성되었습니다.",
  "data": {
    "id": 1,
    "nickname": "진수",
    "email": "jsshin@test.com"
  }
}
```

### Error Response Example
**409 Conflict** — 이미 가입된 이메일
```json
{
  "success": false,
  "status": 409,
  "message": "이미 가입된 이메일입니다.",
  "code": "USR_409_001",
  "meta": {
    "path": "/api/v1/users",
    "timestamp": 1748841600000
  }
}
```

---

## 2. 로그인

**POST /api/v1/auth/login**

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Content-Type | String | Y | 요청 본문 형식 | application/json |

### Response Header
없음

### Request Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| email | String | Y | 이메일 | "jsshin@test.com" |
| password | String | Y | 비밀번호 | "pw1234" |

### Request Example
```json
{
  "email": "jsshin@test.com",
  "password": "pw1234"
}
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.accessToken | String | Y | Access Token (30분 만료) | "eyJhbGciOi..." |
| data.refreshToken | String | Y | Refresh Token (2주 만료) | "eyJhbGciOi..." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Error Response Example
**401 Unauthorized** — 이메일/비밀번호 불일치
```json
{
  "success": false,
  "status": 401,
  "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "code": "ATH_401_001",
  "meta": {
    "path": "/api/v1/auth/login",
    "timestamp": 1748841600000
  }
}
```

---

## 3. 토큰 재발급

**POST /api/v1/auth/reissue**

Refresh Token 을 사용해 새 Access Token + Refresh Token 을 발급한다 (Rotate 전략).

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Refresh Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
POST /api/v1/auth/reissue
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.accessToken | String | Y | 새 Access Token | "eyJhbGciOi..." |
| data.refreshToken | String | Y | 새 Refresh Token | "eyJhbGciOi..." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Error Response Example
**401 Unauthorized** — Refresh Token 이 유효하지 않거나 만료됨
```json
{
  "success": false,
  "status": 401,
  "message": "유효하지 않은 토큰입니다.",
  "code": "ATH_401_002",
  "meta": {
    "path": "/api/v1/auth/reissue",
    "timestamp": 1748841600000
  }
}
```

---

## 4. 로그아웃

**POST /api/v1/auth/logout**

Refresh Token 을 DB 에서 삭제하고, 현재 Access Token 의 `jti` 를 Redis 블랙리스트에 등록(잔여 만료시간만큼 TTL)하여 만료 전에도 사용할 수 없도록 한다.

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다."
}
```

### Error Response Example
**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/auth/logout",
    "timestamp": 1748841600000
  }
}
```

---

## 5. 내 정보 조회

**GET /api/v1/auth/me**

Access Token 으로 인증된 본인의 사용자 정보를 조회한다.

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
GET /api/v1/auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.id | Long | Y | 사용자 ID | 1 |
| data.nickname | String | Y | 닉네임 | "진수" |
| data.email | String | Y | 이메일 | "jsshin@test.com" |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "id": 1,
    "nickname": "진수",
    "email": "jsshin@test.com"
  }
}
```

### Error Response Example
**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/auth/me",
    "timestamp": 1748841600000
  }
}
```

---

## 6. Kakao OAuth 로그인

**POST /api/v1/auth/oauth/kakao**

FE 가 카카오로부터 받은 authorization code 를 전달하면, 서버가 카카오 token endpoint 로 교환하고 user info 를 받아 우리 서버 JWT(Access + Refresh) 를 발급한다. 신규 카카오 사용자는 자동 회원가입, 기존 카카오 사용자는 로그인 처리한다.

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Content-Type | String | Y | 요청 본문 형식 | application/json |

### Response Header
없음

### Request Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| code | String | Y | 카카오로부터 받은 authorization code | "abcdef123456..." |

### Request Example
```json
{
  "code": "abcdef123456..."
}
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.accessToken | String | Y | 우리 서버 Access Token | "eyJhbGciOi..." |
| data.refreshToken | String | Y | 우리 서버 Refresh Token | "eyJhbGciOi..." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Error Response Example
**401 Unauthorized** — OAuth 인증 실패 (잘못된 code 또는 카카오 응답 누락)
```json
{
  "success": false,
  "status": 401,
  "message": "OAuth 인증에 실패했습니다.",
  "code": "ATH_401_005",
  "meta": {
    "path": "/api/v1/auth/oauth/kakao",
    "timestamp": 1748841600000
  }
}
```

**409 Conflict** — 동일 email 의 LOCAL 계정이 이미 존재 (보안상 거부)
```json
{
  "success": false,
  "status": 409,
  "message": "이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요.",
  "code": "ATH_409_002",
  "meta": {
    "path": "/api/v1/auth/oauth/kakao",
    "timestamp": 1748841600000
  }
}
```

**500 Internal Server Error** — 카카오 서버 통신 실패
```json
{
  "success": false,
  "status": 500,
  "message": "외부 OAuth 서버 통신에 실패했습니다.",
  "code": "ATH_500_001",
  "meta": {
    "path": "/api/v1/auth/oauth/kakao",
    "timestamp": 1748841600000
  }
}
```

---

## 7. 게시글 작성

**POST /api/v1/posts**

작성자는 Access Token 으로 인증된 본인.

### Path Parameter
없음

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |
| Content-Type | String | Y | 요청 본문 형식 | application/json |

### Response Header
없음

### Request Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| title | String | Y | 게시글 제목 (최대 50자) | "오늘 학식 뭐임" |
| content | String | N | 게시글 본문 | "돈까스래" |
| isAnonymous | Boolean | N | 익명 여부 (생략 시 true) | true |
| boardType | String | N | 게시판 종류 (`FREE`, `HOT`, `SECRET`) | "FREE" |

### Request Example
```json
{
  "title": "오늘 학식 뭐임",
  "content": "돈까스래",
  "isAnonymous": true,
  "boardType": "FREE"
}
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 201 |
| message | String | Y | 결과 메시지 | "리소스가 생성되었습니다." |
| data.id | Long | Y | 생성된 게시글 ID | 1 |

### Success Response Example
**201 Created**
```json
{
  "success": true,
  "status": 201,
  "message": "리소스가 생성되었습니다.",
  "data": {
    "id": 1
  }
}
```

### Error Response Example
**400 Bad Request** — 제목 누락
```json
{
  "success": false,
  "status": 400,
  "message": "제목은 필수입니다.",
  "code": "POS_400_001",
  "meta": {
    "path": "/api/v1/posts",
    "timestamp": 1748841600000
  }
}
```

**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/posts",
    "timestamp": 1748841600000
  }
}
```

---

## 8. 게시글 목록 조회 / 검색

**GET /api/v1/posts**

`title` 또는 `nickname` 파라미터가 있으면 검색, 없으면 전체 목록을 커서 기반 무한 스크롤로 조회한다. 비로그인도 호출 가능.

### Path Parameter
없음

### Query Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| boardType | String | N | 게시판 종류 필터 (`FREE`, `HOT`, `SECRET`). 미입력 시 전체 | "FREE" |
| cursor | Long | N | 마지막으로 받은 게시글 ID. 첫 페이지는 생략(최신부터 조회) | 27 |
| size | Integer | N | 한 번에 가져올 게시글 수 (기본값 10) | 10 |
| title | String | N | 검색할 제목 키워드 (입력 시 검색 모드) | "학식" |
| nickname | String | N | 검색할 작성자 닉네임 (입력 시 검색 모드) | "진수" |

### Request Header
없음

### Response Header
없음

### Request Body
없음

### Request Example
```
GET /api/v1/posts?boardType=FREE&size=10
GET /api/v1/posts?boardType=FREE&size=10&cursor=27
GET /api/v1/posts?title=학식
GET /api/v1/posts?nickname=진수
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.items | List | Y | 게시글 목록 | [...] |
| data.items[].id | Long | Y | 게시글 ID | 1 |
| data.items[].title | String | Y | 제목 | "오늘 학식 뭐임" |
| data.items[].content | String | N | 본문 | "돈까스래" |
| data.items[].author | String | Y | 작성자 (익명이면 "익명") | "익명" |
| data.items[].createdAt | String | Y | 작성 일시 | "2026-04-24T20:00:00" |
| data.items[].isAnonymous | Boolean | Y | 익명 여부 | true |
| data.items[].boardType | String | N | 게시판 종류 | "FREE" |
| data.items[].likeCount | Long | Y | 좋아요 수 | 5 |
| data.nextCursor | Long | N | 다음 요청에 보낼 cursor (마지막 게시글 id). 더 없으면 null | 1 |
| data.hasNext | Boolean | Y | 다음 페이지 존재 여부 | false |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "items": [
      {
        "id": 1,
        "title": "오늘 학식 뭐임",
        "content": "돈까스래",
        "author": "익명",
        "createdAt": "2026-04-24T20:00:00",
        "isAnonymous": true,
        "boardType": "FREE",
        "likeCount": 5
      }
    ],
    "nextCursor": null,
    "hasNext": false
  }
}
```

---

## 9. 게시글 단건 조회

**GET /api/v1/posts/{postId}**

비로그인도 호출 가능.

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| postId | Long | Y | 게시글 ID | 1 |

### Query Parameter
없음

### Request Header
없음

### Response Header
없음

### Request Body
없음

### Request Example
```
GET /api/v1/posts/1
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.id | Long | Y | 게시글 ID | 1 |
| data.title | String | Y | 제목 | "오늘 학식 뭐임" |
| data.content | String | N | 본문 | "돈까스래" |
| data.author | String | Y | 작성자 (익명이면 "익명") | "익명" |
| data.createdAt | String | Y | 작성 일시 | "2026-04-24T20:00:00" |
| data.isAnonymous | Boolean | Y | 익명 여부 | true |
| data.boardType | String | N | 게시판 종류 | "FREE" |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "id": 1,
    "title": "오늘 학식 뭐임",
    "content": "돈까스래",
    "author": "익명",
    "createdAt": "2026-04-24T20:00:00",
    "isAnonymous": true,
    "boardType": "FREE"
  }
}
```

### Error Response Example
**404 Not Found**
```json
{
  "success": false,
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "code": "POS_404_001",
  "meta": {
    "path": "/api/v1/posts/999",
    "timestamp": 1748841600000
  }
}
```

---

## 10. 게시글 수정

**PUT /api/v1/posts/{postId}**

작성자 본인만 수정 가능.

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| postId | Long | Y | 게시글 ID | 1 |

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |
| Content-Type | String | Y | 요청 본문 형식 | application/json |

### Response Header
없음

### Request Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| title | String | Y | 수정할 제목 (최대 50자) | "학식 진짜 별로임" |
| content | String | N | 수정할 본문 | "내일은 뭐나올까" |

### Request Example
```json
{
  "title": "학식 진짜 별로임",
  "content": "내일은 뭐나올까"
}
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.id | Long | Y | 게시글 ID | 1 |
| data.title | String | Y | 수정된 제목 | "학식 진짜 별로임" |
| data.content | String | N | 수정된 본문 | "내일은 뭐나올까" |
| data.author | String | Y | 작성자 | "익명" |
| data.createdAt | String | Y | 작성 일시 | "2026-04-24T20:00:00" |
| data.isAnonymous | Boolean | Y | 익명 여부 | true |
| data.boardType | String | N | 게시판 종류 | "FREE" |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {
    "id": 1,
    "title": "학식 진짜 별로임",
    "content": "내일은 뭐나올까",
    "author": "익명",
    "createdAt": "2026-04-24T20:00:00",
    "isAnonymous": true,
    "boardType": "FREE"
  }
}
```

### Error Response Example
**400 Bad Request** — 제목 누락
```json
{
  "success": false,
  "status": 400,
  "message": "제목은 필수입니다.",
  "code": "POS_400_001",
  "meta": {
    "path": "/api/v1/posts/1",
    "timestamp": 1748841600000
  }
}
```

**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/posts/1",
    "timestamp": 1748841600000
  }
}
```

**403 Forbidden** — 작성자 본인이 아님
```json
{
  "success": false,
  "status": 403,
  "message": "권한이 없습니다.",
  "code": "ATH_403_001",
  "meta": {
    "path": "/api/v1/posts/1",
    "timestamp": 1748841600000
  }
}
```

**404 Not Found**
```json
{
  "success": false,
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "code": "POS_404_001",
  "meta": {
    "path": "/api/v1/posts/999",
    "timestamp": 1748841600000
  }
}
```

---

## 11. 게시글 삭제

**DELETE /api/v1/posts/{postId}**

작성자 본인만 삭제 가능. 소프트 딜리트(`deleted_at` 시각 기록).

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| postId | Long | Y | 게시글 ID | 1 |

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
DELETE /api/v1/posts/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body
없음 (204 No Content)

### Success Response Example
**204 No Content**
```
(응답 본문 없음)
```

### Error Response Example
**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/posts/1",
    "timestamp": 1748841600000
  }
}
```

**403 Forbidden** — 작성자 본인이 아님
```json
{
  "success": false,
  "status": 403,
  "message": "권한이 없습니다.",
  "code": "ATH_403_001",
  "meta": {
    "path": "/api/v1/posts/1",
    "timestamp": 1748841600000
  }
}
```

**404 Not Found**
```json
{
  "success": false,
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "code": "POS_404_001",
  "meta": {
    "path": "/api/v1/posts/999",
    "timestamp": 1748841600000
  }
}
```

---

## 12. 좋아요 추가

**POST /api/v1/posts/{postId}/likes**

인증된 사용자가 본인 명의로 좋아요를 누른다. 동일 사용자가 동일 게시글에 중복으로 누를 수 없다.

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| postId | Long | Y | 좋아요를 누를 게시글 ID | 1 |

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
POST /api/v1/posts/1/likes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다."
}
```

### Error Response Example
**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/posts/1/likes",
    "timestamp": 1748841600000
  }
}
```

**404 Not Found** — 게시글 없음
```json
{
  "success": false,
  "status": 404,
  "message": "게시글을 찾을 수 없습니다.",
  "code": "POS_404_001",
  "meta": {
    "path": "/api/v1/posts/999/likes",
    "timestamp": 1748841600000
  }
}
```

**409 Conflict** — 이미 좋아요를 누른 게시글
```json
{
  "success": false,
  "status": 409,
  "message": "이미 좋아요를 눌렀습니다.",
  "code": "LIK_409_001",
  "meta": {
    "path": "/api/v1/posts/1/likes",
    "timestamp": 1748841600000
  }
}
```

---

## 13. 좋아요 취소

**DELETE /api/v1/posts/{postId}/likes**

인증된 사용자가 본인이 누른 좋아요를 취소한다.

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| postId | Long | Y | 좋아요를 취소할 게시글 ID | 1 |

### Query Parameter
없음

### Request Header

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| Authorization | String | Y | Bearer + Access Token | "Bearer eyJhbGciOi..." |

### Response Header
없음

### Request Body
없음

### Request Example
```
DELETE /api/v1/posts/1/likes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| status | Integer | Y | HTTP 상태 코드 | 200 |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "status": 200,
  "message": "요청이 성공했습니다."
}
```

### Error Response Example
**401 Unauthorized** — 인증되지 않은 요청
```json
{
  "success": false,
  "status": 401,
  "message": "인증되지 않은 요청입니다.",
  "code": "ATH_401_003",
  "meta": {
    "path": "/api/v1/posts/1/likes",
    "timestamp": 1748841600000
  }
}
```

**404 Not Found** — 좋아요를 누른 적이 없음
```json
{
  "success": false,
  "status": 404,
  "message": "좋아요를 찾을 수 없습니다.",
  "code": "LIK_404_001",
  "meta": {
    "path": "/api/v1/posts/1/likes",
    "timestamp": 1748841600000
  }
}
```

---

## ErrorCode 목록

| Code | HTTP Status | Message |
|------|-------------|---------|
| COM_400_001 | 400 | 잘못된 요청 값입니다. |
| COM_409_001 | 409 | 동시 요청으로 인해 처리에 실패했습니다. 다시 시도해주세요. |
| COM_500_001 | 500 | 서버 내부 오류가 발생했습니다. |
| POS_400_001 | 400 | 제목은 필수입니다. |
| POS_400_002 | 400 | 제목은 50자 이하여야 합니다. |
| POS_404_001 | 404 | 게시글을 찾을 수 없습니다. |
| USR_404_001 | 404 | 사용자를 찾을 수 없습니다. |
| USR_409_001 | 409 | 이미 가입된 이메일입니다. |
| ATH_401_001 | 401 | 이메일 또는 비밀번호가 올바르지 않습니다. |
| ATH_401_002 | 401 | 유효하지 않은 토큰입니다. |
| ATH_401_003 | 401 | 인증되지 않은 요청입니다. |
| ATH_401_004 | 401 | 만료되었거나 로그아웃된 토큰입니다. |
| ATH_401_005 | 401 | OAuth 인증에 실패했습니다. |
| ATH_401_006 | 401 | Access Token이 만료되었습니다. |
| ATH_403_001 | 403 | 권한이 없습니다. |
| ATH_409_002 | 409 | 이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요. |
| ATH_500_001 | 500 | 외부 OAuth 서버 통신에 실패했습니다. |
| LIK_404_001 | 404 | 좋아요를 찾을 수 없습니다. |
| LIK_409_001 | 409 | 이미 좋아요를 눌렀습니다. |