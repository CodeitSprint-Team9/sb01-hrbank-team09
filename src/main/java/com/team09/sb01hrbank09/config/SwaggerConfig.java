package com.team09.sb01hrbank09.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() throws UnknownHostException {
		Info info = new Info()
			.title("HR Bank Team_9")
			.description("HR Bank 문서입니다.")
			.version("1.0");

		List<Server> servers = List.of(new Server()
			.url(InetAddress.getLocalHost().getHostAddress()));

		return new OpenAPI()
			.info(info)
			.servers(servers);
	}
}