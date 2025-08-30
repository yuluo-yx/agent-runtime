/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.config;

import com.alibaba.cloud.ai.agent.runtime.sandbox.common.config.SandboxProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Token authentication filter
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String BEARER_PREFIX = "Bearer ";

	private final SandboxProperties config;

	public TokenAuthenticationFilter(SandboxProperties config) {
		this.config = config;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = extractToken(request);

		if (token != null && validateToken(token)) {
			// Create authentication object
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("sandbox-user",
					null, Collections.emptyList());

			SecurityContextHolder.getContext().setAuthentication(authentication);
			logger.debug("Token authentication successful for request: {}", request.getRequestURI());
		}
		else {
			logger.debug("Token authentication failed for request: {}", request.getRequestURI());
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Extract token from request
	 */
	private String extractToken(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX.length());
		}

		// Also check query parameter for WebSocket connections
		String tokenParam = request.getParameter("token");
		if (StringUtils.hasText(tokenParam)) {
			return tokenParam;
		}

		return null;
	}

	/**
	 * Validate token
	 */
	private boolean validateToken(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}

		String expectedToken = config.getBearerToken();
		if (!StringUtils.hasText(expectedToken)) {
			logger.warn("No secret token configured, allowing all requests");
			return true;
		}

		return expectedToken.equals(token);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();

		// Skip authentication for health check endpoints
		return path.equals("/health") || path.equals("/healthz") || path.startsWith("/actuator/");
	}

}