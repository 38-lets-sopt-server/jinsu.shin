package org.sopt.global.swagger;

import io.swagger.v3.oas.models.examples.Example;

public record ExampleHolder(int code, String name, Example holder) {
}