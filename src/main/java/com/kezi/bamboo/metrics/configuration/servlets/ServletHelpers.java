package com.kezi.bamboo.metrics.configuration.servlets;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
@Component
public final class ServletHelpers {
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    public ServletHelpers(LoginUriProvider loginUriProvider) {
        this.loginUriProvider = loginUriProvider;
    }
    public void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(loginUriProvider.getLoginUri(geUri(req)).toASCIIString());
    }
    private URI geUri(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if(req.getQueryString() != null) {
            uri += "?" + req.getQueryString();
            uri += req.getQueryString();
        }
        return URI.create(uri);
    }
}
