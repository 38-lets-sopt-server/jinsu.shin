package org.sopt.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.user.dto.request.UserCreateRequest;
import org.sopt.domain.user.dto.response.UserResponse;
import org.sopt.domain.user.service.UserService;
import org.sopt.global.exception.SuccessCode;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.swagger.CustomExceptionDescription;
import org.sopt.global.swagger.SwaggerResponseDescription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "닉네임/이메일/비밀번호로 신규 사용자를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @CustomExceptionDescription(SwaggerResponseDescription.SIGNUP)
    @PostMapping
    public ResponseEntity<ApiResponseBody<UserResponse, Void>> signup(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBody.created(SuccessCode.CREATED, response));
    }
}
