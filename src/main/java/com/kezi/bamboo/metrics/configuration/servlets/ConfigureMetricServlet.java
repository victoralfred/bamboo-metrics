package com.kezi.bamboo.metrics.configuration.servlets;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.kezi.bamboo.metrics.util.UserProfileService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static com.kezi.bamboo.metrics.MetricEnvConstants.METRIC_CONFIGURATION_TEMPLATE_FILE;

public class ConfigureMetricServlet extends HttpServlet {
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserProfileService userProfileService;
    private final ServletHelpers servletHelpers;
    public ConfigureMetricServlet(TemplateRenderer templateRenderer, UserProfileService userProfileService, ServletHelpers servletHelpers) {
        this.templateRenderer = templateRenderer;
        this.userProfileService = userProfileService;
        this.servletHelpers = servletHelpers;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!userProfileService.isAdmin()){
            servletHelpers.redirectToLogin(req, resp);
            return;
        }
        String pathInfo = req.getPathInfo(); // e.g., "/{id}/configuration"
        String id = null;
        if (pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if (parts.length > 1) {
                 id = parts[1]; // Extracts {id}
            }
        }
        // Find metric server by ID

        // Handle the rest of the request
        resp.setContentType("text/html;charset=utf-8");
        Map<String, Object> context = new HashMap<>();
        // Checking if user is an admin
        context.put("isAdmin", userProfileService.isAdmin());
        // Fetching the server list
//        List<ConfigurationProperties> serverList = fetchServerList.getServerList();
//        // If the first server name is not null, use the server list; otherwise, use a default list
//        context.put("serverList",
//                serverList != null && !serverList.isEmpty() && serverList.get(0).getServerName() != null
//                        ? serverList
//                        : Optional.empty()
//        );
        templateRenderer.render(METRIC_CONFIGURATION_TEMPLATE_FILE, context, resp.getWriter());
    }
}
