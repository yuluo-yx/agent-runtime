
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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.service;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxSession;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for executing Python code and shell commands
 */
public class ExecutionService {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionService.class);

	private final SandboxClientFactory sandboxClientFactory;

	public ExecutionService(SandboxClientFactory sandboxClientFactory) {
		this.sandboxClientFactory = sandboxClientFactory;
	}

	/**
	 * Execute Python code using IPython-like environment
	 */
	public ExecutionResult runPythonCell(String code) {
		logger.info("Executing Python code: {}", code.substring(0, Math.min(code.length(), 100)));
		if (code.trim().isEmpty()) {
			throw new IllegalArgumentException("Code is required.");
		}
		try (SandboxSession session = sandboxClientFactory.createSession(SandboxType.BASE)) {
			logger.info("Session created: {}", session.getSessionId());
			return session.runPython(code);
		}
		catch (Exception e) {
			logger.error("Error executing Python code", e);
			List<ExecutionResult.TextContent> errorContent = List
				.of(new ExecutionResult.TextContent("Error: " + e.getMessage(), "error"));
			return new ExecutionResult(errorContent, true);
		}
	}

	/**
	 * Execute shell command
	 */
	public ExecutionResult runShellCommand(String command) {
		logger.info("Executing shell command: {}", command);
		if (command == null || command.trim().isEmpty()) {
			throw new IllegalArgumentException("Command is required.");
		}
		try (SandboxSession session = sandboxClientFactory.createSession(SandboxType.BASE)) {
			logger.info("Session created: {}", session.getSessionId());
			return session.runShell(command);
		}
		catch (Exception e) {
			logger.error("Error executing shell command", e);
			List<ExecutionResult.TextContent> errorContent = List
				.of(new ExecutionResult.TextContent("Error: " + e.getMessage(), "error"));
			return new ExecutionResult(errorContent, true);
		}
	}

}