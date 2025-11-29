package com.shms.controller;

import com.shms.service.EmailService; // ✅ Make sure this matches your actual package name
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/contact")
    @ResponseBody
    public Map<String, String> handleContactForm(@RequestParam String name,
                                                 @RequestParam String email,
                                                 @RequestParam String message) {
        emailService.sendEmail(email, "New contact from " + name, message);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Your message has been sent successfully!");
        return response;
    }

}
