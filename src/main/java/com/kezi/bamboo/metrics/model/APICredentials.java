package com.kezi.bamboo.metrics.model;

public class APICredentials {
    private String apiKey;
    private String appKey;
    public APICredentials(String apiKey, String appKey) {
        this.apiKey = apiKey;
        this.appKey = appKey;
    }
    public APICredentials() {}
    public String getApiKey() {
        return apiKey;
    }
    public String getAppKey() {
        return appKey;
    }
}
