package com.example.shms.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class AuthController {

    @GetMapping({"/","/login"})
    public String login() {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        if (Objects.equals(username,"admin") && Objects.equals(password,"1234")) {
            return "dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }
}
