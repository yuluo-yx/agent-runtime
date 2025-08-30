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

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.model.ContainerModel;
import com.alibaba.cloud.ai.agent.runtime.sandbox.core.service.ContainerService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Container management controller
 */
@RestController
@RequestMapping("/containers")
public class ContainerController {

	@Resource
	private ContainerService containerService;

	/**
	 * Create a new container
	 */
	@PostMapping
	public ResponseEntity<ContainerModel> createContainer(
			@RequestParam(defaultValue = "BASE", value = "sandboxType") String sandboxType,
			@RequestParam(required = false, value = "sessionId") String sessionId) {

		if (sessionId == null || sessionId.trim().isEmpty()) {
			sessionId = UUID.randomUUID().toString();
		}

		SandboxType type = SandboxType.fromValue(sandboxType);
		ContainerModel container = containerService.createContainer(sessionId, type);

		return ResponseEntity.ok(container);
	}

	/**
	 * Get container information
	 */
	@GetMapping("/{sessionId}")
	public ResponseEntity<ContainerModel> getContainer(@PathVariable("sessionId") String sessionId) {
		ContainerModel container = containerService.getContainer(sessionId);

		if (container == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(container);
	}

	/**
	 * Delete container
	 */
	@DeleteMapping("/{sessionId}")
	public ResponseEntity<Void> deleteContainer(@PathVariable("sessionId") String sessionId) {
		containerService.removeContainer(sessionId);
		return ResponseEntity.ok().build();
	}

	/**
	 * List all containers
	 */
	@GetMapping
	public ResponseEntity<Map<String, ContainerModel>> listContainers() {
		Map<String, ContainerModel> containers = containerService.listContainers();
		return ResponseEntity.ok(containers);
	}

}