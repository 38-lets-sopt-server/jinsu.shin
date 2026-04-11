package org.sopt.controller;

import org.sopt.dto.request.CreatePostRequest;
import org.sopt.dto.request.UpdatePostRequest;
import org.sopt.dto.response.ApiResponse;
import org.sopt.dto.response.PostResponse;
import org.sopt.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // POST /posts
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @RequestBody CreatePostRequest request
    ) {
        Long id = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글 등록 완료!", id));
    }

    // GET /posts
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        return ResponseEntity.ok(ApiResponse.success(postService.getAllPosts()));
    }

    // GET /posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPost(id)));
    }

    // PUT /posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료!", null));
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
