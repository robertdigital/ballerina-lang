/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.net.http.mock.nonlistening;

import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Service;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.HTTPServicesRegistry;
import org.ballerinalang.net.http.HttpConstants;
import org.ballerinalang.net.http.WebSocketConstants;
import org.ballerinalang.net.http.WebSocketService;
import org.ballerinalang.net.http.WebSocketServicesRegistry;

import static org.ballerinalang.net.http.HttpConstants.MOCK_SERVER;

/**
 * Get the ID of the connection.
 *
 * @since 0.966
 */

@BallerinaFunction(
        orgName = "ballerina", packageName = "http",
        functionName = "register",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = MOCK_SERVER,
                structPackage = "ballerina.http"),
        args = {@Argument(name = "serviceType", type = TypeKind.TYPEDESC),
                @Argument(name = "annotationData", type = TypeKind.MAP)},
        isPublic = true
)
public class NonListeningRegister extends org.ballerinalang.net.http.serviceendpoint.Register {

    @Override
    public void execute(Context context) {
        Service service = BLangConnectorSPIUtil.getServiceRegistered(context);
        Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);

        HTTPServicesRegistry httpServicesRegistry = getHttpServicesRegistry(serviceEndpoint);
        WebSocketServicesRegistry webSocketServicesRegistry = getWebSocketServicesRegistry(serviceEndpoint);

        // TODO: Check if this is valid.
        // TODO: In HTTP to WebSocket upgrade register WebSocket service in WebSocketServiceRegistry
        if (HttpConstants.HTTP_SERVICE_ENDPOINT_NAME.equals(service.getEndpointName())) {
            httpServicesRegistry.registerService(service);
        }
        if (WebSocketConstants.WEBSOCKET_ENDPOINT_NAME.equals(service.getEndpointName())) {
            WebSocketService webSocketService = new WebSocketService(service);
            webSocketServicesRegistry.registerService(webSocketService);
        }
        context.setReturnValues();
    }
}
