
package com.shms.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {
    @GetMapping({"/","/home"})
    public String home(){
        return "home";
    }

//    @GetMapping("/about")
//    public String about() {
//        return "about";
//    }





//    public String dashboard(HttpSession session, Model model) {
//        Object u = session.getAttribute("currentUser");
//        model.addAttribute("user", u);
//        return "dashboard";
//    }
}
