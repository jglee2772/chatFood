package com.chatfood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "ChatFood에 오신 것을 환영합니다!");
        return "mainpage";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/joinpage")
    public String joinpage() {
        return "joinpage";
    }

}

