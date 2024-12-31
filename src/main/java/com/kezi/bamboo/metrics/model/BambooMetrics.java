package com.kezi.bamboo.metrics.model;

import com.datadog.api.client.v2.model.MetricIntakeType;

import java.util.Arrays;
import java.util.List;

/**
 * Creates a model for Metrics to be sent to the metric server
 */
public class BambooMetrics {
    private final String metricName;
    private final Double metricValue;
    private final String metricUnit;
    private final MetricIntakeType metricType;
    private final List<String> metricTags;

    public BambooMetrics(BambooMetricsBuilder builder) {
        this.metricName = builder.metricName;
        this.metricValue = builder.metricValue;
        this.metricUnit = builder.metricUnit;
        this.metricType = builder.metricType;
        this.metricTags = builder.metricTags;
    }

    public String getMetricName() {
        return metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public MetricIntakeType getMetricType() {
        return metricType;
    }

    public List<String> getMetricTags() {
        return metricTags;
    }

    @Override
    public String toString() {
        return "BambooMetrics{" +
                "metricName='" + metricName + '\'' +
                ", metricValue=" + metricValue +
                ", metricUnit='" + metricUnit + '\'' +
                ", metricType=" + metricType +
                ", metricTags=" + metricTags +
                '}';
    }

    public static class BambooMetricsBuilder{
        private final String metricName;
        private Double metricValue;
        private String metricUnit;
        private final MetricIntakeType metricType;
        private final List<String> metricTags;

        public BambooMetricsBuilder(String metricName, MetricIntakeType metricType, List<String> metricTags){
            this.metricName = metricName;
            this.metricType = metricType;
            this.metricTags = metricTags;
        }

        public BambooMetricsBuilder metricValue(Double metricValue) {
            this.metricValue = metricValue;
            return this;
        }
        public BambooMetricsBuilder metricUnit(String metricUnit) {
            this.metricUnit = metricUnit;
            return this;
        }

        public BambooMetrics build(){
            return new BambooMetrics(this);
        }
    }
}
