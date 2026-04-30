package org.sopt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.domain.BoardType;
import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.ApiResponse;
import org.sopt.dto.response.CreatePostResponse;
import org.sopt.dto.response.PostDetailResponse;
import org.sopt.dto.response.PostSummaryResponse;
import org.sopt.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검증 실패 (제목 누락 또는 50자 초과)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 userId")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(
            @RequestBody CreatePostRequest request
    ) {
        CreatePostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글 등록 완료!", response));
    }

    @Operation(summary = "게시글 목록 조회", description = "게시판 종류별 게시글 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 boardType 값")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostSummaryResponse>>> getAllPosts(
            @Parameter(description = "게시판 종류 (FREE, HOT, SECRET)", example = "FREE")
            @RequestParam(required = false) BoardType boardType,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(postService.getAllPosts(boardType, page, size)));
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 ID로 특정 게시글을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @Parameter(description = "조회할 게시글 ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPost(id)));
    }

    @Operation(summary = "게시글 수정", description = "게시글 제목과 내용을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검증 실패 (제목 누락 또는 50자 초과)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @Parameter(description = "수정할 게시글 ID", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        PostDetailResponse response = postService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료!", response));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 소프트 딜리트합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
