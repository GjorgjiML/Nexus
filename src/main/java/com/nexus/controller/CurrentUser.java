package com.nexus.controller;

import com.nexus.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static UserPrincipal require() {
        UserPrincipal principal = optional();
        if (principal == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return principal;
    }

    public static UserPrincipal optional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        return null;
    }

    public static Long idOrNull() {
        UserPrincipal principal = optional();
        return principal != null ? principal.getId() : null;
    }
}
