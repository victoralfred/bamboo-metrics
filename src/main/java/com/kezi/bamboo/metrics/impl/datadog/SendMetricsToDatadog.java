package com.kezi.bamboo.metrics.impl.datadog;


import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.datadog.api.client.v2.api.MetricsApi;
import com.datadog.api.client.v2.model.MetricPayload;
import com.datadog.api.client.v2.model.MetricPoint;
import com.datadog.api.client.v2.model.MetricSeries;
import com.kezi.bamboo.metrics.api.PushMetrics;
import com.kezi.bamboo.metrics.util.LoadPluginSettings;
import com.kezi.bamboo.metrics.model.CustomBambooMetrics;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@BambooComponent
public class SendMetricsToDatadog implements PushMetrics {
    private final Logger log = LoggerFactory.getLogger(SendMetricsToDatadog.class);
    private final LoadPluginSettings loadPluginSettings;
    @Inject
    public SendMetricsToDatadog(LoadPluginSettings metricFactory) {
        this.loadPluginSettings = metricFactory;
    }
    @Override
    public MetricPayload sendMetrics(CustomBambooMetrics metrics) throws ExecutionException, InterruptedException {
        return new MetricPayload()
                .series(Collections.singletonList(new MetricSeries().metric(metrics.getMetricName())
                    .type(metrics.getMetricType())
                    .points(Collections.singletonList(new MetricPoint().timestamp(OffsetDateTime.now()
                           .toInstant().getEpochSecond()).value(metrics.getMetricValue())
                    )).tags(metrics.getMetricTags()))
                );
    }
    @Override
    public void sendMetrics(MetricPayload payload) throws ExecutionException, InterruptedException {
        MetricsApi api = loadPluginSettings.getMetricsApi();
        api.submitMetricsAsync(payload).get();
    }

}
