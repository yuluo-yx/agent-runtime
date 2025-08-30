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

package com.alibaba.cloud.ai.agent.runtime.sandbox.client;

import com.alibaba.cloud.ai.agent.runtime.sandbox.common.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.common.model.ContainerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

/**
 * Client for managing sandbox containers
 */
public class SandboxManagerClient implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SandboxManagerClient.class);

	private final String baseUrl;

	private final String authToken;

	private final CloseableHttpClient httpClient;

	private final ObjectMapper objectMapper;

	/**
	 * Constructor
	 */
	public SandboxManagerClient(String baseUrl, String authToken) {
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
		this.authToken = authToken;
		this.httpClient = HttpClients.createDefault();
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Create a new container
	 */
	public ContainerModel createContainer(SandboxType sandboxType) throws SandboxClientException {
		return createContainer(sandboxType, null);
	}

	/**
	 * Create a new container with session ID
	 */
	public ContainerModel createContainer(SandboxType sandboxType, String sessionId) throws SandboxClientException {
		try {
			if (sessionId == null || sessionId.trim().isEmpty()) {
				sessionId = UUID.randomUUID().toString();
			}

			String url = baseUrl + "/containers?sandboxType=" + sandboxType.getValue() + "&sessionId=" + sessionId;
			HttpPost request = new HttpPost(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			logger.debug("Creating container: {}", url);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return handleResponse(response, ContainerModel.class);
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to create container", e);
		}
	}

	/**
	 * Get container information
	 */
	public ContainerModel getContainer(String sessionId) throws SandboxClientException {
		try {
			String url = baseUrl + "/containers/" + sessionId;
			HttpGet request = new HttpGet(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			logger.debug("Getting container: {}", url);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				if (response.getCode() == 404) {
					return null;
				}
				return handleResponse(response, ContainerModel.class);
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to get container", e);
		}
	}

	/**
	 * Delete container
	 */
	public void deleteContainer(String sessionId) throws SandboxClientException {
		try {
			String url = baseUrl + "/containers/" + sessionId;
			HttpDelete request = new HttpDelete(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			logger.debug("Deleting container: {}", url);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				if (response.getCode() >= 400) {
					String responseBody = EntityUtils.toString(response.getEntity());
					throw new SandboxClientException(String.format("Failed to delete container with status %d: %s",
							response.getCode(), responseBody));
				}
			}

		}
		catch (URISyntaxException | IOException | ParseException e) {
			throw new SandboxClientException("Failed to delete container", e);
		}
	}

	/**
	 * List all containers
	 */
	public Map<String, ContainerModel> listContainers() throws SandboxClientException {
		try {
			String url = baseUrl + "/containers";
			HttpGet request = new HttpGet(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			logger.debug("Listing containers: {}", url);

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return handleResponse(response, new TypeReference<Map<String, ContainerModel>>() {
				});
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to list containers", e);
		}
	}

	/**
	 * Health check
	 */
	public boolean healthCheck() {
		try {
			String url = baseUrl + "/healthz";
			HttpGet request = new HttpGet(new URI(url));

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				String responseBody = EntityUtils.toString(response.getEntity());
				return response.getCode() == 200 && "OK".equals(responseBody);
			}

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
		try {
			String url = baseUrl + "/health";
			HttpGet request = new HttpGet(new URI(url));

			// Set headers
			if (authToken != null && !authToken.isEmpty()) {
				request.setHeader("Authorization", "Bearer " + authToken);
			}

			try (CloseableHttpResponse response = httpClient.execute(request)) {
				return handleResponse(response, new TypeReference<Map<String, Object>>() {
				});
			}

		}
		catch (URISyntaxException | IOException e) {
			throw new SandboxClientException("Failed to get health status", e);
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
	 * Handle HTTP response with TypeReference
	 */
	private <T> T handleResponse(CloseableHttpResponse response, TypeReference<T> typeReference)
			throws SandboxClientException {
		try {
			int statusCode = response.getCode();
			HttpEntity entity = response.getEntity();
			String responseBody = entity != null ? EntityUtils.toString(entity) : "";

			logger.debug("Response status: {}, body: {}", statusCode, responseBody);

			if (statusCode >= 200 && statusCode < 300) {
				return objectMapper.readValue(responseBody, typeReference);
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
