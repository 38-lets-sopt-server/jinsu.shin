package org.sopt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.dto.request.LikeRequest;
import org.sopt.dto.response.ApiResponse;
import org.sopt.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like", description = "좋아요 관련 API")
@RestController
@RequestMapping("/posts/{postId}/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "좋아요 추가", description = "게시글에 좋아요를 추가합니다. 중복 좋아요는 불가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글 또는 사용자를 찾을 수 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 좋아요를 눌렀습니다")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addLike(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @RequestBody LikeRequest request
    ) {
        likeService.addLike(postId, request);
        return ResponseEntity.ok(ApiResponse.success("좋아요 추가 완료!", null));
    }

    @Operation(summary = "좋아요 취소", description = "게시글의 좋아요를 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글, 사용자 또는 좋아요를 찾을 수 없음")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> cancelLike(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @RequestBody LikeRequest request
    ) {
        likeService.cancelLike(postId, request);
        return ResponseEntity.ok(ApiResponse.success("좋아요 취소 완료!", null));
    }
}
