![banner_server.png](banner_server.png)

# 에브리타임 클론 서버

SOPT 38기 서버 파트 세미나 실습과 과제를 위해 만든 게시판 서버입니다. 대학 커뮤니티 앱 에브리타임의 자유게시판을 모델로 잡았고, 순수 자바 콘솔 게시판에서 출발해 매주 한 단계씩 Spring 백엔드로 발전시켜 왔습니다. 회차가 쌓일 때마다 그때그때 어떤 고민을 했고 무엇을 새로 배웠는지도 함께 남겨두려고 합니다.

## 기술 스택

**Language & Framework**

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logoColor=white)

**Database**

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

**Auth**

![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Kakao](https://img.shields.io/badge/Kakao_OAuth-FFCD00?style=for-the-badge&logo=kakaotalk&logoColor=000000)

**Infra & Deploy**

![EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![RDS](https://img.shields.io/badge/Amazon_RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![Caddy](https://img.shields.io/badge/Caddy-1F88C0?style=for-the-badge&logo=caddy&logoColor=white)

**Build & Docs & Test**

![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=000000)
![k6](https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white)

## 회차별 기록

### 몸풀기 과제

우선 자바만으로 게시판을 콘솔에서 만들어봤습니다. 프레임워크 없이 객체와 컬렉션만으로 글을 쓰고 읽으면서 기본기를 점검하는 단계였어요.

### 1차 세미나

컨트롤러, 서비스, 리포지토리, 도메인으로 나누는 4계층 구조를 잡고 게시글 CRUD를 만들었습니다. 처음에는 한 클래스에 기능을 다 몰아넣었는데, 예외 처리와 유효성 검사를 따로 떼어내면서 책임 분리의 중요성을 알게 되었습니다. 응답 형식도 공통 포맷으로 묶어 통일했습니다.

### 2차 세미나

기존 코드를 Spring Boot로 옮겼습니다. 직접 객체를 만들던 부분을 빈 주입으로 바꾸고, 컨트롤러도 REST API 형태로 리팩토링했습니다. 예외는 `GlobalExceptionHandler`에서 한곳에 모아 처리하고, `ErrorCode` enum으로 에러 코드 체계를 세웠습니다. 게시판 종류별 조회와 페이지네이션을 붙이고, 용도에 맞게 요청과 응답 DTO를 나눈 회차이기도 합니다.

### 3차 세미나

데이터를 메모리가 아니라 DB에 저장하는 방식으로 마이그레이션했습니다. JPA와 H2에서 MySQL로 옮겼고, `BaseEntity`와 JPA Auditing으로 생성 시각과 수정 시각을 자동으로 관리하도록 리팩토링했습니다. 글 삭제는 실제로 지우는 대신 플래그만 바꾸는 소프트 딜리트로 처리했습니다. 좋아요 기능을 추가하면서 같은 글에 좋아요가 몰리는 상황을 대비해 낙관적 락과 재시도 로직을 넣었고, 목록 조회에서 발생했던 N+1 문제는 fetch join으로 해결했습니다. 제목 검색은 JPQL로 만들었다가 나중에 QueryDSL로 다시 리팩토링하였고, API 문서는 Swagger로 자동화했습니다.

### 4차 세미나

배포를 진행했습니다. 서버는 AWS EC2에 올리고 DB는 Amazon RDS를 사용했으며, 앞단에는 Caddy를 두어 HTTPS까지 적용했습니다.

### 5차 세미나

기획, 디자인, 웹 파트와 함께 네이버 N배송을 리디자인한 합동 세미나를 진행했습니다. 작업은 별도 레포에서 진행했습니다. 결과물 : [38-COLLABORATION-SERVER-NAVER](https://github.com/SOPT-all/38-COLLABORATION-SERVER-NAVER)

### 6차 세미나

인증과 인가를 적용했습니다. 회원가입과 로그인을 만들고, JWT 액세스 토큰과 리프레시 토큰을 발급하고 검증하는 로직을 구현했습니다. Spring Security와 직접 만든 `JwtAuthFilter`를 연동해 요청마다 토큰을 확인했고, 비밀번호는 BCrypt로 해싱해서 저장했습니다. 심화 과제로는 카카오 OAuth 2.0 소셜 로그인을 추가하고, 토큰에 jti를 넣어 로그아웃할 때 Redis 블랙리스트로 무효화하는 흐름까지 구현해봤습니다. 게시글 목록은 놓쳤던 요구사항에 맞춰 커서 기반 무한 스크롤로 바꿨고, 그동안 여기저기 흩어져 있던 공통 응답과 예외 구조, 패키지 구조도 전반적으로 리팩토링했습니다.

### 7차 세미나

Java 21의 가상 스레드를 적용해보고 적용 전후 성능을 비교했습니다. on/off를 설정으로 분리한 뒤 k6로 부하 테스트를 진행했습니다. 커넥션 풀이 병목인 경우에는 가상 스레드가 큰 도움이 안 됐는데, 스레드 자체가 병목인 경우에는 처리량이 약 1.77배까지 올라갔습니다. 병목 지점에 따라 가상스레드가 유효한 순간이 언제 인지 확인할 수 있었습니다. 추가로 메이커스 코드 리뷰를 반영하여 `ErrorCode`를 기반으로 Swagger 에러 응답을 자동 문서화하고, Bean Validation으로 입력 검증도 추가했습니다.

### 8차 세미나

앱잼 가이드와 노하우를 전달 받고 SOPT 출신 연사분들의 강연을 듣는 시간이었습니다. 별도의 코드 작업은 진행하지 않았습니다.
## 주요 기능

- 게시글 작성, 조회, 수정, 삭제 (소프트 딜리트)
- 게시판 종류별 조회와 커서 기반 무한 스크롤
- 제목 검색 (QueryDSL)
- 좋아요 추가와 취소 (동시성 대응)
- 회원가입, 로그인, 내 정보 조회
- JWT 인증과 토큰 재발급, 로그아웃 블랙리스트
- 카카오 소셜 로그인
- Swagger API 문서

## 프로젝트 구조

```
src/main/java/org/sopt
├── domain
│   ├── post      게시글
│   ├── user      회원
│   ├── auth      인증, 토큰, OAuth
│   └── like      좋아요
└── global
    ├── config       공통 설정
    ├── security      인증 필터, 토큰
    ├── persistence    BaseEntity
    ├── response       공통 응답
    ├── exception      예외 처리, 에러 코드
    └── swagger       문서 자동화
```

도메인마다 controller, service, repository, entity, dto, exception을 두고, 공통으로 쓰는 부분은 global 아래에 모아둔 구조입니다.

## 실행 방법

MySQL과 Redis는 docker-compose로 띄웁니다.

```bash
docker-compose up -d
./gradlew bootRun
```
