package com.kezi.bamboo.metrics.impl.events;

import com.atlassian.bamboo.event.BuildFinishedEvent;
import com.atlassian.bamboo.event.ChainCompletedEvent;
import com.atlassian.event.api.EventListener;
import com.kezi.bamboo.metrics.impl.datadog.DatadogMetricPusher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class BuildEvents{
    private static final Logger log = LoggerFactory.getLogger(BuildEvents.class);
    private final DatadogMetricPusher pushMetric;

    public BuildEvents(DatadogMetricPusher pushMetric) {
        this.pushMetric = pushMetric;
    }
    @EventListener
    public void onBuildFinishedEvent(ChainCompletedEvent event) {
        ChainCompletedEvent chainCompletedEvent = (ChainCompletedEvent) event;

        log.info("Build completed {}", chainCompletedEvent.getBuildContext());

    }
}
