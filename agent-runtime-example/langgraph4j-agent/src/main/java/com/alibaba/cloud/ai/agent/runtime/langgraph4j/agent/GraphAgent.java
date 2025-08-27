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

package com.alibaba.cloud.ai.agent.runtime.langgraph4j.agent;

import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.action.NodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.bsc.langgraph4j.langchain4j.serializer.std.LC4jStateSerializer;

import java.util.Map;
import java.util.Optional;

/**
 * LangGraph4j Agent Application.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

public class GraphAgent {

    public CompiledGraph<State> graph() throws GraphStateException {

        return new StateGraph<>(State.serializer())
                .addNode("set_id", AsyncNodeAction.node_async(new SetIdNode()))
                .addNode("reverse_id", AsyncNodeAction.node_async(new ReverseIdNode()))

                .addEdge(StateGraph.START, "set_id")
                .addEdge("set_id", "reverse_id")
                .addEdge("reverse_id", StateGraph.END)

                .compile();
    }

    static class SetIdNode implements NodeAction<State> {

        @Override
        public Map<String, Object> apply(final State state) throws Exception {

            String id = state.data().get("id").toString();
            if (id.isEmpty()) {
                throw new RuntimeException("input id is null");
            }

            return Map.of("id", id);
        }
    }

    static class ReverseIdNode implements NodeAction<State> {

        @Override
        public Map<String, Object> apply(final State state) throws Exception {

            if (state.next().get().toString().isEmpty()) {
                throw new RuntimeException("input id is null");
            }

            return Map.of("id", new StringBuilder(state.next().get().toString()).reverse().toString());
        }
    }

    static class State extends MessagesState<String> {

        State(final Map<String, Object> initData) {
            super(initData);
        }

        public Optional<String> next() {
            return this.value("id");
        }

        public static StateSerializer<State> serializer() {

            return new LC4jStateSerializer<>(State::new);
        }
    }

}
