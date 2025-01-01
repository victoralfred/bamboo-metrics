package com.kezi.bamboo.metrics.api;

import com.datadog.api.client.v2.model.MetricPayload;
import com.kezi.bamboo.metrics.model.CustomBambooMetrics;

import java.util.concurrent.ExecutionException;
public interface PushMetrics {
    /**
     * Prepare a message of metrics of type COUNT or GAUGE
     * @param metrics - Bamboo event constructed message with metric {@link CustomBambooMetrics}
     * @return - Datadog metric payload{@link MetricPayload}
     * @throws ExecutionException -Throws exception is operation failed
     * @throws InterruptedException - Throws exception if operation is interrupted
     */
    MetricPayload sendMetrics(CustomBambooMetrics metrics) throws ExecutionException, InterruptedException;

    /**
     * Sends the message to datadog using the API
     * @param payload  Bamboo event constructed message with metric
     * @throws ExecutionException -Throws exception is operation failed
     * @throws InterruptedException - Throws exception if operation is interrupted
     */
    void sendMetrics(MetricPayload payload) throws ExecutionException, InterruptedException;
}
