package com.kezi.bamboo.metrics.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.bamboo.crypto.instance.SecretEncryptionService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.v2.model.*;
import com.kezi.bamboo.metrics.impl.datadog.DatadogMetricPusher;
import com.kezi.bamboo.metrics.model.*;
import com.kezi.bamboo.metrics.util.UserProfileService;
import jakarta.inject.Inject;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.kezi.bamboo.metrics.util.LoadPluginSettings.setActiveMetricServer;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Path("/")
public class MetricConfigRestController {
    private static final Logger log = LoggerFactory.getLogger(MetricConfigRestController.class);
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TransactionTemplate transactionTemplate;
    @ComponentImport
    private final SecretEncryptionService secretEncryptionService;
    private final DatadogMetricPusher pushMetric;
    @ComponentImport
    private final ActiveObjects activeObjects;
    private final UserProfileService userProfileService;
    private final ConfigurableApplicationContext context;
    private final ServerStatus status=new ServerStatus();
    @Inject
    public MetricConfigRestController(PluginSettingsFactory pluginSettingsFactory,
                                      TransactionTemplate transactionTemplate,
                                      SecretEncryptionService secretEncryptionService,
                                      DatadogMetricPusher pushMetric, ActiveObjects activeObjects,
                                      UserProfileService userProfileService, ConfigurableApplicationContext context) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.secretEncryptionService = secretEncryptionService;
        this.pushMetric = pushMetric;
        this.activeObjects = checkNotNull(activeObjects);
        this.userProfileService = userProfileService;
        this.context = context;
    }

    /**
     * Test connection to the metric server
     * @param request
     * @return
     */
    @POST
    @Path("connection")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testServerConnection (final APITest apiTest, @Context HttpServletRequest request) {
        ensureAdminAccess();
        BambooMetrics metrics =
                new BambooMetrics.BambooMetricsBuilder(
                        "bamboo.api.test",
                        MetricIntakeType.COUNT,
                        null
                ).build();
        try{
            log.info("Metrics server connection test initiated");
            testApiCredentialsCanAccessServer(apiTest.getApiKey(), apiTest.getAppKey(),metrics);
            status.setMessage("API successful");
            status.setPassed(true);
            log.info("Metrics server connection passed");
        } catch (RuntimeException | ExecutionException | InterruptedException e){
            log.error("Metrics server connection test failed", e);
            status.setMessage("Metric server connection test failed with a runtime exception - "+e.getMessage());
            status.setPassed(false);
        }
        return Response.ok(status).build();
    }

    /**
     * Add a new server to the servers configuration in db
     * @param serverConfig
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addConfiguration (final ServerConfig serverConfig,
                                      @Context HttpServletRequest request) {
        ensureAdminAccess();
        try{
            transactionTemplate.execute(new TransactionCallback<ServerProperties>() {
                public ServerProperties doInTransaction() {
                    ServerProperties properties
                            = activeObjects.create(ServerProperties.class);
                    properties.setServerName(serverConfig.getServerName());
                    properties.setDescription(serverConfig.getDescription());
                    properties.setApiKey(secretEncryptionService.encrypt(serverConfig.getApiKey()));
                    properties.setAppKey(secretEncryptionService.encrypt(serverConfig.getAppKey()));
                    properties.setEnabled(false);
                    properties.save();
                    return properties;
                }
            });
            status.setMessage("Added "+ serverConfig.getServerName()+ " successfully!");
            status.setPassed(true);
            log.info("Added new metric server configuration:  {}", serverConfig.getServerName());
        }catch(Exception e){
            log.error("Failed to add configuration", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Configuration could not be saved: " + e.getMessage())
                    .build();
        }
        // Todo: return a well formated message
        return Response.ok(status).build();
    }

    /**
     * Set the server to use for metric reporting
     * @param request -
     * @return -
     * Todo - complete the docs
     */
    @POST
    @Path("default/server/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDefaultMetricServer(@PathParam("id") final int id, @Context HttpServletRequest request) {
        ensureAdminAccess();
        try {
            transactionTemplate.execute(() -> {
                ServerProperties serverProperties = fetchServerPropertiesById(id);
                ServerConfig serverConfig = mapToServerConfig(serverProperties);
                // Store the plugin in global plugin state
                setActiveMetricServer(pluginSettingsFactory, serverConfig);
                // Update the database and set only server with the given id to true
                for( ServerProperties server: activeObjects.find(ServerProperties.class)){
                    server.setEnabled(server.getID() == id);
                    server.save();
                }
                log.info(serverConfig.toString());
                return null; // TransactionTemplate requires a return value
            });
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Failed to set default metric server for ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to set default metric server: " + e.getMessage())
                    .build();
        }
    }

    private void ensureAdminAccess() {
        Optional<String> username = userProfileService.getUsername();
        if (username.isEmpty() || !userProfileService.isAdmin()) {
            throw new WebApplicationException(new Throwable("Unauthorized"), Response.Status.UNAUTHORIZED);
        }
    }
    private ServerProperties fetchServerPropertiesById(int id) {
        Query query = Query.select().where("ID = ?", id);
        return Arrays.stream(activeObjects.find(ServerProperties.class, query))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No ServerProperties found with ID: " + id));
    }
    private ServerConfig mapToServerConfig(ServerProperties properties) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setServerName(properties.getServerName());
        serverConfig.setDescription(properties.getDescription());
        serverConfig.setApiKey(properties.getApiKey());
        serverConfig.setAppKey(properties.getAppKey());
        serverConfig.setEnabled(properties.isEnabled());
        return serverConfig;
    }

    private void testApiCredentialsCanAccessServer(final String apiKey, final String appKey, BambooMetrics metrics) throws ExecutionException, InterruptedException {
        ApiClient apiClient = new ApiClient();
        apiClient.addDefaultHeader("DD-API-KEY", apiKey);
        apiClient.addDefaultHeader("DD-APP-KEY", appKey);
//
    }
}
