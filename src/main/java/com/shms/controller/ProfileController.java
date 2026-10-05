package com.shms.controller;

import com.shms.entity.User;
import com.shms.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public ProfileController(UserRepository users, PasswordEncoder encoder){this.users=users;this.encoder=encoder;}

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        Object value=session.getAttribute("currentUser");
        if(!(value instanceof User user)) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@RequestParam String firstName, @RequestParam String lastName,
                         @RequestParam(required=false) String phone,
                         @RequestParam(required=false) String newPassword,
                         HttpSession session, RedirectAttributes ra){
        Object value=session.getAttribute("currentUser");
        if(!(value instanceof User user)) return "redirect:/login";
        user.setFirstName(firstName); user.setLastName(lastName); user.setPhone(phone);
        if(newPassword != null && !newPassword.isBlank()) user.setPassword(encoder.encode(newPassword));
        users.save(user); session.setAttribute("currentUser", user);
        ra.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
    }
}
