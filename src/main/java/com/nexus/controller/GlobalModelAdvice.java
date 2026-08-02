package com.nexus.controller;

import com.nexus.security.UserPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUsername")
    public String currentUsername() {
        UserPrincipal principal = CurrentUser.optional();
        return principal != null ? principal.getUsername() : null;
    }

    @ModelAttribute("currentAvatarPath")
    public String currentAvatarPath() {
        UserPrincipal principal = CurrentUser.optional();
        return principal != null ? principal.getAvatarPath() : null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        return CurrentUser.optional() != null;
    }
}
