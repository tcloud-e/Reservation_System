package com.example.controller;

import com.example.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
    this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register/{role}")
    public String registerForm(@PathVariable String role, Model model) {
        model.addAttribute("role", role);
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
