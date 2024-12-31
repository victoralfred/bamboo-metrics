package com.kezi.bamboo.metrics.model;

import net.java.ao.Entity;

public interface ServerProperties extends Entity {
    String getApiKey();
    void setApiKey(String apiKey);
    String getAppKey();
    void setAppKey(String appKey);
    String getServerName();
    void setServerName(String serverName);
    String getDescription();
    void setDescription(String description);
    boolean isEnabled();
    void setEnabled(boolean enabled);
}
