package com.chatfood.controller;

/**
 * Controller (컨트롤러) - 웹 요청을 받아서 처리하는 클래스
 * 
 * 역할:
 * - 클라이언트의 HTTP 요청을 받아서 Service 호출
 * - Service에서 받은 결과를 클라이언트에게 응답
 * - URL 경로와 메서드를 매핑
 * 
 * 주요 어노테이션:
 * - @Controller : HTML 페이지를 반환하는 컨트롤러
 * - @RestController : JSON 데이터를 반환하는 API 컨트롤러
 * - @GetMapping : GET 요청 처리 (조회)
 * - @PostMapping : POST 요청 처리 (생성)
 * - @PutMapping : PUT 요청 처리 (수정)
 * - @DeleteMapping : DELETE 요청 처리 (삭제)
 * 
 * 사용 예시:
 * - @GetMapping("/login") : 로그인 페이지 보여주기
 * - @PostMapping("/api/users") : 회원가입 처리
 * - @GetMapping("/api/users/{id}") : 특정 사용자 조회
 */

import com.chatfood.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 여기에 필요한 API 엔드포인트를 추가하세요
    // 예: @PostMapping("/api/users/register") - 회원가입 처리
    //     @PostMapping("/api/users/login") - 로그인 처리
    //     @GetMapping("/api/users") - 사용자 목록 조회
    
    // 참고: 로그인/회원가입 페이지는 HomeController에 이미 정의되어 있습니다.
}
