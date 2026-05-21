package org.sopt.validator;

import org.sopt.exception.BadRequestException;
import org.sopt.global.exception.ErrorCode;

public class PostValidator {
    private static final int TITLE_MAX_LENGTH = 50;

    private PostValidator() {}

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException(ErrorCode.POS_400_001);
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException(ErrorCode.POS_400_002);
        }
    }

    public static void validatePost(String title) {
        validateTitle(title);
    }
}
