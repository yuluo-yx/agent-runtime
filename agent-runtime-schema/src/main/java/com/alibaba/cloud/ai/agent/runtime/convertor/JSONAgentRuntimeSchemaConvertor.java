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

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Agent Runtime Schema Convertor for JSON format.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
public final class JSONAgentRuntimeSchemaConvertor extends AbstractAgentRuntimeSchemaConvertor {

    public JSONAgentRuntimeSchemaConvertor(String resourcePath) {
        super(resourcePath);
    }

    @Override
    public AgentRuntimeSchema doConvert() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(this.fileReader, AgentRuntimeSchema.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to AgentRuntimeSchema", e);
        }
    }

}
