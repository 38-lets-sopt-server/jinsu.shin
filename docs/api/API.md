# API 명세서

---

## 1. 게시글 생성

**POST /posts**

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
| userId | Long | Y | 작성자 유저 ID | 1 |
| title | String | Y | 게시글 제목 (최대 50자) | "오늘 학식 뭐임" |
| content | String | N | 게시글 본문 | "돈까스래" |
| isAnonymous | Boolean | Y | 익명 여부 | true |
| boardType | String | N | 게시판 종류 (FREE, HOT, SECRET) | "FREE" |

### Request Example
```json
{
  "userId": 1,
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
| message | String | Y | 결과 메시지 | "게시글 등록 완료!" |
| data.id | Long | Y | 생성된 게시글 ID | 1 |

### Success Response Example
**201 Created**
```json
{
  "success": true,
  "message": "게시글 등록 완료!",
  "data": {
    "id": 1
  }
}
```

### Error Response Example
**400 Bad Request** — 제목 없음
```json
{
  "success": false,
  "message": "POST_002: 제목은 필수입니다.",
  "data": null
}
```

**400 Bad Request** — 제목 50자 초과
```json
{
  "success": false,
  "message": "POST_003: 제목은 50자 이하여야 합니다.",
  "data": null
}
```

**500 Internal Server Error**
```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

---

## 2. 게시글 목록 조회

**GET /posts**

### Path Parameter
없음

### Query Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| boardType | String | N | 게시판 종류 필터 (FREE, HOT, SECRET). 미입력 시 전체 조회 | "FREE" |
| page | Integer | N | 페이지 번호 (기본값: 0) | 0 |
| size | Integer | N | 페이지당 게시글 수 (기본값: 10) | 10 |

### Request Header
없음

### Response Header
없음

### Request Body
없음

### Request Example
```
GET /posts?boardType=FREE&page=0&size=10
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data | List | Y | 게시글 목록 | [...] |
| data[].id | Long | Y | 게시글 ID | 1 |
| data[].title | String | Y | 제목 | "오늘 학식 뭐임" |
| data[].content | String | N | 본문 | "돈까스래" |
| data[].author | String | N | 작성자 | "진수" |
| data[].createdAt | String | Y | 작성 시각 | "2026-04-24T20:00:00" |
| data[].isAnonymous | Boolean | Y | 익명 여부 | true |
| data[].boardType | String | N | 게시판 종류 | "FREE" |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "message": "요청이 성공했습니다.",
  "data": [
    {
      "id": 1,
      "title": "오늘 학식 뭐임",
      "content": "돈까스래",
      "author": "익명",
      "createdAt": "2026-04-24T20:00:00",
      "isAnonymous": true,
      "boardType": "FREE"
    }
  ]
}
```

### Error Response Example
**500 Internal Server Error**
```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

---

## 3. 게시글 단건 조회

**GET /posts/{id}**

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| id | Long | Y | 게시글 ID | 1 |

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
GET /posts/1
```

### Response Body

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| success | Boolean | Y | 성공 여부 | true |
| message | String | Y | 결과 메시지 | "요청이 성공했습니다." |
| data.id | Long | Y | 게시글 ID | 1 |
| data.title | String | Y | 제목 | "오늘 학식 뭐임" |
| data.content | String | N | 본문 | "돈까스래" |
| data.author | String | N | 작성자 | "진수" |
| data.createdAt | String | Y | 작성 시각 | "2026-04-24T20:00:00" |
| data.isAnonymous | Boolean | Y | 익명 여부 | true |
| data.boardType | String | N | 게시판 종류 | "FREE" |

### Success Response Example
**200 OK**
```json
{
  "success": true,
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
  "message": "POST_001: 게시글을 찾을 수 없습니다. id: 999",
  "data": null
}
```

**500 Internal Server Error**
```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

---

## 4. 게시글 수정

**PUT /posts/{id}**

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| id | Long | Y | 게시글 ID | 1 |

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
| message | String | Y | 결과 메시지 | "게시글 수정 완료!" |
| data | null | Y | 없음 | null |

### Success Response Example
**200 OK**
```json
{
  "success": true,
  "message": "게시글 수정 완료!",
  "data": null
}
```

### Error Response Example
**400 Bad Request** — 제목 없음
```json
{
  "success": false,
  "message": "POST_002: 제목은 필수입니다.",
  "data": null
}
```

**400 Bad Request** — 제목 50자 초과
```json
{
  "success": false,
  "message": "POST_003: 제목은 50자 이하여야 합니다.",
  "data": null
}
```

**404 Not Found**
```json
{
  "success": false,
  "message": "POST_001: 게시글을 찾을 수 없습니다. id: 999",
  "data": null
}
```

**500 Internal Server Error**
```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

---

## 5. 게시글 삭제

**DELETE /posts/{id}**

### Path Parameter

| Name | Type | Required | Description | Example |
|------|------|----------|-------------|---------|
| id | Long | Y | 게시글 ID | 1 |

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
DELETE /posts/1
```

### Response Body
없음

### Success Response Example
**204 No Content**
```
(응답 본문 없음)
```

### Error Response Example
**404 Not Found**
```json
{
  "success": false,
  "message": "POST_001: 게시글을 찾을 수 없습니다. id: 999",
  "data": null
}
```

**500 Internal Server Error**
```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```
