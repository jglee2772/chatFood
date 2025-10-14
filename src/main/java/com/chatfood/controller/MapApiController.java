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

@Controller
public class MapApiController {

    @Value("${naver.maps.client.id}")
    private String clientId;

    @Value("${naver.maps.client.secret}")
    private String clientSecret;

    /**
     * 맛집 지도 페이지
     */
    @GetMapping("/foodMap")
    public String foodMap(Model model) {
        model.addAttribute("clientId", clientId);
        return "foodMap";
    }

    /**
     * 주소 검색 API (Geocoding)
     */
    @GetMapping("/api/map/search")
    @ResponseBody
    public String searchAddress(@RequestParam String query) {
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + query;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            
            // 응답 로깅
            System.out.println("네이버 API 응답: " + responseBody);
            
            // JSON 유효성 검사
            if (responseBody != null && responseBody.trim().startsWith("{")) {
                return responseBody;
            } else {
                // JSON이 아닌 경우 에러 응답 생성
                return "{\"error\":\"Invalid JSON response\",\"message\":\"API 응답이 올바르지 않습니다.\"}";
            }
        } catch (Exception e) {
            System.err.println("API 호출 오류: " + e.getMessage());
            return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\",\"message\":\"검색 중 오류가 발생했습니다.\"}";
        }
    }

}
