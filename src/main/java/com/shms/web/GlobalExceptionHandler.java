package com.shms.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled application error on {} {}", request.getMethod(), request.getRequestURI(), ex);
        model.addAttribute("status", 500);
        model.addAttribute("title", "Something went wrong");
        model.addAttribute("message", "We could not complete that request. Please try again.");
        return "error/error";
    }
}
