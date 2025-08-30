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

package com.alibaba.cloud.ai.agent.runtime.convertor;

import com.alibaba.cloud.ai.agent.runtime.AgentFramework;
import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;

import java.util.*;

/**
 * Agent Runtime Schema Convertor For Properties.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
public final class PropertiesAgentRuntimeSchemaConvertor extends AbstractAgentRuntimeSchemaConvertor {

    public PropertiesAgentRuntimeSchemaConvertor(String resourcePath) {
        super(resourcePath);
    }
    
    @Override
    public AgentRuntimeSchema doConvert() {

        Properties props = new Properties();
        try  {

            props.load(this.fileReader);
            AgentRuntimeSchema.Builder builder = AgentRuntimeSchema.builder();
            builder.name(props.getProperty("name"));
            builder.version(props.getProperty("version"));
            builder.description(props.getProperty("description"));
            builder.framework(AgentFramework.valueOf(props.getProperty("framework")));

            List<Map<String, String>> envs = new ArrayList<>();
            Map<Integer, Map<String, String>> envMap = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("envs.")) {
                    String[] parts = key.split("\\.");
                    if (parts.length == 3) {
                        int idx = Integer.parseInt(parts[1]);
                        envMap.computeIfAbsent(idx, k -> new HashMap<>())
                                .put(parts[2], props.getProperty(key));
                    }
                }
            }
            for (int i = 0; i < envMap.size(); i++) {
                envs.add(envMap.get(i));
            }
            builder.envs(envs);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Properties to AgentRuntimeSchema", e);
        }
    }

}
