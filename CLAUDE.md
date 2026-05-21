# 작업 워크플로우

## 작업 시작 전 모드 선택 (필수)

새 작업을 시작할 때 **항상 사용자에게 어떤 모드로 진행할지 먼저 물어본다.**

- **normal 모드** — 세미나 실습/과제 구현용 (이슈·브랜치·PR 풀 사이클)
- **easy 모드** — 잡일·정리·설정 등 가벼운 작업용 (main에 직접 푸시)

사용자가 모드를 명시하지 않았다면 무조건 먼저 물어보고, 답을 들은 뒤에 작업을 시작한다.

---

## normal 모드 (세미나 실습 / 세미나 과제)

### 흐름
1. **이슈 템플릿 확인** — `.github/ISSUE_TEMPLATE/` 에서 적절한 템플릿을 읽는다.
   - 실습: `seminar-template.md` → 제목 `[SEMINAR] N차 세미나 실습 구현`
   - 과제: `assignment-template.md` → 제목 `[ASSIGNMENT] N차 과제 구현`
   - **이슈는 타입이 아니라 "몇 차 세미나 실습 / 몇 차 세미나 과제" 단위로 나눈다.**
2. **이슈 자동 생성** — 사용자에게 Description / To-Do 내용을 브리핑하고 승인받은 뒤 `gh issue create` 로 생성한다.
3. **브랜치 생성·전환** — 컨벤션: `feat/#<이슈번호>/<N>rd` (예: `feat/#17/6rd`).
   - 한 브랜치는 **해당 회차의 실습 이슈 → 과제 이슈까지 살아 있다가**, 과제까지 머지되면 정리한다.
   - 실습 → 과제로 넘어갈 때는 브랜치를 **삭제하지 않는다.**
4. **단계별 커밋** — 단일 커밋 금지. 작업 단위를 나눠 단계별로 진행하고, 각 단계마다:
   - 커밋 메시지를 제안 → 사용자 승인 → 커밋.
5. **푸시 & PR 자동 생성** — `gh pr create` 로 `.github/PULL_REQUEST_TEMPLATE.md` 형식에 맞춰 PR 생성.
6. **머지는 사용자가 직접 수동으로** 검수 후 진행한다. Claude 는 머지하지 않는다.
7. **머지 후 정리** (단, **실습 → 과제 사이에는 정리 단계를 건너뛴다**):
   - `main` 으로 스위치 → `git pull` → 작업 브랜치 삭제.

### 커밋 메시지 컨벤션 (normal 모드)
- 형식: `<type>(#<이슈번호>/<domain>): <설명>`
- 예: `feat(#15/auth): 로그인 API 및 AuthService 구현`
- type: `feat` / `fix` / `refactor` / `chore` / `docs` 등.

---

## easy 모드 (잡일·정리·설정)

### 흐름
1. 브랜치·이슈·PR 없이 **`main` 에서 직접 작업**.
2. 작업 범위를 나눠 단계별로 진행.
3. 각 단계마다 커밋 메시지 제안 → 사용자 승인 → 커밋.
4. 작업이 끝나면 **`main` 으로 직접 푸시.**

### 커밋 메시지 컨벤션 (easy 모드)
- 형식: `<type>(<domain>): <설명>`
- 예: `chore(global): 2·3차 세미나 문서 폴더 제거`
- 이슈 번호는 붙이지 않는다.

---

## 공통 규칙

- **커밋 메시지에 `Co-Authored-By: ...` 줄을 절대 포함하지 않는다.**
- 모든 커밋·푸시·PR 생성 전에 사용자에게 브리핑하고 승인을 받는다.
- 브리핑 없이 임의로 커밋·푸시·머지하지 않는다.
- 커밋 메시지 본문은 한글로 작성한다.

---

# 구현 컨벤션

> **이 컨벤션은 반드시 참고한다. 코드를 읽거나 작성할 때 아래 패키지 구조·원칙이 지켜지지 않은 부분을 발견하면 즉시 사용자에게 알리고 수정을 제안한다.**

## 패키지 구조

> Base Package: `org.sopt`

```
src/main/java/org/sopt
├── global
│   ├── config
│   ├── persistence
│   │   └── BaseEntity
│   ├── response
│   │   ├── ApiResponseBody
│   │   └── ErrorMeta
│   ├── exception
│   │   ├── GlobalExceptionHandler
│   │   ├── BusinessException
│   │   ├── ErrorCode
│   │   └── SuccessCode
│   └── util
│
├── domain
│   └── {domain}
│       ├── controller
│       │   └── {Domain}Controller
│       ├── service
│       │   └── {Domain}Service
│       ├── repository
│       │   └── {Domain}Repository
│       ├── entity
│       │   └── {Domain}Entity
│       ├── dto
│       │   ├── request
│       │   │   └── {Domain}Request
│       │   └── response
│       │       └── {Domain}Response
│       └── exception
│           └── {Domain}Exception
│
└── Application
```

## 공통 응답 및 예외 처리 구조

### 구성 요약

| 위치 | 역할 |
| --- | --- |
| `global/response/ApiResponseBody<T, M>` | 모든 응답을 감싸는 공통 record. `ok` / `created` / `onFailure` 정적 팩토리로만 생성. |
| `global/response/ErrorMeta` | 실패 응답에 `path`·`timestamp` 부착. |
| `global/exception/BusinessException` | 도메인 예외는 모두 이 클래스를 상속해 사용. |
| `global/exception/ErrorCode` | **현재는 단일 enum** 에 전체 에러 코드 관리. (TODO: 추후 도메인별 분리) |
| `global/exception/SuccessCode` | 성공 응답의 status·message enum (ErrorCode 와 동일 패턴). |
| `global/exception/GlobalExceptionHandler` | `BusinessException` / `HttpMessageNotReadableException` / `Exception` 을 잡아 `ApiResponseBody.onFailure` 로 응답. |

### 에러 코드 네이밍

형식: `{DOMAIN(3글자)}_{STATUS(3자리)}_{SEQUENCE(3자리)}` — 에러 코드별로 카운팅 초기화.

### ApiResponseBody

```java
package org.sopt.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.exception.SuccessCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseBody<T, M>(
    boolean success,
    int status,
    String message,
    T data,
    String code,
    M meta
) {

    public static ApiResponseBody<Void, Void> ok(SuccessCode successCode) {
        return new ApiResponseBody<>(true, successCode.getStatus(), successCode.getMessage(), null, null, null);
    }

    public static <T> ApiResponseBody<T, Void> ok(SuccessCode successCode, T data) {
        return new ApiResponseBody<>(true, successCode.getStatus(), successCode.getMessage(), data, null, null);
    }

    public static <T> ApiResponseBody<T, Void> created(SuccessCode successCode, T data) {
        return new ApiResponseBody<>(true, successCode.getStatus(), successCode.getMessage(), data, null, null);
    }

    public static ApiResponseBody<Void, ErrorMeta> onFailure(ErrorCode errorCode, ErrorMeta errorMeta) {
        return new ApiResponseBody<>(false, errorCode.getStatus(), errorCode.getMessage(), null, errorCode.getCode(), errorMeta);
    }

    public static ApiResponseBody<Void, ErrorMeta> onFailure(ErrorCode errorCode, String message, ErrorMeta errorMeta) {
        return new ApiResponseBody<>(false, errorCode.getStatus(), message, null, errorCode.getCode(), errorMeta);
    }
}
```

### BusinessException

```java
package org.sopt.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + " - " + detail);
        this.errorCode = errorCode;
    }
}
```

### ErrorCode

> 현재는 모든 도메인의 에러 코드를 하나의 enum 에서 관리. 추후 도메인별 분리 예정.

```java
package org.sopt.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== COMMON =====
    INVALID_NUMBER_FORMAT(400, "COM_400_001", "숫자만 입력해주세요."),
    INVALID_EMAIL_FORMAT(400, "COM_400_002", "잘못된 이메일 형식입니다."),
    INVALID_DATE_FORMAT(400, "COM_400_003", "잘못된 날짜 형식입니다."),
    INVALID_NULL_DATA(400, "COM_400_004", "빈 값은 허용되지 않습니다."),
    INVALID_MAPPING_PARAMETER(400, "COM_400_005", "매핑할 수 없는 값입니다."),
    RESOURCE_NOT_FOUND(404, "COM_404_001", "존재하지 않는 리소스입니다."),
    INTERNAL_SERVER_ERROR(500, "COM_500_001", "서버 내부 오류가 발생했습니다."),

    // ===== MEMBER =====
    AGE_MUST_UPPER_THAN_20(400, "MEM_400_001", "20세 미만은 가입할 수 없습니다."),
    MEMBER_NOT_FOUND(404, "MEM_404_001", "존재하지 않는 회원입니다."),
    MEMBER_BY_EMAIL_ALREADY_EXISTS(409, "MEM_409_001", "해당 이메일로 가입된 회원이 이미 존재합니다."),

    // ===== ARTICLE =====
    ARTICLE_NOT_FOUND(404, "ART_404_001", "존재하지 않는 게시물입니다."),
    ARTICLE_BY_NAME_ALREADY_EXISTS(409, "ART_409_001", "해당 제목으로 작성된 게시글이 이미 존재합니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

### GlobalExceptionHandler

```java
package org.sopt.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.response.ErrorMeta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handle(
        BusinessException e, HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponseBody.onFailure(errorCode, meta));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest request
    ) {
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();

        if (root instanceof BusinessException be) {
            ErrorCode errorCode = be.getErrorCode();
            ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
            return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponseBody.onFailure(errorCode, meta));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseBody.onFailure(ErrorCode.INVALID_MAPPING_PARAMETER, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseBody<Void, ErrorMeta>> handle(
        Exception e, HttpServletRequest request
    ) {
        ErrorMeta meta = new ErrorMeta(request.getRequestURI(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseBody.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), meta));
    }
}
```

## 네이밍 컨벤션

### 클래스 내 멤버 순서

클래스 내부 멤버는 아래 순서대로 배치한다.

1. 상수 (`static final`)
2. `static` 변수
3. 인스턴스 변수
4. 생성자
5. `public` 메서드 — getter / setter 는 다른 `public` 메서드를 모두 작성한 뒤 마지막에 둔다.
6. `protected` 메서드
7. package-private 메서드
8. `private` 메서드
9. nested 클래스 / 인터페이스

### API URL 설계

1. 리소스는 **복수형** 으로 작성 (예: `/users`, `/articles`).
2. 리소스명은 **kebab-case** (예: `/user-profiles`).
3. 리소스 변수명은 **camelCase** (예: `{userId}`, `{articleId}`).
4. 불필요한 리소스까지 요청하지 않는지 구분 — 응답에 꼭 필요한 필드만 담는다.
5. **리소스 위치 결정** → `path variable` (예: `/articles/{articleId}`).
6. **리소스 조회 시 필터링·검색** → `query string` (예: `/articles?keyword=spring&page=1`).

## 컨벤션 위반 발견 시 대응

코드를 읽거나 작성하는 도중 위 구조·원칙에 어긋난 부분을 발견하면:

1. 어디가 어떻게 위반되었는지 사용자에게 즉시 알린다.
2. 위 컨벤션에 맞춘 수정안을 제안한다.
3. 사용자의 승인을 받은 뒤에 수정한다.
