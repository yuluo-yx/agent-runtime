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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.model;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Container model representing a sandbox container instance
 */
public class ContainerModel {

	@NotBlank
	@JsonProperty("session_id")
	private String sessionId;

	@NotBlank
	@JsonProperty("container_id")
	private String containerId;

	@NotBlank
	@JsonProperty("base_url")
	private String baseUrl;

	@JsonProperty("browser_url")
	private String browserUrl;

	@JsonProperty("client_browser_ws")
	private String clientBrowserWs;

	@NotNull
	private List<Integer> ports;

	@NotBlank
	private String sandboxType;

	@JsonProperty("runtime_token")
	private String runtimeToken;

	private Map<String, String> environment;

	@JsonProperty("mount_dir")
	private String mountDir;

	// Constructors
	public ContainerModel() {
	}

	public ContainerModel(String sessionId, String containerId, String baseUrl, List<Integer> ports, String sandboxType) {
		this.sessionId = sessionId;
		this.containerId = containerId;
		this.baseUrl = baseUrl;
		this.ports = ports;
		this.sandboxType = sandboxType;
	}

	// Getters and Setters
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBrowserUrl() {
		return browserUrl;
	}

	public void setBrowserUrl(String browserUrl) {
		this.browserUrl = browserUrl;
	}

	public String getClientBrowserWs() {
		return clientBrowserWs;
	}

	public void setClientBrowserWs(String clientBrowserWs) {
		this.clientBrowserWs = clientBrowserWs;
	}

	public List<Integer> getPorts() {
		return ports;
	}

	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}


	public String getRuntimeToken() {
		return runtimeToken;
	}

	public void setRuntimeToken(String runtimeToken) {
		this.runtimeToken = runtimeToken;
	}

	public Map<String, String> getEnvironment() {
		return environment;
	}

	public void setEnvironment(Map<String, String> environment) {
		this.environment = environment;
	}

	public String getMountDir() {
		return mountDir;
	}

	public void setMountDir(String mountDir) {
		this.mountDir = mountDir;
	}

	public String getSandboxType() {
		return sandboxType;
	}

	public void setSandboxType(String sandboxType) {
		this.sandboxType = sandboxType;
	}

	@Override
	public String toString() {
		return "ContainerModel{" + "sessionId='" + sessionId + '\'' + ", containerId='" + containerId + '\''
				+ ", baseUrl='" + baseUrl + '\'' + ", ports=" + ports + ", sandboxType" + sandboxType + '\'' + '}';
	}

}