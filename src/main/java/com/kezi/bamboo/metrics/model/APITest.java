package com.kezi.bamboo.metrics.model;

public class APITest {
    private String apiKey;
    private String appKey;
    public APITest(String apiKey, String appKey) {
        this.apiKey = apiKey;
        this.appKey = appKey;
    }
    public APITest() {}
    public String getApiKey() {
        return apiKey;
    }
    public String getAppKey() {
        return appKey;
    }
}
