package org.sopt.validator;

public class PostValidator {
    private PostValidator() {}

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다!");
        }
    }

    public static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다!");
        }
    }

    public static void validatePost(String title, String content) {
        validateTitle(title);
        validateContent(content);
    }
}
