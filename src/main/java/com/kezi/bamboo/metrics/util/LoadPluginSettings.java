package com.kezi.bamboo.metrics.util;

import com.atlassian.bamboo.crypto.instance.SecretEncryptionService;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v2.api.MetricsApi;
import com.kezi.bamboo.metrics.model.ServerConfig;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles loading of Plugin configuration in the global plugin state {@link PluginSettingsFactory}
 */
@BambooComponent
public class LoadPluginSettings {
    private final Logger log = LoggerFactory.getLogger(LoadPluginSettings.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final SecretEncryptionService secretEncryptionService;
    @Inject
    public LoadPluginSettings(@ComponentImport PluginSettingsFactory pluginSettingsFactory,
                              @ComponentImport TransactionTemplate transactionTemplate,
                              @ComponentImport SecretEncryptionService secretEncryptionService) {

        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.secretEncryptionService = secretEncryptionService;
    }


    /**
     * Loads the server currently loaded in the Global {@link PluginSettings} to use for the
     * specified Metric Server
     * @param settings-
     * @param secretEncryptionService-
     * @param serverConfig todo
     */
    public static void getApplicationProperties(PluginSettings settings,
                                                SecretEncryptionService secretEncryptionService,
                                                final ServerConfig serverConfig) {
            serverConfig.setServerName(settings.get(ServerConfig.class.getName()+"serverName").toString());
            serverConfig.setDescription(settings.get(ServerConfig.class.getName()+"description").toString());
            serverConfig.setApiKey(settings.get(ServerConfig.class.getName()+"apiKey").toString());
            serverConfig.setAppKey(settings.get(ServerConfig.class.getName()+"appKey").toString());

    }

    /**
     * Saves the Plugin setting which should be used in the global scope for this plugin.
     * @param pluginSettingsFactory Plugin setting factory
     * @param serverConfig object to be stored
     */
    public static void setActiveMetricServer(PluginSettingsFactory pluginSettingsFactory,
                                             final ServerConfig serverConfig){
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        settings.put(ServerConfig.class.getName() + "serverName", serverConfig.getServerName());
        settings.put(ServerConfig.class.getName() + "description", serverConfig.getDescription());
        settings.put(ServerConfig.class.getName() + "apiKey", serverConfig.getApiKey());
        settings.put(ServerConfig.class.getName() + "appKey", serverConfig.getAppKey());
        settings.put(ServerConfig.class.getName() + "serverId", String.valueOf(serverConfig.getId()));
        settings.put(ServerConfig.class.getName() + "enabled", String.valueOf(serverConfig.getEnabled()));
    }

    public MetricsApi getMetricsApi() {
        String API_KEY = "";
        String APP_KEY = "";
        try{
            ServerConfig serverConfig = transactionTemplate.execute(new TransactionCallback<ServerConfig>() {
                public ServerConfig doInTransaction() {
                    ServerConfig config = new ServerConfig();
                    getApplicationProperties(pluginSettingsFactory.createGlobalSettings(), secretEncryptionService,config);
                    return config;
                }
            });
            if (serverConfig != null) {
                log.info("Loaded the Metric server Configuration");
                API_KEY = serverConfig.getApiKey();
                APP_KEY = serverConfig.getAppKey();
            }
        }catch(Exception e){
            log.error("Failed to retrieve configuration", e);
            throw  new RuntimeException("Error retrieving configuration: " + e.getMessage());
        }
        ApiClient apiClient = new ApiClient();
        apiClient.addDefaultHeader("DD-API-KEY", secretEncryptionService.decrypt(API_KEY));
        apiClient.addDefaultHeader("DD-APP-KEY", secretEncryptionService.decrypt(APP_KEY));
        log.error("DD-API-KEY {}", secretEncryptionService.decrypt(API_KEY));
        log.error("DD-APP-KEY {}", secretEncryptionService.decrypt(APP_KEY));
        return new MetricsApi(apiClient);
    }
}
