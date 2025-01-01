package com.kezi.bamboo.metrics.util;

import com.kezi.bamboo.metrics.model.ConfigurationProperties;
import com.kezi.bamboo.metrics.model.ServerProperties;
import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class ServerTransformer implements Function<ServerProperties, ConfigurationProperties> {
    @Override
    public ConfigurationProperties apply(ServerProperties serverConfig) {
        return new ConfigurationProperties.Builder()
                .setId(serverConfig.getID())
                .setServerName(serverConfig.getServerName())
                .setDescription(serverConfig.getDescription())
                .setApiKey(serverConfig.getApiKey())
                .setAppKey(serverConfig.getAppKey())
                .setEnabled(serverConfig.isEnabled())
                .build();
    }
}
