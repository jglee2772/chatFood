-- H2 → PostgreSQL 마이그레이션 검증 스크립트
-- 배포 전 로컬에서 PostgreSQL 테스트용

-- 1. 테이블 생성 확인
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    gender VARCHAR(10),
    like_category VARCHAR(500),
    often_category VARCHAR(50),
    age VARCHAR(20),
    region VARCHAR(50),
    choice_food VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 2. 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- 3. 테스트 데이터 삽입
INSERT INTO users (name, email, password, gender, age, region, created_at, updated_at) 
VALUES ('테스트사용자', 'test@example.com', 'password123', '남성', '30대', '서울', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- 4. 데이터 조회 테스트
SELECT * FROM users WHERE email = 'test@example.com';

-- 5. 테이블 구조 확인
\d users;

-- 6. 인덱스 확인
\di;
