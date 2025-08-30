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
import com.alibaba.cloud.ai.agent.runtime.LoaderType;
import com.alibaba.cloud.ai.agent.runtime.common.AgentRuntimeException;
import com.alibaba.cloud.ai.agent.runtime.common.model.BaseAgent;
import com.alibaba.cloud.ai.agent.runtime.framework.adapter.SAAGraphAdapter;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class YamlAgentLoader extends AbstractAgentRuntimeLoader {

    private static final LoaderType YAML_LOADER = LoaderType.YAML;

    @Override
    protected BaseAgent loader() throws FileNotFoundException {

        BaseAgent baseAgent = null;
        AgentRuntimeSchema runtimeConfig = getRuntimeConfig();

        // check agent framework type, call related adapter to convert
        // runtime can run Agent Bean.
        // convert to framework agent to BaseAgent.
        switch (runtimeConfig.getFramework()) {
            case SPRING_AI_ALIBABA_GRAPH -> {

                // check SAA graph schemas
                if (runtimeConfig.getSchema() == null || runtimeConfig.getSchema().isEmpty()) {
                    throw new AgentRuntimeException("Agent schema is empty");
                }

                baseAgent = SAAGraphAdapter.convert(new FileReader(runtimeConfig.getSchema()));
            }
            case ADK_JAVA -> throw new AgentRuntimeException("not support ADK-Java yet.");
            case LANGGRAPH4J -> throw new AgentRuntimeException("not support LangGraph4J yet.");
            default -> throw new AgentRuntimeException("not support framework: " + runtimeConfig.getFramework());
        }

        return baseAgent;
    }

    @Override
    public LoaderType getLoaderType() {

        return YAML_LOADER;
    }

}
