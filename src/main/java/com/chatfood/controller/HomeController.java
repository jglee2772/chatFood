package com.chatfood.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${kakao.javascript.key}")
    private String kakaoJavaScriptKey;

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
    public String foodMap(Model model) {
        // 환경 변수에서 카카오 API 키를 가져와서 JavaScript로 전달
        System.out.println("🔑 환경 변수에서 가져온 카카오 API 키: " + kakaoJavaScriptKey);
        
        // API 키가 null이거나 비어있으면 빈 문자열 전달
        String apiKey = (kakaoJavaScriptKey != null) ? kakaoJavaScriptKey : "";
        model.addAttribute("kakaoApiKey", apiKey);
        return "foodMap";
    }

}