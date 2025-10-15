package com.chatfood.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 맛집 지도 컨트롤러
 * 카카오 REST API(음식점 검색) + 카카오 Maps JS API(지도 표시)
 */
@Controller
public class MapApiController {

    @Value("${kakao.rest.api.key:}")
    private String kakaoRestApiKey;
    
    @Value("${kakao.javascript.key:}")
    private String kakaoJavaScriptKey;

    private static final String KAKAO_LOCAL_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private static final String FOOD_CATEGORY_CODE = ""; // 음식점 카테고리 (빈 문자열 = 필터 없음)
    private static final int SEARCH_SIZE = 15; // 검색 결과 최대 개수

    /**
     * 맛집 지도 페이지
     */
    @GetMapping("/foodMap")
    public String foodMap(Model model) {
        model.addAttribute("kakaoJsKey", kakaoJavaScriptKey);
        return "foodMap";
    }

    /**
     * 맛집 검색 API
     * 카카오 로컬 API를 사용하여 키워드로 음식점 검색
     * 
     * @param query 검색 키워드 (예: "초밥", "피자", "치킨")
     * @param x 경도 (longitude)
     * @param y 위도 (latitude)
     * @param radius 검색 반경 (미터 단위, 기본값 5000m)
     * @return 검색 결과 JSON
     */
    @GetMapping("/api/restaurants/search")
    @ResponseBody
    public String searchRestaurants(
            @RequestParam String query,
            @RequestParam(required = false) String x,
            @RequestParam(required = false) String y,
            @RequestParam(defaultValue = "20000") int radius) {
        
        System.out.println("========================================");
        System.out.println("🔍 맛집 검색 요청: " + query);
        System.out.println("📍 위치: 경도=" + x + ", 위도=" + y + ", 반경=" + radius + "m");
        
        // API 키 검증
        if (!isApiKeyConfigured()) {
            System.err.println("❌ API 키가 설정되지 않았습니다.");
            return createApiKeyGuideMessage();
        }
        
        try {
            String searchUrl = buildSearchUrl(query, x, y, radius);
            System.out.println("📡 요청 URL: " + searchUrl);
            
            HttpHeaders headers = createHeaders();
            System.out.println("🔑 Authorization: KakaoAK " + kakaoRestApiKey.substring(0, 10) + "...");
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                searchUrl, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            
            System.out.println("✅ 응답 상태: " + response.getStatusCode());
            System.out.println("📦 응답 본문 길이: " + response.getBody().length() + " 문자");
            System.out.println("📄 응답 본문 내용: " + response.getBody());
            System.out.println("========================================");
            
            logSearchSuccess(query);
            return response.getBody();
            
        } catch (Exception e) {
            System.err.println("❌ 검색 중 오류 발생");
            System.err.println("   에러 타입: " + e.getClass().getName());
            System.err.println("   에러 메시지: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            
            logSearchError(query, e);
            return createErrorMessage(e.getMessage());
        }
    }
    
    /**
     * API 키 설정 여부 확인
     */
    private boolean isApiKeyConfigured() {
        return kakaoRestApiKey != null 
            && !kakaoRestApiKey.isEmpty() 
            && !kakaoRestApiKey.startsWith("발급받은");
    }
    
    /**
     * 검색 URL 생성 (좌표 기반)
     */
    private String buildSearchUrl(String query, String x, String y, int radius) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(KAKAO_LOCAL_API_URL)
                  .append("?query=").append(encodedQuery)
                  .append("&size=").append(SEARCH_SIZE);
        
        // 카테고리 필터가 있으면 추가
        if (FOOD_CATEGORY_CODE != null && !FOOD_CATEGORY_CODE.isEmpty()) {
            urlBuilder.append("&category_group_code=").append(FOOD_CATEGORY_CODE);
        }
        
        // 좌표가 있으면 추가
        if (x != null && y != null && !x.isEmpty() && !y.isEmpty()) {
            urlBuilder.append("&x=").append(x)
                      .append("&y=").append(y)
                      .append("&radius=").append(radius);
        }
        
        return urlBuilder.toString();
    }
    
    /**
     * HTTP 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
        return headers;
    }
    
    /**
     * 검색 성공 로그
     */
    private void logSearchSuccess(String query) {
        System.out.println("✅ 카카오 로컬 API 검색 성공: " + query);
    }
    
    /**
     * 검색 오류 로그
     */
    private void logSearchError(String query, Exception e) {
        System.err.println("❌ 카카오 로컬 API 오류: " + query);
        System.err.println("   에러 메시지: " + e.getMessage());
    }
    
    /**
     * API 키 설정 안내 메시지
     */
    private String createApiKeyGuideMessage() {
        return """
            {
                "error": "API_KEY_NOT_CONFIGURED",
                "message": "카카오 API 키가 설정되지 않았습니다.",
                "guide": {
                    "step1": "https://developers.kakao.com/ 접속",
                    "step2": "내 애플리케이션 → 애플리케이션 추가",
                    "step3": "플랫폼 설정 → Web 플랫폼 등록 (http://localhost:8080)",
                    "step4": "제품 설정 → 로컬 → 사용 설정",
                    "step5": "REST API 키와 JavaScript 키 복사",
                    "step6": "application.properties에 추가"
                }
            }
            """;
    }
    
    /**
     * 에러 메시지 생성
     */
    private String createErrorMessage(String errorMessage) {
        return String.format(
            "{\"error\":\"SEARCH_FAILED\",\"message\":\"%s\"}", 
            errorMessage.replace("\"", "\\\"")
        );
    }
}
