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
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Represents a sandbox session with a container
 */
public class SandboxSession implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(SandboxSession.class);

	private ContainerModel container;

	private final SandboxHttpClient httpClient;

	private final SandboxManagerClient managerClient;

	private boolean closed = false;

	/**
	 * Constructor
	 */
	public SandboxSession(ContainerModel container, SandboxHttpClient httpClient, SandboxManagerClient managerClient) {
		this.container = container;
		this.httpClient = httpClient;
		this.managerClient = managerClient;
	}

	/**
	 * Execute Python code
	 */
	public ExecutionResult runPython(String code) throws SandboxClientException {
		checkClosed();
		logger.debug("Executing Python code in session: {}", container.getSessionId());
		return httpClient.runPythonCell(code);
	}

	/**
	 * Execute Python code with split output option
	 */
	public ExecutionResult runPython(String code, boolean splitOutput) throws SandboxClientException {
		checkClosed();
		logger.debug("Executing Python code in session: {} with splitOutput: {}", container.getSessionId(),
				splitOutput);
		return httpClient.runPythonCell(code, splitOutput);
	}

	/**
	 * Execute shell command
	 */
	public ExecutionResult runShell(String command) throws SandboxClientException {
		checkClosed();
		logger.debug("Executing shell command in session: {}", container.getSessionId());
		return httpClient.runShellCommand(command);
	}

	/**
	 * Execute shell command with split output option
	 */
	public ExecutionResult runShell(String command, boolean splitOutput) throws SandboxClientException {
		checkClosed();
		logger.debug("Executing shell command in session: {} with splitOutput: {}", container.getSessionId(),
				splitOutput);
		return httpClient.runShellCommand(command, splitOutput);
	}

	/**
	 * Check if container is healthy
	 */
	public boolean isHealthy() {
		if (closed) {
			return false;
		}
		return httpClient.healthCheck();
	}

	/**
	 * Get container information
	 */
	public ContainerModel getContainer() {
		return container;
	}

	/**
	 * Get session ID
	 */
	public String getSessionId() {
		return container.getSessionId();
	}

	/**
	 * Get container ID
	 */
	public String getContainerId() {
		return container.getContainerId();
	}

	/**
	 * Get base URL
	 */
	public String getBaseUrl() {
		return container.getBaseUrl();
	}

	/**
	 * Get browser URL (if available)
	 */
	public String getBrowserUrl() {
		return container.getBrowserUrl();
	}

	/**
	 * Get sandbox type
	 */
	public String getSandboxType() {
		return container.getSandboxType();
	}

	/**
	 * Check if session is closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Restart the session (recreate container)
	 */
	public void restart(){
		logger.info("Restarting sandbox session: {}", container.getSessionId());

		// Close current resources
		try {
			httpClient.close();
		}
		catch (IOException e) {
			logger.warn("Failed to close HTTP client during restart", e);
		}

		// Remove old container
		try {
			managerClient.deleteContainer(container.getSessionId());
		}
		catch (SandboxClientException e) {
			logger.warn("Failed to delete old container during restart", e);
		}
		// Create new container with same session ID
		try {
			this.container = managerClient.createContainer(SandboxType.fromValue(getSandboxType()), getSessionId());
		}catch (SandboxClientException e) {
			logger.error("Failed to create new container during restart", e);
			throw new SandboxClientException("Failed to restart session: " + container.getSessionId());
		}
	}

	/**
	 * Check if session is closed and throw exception if it is
	 */
	private void checkClosed() throws SandboxClientException {
		if (closed) {
			throw new SandboxClientException("Session is closed: " + container.getSessionId());
		}
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}

		logger.info("Closing sandbox session: {}", container.getSessionId());

		try {
			// Close HTTP client
			httpClient.close();
		}
		catch (IOException e) {
			logger.error("Failed to close HTTP client", e);
		}

		try {
			// Remove container
			managerClient.deleteContainer(container.getSessionId());
		}
		catch (SandboxClientException e) {
			logger.error("Failed to delete container during close", e);
		}

		closed = true;
		logger.info("Sandbox session closed: {}", container.getSessionId());
	}

	@Override
	public String toString() {
		return String.format("SandboxSession{sessionId='%s', containerId='%s', baseUrl='%s', closed=%s}",
				container.getSessionId(), container.getContainerId(), container.getBaseUrl(), closed);
	}

}
