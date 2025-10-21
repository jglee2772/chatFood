package com.chatfood.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List; // List를 import 합니다.

@Getter
@Setter
@NoArgsConstructor
public class ChatResponse {
    private String reply;
    // ✨ 추천 메뉴 객체 리스트를 담을 필드를 추가합니다.
    private List<Recommendation> recommendations;
    // Python AI 개인화 추천
    private List<Recommendation> pythonRecommendations;
}

