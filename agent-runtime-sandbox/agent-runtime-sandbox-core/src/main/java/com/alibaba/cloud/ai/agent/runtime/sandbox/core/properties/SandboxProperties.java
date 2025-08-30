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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.properties;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums.SandboxType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Sandbox configuration class.
 */
@ConfigurationProperties(prefix = "sandbox")
public class SandboxProperties {

	/**
	 * Sandbox manager settings.
	 */
	private SandboxType defaultSandboxType = SandboxType.BASE;

	private int poolSize = 1;

	private boolean autoCleanup = true;

	private String containerPrefixKey = "sandbox-";

	private String deploymentType = "docker";

	private String defaultMountDir = "sessions_mount_dir";

	private List<Integer> portRange = List.of(49152, 59152);

	/**
	 * Redis settings.
	 */
	private boolean redisEnabled = false;

	private String redisServer = "localhost";

	private int redisPort = 6379;

	private int redisDb = 0;

	private String redisUser;

	private String redisPassword;

	private String redisPortKey = "_agent_runtime_container_occupied_ports";

	private String redisContainerPoolKey = "_agent_runtime_container_container_pool";

	/**
	 * Storage settings.
	 */
	private String storageType = "LOCAL";

	private String storagePath = "/tmp/sandbox-storage";

	/**
	 * OSS settings (for cloud storage).
	 */
	private String ossEndpoint;

	private String ossAccessKeyId;

	private String ossAccessKeySecret;

	private String ossBucketName;

	/**
	 * Docker settings.
	 */
	private String dockerHost = "unix:///var/run/docker.sock";

	private Map<String, String> dockerEnvironment;

	public SandboxType getDefaultSandboxType() {
		return defaultSandboxType;
	}

	public void setDefaultSandboxType(SandboxType defaultSandboxType) {
		this.defaultSandboxType = defaultSandboxType;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public boolean isAutoCleanup() {
		return autoCleanup;
	}

	public void setAutoCleanup(boolean autoCleanup) {
		this.autoCleanup = autoCleanup;
	}

	public String getContainerPrefixKey() {
		return containerPrefixKey;
	}

	public void setContainerPrefixKey(String containerPrefixKey) {
		this.containerPrefixKey = containerPrefixKey;
	}

	public String getDeploymentType() {
		return deploymentType;
	}

	public void setDeploymentType(String deploymentType) {
		this.deploymentType = deploymentType;
	}

	public String getDefaultMountDir() {
		return defaultMountDir;
	}

	public void setDefaultMountDir(String defaultMountDir) {
		this.defaultMountDir = defaultMountDir;
	}

	public List<Integer> getPortRange() {
		return portRange;
	}

	public void setPortRange(List<Integer> portRange) {
		this.portRange = portRange;
	}

	public boolean isRedisEnabled() {
		return redisEnabled;
	}

	public void setRedisEnabled(boolean redisEnabled) {
		this.redisEnabled = redisEnabled;
	}

	public String getRedisServer() {
		return redisServer;
	}

	public void setRedisServer(String redisServer) {
		this.redisServer = redisServer;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public int getRedisDb() {
		return redisDb;
	}

	public void setRedisDb(int redisDb) {
		this.redisDb = redisDb;
	}

	public String getRedisUser() {
		return redisUser;
	}

	public void setRedisUser(String redisUser) {
		this.redisUser = redisUser;
	}

	public String getRedisPassword() {
		return redisPassword;
	}

	public void setRedisPassword(String redisPassword) {
		this.redisPassword = redisPassword;
	}

	public String getRedisPortKey() {
		return redisPortKey;
	}

	public void setRedisPortKey(String redisPortKey) {
		this.redisPortKey = redisPortKey;
	}

	public String getRedisContainerPoolKey() {
		return redisContainerPoolKey;
	}

	public void setRedisContainerPoolKey(String redisContainerPoolKey) {
		this.redisContainerPoolKey = redisContainerPoolKey;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getOssEndpoint() {
		return ossEndpoint;
	}

	public void setOssEndpoint(String ossEndpoint) {
		this.ossEndpoint = ossEndpoint;
	}

	public String getOssAccessKeyId() {
		return ossAccessKeyId;
	}

	public void setOssAccessKeyId(String ossAccessKeyId) {
		this.ossAccessKeyId = ossAccessKeyId;
	}

	public String getOssAccessKeySecret() {
		return ossAccessKeySecret;
	}

	public void setOssAccessKeySecret(String ossAccessKeySecret) {
		this.ossAccessKeySecret = ossAccessKeySecret;
	}

	public String getOssBucketName() {
		return ossBucketName;
	}

	public void setOssBucketName(String ossBucketName) {
		this.ossBucketName = ossBucketName;
	}

	public String getDockerHost() {
		return dockerHost;
	}

	public void setDockerHost(String dockerHost) {
		this.dockerHost = dockerHost;
	}

	public Map<String, String> getDockerEnvironment() {
		return dockerEnvironment;
	}

	public void setDockerEnvironment(Map<String, String> dockerEnvironment) {
		this.dockerEnvironment = dockerEnvironment;
	}

}
