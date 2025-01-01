package com.kezi.bamboo.metrics.configuration.servlets;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.kezi.bamboo.metrics.model.ConfigurationProperties;
import com.kezi.bamboo.metrics.util.FetchServerList;
import com.kezi.bamboo.metrics.util.UserProfileService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kezi.bamboo.metrics.MetricEnvConstants.METRIC_ADMIN_TEMPLATE_FILE;

public class AddMetricServerServlet extends HttpServlet {

    @ComponentImport
    private final TemplateRenderer templateRenderer;
    private final UserProfileService userProfileService;
    private final FetchServerList fetchServerList;
    private final ServletHelpers servletHelpers;
    public AddMetricServerServlet(UserProfileService userProfileService,
                                  TemplateRenderer templateRenderer,
                                  FetchServerList fetchServerList, ServletHelpers servletHelpers)
    {

        this.userProfileService = userProfileService;
        this.templateRenderer = templateRenderer;
        this.fetchServerList = fetchServerList;

        this.servletHelpers = servletHelpers;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!userProfileService.isAdmin()){
            servletHelpers.redirectToLogin(req, resp);
            return;
        }
        resp.setContentType("text/html;charset=utf-8");
        Map<String, Object> context = new HashMap<>();
        // Checking if user is an admin
        context.put("isAdmin", userProfileService.isAdmin());
        // Fetching the server list
        List<ConfigurationProperties> serverList = fetchServerList.getServerList();
        // If the first server name is not null, use the server list; otherwise, use a default list
        context.put("serverList",
                serverList != null && !serverList.isEmpty() && serverList.get(0).getServerName() != null
                        ? serverList
                        : Optional.empty()
        );
        templateRenderer.render(METRIC_ADMIN_TEMPLATE_FILE, context, resp.getWriter());
    }


}
