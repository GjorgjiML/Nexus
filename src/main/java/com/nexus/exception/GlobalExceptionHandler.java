package com.nexus.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(NotFoundException ex, HttpServletRequest request, Model model) {
        if (wantsJson(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleForbidden(Exception ex, HttpServletRequest request, Model model) {
        if (wantsJson(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Forbidden"));
        }
        model.addAttribute("message", ex.getMessage() != null ? ex.getMessage() : "You do not have permission to do that.");
        return "error/403";
    }

    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequest(BadRequestException ex, HttpServletRequest request, Model model) {
        if (wantsJson(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", 400);
        return new ModelAndView("error/500", model.asMap(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUpload(MaxUploadSizeExceededException ex, HttpServletRequest request, Model model) {
        String message = "File is too large. Maximum size is 5MB.";
        if (wantsJson(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
        model.addAttribute("message", message);
        model.addAttribute("status", 400);
        return new ModelAndView("error/500", model.asMap(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResource(NoResourceFoundException ex, Model model) {
        model.addAttribute("message", "Page not found");
        return "error/404";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())
                && ex.getSupportedHttpMethods() != null
                && ex.getSupportedHttpMethods().contains(org.springframework.http.HttpMethod.GET)) {
            return new RedirectView(request.getRequestURI());
        }
        if (wantsJson(request)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(Map.of("error", "Method not allowed"));
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        mav.addObject("message", "Method not allowed");
        mav.addObject("status", 405);
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneric(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled error on {}", request.getRequestURI(), ex);
        if (wantsJson(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
        model.addAttribute("message", "Something went wrong. Please try again later.");
        return "error/500";
    }

    private boolean wantsJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xhr = request.getHeader("X-Requested-With");
        return (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE))
                || "XMLHttpRequest".equalsIgnoreCase(xhr)
                || request.getRequestURI().startsWith("/api/");
    }
}
