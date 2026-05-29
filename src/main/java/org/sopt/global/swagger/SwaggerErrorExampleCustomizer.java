package org.sopt.global.swagger;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.sopt.global.exception.ErrorCode;
import org.sopt.global.response.ApiResponseBody;
import org.sopt.global.response.ErrorMeta;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@Component
public class SwaggerErrorExampleCustomizer implements OperationCustomizer {

    private static final String MEDIA_TYPE = "application/json";
    private static final String EXAMPLE_PATH = "/api/v1/...";

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        CustomExceptionDescription annotation = handlerMethod.getMethodAnnotation(CustomExceptionDescription.class);
        Set<ErrorCode> errorCodes = annotation != null
                ? annotation.value().getErrorCodes()
                : SwaggerResponseDescription.common();
        addErrorExamples(operation, errorCodes);
        return operation;
    }

    private void addErrorExamples(Operation operation, Set<ErrorCode> errorCodes) {
        ApiResponses responses = operation.getResponses();
        Map<Integer, List<ExampleHolder>> holdersByStatus = errorCodes.stream()
                .map(this::toExampleHolder)
                .collect(groupingBy(ExampleHolder::code));

        holdersByStatus.forEach((status, holders) -> {
            MediaType mediaType = new MediaType();
            holders.forEach(holder -> mediaType.addExamples(holder.name(), holder.holder()));
            Content content = new Content().addMediaType(MEDIA_TYPE, mediaType);
            responses.addApiResponse(String.valueOf(status), new ApiResponse().content(content));
        });
    }

    private ExampleHolder toExampleHolder(ErrorCode errorCode) {
        Example example = new Example()
                .summary(errorCode.getCode())
                .description(errorCode.getMessage())
                .value(ApiResponseBody.onFailure(errorCode, new ErrorMeta(EXAMPLE_PATH, Instant.now().toEpochMilli())));
        return new ExampleHolder(errorCode.getStatus(), errorCode.name(), example);
    }
}