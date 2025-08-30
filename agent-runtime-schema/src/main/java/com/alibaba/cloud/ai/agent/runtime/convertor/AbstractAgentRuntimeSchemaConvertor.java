package com.alibaba.cloud.ai.agent.runtime.convertor;

import com.alibaba.cloud.ai.agent.runtime.AgentRuntimeSchema;
import com.alibaba.cloud.ai.agent.runtime.common.AgentRuntimeException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Objects;

import static com.alibaba.cloud.ai.agent.runtime.AgentConfigTypes.*;

/**
 * Abstract base class for Agent Runtime Schema Convertors.
 * Provides common behaviors for all convertors.
 *
 * @author yuluo
 */
public abstract class AbstractAgentRuntimeSchemaConvertor implements IAgentRuntimeSchemaConvertor {

    /**
     * Common resource path, can be used by subclasses.
     */
    protected String resourcePath;

    /**
     * Common file reader, can be used by subclasses.
     */
    protected Reader fileReader;

    public AbstractAgentRuntimeSchemaConvertor(String resourcePath) {

        assert Objects.nonNull(resourcePath) && !resourcePath.isEmpty() : "Resource path must not be null or empty";
        this.resourcePath = resourcePath;

        try {
            this.fileReader = new FileReader(this.resourcePath);
        } catch (FileNotFoundException e) {
            throw new AgentRuntimeException(e);
        }
    }

    public static AbstractAgentRuntimeSchemaConvertor createConvertor(String resourcePath) {

        String ext = null;
        if (resourcePath.lastIndexOf('.') != -1) {
            ext = resourcePath.substring(resourcePath.lastIndexOf('.') + 1).toLowerCase();
        }
        if (Objects.isNull(ext) || ext.isEmpty()) {
            throw new IllegalArgumentException("Resource path must have a valid file extension: " + resourcePath);
        }

        return switch (ext) {
            case "json" -> new JSONAgentRuntimeSchemaConvertor(resourcePath);
            case "yaml", "yml" -> new YamlAgentRuntimeSchemaConvertor(resourcePath);
            case "properties" -> new PropertiesAgentRuntimeSchemaConvertor(resourcePath);
            default -> throw new IllegalArgumentException("Unsupported resource type: " + resourcePath);
        };
    }

    @Override
    public AgentRuntimeSchema convert() {

        AbstractAgentRuntimeSchemaConvertor convertor = createConvertor(resourcePath);
        return convertor.doConvert();
    }

    /**
     * Actual convert logic to be implemented by subclasses.
     */
    protected abstract AgentRuntimeSchema doConvert();

}
