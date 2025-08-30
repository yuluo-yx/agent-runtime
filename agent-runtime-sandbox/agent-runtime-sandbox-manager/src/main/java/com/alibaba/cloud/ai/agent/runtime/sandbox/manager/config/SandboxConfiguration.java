package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.config;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.properties.SandboxProperties;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ContainerService;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ExecutionService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SandboxProperties.class)
public class SandboxConfiguration {

	@Bean
	public ContainerService containerService(SandboxProperties properties) {
		return new ContainerService(properties);
	}

	@Bean
	public SandboxClientFactory sandboxClientFactory(ContainerService containerService) {
		return new SandboxClientFactory(containerService);
	}

	@Bean
	public ExecutionService executionService(SandboxClientFactory clientFactory) {
		return new ExecutionService(clientFactory);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
