package org.sopt.validator;

public class PostValidator {
    private static final int TITLE_MAX_LENGTH = 50;

    private PostValidator() {}

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다!");
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("제목은 50자 이하여야 합니다!");
        }
    }

    public static void validatePost(String title) {
        validateTitle(title);
    }
}
