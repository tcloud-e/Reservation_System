package com.example.controller;

import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ロールごとに登録ページを分ける（/register/admin, /register/teacher, /register/student）
    @GetMapping("/register/{role}")
    public String registerForm(@PathVariable String role, Model model) {
        model.addAttribute("role", role); // admin / teacher / student
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String role,
            Model model) {

        try {
            userService.register(username, password, fullName, role);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", role.replace("ROLE_", "").toLowerCase());
            return "register";
        }

        return "redirect:/login?registered";
    }
}
