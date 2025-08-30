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

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Define the Agent Runtime Config Schema.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
public final class AgentRuntimeSchema implements Serializable {

    @Serial
    private static final long serialVersionUID = 8068509879445395354L;

    /**
     * Agent Name.
     */
    private String name;

    /**
     * Agent Version.
     */
    private String version;

    /**
     * Agent Description.
     */
    private String description;

    /**
     * Agent Framework.
     * e.g. LangGraph, Spring AI Alibaba Graph, AutoGen, etc.
     */
    private AgentFramework framework;

    /**
     * Agent types
     */
    private LoaderType types;

    /**
     * Agent schema or java class or classpath.
     */
    private String schema;

    /**
     * Agent Environment.
     */
    private List<Map<String, String>> envs;

    public List<Map<String, String>> getEnvs() {
        return envs;
    }

    public void setEnvs(final List<Map<String, String>> envs) {
        this.envs = envs;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LoaderType getTypes() {
        return types;
    }

    public void setTypes(final LoaderType types) {
        this.types = types;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    public AgentFramework getFramework() {
        return framework;
    }

    public void setFramework(final AgentFramework framework) {
        this.framework = framework;
    }

    @Override
    public String toString() {

        return "AgentRuntimeSchema{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", framework=" + framework +
                ", types=" + types +
                ", schema='" + schema + '\'' +
                ", envs=" + envs +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final AgentRuntimeSchema agentRuntimeSchema;

        public Builder() {
            this.agentRuntimeSchema = new AgentRuntimeSchema();
        }

        public Builder name(final String name) {
            this.agentRuntimeSchema.setName(name);
            return this;
        }

        public Builder version(final String version) {
            this.agentRuntimeSchema.setVersion(version);
            return this;
        }

        public Builder description(final String description) {
            this.agentRuntimeSchema.setDescription(description);
            return this;
        }

        public Builder framework(final AgentFramework framework) {
            this.agentRuntimeSchema.setFramework(framework);
            return this;
        }

        public Builder envs(final List<Map<String, String>> envs) {
            this.agentRuntimeSchema.setEnvs(envs);
            return this;
        }

        public AgentRuntimeSchema build() {
            return this.agentRuntimeSchema;
        }
    }

}
