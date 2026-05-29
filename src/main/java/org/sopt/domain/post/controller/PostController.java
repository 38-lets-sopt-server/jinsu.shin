package org.sopt.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.post.dto.request.CreatePostRequest;
import org.sopt.domain.post.dto.request.UpdatePostRequest;
import org.sopt.domain.post.dto.response.CreatePostResponse;
import org.sopt.domain.post.dto.response.PostDetailResponse;
import org.sopt.domain.post.dto.response.PostFeedResponse;
import org.sopt.domain.post.entity.BoardType;
import org.sopt.domain.post.service.PostService;
import org.sopt.global.exception.SuccessCode;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.security.LoginUserId;
import org.sopt.global.swagger.CustomExceptionDescription;
import org.sopt.global.swagger.SwaggerResponseDescription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "게시글 작성 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.CREATE_POST)
    @PostMapping
    public ResponseEntity<ApiResponseBody<CreatePostResponse, Void>> createPost(
            @LoginUserId Long userId,
            @RequestBody CreatePostRequest request
    ) {
        CreatePostResponse response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBody.created(SuccessCode.CREATED, response));
    }

    @Operation(summary = "게시글 목록 조회 / 검색",
            description = "title 또는 nickname 파라미터가 있으면 검색, 없으면 전체 목록을 커서 기반 무한 스크롤로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponseBody<PostFeedResponse, Void>> getPosts(
            @Parameter(description = "게시판 종류 (FREE, HOT, SECRET)", example = "FREE")
            @RequestParam(required = false) BoardType boardType,
            @Parameter(description = "커서 (마지막으로 받은 게시글 id, 첫 페이지는 생략)", example = "27")
            @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 번에 가져올 게시글 수", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "검색할 제목 키워드", example = "학식")
            @RequestParam(required = false) String title,
            @Parameter(description = "검색할 작성자 닉네임", example = "진수")
            @RequestParam(required = false) String nickname
    ) {
        boolean isSearch = (title != null && !title.isBlank()) || (nickname != null && !nickname.isBlank());
        PostFeedResponse result = isSearch
                ? postService.searchFeed(title, nickname, cursor, size)
                : postService.getFeed(boardType, cursor, size);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, result));
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 특정 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.GET_POST)
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponseBody<PostDetailResponse, Void>> getPost(
            @Parameter(description = "조회할 게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, postService.getPost(postId)));
    }

    @Operation(summary = "게시글 수정", description = "게시글 제목과 내용을 수정합니다. 작성자 본인만 수정 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_POST)
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponseBody<PostDetailResponse, Void>> updatePost(
            @LoginUserId Long userId,
            @Parameter(description = "수정할 게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        PostDetailResponse response = postService.updatePost(userId, postId, request);
        return ResponseEntity.ok(ApiResponseBody.ok(SuccessCode.OK, response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 소프트 딜리트합니다. 작성자 본인만 삭제 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.DELETE_POST)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @LoginUserId Long userId,
            @Parameter(description = "삭제할 게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
