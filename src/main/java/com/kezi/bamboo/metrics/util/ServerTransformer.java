package com.kezi.bamboo.metrics.util;

import com.kezi.bamboo.metrics.model.ServerConfig;
import com.kezi.bamboo.metrics.model.ServerProperties;
import org.springframework.stereotype.Component;

import java.util.function.Function;
@Component
public class ServerTransformer implements Function<ServerProperties, ServerConfig> {
    @Override
    public ServerConfig apply(ServerProperties serverConfig) {
        ServerConfig config = new ServerConfig();
        config.setId(serverConfig.getID());
        config.setServerName(serverConfig.getServerName());
        config.setDescription(serverConfig.getDescription());
        config.setApiKey(serverConfig.getApiKey());
        config.setAppKey(serverConfig.getAppKey());
        config.setEnabled(serverConfig.isEnabled());
        return config;
    }
}
