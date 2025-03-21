package com.team09.sb01hrbank09;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "HR Bank Team_9",
		version = "1.0",
		description = "hr bank 문서입니다."
	)
)
public class SwaggerConfig {
}