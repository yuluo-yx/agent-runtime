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

package com.alibaba.cloud.ai.agent.runtime;

import com.alibaba.cloud.ai.agent.runtime.common.enums.IEnum;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public enum AgentFramework implements IEnum {

    LANGGRAPH("Langgraph", "An open-source framework for building AI agents and applications using large language models."),
    SPRING_AI_ALIBABA_GRAPH("Spring AI Alibaba Graph", "A framework developed by Alibaba for creating AI agents and applications using large language models."),
    AUTOGEN("AutoGen", "A framework for generating AI agents and applications using large language models.");

    private final String name;

    private final String desc;

    AgentFramework(final String name, final String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
