package com.kezi.bamboo.metrics.util;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

@BambooComponent
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    private final UserManager userManager;
    public UserProfileService(@ComponentImport UserManager userManager) {
        this.userManager = userManager;
    }
    public boolean isAdmin() {
        Optional<UserProfile> username = Optional.ofNullable(userManager.getRemoteUser());
        return username.isPresent() && userManager.isSystemAdmin(username.get().getUserKey());
    }
    public Optional<String> getUsername() {
        return Optional.ofNullable(Objects.requireNonNull(userManager.getRemoteUser()).getUsername());
    }

}
