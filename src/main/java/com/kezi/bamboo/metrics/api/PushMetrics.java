package com.kezi.bamboo.metrics.api;

import com.kezi.bamboo.metrics.model.BambooMetrics;

import java.util.concurrent.ExecutionException;
public interface PushMetrics {
    /**
     * This method is the Handler for pushing metrics sent to the Metrics server
     * using HTTP method
     * @param metrics -
     */
    void sendMetrics(BambooMetrics metrics) throws ExecutionException, InterruptedException;
}
