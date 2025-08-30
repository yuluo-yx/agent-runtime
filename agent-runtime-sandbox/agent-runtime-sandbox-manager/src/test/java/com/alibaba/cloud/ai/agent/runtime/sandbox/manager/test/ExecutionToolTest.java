package com.alibaba.cloud.ai.agent.runtime.sandbox.manager.test;

import com.alibaba.cloud.ai.agent.runtime.sandbox.core.tools.PythonExecutionTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Execution Tool Test")
public class ExecutionToolTest {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionToolTest.class);

	@Resource
	private ChatModel chatModel;

	@Test
	@DisplayName("Test Python Execution")
	public void testPythonExecution() {
		String call = chatModel.call("Write a Python script to print 'Hello, World!'");
		logger.info(call);
	}

	@Test
	@DisplayName("Test Shell Execution")
	public void testShellExecution() {
		// Implementation of the test
	}

}
