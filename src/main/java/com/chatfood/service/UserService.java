package com.chatfood.service;

import com.chatfood.entity.User;
import com.chatfood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입
     * @param user 회원가입할 사용자 정보
     * @return 저장된 사용자 정보
     * @throws IllegalArgumentException 이미 존재하는 이메일인 경우
     */
    @Transactional
    public User registerUser(User user) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // TODO: 비밀번호 암호화 (나중에 Spring Security 추가 시)
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 정보 (Optional)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 로그인
     * @param email 이메일
     * @param password 비밀번호
     * @return 로그인 성공 시 사용자 정보 (Optional)
     */
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 정보 (Optional)
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
