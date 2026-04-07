package org.sopt.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Long id) {
        super(id + "번 게시글을 찾을 수 없습니다!");
    }
}
