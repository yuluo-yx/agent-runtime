
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

package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.service;

import com.alibaba.cloud.ai.agent.runtime.sandbox.common.model.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for executing Python code and shell commands
 */
@Service
public class ExecutionService {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionService.class);

	private static final int TIMEOUT_SECONDS = 30;

	private static final boolean SPLIT_OUTPUT_MODE = true;

	/**
	 * Execute Python code using IPython-like environment
	 */
	public ExecutionResult runPythonCell(String code) {
		logger.info("Executing Python code: {}", code.substring(0, Math.min(code.length(), 100)));

		try {
			if (code.trim().isEmpty()) {
				throw new IllegalArgumentException("Code is required.");
			}

			// Create temporary Python file
			Path tempFile = createTempPythonFile(code);

			try {
				// Execute Python code
				ProcessResult result = executeCommand("python3", tempFile.toString());

				List<ExecutionResult.TextContent> contentList = new ArrayList<>();

				if (SPLIT_OUTPUT_MODE) {
					contentList.add(new ExecutionResult.TextContent(result.stdout, "stdout"));
					if (!result.stderr.isEmpty()) {
						contentList.add(new ExecutionResult.TextContent(result.stderr, "stderr"));
					}
				}
				else {
					contentList.add(new ExecutionResult.TextContent(result.stdout + "\n" + result.stderr, "output"));
				}

				boolean isError = !result.stderr.isEmpty() || result.exitCode != 0;
				return new ExecutionResult(contentList, isError);

			}
			finally {
				// Clean up temporary file
				Files.deleteIfExists(tempFile);
			}

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

		try {
			if (command == null || command.trim().isEmpty()) {
				throw new IllegalArgumentException("Command is required.");
			}

			ProcessResult result = executeCommand("/bin/bash", "-c", command);

			List<ExecutionResult.TextContent> contentList = new ArrayList<>();

			if (SPLIT_OUTPUT_MODE) {
				contentList.add(new ExecutionResult.TextContent(result.stdout, "stdout"));
				if (!result.stderr.isEmpty()) {
					contentList.add(new ExecutionResult.TextContent(result.stderr, "stderr"));
				}
				contentList.add(new ExecutionResult.TextContent(String.valueOf(result.exitCode), "returncode"));
			}
			else {
				contentList.add(new ExecutionResult.TextContent(
						result.stdout + "\n" + result.stderr + "\n" + result.exitCode, "output"));
			}

			boolean isError = !result.stderr.isEmpty();
			return new ExecutionResult(contentList, isError);

		}
		catch (Exception e) {
			logger.error("Error executing shell command", e);
			List<ExecutionResult.TextContent> errorContent = List
				.of(new ExecutionResult.TextContent("Error: " + e.getMessage(), "error"));
			return new ExecutionResult(errorContent, true);
		}
	}

	/**
	 * Create temporary Python file with the given code
	 */
	private Path createTempPythonFile(String code) throws IOException {
		Path tempFile = Files.createTempFile("sandbox_", ".py");
		Files.write(tempFile, code.getBytes());
		return tempFile;
	}

	/**
	 * Execute command and capture output
	 */
	private ProcessResult executeCommand(String... command) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(false);

		Process process = pb.start();

		// Capture stdout and stderr
		StringBuilder stdout = new StringBuilder();
		StringBuilder stderr = new StringBuilder();

		Thread stdoutThread = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stdout.append(line).append("\n");
				}
			}
			catch (IOException e) {
				logger.error("Error reading stdout", e);
			}
		});

		Thread stderrThread = new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					stderr.append(line).append("\n");
				}
			}
			catch (IOException e) {
				logger.error("Error reading stderr", e);
			}
		});

		stdoutThread.start();
		stderrThread.start();

		// Wait for process to complete with timeout
		boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

		if (!finished) {
			process.destroyForcibly();
			throw new RuntimeException("Process timed out after " + TIMEOUT_SECONDS + " seconds");
		}

		// Wait for output threads to complete
		stdoutThread.join(1000);
		stderrThread.join(1000);

		int exitCode = process.exitValue();

		return new ProcessResult(stdout.toString().trim(), stderr.toString().trim(), exitCode);
	}

	/**
	 * Process execution result
	 */
	private static class ProcessResult {

		final String stdout;

		final String stderr;

		final int exitCode;

		ProcessResult(String stdout, String stderr, int exitCode) {
			this.stdout = stdout;
			this.stderr = stderr;
			this.exitCode = exitCode;
		}

	}

}