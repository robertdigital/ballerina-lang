/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.messaging.kafka.nativeimpl.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.KafkaException;
import org.ballerinalang.jvm.scheduling.Scheduler;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.messaging.kafka.observability.KafkaMetricsUtil;
import org.ballerinalang.messaging.kafka.observability.KafkaObservabilityConstants;
import org.ballerinalang.messaging.kafka.observability.KafkaTracingUtil;

import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.NATIVE_PRODUCER;
import static org.ballerinalang.messaging.kafka.utils.KafkaConstants.PRODUCER_ERROR;
import static org.ballerinalang.messaging.kafka.utils.KafkaUtils.createKafkaError;

/**
 * Native action closes producer instance.
 */
public class Close {

    public static Object close(ObjectValue producerObject) {
        KafkaTracingUtil.traceResourceInvocation(Scheduler.getStrand(), producerObject);
        KafkaProducer kafkaProducer = (KafkaProducer) producerObject.getNativeData(NATIVE_PRODUCER);
        try {
            kafkaProducer.close();
            KafkaMetricsUtil.reportProducerClose(producerObject);
        } catch (KafkaException e) {
            KafkaMetricsUtil.reportProducerError(producerObject, KafkaObservabilityConstants.ERROR_TYPE_CLOSE);
            return createKafkaError("Failed to close the Kafka producer: " + e.getMessage(), PRODUCER_ERROR);
        }

        return null;
    }
}
