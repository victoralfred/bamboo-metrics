package com.kezi.bamboo.metrics.impl.events;

import com.atlassian.bamboo.event.BuildDeletedEvent;
import com.atlassian.bamboo.event.ChainCreatedEvent;
import com.atlassian.bamboo.event.ChainDeletedEvent;
import com.atlassian.bamboo.event.ProjectCreatedEvent;
import com.atlassian.bamboo.v2.events.BuildCreatedEvent;
import com.atlassian.event.api.EventListener;
import com.datadog.api.client.v2.model.MetricIntakeType;
import com.kezi.bamboo.metrics.impl.datadog.SendMetricsToDatadog;
import com.kezi.bamboo.metrics.model.CustomBambooMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BambooEventsHandler {
    private static final Logger log = LoggerFactory.getLogger(BambooEventsHandler.class);
    private final SendMetricsToDatadog pushMetric;
    public BambooEventsHandler(SendMetricsToDatadog pushMetric) {
        this.pushMetric = pushMetric;
    }

    @EventListener
    public void onProjectCreatedEvent(ProjectCreatedEvent event) {
        CustomBambooMetrics metrics =
                new CustomBambooMetrics.BambooMetricsBuilder(
                        "bamboo.project.created",
                        MetricIntakeType.COUNT,
                        List.of("project:".concat(event.getProjectKey()))
                ).build();
        try{
            log.info("Project: {} created Event occurred at {}",event.getProjectKey(), event.getEventCreationTime());
            // Create the metric and push metric
            pushMetric.sendMetrics(pushMetric.sendMetrics(metrics));
        }catch(RuntimeException | ExecutionException | InterruptedException e){
            log.error(e.getMessage());
        }

    }

    @EventListener
    public void onPlanCreatedEvent(ChainCreatedEvent event) {
        //Occurs when a Plan is created
        CustomBambooMetrics metrics =
                new CustomBambooMetrics.BambooMetricsBuilder(
                        "bamboo.plan.created",
                        MetricIntakeType.COUNT,
                        List.of("plan:".concat(event.getPlanKey().getKey()))
                ).build();
        try{
            log.info("Plan: {} created Event occurred at {}",event.getPlanKey().getKey(), event.getEventCreationTime());
            pushMetric.sendMetrics(pushMetric.sendMetrics(metrics));
        }catch(RuntimeException | ExecutionException | InterruptedException e){
            log.error(e.getMessage());
        }
    }
    @EventListener
    public void onPlanDeletedEvent(ChainDeletedEvent event) {
        //Occurs when a Plan is deleted
        CustomBambooMetrics metrics =
                new CustomBambooMetrics.BambooMetricsBuilder(
                        "bamboo.plan.deleted",
                        MetricIntakeType.COUNT,
                        List.of("plan:".concat(event.getPlanKey().getKey()))
                ).build();
        try{
            log.info("Plan: {} deleted Event occurred at {}",event.getPlanKey().getKey(), event.getTimestamp());
            pushMetric.sendMetrics(pushMetric.sendMetrics(metrics));
        }catch(RuntimeException | ExecutionException | InterruptedException e){
            log.error(e.getMessage());
        }
    }
    @EventListener
    public void onJobCreatedEvent(BuildCreatedEvent event) {
                CustomBambooMetrics metrics =
                        new CustomBambooMetrics.BambooMetricsBuilder(
                                "bamboo.job.created",
                                MetricIntakeType.COUNT,
                                List.of("job:".concat(event.getPlanKey().getKey()))
                        ).build();
        try{
            log.info("Job: {} created Event occurred at {}",event.getPlanKey().getKey(), event.getTimestamp());
            pushMetric.sendMetrics(pushMetric.sendMetrics(metrics));
        }catch(RuntimeException | ExecutionException | InterruptedException e){
            log.error(e.getMessage());
        }
    }
    @EventListener
    public void onJobDeletedEvent(BuildDeletedEvent event) {
        CustomBambooMetrics metrics =
                new CustomBambooMetrics.BambooMetricsBuilder(
                        "bamboo.job.deleted ",
                        MetricIntakeType.COUNT,
                        List.of("job:".concat(event.getPlanKey().getKey()))
                )
                        .build();
        try{
            log.info("Job: {} deleted Event occurred at {}",event.getPlanKey().getKey(), event.getTimestamp());
            pushMetric.sendMetrics(pushMetric.sendMetrics(metrics));
        }catch(RuntimeException | ExecutionException | InterruptedException e){
            log.error(e.getMessage());
        }
    }

}
