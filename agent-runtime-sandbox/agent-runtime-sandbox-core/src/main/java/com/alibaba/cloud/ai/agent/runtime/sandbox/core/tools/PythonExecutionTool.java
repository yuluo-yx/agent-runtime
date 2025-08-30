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
 * Python code execution tool class Provides a simple API for large models to call Python
 * code execution functionality
 */
public class PythonExecutionTool {

	private static final Logger logger = LoggerFactory.getLogger(PythonExecutionTool.class);

	private final SandboxClientFactory clientFactory;

	/**
	 * Constructor
	 * @param clientFactory Sandbox client factory
	 */
	public PythonExecutionTool(SandboxClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Execute Python code (using new session)
	 * @param code Python code
	 * @return execution result
	 */
	@Description("Execute Python code in a secure sandbox environment. " + "Input should be valid Python code. "
			+ "The output will be the result of the executed code or error messages if any.")
	public ExecutionResult execute(String code) {
		return execute(code, false, null);
	}

	/**
	 * Execute Python code
	 * @param code Python code
	 * @param splitOutput whether to split output
	 * @return execution result
	 */
	public ExecutionResult execute(String code, boolean splitOutput) {
		return execute(code, splitOutput, null);
	}

	/**
	 * Execute Python code (using specified session)
	 * @param code Python code
	 * @param session specified session, if null then create new session
	 * @return execution result
	 */
	public ExecutionResult execute(String code, SandboxSession session) {
		return execute(code, false, session);
	}

	/**
	 * Execute Python code (full parameters)
	 * @param code Python code
	 * @param splitOutput whether to split output
	 * @param session specified session, if null then create new session
	 * @return execution result
	 */
	public ExecutionResult execute(String code, boolean splitOutput, SandboxSession session) {
		if (code == null || code.trim().isEmpty()) {
			logger.error("Python code cannot be empty");
			return createErrorResult("Python code cannot be empty");
		}

		logger.info("Starting to execute Python code, code length: {}", code.length());
		logger.debug("Python code content: {}", code.substring(0, Math.min(code.length(), 200)));

		try {
			boolean useExternalSession = session != null;
			SandboxSession executionSession = useExternalSession ? session : createNewSession();

			try {
				ExecutionResult result = executionSession.runPython(code, splitOutput);
				logger.info("Python code execution completed, has error: {}", result.isError());
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
			logger.error("Sandbox exception occurred while executing Python code", e);
			return createErrorResult("Sandbox exception: " + e.getMessage());
		}
		catch (Exception e) {
			logger.error("Unknown exception occurred while executing Python code", e);
			return createErrorResult("Unknown exception: " + e.getMessage());
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