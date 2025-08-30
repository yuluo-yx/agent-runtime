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

package com.alibaba.cloud.ai.agent.runtime.sandbox.core.enums;

/**
 * Sandbox type enumeration
 */
public enum SandboxType {

	BASE("base"), FILESYSTEM("filesystem"), BROWSER("browser"), CUSTOM("custom");

	private final String value;

	SandboxType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static SandboxType fromValue(String value) {
		for (SandboxType type : values()) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown sandbox type: " + value);
	}

	@Override
	public String toString() {
		return value;
	}

}