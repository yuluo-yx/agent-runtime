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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.tools;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxClientFactory;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.client.SandboxSession;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.exceptions.SandboxClientException;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;

import java.util.List;

/**
 * Shell command execution tool class Provides a simple API for large models to call Shell
 * command execution functionality
 */
public class ShellExecutionTool {

	private static final Logger logger = LoggerFactory.getLogger(ShellExecutionTool.class);

	private final SandboxClientFactory clientFactory;

	/**
	 * Constructor
	 * @param clientFactory Sandbox client factory
	 */
	public ShellExecutionTool(SandboxClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Execute Shell command (using new session)
	 * @param command Shell command
	 * @return execution result
	 */
	@Description("Execute Shell command in a secure sandbox environment. " + "Input should be valid Shell command. "
			+ "The output will be the result of the executed command or error messages if any.")
	public ExecutionResult execute(String command) {
		return execute(command, false, null);
	}

	/**
	 * Execute Shell command
	 * @param command Shell command
	 * @param splitOutput whether to split output
	 * @return execution result
	 */
	public ExecutionResult execute(String command, boolean splitOutput) {
		return execute(command, splitOutput, null);
	}

	/**
	 * Execute Shell command (using specified session)
	 * @param command Shell command
	 * @param session specified session, if null then create new session
	 * @return execution result
	 */
	public ExecutionResult execute(String command, SandboxSession session) {
		return execute(command, false, session);
	}

	/**
	 * Execute Shell command (full parameters)
	 * @param command Shell command
	 * @param splitOutput whether to split output
	 * @param session specified session, if null then create new session
	 * @return execution result
	 */
	public ExecutionResult execute(String command, boolean splitOutput, SandboxSession session) {
		if (command == null || command.trim().isEmpty()) {
			logger.error("Shell command cannot be empty");
			return createErrorResult("Shell command cannot be empty");
		}

		logger.info("Starting to execute Shell command: {}", command);

		try {
			boolean useExternalSession = session != null;
			SandboxSession executionSession = useExternalSession ? session : createNewSession();

			try {
				ExecutionResult result = executionSession.runShell(command, splitOutput);
				logger.info("Shell command execution completed, has error: {}", result.isError());
				return result;
			}
			finally {
				// Only close when using internally created session
				if (!useExternalSession && executionSession != null) {
					try {
						executionSession.close();
					}
					catch (Exception e) {
						logger.warn("Error occurred while closing session", e);
					}
				}
			}
		}
		catch (SandboxClientException e) {
			logger.error("Sandbox exception occurred while executing Shell command", e);
			return createErrorResult("Sandbox exception: " + e.getMessage());
		}
		catch (Exception e) {
			logger.error("Unknown exception occurred while executing Shell command", e);
			return createErrorResult("Unknown exception: " + e.getMessage());
		}
	}

	/**
	 * Execute multiple Shell commands (in sequence)
	 * @param commands Shell command list
	 * @return execution result list
	 */
	public List<ExecutionResult> executeMultiple(List<String> commands) {
		return executeMultiple(commands, false);
	}

	/**
	 * Execute multiple Shell commands (in sequence)
	 * @param commands Shell command list
	 * @param splitOutput whether to split output
	 * @return execution result list
	 */
	public List<ExecutionResult> executeMultiple(List<String> commands, boolean splitOutput) {
		if (commands == null || commands.isEmpty()) {
			logger.error("Command list cannot be empty");
			return List.of(createErrorResult("Command list cannot be empty"));
		}

		logger.info("Starting to execute multiple Shell commands, command count: {}", commands.size());

		try (SandboxSession session = createNewSession()) {
			return commands.stream().map(command -> execute(command, splitOutput, session)).toList();
		}
		catch (Exception e) {
			logger.error("Exception occurred while executing multiple Shell commands", e);
			return List
				.of(createErrorResult("Exception occurred while executing multiple commands: " + e.getMessage()));
		}
	}

	/**
	 * Create new session
	 * @return new session
	 */
	private SandboxSession createNewSession() {
		return clientFactory.createSession(SandboxType.BASE);
	}

	/**
	 * Create error result
	 * @param errorMessage error message
	 * @return error result
	 */
	private ExecutionResult createErrorResult(String errorMessage) {
		ExecutionResult.TextContent errorContent = new ExecutionResult.TextContent(errorMessage, "error");
		return new ExecutionResult(List.of(errorContent), true);
	}

}