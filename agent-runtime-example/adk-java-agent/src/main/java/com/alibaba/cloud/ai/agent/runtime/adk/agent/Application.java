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

/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.agent.runtime.adk.agent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.models.langchain4j.LangChain4j;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import io.reactivex.rxjava3.core.Flowable;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * ADK Java Agent Application.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
public class Application {

    private static final BaseAgent AGENT = initAgent();

    public static void main(final String[] args) {

        InMemoryRunner runner = new InMemoryRunner(AGENT);
        String userId = "user1";
        Session session = runner.sessionService()
                .createSession("ADK Test Agent", userId)
                .blockingGet();

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> System.out.println("\nBye!"))
        );

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.print("\nYou > ");
                String userInput = scanner.nextLine();
                if ("quit".equalsIgnoreCase(userInput)) {
                    break;
                }
                Content content = Content.fromParts(Part.fromText(userInput));
                Flowable<Event> events = runner.runAsync(session.userId(), session.id(), content);
                System.out.print("\nAgent > ");
                events.blockingForEach(event -> System.out.println(event.stringifyContent()));
            }
        }
    }

    private static BaseAgent initAgent() {

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode")
                .modelName("qwen-plus")
                .build();

        // 转为 adk 支持的 ChatModel
        LangChain4j modelAdapter = new LangChain4j(chatModel);

        return LlmAgent.builder()
                .name("ADK Test Agent")
                .model(modelAdapter)
                .description("专业的 AI Java 助手")
                .instruction("""
                        你是一个专业的Java技术专家助手。请：
                        1. 准确回答Java技术问题
                        2. 提供实用的代码示例
                        3. 解释复杂概念时使用通俗易懂的语言
                        4. 保持回答简洁而全面
                        5. 如果需要代码，使用markdown格式
                        """)
                .build();
    }

}
