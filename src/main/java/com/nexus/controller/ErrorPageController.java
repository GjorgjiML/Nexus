package com.nexus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorPageController {

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("message", "The page you are looking for does not exist.");
        return "error/404";
    }

    @GetMapping("/403")
    public String forbidden(Model model) {
        model.addAttribute("message", "You do not have permission to access this page.");
        return "error/403";
    }

    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("message", "Something went wrong on our side.");
        return "error/500";
    }
}
