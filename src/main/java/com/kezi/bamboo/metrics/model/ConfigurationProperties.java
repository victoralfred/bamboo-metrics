package com.kezi.bamboo.metrics.model;


public final class ConfigurationProperties {
    private int id;
    private String apiKey;
    private String appKey;
    private String serverName;
    private String description;
    private boolean isEnabled;
    public ConfigurationProperties() {}
    public ConfigurationProperties(Builder builder) {
        this.id = builder.id;
        this.apiKey = builder.apiKey;
        this.appKey = builder.appKey;
        this.serverName = builder.serverName;
        this.description = builder.description;
        this.isEnabled = builder.isEnabled;
    }
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setEnabled(boolean isActive) {
        this.isEnabled = isActive;
    }
    public boolean getEnabled() {
        return isEnabled;
    }
    public static class Builder{
        private int id;
        private String apiKey;
        private String appKey;
        private String serverName;
        private String description;
        private boolean isEnabled;
        public Builder setId(int id) {
            this.id = id;
            return this;
        }
        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }
        public Builder setServerName(String serverName) {
            this.serverName = serverName;
            return this;
        }
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        public Builder setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }
        public ConfigurationProperties build() {
            return new ConfigurationProperties(this);
        }
    }
}
