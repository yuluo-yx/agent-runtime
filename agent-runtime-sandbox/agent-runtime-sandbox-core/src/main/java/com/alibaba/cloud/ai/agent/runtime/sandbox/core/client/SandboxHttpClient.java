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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.client;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP client for communicating with sandbox server
 */
public class SandboxHttpClient implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SandboxHttpClient.class);

	private final String baseUrl;

	private final String authToken;

	private final CloseableHttpClient httpClient;

	private final ObjectMapper objectMapper;

	/**
	 * Constructor with container model
	 */
	public SandboxHttpClient(ContainerModel containerModel) {
		this.baseUrl = containerModel.getBaseUrl();
		this.authToken = containerModel.getRuntimeToken();
		this.httpClient = HttpClients.createDefault();
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Constructor with base URL and token
	 */
	public SandboxHttpClient(String baseUrl, String authToken) {
		this.baseUrl = baseUrl;
		this.authToken = authToken;
		this.httpClient = HttpClients.createDefault();
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Execute Python code
	 */
	public ExecutionResult runPythonCell(String code) throws SandboxClientException {
		return runPythonCell(code, false);
	}

	/**
	 * Execute Python code with split output option
	 */
	public ExecutionResult runPythonCell(String code, boolean splitOutput) throws SandboxClientException {
		Map<String, Object> payload = new HashMap<>();
		payload.put("code", code);
		payload.put("split_output", splitOutput);

		return executeRequest("/tools/run_ipython_cell", payload, ExecutionResult.class);
	}

	/**
	 * Execute shell command
	 */
	public ExecutionResult runShellCommand(String command) throws SandboxClientException {
		return runShellCommand(command, false);
	}

	/**
	 * Execute shell command with split output option
	 */
	public ExecutionResult runShellCommand(String command, boolean splitOutput) throws SandboxClientException {
		Map<String, Object> payload = new HashMap<>();
		payload.put("command", command);
		payload.put("split_output", splitOutput);

		return executeRequest("/tools/run_shell_command", payload, ExecutionResult.class);
	}

	/**
	 * Health check
	 */
	public boolean healthCheck() {
		try {
			String response = executeGetRequest("/healthz", String.class);
			return "\"OK\"".equals(response);
		}
		catch (Exception e) {
			logger.debug("Health check failed", e);
			return false;
		}
	}

	/**
	 * Get detailed health status
	 */
	public Map<String, Object> getHealthStatus() throws SandboxClientException {
		return executeGetRequest("/health", Map.class);
	}

	/**
	 * Execute POST request
	 */
	private <T> T executeRequest(String endpoint, Object payload, Class<T> responseType) throws SandboxClientException {
		try {
			String url = baseUrl + endpoint;
			HttpPost request = new HttpPost(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}
			request.setHeader("Content-Type", "application/json");

			// Set request body
			String jsonPayload = objectMapper.writeValueAsString(payload);
			request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

			logger.debug("Executing request: {} with payload: {}", url, jsonPayload);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return handleResponse(response, responseType);
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to execute request to " + endpoint, e);
		}
	}

	/**
	 * Execute GET request
	 */
	private <T> T executeGetRequest(String endpoint, Class<T> responseType) throws SandboxClientException {
		try {
			String url = baseUrl + endpoint;
			HttpGet request = new HttpGet(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			logger.debug("Executing GET request: {}", url);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return handleResponse(response, responseType);
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to execute GET request to " + endpoint, e);
		}
	}

	/**
	 * Handle HTTP response
	 */
	private <T> T handleResponse(CloseableHttpResponse response, Class<T> responseType) throws SandboxClientException {
		try {
			int statusCode = response.getCode();
			HttpEntity entity = response.getEntity();
			String responseBody = entity != null ? EntityUtils.toString(entity) : "";

			logger.debug("Response status: {}, body: {}", statusCode, responseBody);

			if (statusCode >= 200 && statusCode < 300) {
				if (responseType == String.class) {
					return responseType.cast(responseBody);
				}
				else {
					return objectMapper.readValue(responseBody, responseType);
				}
			}
			else {
				throw new SandboxClientException(
						String.format("Request failed with status %d: %s", statusCode, responseBody));
			}

		}
		catch (IOException | ParseException e) {
			throw new SandboxClientException("Failed to parse response", e);
		}
	}

	/**
	 * Get base URL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Get auth token
	 */
	public String getAuthToken() {
		return authToken;
	}

	@Override
	public void close() throws IOException {
		if (httpClient != null) {
			httpClient.close();
		}
	}

}
