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

package com.alibaba.cloud.ai.agent.runtime.saa.agent;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;

import java.util.Map;

/**
 * Spring AI Alibaba Graph Agent Application define.
 *
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */
public class GraphAgent {

    public CompiledGraph graph() throws GraphStateException {

        return new StateGraph("Reverser ID Agent", () -> Map.of("id", new ReplaceStrategy()))

                .addNode("set_id", AsyncNodeAction.node_async(new SetIdNode()))
                .addNode("reverse_id", AsyncNodeAction.node_async(new ReverseIdNode()))

                .addEdge(StateGraph.START, "set_id")
                .addEdge("set_id", "reverse_id")
                .addEdge("reverse_id", StateGraph.END)

                .compile();
    }

    static class SetIdNode implements NodeAction {

        @Override
        public Map<String, Object> apply(final OverAllState state) throws Exception {

            String id = state.value("id", "");
            if (id.isEmpty()) {
                throw new RuntimeException("input id is null");
            }

            return Map.of("id", id);
        }
    }

    static class ReverseIdNode implements NodeAction {

        @Override
        public Map<String, Object> apply(final OverAllState state) throws Exception {

            String id = state.value("id", "");
            if (id.isEmpty()) {
                throw new RuntimeException("input id is null");
            }

            return Map.of("id", new StringBuilder(id).reverse().toString());
        }
    }

}
