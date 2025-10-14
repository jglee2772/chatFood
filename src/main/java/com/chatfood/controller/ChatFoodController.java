package com.chatfood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatFoodController {

    @GetMapping("/chatFood")
    public String chatFood() {
        return "chatFood";
    }

}