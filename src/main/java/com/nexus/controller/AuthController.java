package com.nexus.controller;

import com.nexus.dto.LoginRequest;
import com.nexus.dto.RegisterRequest;
import com.nexus.exception.BadRequestException;
import com.nexus.security.CookieUtil;
import com.nexus.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (CurrentUser.optional() != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            String token = authService.login(request);
            cookieUtil.addAuthCookie(response, token);
            return "redirect:/";
        } catch (AuthenticationException ex) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (CurrentUser.optional() != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.register(request);
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(request.getUsername());
            loginRequest.setPassword(request.getPassword());
            String token = authService.login(loginRequest);
            cookieUtil.addAuthCookie(response, token);
            redirectAttributes.addFlashAttribute("success", "Welcome to Nexus!");
            return "redirect:/";
        } catch (BadRequestException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        cookieUtil.clearAuthCookie(response);
        return "redirect:/login";
    }
}
