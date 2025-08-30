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
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ContainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Client for managing sandbox containers
 */
public class SandboxManagerClient {

	private static final Logger logger = LoggerFactory.getLogger(SandboxManagerClient.class);

	private final ContainerService containerService;

	/**
	 * Constructor
	 */
	public SandboxManagerClient(ContainerService containerService) {
		this.containerService = containerService;
	}

	/**
	 * Create a new container
	 */
	public ContainerModel createContainer(SandboxType sandboxType) {
		return createContainer(sandboxType, null);
	}

	/**
	 * Create a new container with session ID
	 */
	public ContainerModel createContainer(SandboxType sandboxType, String sessionId) {
		if (sessionId == null || sessionId.trim().isEmpty()) {
			sessionId = UUID.randomUUID().toString();
		}

		return containerService.createContainer(sessionId, sandboxType);
	}

	/**
	 * Get container information
	 */
	public ContainerModel getContainer(String sessionId) {
		return containerService.getContainer(sessionId);
	}

	/**
	 * Delete container
	 */
	public void deleteContainer(String sessionId) {
		containerService.removeContainer(sessionId);

	}

	/**
	 * List all containers
	 */
	public Map<String, ContainerModel> listContainers() {
		return containerService.listContainers();
	}

	/**
	 * Health check
	 */
	public boolean healthCheck() {
		return true;
	}

	/**
	 * Get detailed health status
	 */

	public Map<String, Object> getHealthStatus() {
		return Map.of();
	}

}
