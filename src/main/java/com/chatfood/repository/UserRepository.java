package com.chatfood.repository;

/**
 * Repository (레포지토리) - 데이터베이스 CRUD 작업을 수행하는 인터페이스
 * 
 * 역할:
 * - 데이터베이스에 SQL 쿼리를 실행하는 메서드를 정의
 * - JpaRepository를 상속받으면 기본 CRUD 메서드 자동 제공
 *   (save, findById, findAll, delete 등)
 * - 필요한 커스텀 쿼리 메서드를 추가로 정의 가능
 * 
 * 기본 제공 메서드:
 * - save(entity) : INSERT 또는 UPDATE
 * - findById(id) : SELECT (id로 조회)
 * - findAll() : SELECT (전체 조회)
 * - deleteById(id) : DELETE
 * - existsById(id) : 존재 여부 확인
 * 
 * 커스텀 메서드 예시:
 * - findByUsername(String username) : username으로 찾기
 * - existsByEmail(String email) : email 중복 체크
 */

import com.chatfood.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 중복 체크
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}
