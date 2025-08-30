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

package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.controller;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ExecutionResult;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ExecutionService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for code execution endpoints
 */
@RestController
@RequestMapping("/tools")
public class ExecutionController {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionController.class);

	@Resource
	private ExecutionService executionService;

	/**
	 * Execute IPython cell
	 */
	@PostMapping("/run_ipython_cell")
	public ResponseEntity<ExecutionResult> runIpythonCell(@RequestBody Map<String, String> request) {
		try {
			String code = request.get("code");
			if (code == null) {
				throw new IllegalArgumentException("Code parameter is required");
			}

			logger.info("Received IPython cell execution request");
			ExecutionResult result = executionService.runPythonCell(code);
			return ResponseEntity.ok(result);

		}
		catch (Exception e) {
			logger.error("Error executing IPython cell", e);
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Execute shell command
	 */
	@PostMapping("/run_shell_command")
	public ResponseEntity<ExecutionResult> runShellCommand(@RequestBody Map<String, String> request) {
		try {
			String command = request.get("command");
			if (command == null) {
				throw new IllegalArgumentException("Command parameter is required");
			}

			logger.info("Received shell command execution request: {}", command);
			ExecutionResult result = executionService.runShellCommand(command);
			return ResponseEntity.ok(result);

		}
		catch (Exception e) {
			logger.error("Error executing shell command", e);
			return ResponseEntity.badRequest().build();
		}
	}

}