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
import com.datadog.api.client.v2.model.MetricPayload;
import com.kezi.bamboo.metrics.impl.datadog.SendMetricsToDatadog;
import com.kezi.bamboo.metrics.model.CustomBambooMetrics;
import com.kezi.bamboo.metrics.model.ConfigurationProperties;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

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
     * @param configurationProperties todo
     */
    public static void getApplicationProperties(PluginSettings settings,
                                                SecretEncryptionService secretEncryptionService,
                                                final ConfigurationProperties configurationProperties) {
            configurationProperties.setServerName(settings.get(ConfigurationProperties.class.getName()+"serverName").toString());
            configurationProperties.setDescription(settings.get(ConfigurationProperties.class.getName()+"description").toString());
            configurationProperties.setApiKey(settings.get(ConfigurationProperties.class.getName()+"apiKey").toString());
            configurationProperties.setAppKey(settings.get(ConfigurationProperties.class.getName()+"appKey").toString());

    }

    /**
     * Saves the Plugin setting which should be used in the global scope for this plugin.
     * @param pluginSettingsFactory Plugin setting factory
     * @param configurationProperties object to be stored
     */
    public static void setActiveMetricServer(PluginSettingsFactory pluginSettingsFactory,
                                             final ConfigurationProperties configurationProperties){
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        settings.put(ConfigurationProperties.class.getName() + "serverName", configurationProperties.getServerName());
        settings.put(ConfigurationProperties.class.getName() + "description", configurationProperties.getDescription());
        settings.put(ConfigurationProperties.class.getName() + "apiKey", configurationProperties.getApiKey());
        settings.put(ConfigurationProperties.class.getName() + "appKey", configurationProperties.getAppKey());
        settings.put(ConfigurationProperties.class.getName() + "serverId", String.valueOf(configurationProperties.getId()));
        settings.put(ConfigurationProperties.class.getName() + "enabled", String.valueOf(configurationProperties.getEnabled()));
    }

    public MetricsApi getMetricsApi() {
        String API_KEY = "";
        String APP_KEY = "";
        try{
            ConfigurationProperties configurationProperties = transactionTemplate.execute(new TransactionCallback<ConfigurationProperties>() {
                public ConfigurationProperties doInTransaction() {
                    ConfigurationProperties config = new ConfigurationProperties();
                    getApplicationProperties(pluginSettingsFactory.createGlobalSettings(), secretEncryptionService,config);
                    return config;
                }
            });
            if (configurationProperties != null) {
                log.info("Loaded the Metric server Configuration");
                API_KEY = configurationProperties.getApiKey();
                APP_KEY = configurationProperties.getAppKey();
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

    /**
     * Utility method to test connection to metric server
     * @param apiKey - API Key
     * @param appKey - APP key
     * @param metrics -
     * @param pushMetric -
     * @throws ExecutionException-
     * @throws InterruptedException-
     */
    public static void testApiCredentialsCanAccessServer(final String apiKey,
                                                         final String appKey,
                                                         final CustomBambooMetrics metrics,
                                                         final SendMetricsToDatadog pushMetric
                                                         )
            throws ExecutionException, InterruptedException {
        ApiClient apiClient = new ApiClient();
        apiClient.addDefaultHeader("DD-API-KEY", apiKey);
        apiClient.addDefaultHeader("DD-APP-KEY", appKey);
        MetricPayload metricPayload = pushMetric.sendMetrics(metrics);
        MetricsApi metricsApi = new MetricsApi(apiClient);
        metricsApi.submitMetricsAsync(metricPayload).get();
    }
}
