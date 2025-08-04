package com.behavior.sdk.trigger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login.html";
    }
}