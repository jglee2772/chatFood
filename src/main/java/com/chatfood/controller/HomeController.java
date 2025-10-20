package com.chatfood.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "mainpage";
    }

    /**
     * "/chat" 주소로 GET 요청이 왔을 때
     * templates 폴더에 있는 "chatFood.html" 파일을 찾아서 보여줍니다.
     * @return 보여줄 HTML 파일의 이름
     */
    @GetMapping("/chatFood")
    public String chatPage() {
        return "chatFood"; // Thymeleaf가 "templates/chatFood.html"을 찾습니다.
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/foodMap")
    public String foodMap() {
        return "foodMap";
    }

}

