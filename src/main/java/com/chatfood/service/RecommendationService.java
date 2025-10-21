package com.chatfood.service;

import com.chatfood.dto.FlaskResponse;
import com.chatfood.dto.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RecommendationService {

    private final WebClient webClient;

    // 서비스가 생성될 때 WebClient를 초기화합니다.
    // Python Flask 서버의 주소를 가리킵니다.
    public RecommendationService(@Value("${python.ai.server.url:http://127.0.0.1:5000}") String pythonAiUrl) {
        this.webClient = WebClient.create(pythonAiUrl);
    }

    // 사용자 정보를 Flask 서버로 보내고 추천 결과를 받아옵니다.
    public Mono<FlaskResponse> getRecommendations(UserInfo userInfo) {
        return this.webClient.post() // POST 방식으로 요청
                .uri("/recommend")   // /recommend 경로로
                .bodyValue(userInfo) // 요청 본문에 userInfo 객체를 JSON으로 담아서
                .retrieve()          // 응답을 받아
                .bodyToMono(FlaskResponse.class); // FlaskResponse 객체로 변환
    }
}
