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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Execution result model for code/command execution
 */
public class ExecutionResult {

	private List<TextContent> content;

	@JsonProperty("is_error")
	private boolean isError;

	// Constructors
	public ExecutionResult() {
	}

	public ExecutionResult(List<TextContent> content, boolean isError) {
		this.content = content;
		this.isError = isError;
	}

	// Getters and Setters
	public List<TextContent> getContent() {
		return content;
	}

	public void setContent(List<TextContent> content) {
		this.content = content;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean error) {
		isError = error;
	}

	/**
	 * Text content model
	 */
	public static class TextContent {

		private String type = "text";

		private String text;

		private String description;

		public TextContent() {
		}

		public TextContent(String text, String description) {
			this.text = text;
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

}