package com.chatfood.service;

/**
 * Service (서비스) - 비즈니스 로직을 처리하는 클래스
 * 
 * 역할:
 * - Repository를 사용해서 실제 비즈니스 로직 구현
 * - Controller와 Repository 사이의 중간 계층
 * - 복잡한 로직, 검증, 트랜잭션 처리 등을 담당
 * 
 * 사용 예시:
 * - 회원가입: 중복 체크 + 비밀번호 암호화 + 저장
 * - 로그인: 인증 처리
 * - 데이터 조회: Repository에서 가져온 데이터 가공
 * - 데이터 수정/삭제: 권한 확인 후 처리
 * 
 * Repository 사용 방법:
 * - @RequiredArgsConstructor로 자동 주입받아 사용
 * - userRepository.save(), findById() 등의 메서드 호출
 */

import com.chatfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 여기에 비즈니스 로직 메서드를 추가하세요
    // 예: public User registerUser(User user) { ... }
    //     public User login(String username, String password) { ... }
}
