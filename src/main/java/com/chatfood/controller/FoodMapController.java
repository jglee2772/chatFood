package com.chatfood.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FoodMapController {

    @Value("${naver.maps.client.id}")
    private String naverMapsClientId;

    @GetMapping("/foodMap")
    public String foodMap(Model model) {
        model.addAttribute("naverMapsClientId", naverMapsClientId);
        return "foodMap";
    }

}
