package com.chatfood.controller;

import com.chatfood.entity.User;
import com.chatfood.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 기본 정보 추출
            String name = (String) requestData.get("name");
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            
            // 추가 정보 추출
            String gender = (String) requestData.get("gender");
            String likeCategory = (String) requestData.get("likeCategory");
            String oftenCategory = (String) requestData.get("oftenCategory");
            String age = (String) requestData.get("age");
            String region = (String) requestData.get("region");
            String choiceFood = (String) requestData.get("choiceFood");
            
            // 기본 정보 검증
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "이름을 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "이메일을 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "비밀번호를 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // User 객체 생성 및 데이터 설정
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim());
            user.setPassword(password);
            user.setGender(gender);
            user.setLikeCategory(likeCategory);
            user.setOftenCategory(oftenCategory);
            user.setAge(age);
            user.setRegion(region);
            user.setChoiceFood(choiceFood);
            
            // 회원가입 처리
            User savedUser = userService.registerUser(user);
            
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다!");
            response.put("userId", savedUser.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            var userOpt = userService.login(email, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("success", true);
                response.put("message", "로그인 성공!");
                response.put("userId", user.getId());
                response.put("name", user.getName());
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
