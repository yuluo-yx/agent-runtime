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

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ContainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating sandbox clients
 */
public class SandboxClientFactory {

	private static final Logger logger = LoggerFactory.getLogger(SandboxClientFactory.class);

	private final SandboxManagerClient managerClient;

	/**
	 * Constructor
	 */
	public SandboxClientFactory(ContainerService containerService) {
		this.managerClient = new SandboxManagerClient(containerService);
	}

	/**
	 * Create a new sandbox session
	 */
	public SandboxSession createSession(SandboxType sandboxType) {
		return createSession(sandboxType, null);
	}

	/**
	 * Create a new sandbox session with session ID
	 */
	public SandboxSession createSession(SandboxType sandboxType, String sessionId) {
		logger.info("Creating sandbox session with type: {}, sessionId: {}", sandboxType, sessionId);

		// Create container
		ContainerModel container = managerClient.createContainer(sandboxType, sessionId);

		// Wait for container to be ready
		waitForContainerReady(container);

		// Create HTTP client for the container
		SandboxHttpClient httpClient = new SandboxHttpClient(container);

		return new SandboxSession(container, httpClient, managerClient);
	}

	/**
	 * Get existing session
	 */
	public SandboxSession getSession(String sessionId) {
		logger.info("Getting existing sandbox session: {}", sessionId);

		ContainerModel container = managerClient.getContainer(sessionId);
		if (container == null) {
			throw new SandboxClientException("Container not found for session: " + sessionId);
		}

		SandboxHttpClient httpClient = new SandboxHttpClient(container);

		return new SandboxSession(container, httpClient, managerClient);
	}

	/**
	 * Check if manager is healthy
	 */
	public boolean isManagerHealthy() {
		return managerClient.healthCheck();
	}

	/**
	 * Get manager client
	 */
	public SandboxManagerClient getManagerClient() {
		return managerClient;
	}

	/**
	 * Wait for container to be ready
	 */
	private void waitForContainerReady(ContainerModel container) {
		logger.debug("Waiting for container to be ready: {}", container.getSessionId());

		SandboxHttpClient httpClient = new SandboxHttpClient(container);

		int maxAttempts = 30;
		int attempt = 0;

		while (attempt < maxAttempts) {
			try {
				if (httpClient.healthCheck()) {
					logger.debug("Container is ready: {}", container.getSessionId());
					httpClient.close();
					return;
				}

				Thread.sleep(1000);
				attempt++;

			}
			catch (Exception e) {
				logger.debug("Container not ready yet, attempt: {}", attempt + 1);
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new SandboxClientException("Interrupted while waiting for container", ie);
				}
				attempt++;
			}
		}

		try {
			httpClient.close();
		}
		catch (Exception e) {
			logger.warn("Failed to close HTTP client", e);
		}

		throw new SandboxClientException(
				"Container failed to become ready within timeout: " + container.getSessionId());
	}

}
