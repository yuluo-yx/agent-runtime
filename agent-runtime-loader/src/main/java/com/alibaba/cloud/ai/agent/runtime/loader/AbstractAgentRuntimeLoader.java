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

package com.alibaba.cloud.ai.agent.runtime.loader;

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.alibaba.cloud.ai.agent.runtime.IAgentRuntimeLoader;
import com.alibaba.cloud.ai.agent.runtime.LoaderType;
import com.alibaba.cloud.ai.agent.runtime.common.AgentRuntimeException;
import com.alibaba.cloud.ai.agent.runtime.common.model.BaseAgent;
import com.alibaba.cloud.ai.agent.runtime.convertor.AbstractAgentRuntimeSchemaConvertor;
import com.alibaba.cloud.ai.agent.runtime.convertor.IAgentRuntimeSchemaConvertor;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract loader for agent runtime schema.
 * Provides common functionality for loading and managing agent runtime configurations.
 *
 * @author yuluo
 * @version 1.0
 */
public abstract class AbstractAgentRuntimeLoader implements IAgentRuntimeLoader {

    private static AgentRuntimeSchema agentRuntimeSchema;

    public static IAgentRuntimeLoader createLoader(String configPath) {

        agentRuntimeSchema = loadAgentRuntimeConfig(configPath);
        LoaderType type = agentRuntimeSchema.getTypes();

        return switch (type) {
            case SPI -> new SPIAgentLoader();
            case CLASSPATH -> new ClassPathAgentLoader();
            case YAML -> new YamlAgentLoader();
            default -> throw new AgentRuntimeException("Unknown agent type: " + type);
        };
    }

    @Override
    public BaseAgent load() {

        try {
            return loadAgentBasedOnType(agentRuntimeSchema);
        } catch (FileNotFoundException e) {
            throw new AgentRuntimeException("Failed to load agent schema: " + e.getMessage() + ", loader type: " + getLoaderType(), e);
        }
    }

    private static AgentRuntimeSchema loadAgentRuntimeConfig(String configPath) {

        List<String> supportedFiles = List.of(
                "runtime.config.json",
                "runtime.config.yaml",
                "runtime.config.yml",
                "runtime.config.properties"
        );

        String configFilePath = configPath;
        if (configPath == null || configPath.isEmpty()) {
            configFilePath = findConfigFileInCurrentDirectory(supportedFiles);
        }

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(configFilePath);
        return convertor.convert();
    }

    private static String findConfigFileInCurrentDirectory(List<String> supportedFiles) {

        Path currentDir = Paths.get("").toAbsolutePath();
        List<String> foundConfigs = new ArrayList<>();

        for (String fileName : supportedFiles) {
            Path filePath = currentDir.resolve(fileName);
            if (Files.exists(filePath)) {
                foundConfigs.add(filePath.toString());
            }
        }

        if (foundConfigs.isEmpty()) {
            throw new IllegalStateException("No runtime config file found in the current directory.");
        }
        if (foundConfigs.size() > 1) {
            throw new IllegalStateException("Multiple runtime config files found: " + foundConfigs);
        }

        return foundConfigs.get(0);
    }

    private static BaseAgent loadAgentBasedOnType(AgentRuntimeSchema schema) throws FileNotFoundException {

        if (schema == null) {
            throw new AgentRuntimeException("AgentRuntimeSchema is null");
        }

        return switch (schema.getTypes()) {
            case YAML -> new YamlAgentLoader().loader();
            case CLASSPATH -> new ClassPathAgentLoader().loader();
            case SPI -> new SPIAgentLoader().loader();
            default -> throw new AgentRuntimeException("Unsupported loader type: " + schema.getTypes());
        };
    }

    public AgentRuntimeSchema getRuntimeConfig() {

        return agentRuntimeSchema;
    }

    /**
     * Abstract method to be implemented by subclasses for loading agents.
     *
     * @return {@link BaseAgent}
     * @throws FileNotFoundException if the agent file is not found
     */
    protected abstract BaseAgent loader() throws FileNotFoundException;

}
