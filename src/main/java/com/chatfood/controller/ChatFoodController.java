package com.chatfood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatFoodController {

    @GetMapping("/chatFood")
    public String chatFood(Model model) {
        model.addAttribute("message", "ChatFood에 오신 것을 환영합니다!");
        return "chatFood";
    }

    @GetMapping("/foodMap")
    public String foodMap(Model model) {
        model.addAttribute("message", "ChatFood에 오신 것을 환영합니다!");
        return "foodMap";
    }

}