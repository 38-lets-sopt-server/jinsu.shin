package org.sopt.validator;

import org.sopt.exception.ErrorCode;

public class PostValidator {
    private static final int TITLE_MAX_LENGTH = 50;

    private PostValidator() {}

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    ErrorCode.POST_002.getCode() + ": " + ErrorCode.POST_002.getMessage());
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    ErrorCode.POST_003.getCode() + ": " + ErrorCode.POST_003.getMessage());
        }
    }

    public static void validatePost(String title) {
        validateTitle(title);
    }
}
