/*
 * Copyright (c) 2008-2023, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.internal.nio.Connection;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.ContainsKeyOperation;
import com.hazelcast.security.SecurityInterceptorConstants;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.spi.impl.operationservice.Operation;

import java.security.Permission;

public class ReplicatedMapContainsKeyMessageTask
        extends AbstractPartitionMessageTask<ReplicatedMapContainsKeyCodec.RequestParameters> {

    public ReplicatedMapContainsKeyMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ContainsKeyOperation(parameters.name, parameters.key);
    }

    @Override
    protected ReplicatedMapContainsKeyCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapContainsKeyCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ReplicatedMapContainsKeyCodec.encodeResponse((Boolean) response);
    }

    @Override
    public String getServiceName() {
        return ReplicatedMapService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(parameters.name, ActionConstants.ACTION_READ);
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public String getMethodName() {
        return SecurityInterceptorConstants.CONTAINS_KEY;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{parameters.key};
    }

    @Override
    protected String getUserCodeNamespace() {
        // This task is not Namespace-aware so it doesn't matter
        return null;
    }
}
