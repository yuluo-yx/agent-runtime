package com.alibaba.cloud.ai.agent.runtime.convertor.convertot;

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.alibaba.cloud.ai.agent.runtime.convertor.AbstractAgentRuntimeSchemaConvertor;
import com.alibaba.cloud.ai.agent.runtime.convertor.IAgentRuntimeSchemaConvertor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Map;

class AgentRuntimeSchemaConvertorTest {

    private String getResourcePath(String resourceName) {

        URL url = getClass().getClassLoader().getResource(resourceName);
        Assertions.assertNotNull(url, "Resource not found: " + resourceName);

        return url.getPath();
    }

    @Test
    void testJsonConvertor() {

        String path = getResourcePath("template/runtime.config.json");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }

    @Test
    void testYamlConvertor() {

        String path = getResourcePath("template/runtime.config.yaml");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }

    @Test
    void testPropertiesConvertor() {

        String path = getResourcePath("template/runtime.config.properties");

        IAgentRuntimeSchemaConvertor convertor = AbstractAgentRuntimeSchemaConvertor.createConvertor(path);
        AgentRuntimeSchema schema = convertor.convert();
        Assertions.assertEquals("MyAgent", schema.getName());
        Assertions.assertEquals("1.0.0", schema.getVersion());
        Assertions.assertEquals("A demo agent for testing.", schema.getDescription());
        Assertions.assertEquals("Spring AI Alibaba Graph", schema.getFramework());
        List<Map<String, String>> envs = schema.getEnvs();
        Assertions.assertEquals("/usr/lib/jvm/java-17", envs.get(0).get("JAVA_HOME"));
        Assertions.assertEquals("test", envs.get(1).get("AGENT_MODE"));
    }
}
