package com.chatfood.entity;

/**
 * Entity (엔티티) - 데이터베이스 테이블 구조를 정의하는 클래스
 * 
 * 역할:
 * - 데이터베이스의 테이블을 Java 클래스로 표현
 * - @Entity 어노테이션을 붙이면 자동으로 테이블 생성
 * - 테이블의 컬럼은 클래스의 필드로 정의
 * 
 * 사용 예시:
 * - User, Food, Order, Review 등의 테이블마다 하나씩 생성
 * - 각 필드는 테이블의 컬럼이 됨
 */

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여기에 필요한 컬럼들을 추가하세요
    // 예: private String username;
    //     private String password;
    //     private String email;
}
