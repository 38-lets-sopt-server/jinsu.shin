package org.sopt.domain.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.like.service.LikeService;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.security.LoginUserId;
import org.sopt.global.swagger.CustomExceptionDescription;
import org.sopt.global.swagger.SwaggerResponseDescription;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like", description = "좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 추가", description = "게시글에 좋아요를 추가합니다. 중복 좋아요는 불가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "좋아요 추가 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.ADD_LIKE)
    @PostMapping
    public ResponseEntity<ApiResponseBody<Void, Void>> addLike(
            @LoginUserId Long userId,
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        likeService.addLike(postId, userId);
        return ResponseEntity.ok(ApiResponseBody.ok());
    }

    @Operation(summary = "좋아요 취소", description = "게시글의 좋아요를 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "좋아요 취소 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.CANCEL_LIKE)
    @DeleteMapping
    public ResponseEntity<ApiResponseBody<Void, Void>> cancelLike(
            @LoginUserId Long userId,
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        likeService.cancelLike(postId, userId);
        return ResponseEntity.ok(ApiResponseBody.ok());
    }
}
