package com.nexus.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${nexus.cookie.name}")
    private String cookieName;

    @Value("${nexus.cookie.secure}")
    private boolean secure;

    @Value("${nexus.cookie.same-site}")
    private String sameSite;

    @Value("${nexus.jwt.expiration-ms}")
    private long expirationMs;

    public void addAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationMs / 1000));
        cookie.setAttribute("SameSite", sameSite);
        response.addCookie(cookie);
    }

    public void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", sameSite);
        response.addCookie(cookie);
    }
}
