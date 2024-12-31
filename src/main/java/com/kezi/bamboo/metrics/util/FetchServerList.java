package com.kezi.bamboo.metrics.util;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.kezi.bamboo.metrics.model.ServerConfig;
import com.kezi.bamboo.metrics.model.ServerProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@BambooComponent
public class FetchServerList {
    @ComponentImport
    private final ActiveObjects activeObjects;
    @ComponentImport
    private final TransactionTemplate transactionTemplate;
    private final ServerTransformer serverTransformer;
    public FetchServerList(ActiveObjects activeObjects, TransactionTemplate transactionTemplate, ServerTransformer serverTransformer) {
        this.activeObjects = activeObjects;
        this.transactionTemplate = transactionTemplate;
        this.serverTransformer = serverTransformer;
    }
    public List<ServerConfig> getServerList() {
        return transactionTemplate.execute((TransactionCallback<List<ServerConfig>>) () -> {
            List<ServerConfig> serverConfigs = new ArrayList<>(); // Use plural for clarity
            for (ServerProperties serverProperties : activeObjects.find(ServerProperties.class)) {
                ServerConfig config = serverTransformer.apply(serverProperties);
                serverConfigs.add(config);
            }
            if (serverConfigs.isEmpty()) {
                return Collections.emptyList();
            }
            return serverConfigs;
        });
    }
}
