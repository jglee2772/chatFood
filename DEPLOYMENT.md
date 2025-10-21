# 🚀 Render 배포 가이드

## 📋 배포 구조

### **1. Python Flask AI 서버 (foodchat)**
- **목적**: AI 추천 서비스
- **포트**: Render에서 자동 할당
- **엔드포인트**: `/recommend`

### **2. Spring Boot 웹 서버**
- **목적**: 메인 웹 애플리케이션
- **포트**: Render에서 자동 할당
- **데이터베이스**: PostgreSQL

## 🛠️ 배포 단계

### **Step 1: Python AI 서버 배포**

1. **Render 대시보드** → **New** → **Web Service**
2. **Repository**: GitHub 저장소 연결
3. **Root Directory**: `foodchat`
4. **Build Command**: `pip install -r requirements.txt`
5. **Start Command**: `gunicorn app:app --bind 0.0.0.0:$PORT`

**환경 변수 설정:**
```
PORT=5000
```

### **Step 2: PostgreSQL 데이터베이스 생성**

1. **Render 대시보드** → **New** → **PostgreSQL**
2. **Database Name**: `foodchat`
3. **User**: 자동 생성
4. **Password**: 자동 생성

### **Step 3: Spring Boot 서버 배포**

1. **Render 대시보드** → **New** → **Web Service**
2. **Repository**: GitHub 저장소 연결
3. **Root Directory**: `/` (루트)
4. **Build Command**: `./gradlew build -x test`
5. **Start Command**: `java -jar build/libs/*.jar`

**환경 변수 설정:**
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://[PostgreSQL 호스트]:5432/foodchat
DB_USERNAME=[PostgreSQL 사용자명]
DB_PASSWORD=[PostgreSQL 비밀번호]
PYTHON_AI_URL=[Python AI 서버 URL]
OPENAI_API_KEY=[OpenAI API 키]
KAKAO_REST_API_KEY=[카카오 API 키]
KAKAO_JAVASCRIPT_KEY=[카카오 JavaScript 키]
```

## 🗄️ **데이터베이스 마이그레이션**

### **H2 → PostgreSQL 호환성**
- ✅ **JPA/Hibernate 사용**: 데이터베이스 독립성 확보
- ✅ **Entity 클래스**: `@Entity`, `@Table`, `@Column` 어노테이션 사용
- ✅ **Repository**: `JpaRepository` 인터페이스 사용
- ✅ **커스텀 쿼리**: 없음 (JPA 메서드명 기반 쿼리만 사용)

### **데이터 타입 호환성**
- ✅ **VARCHAR**: H2와 PostgreSQL 모두 지원
- ✅ **TIMESTAMP**: `LocalDateTime` 타입 호환
- ✅ **BIGINT**: `Long` 타입 호환
- ✅ **UNIQUE 제약조건**: 이메일 중복 방지

### **배포 전 테스트**
```bash
# 로컬 PostgreSQL 테스트
SPRING_PROFILES_ACTIVE=test ./gradlew bootRun

# 데이터베이스 연결 테스트
psql -h localhost -U postgres -d foodchat_test -f test-database-migration.sql
```

## 🔗 서비스 연결

### **Python AI 서버 URL 확인**
1. Python AI 서버 배포 완료 후
2. Render 대시보드에서 **Service URL** 복사
3. Spring Boot 서버의 `PYTHON_AI_URL` 환경 변수에 설정

### **데이터베이스 연결 확인**
1. PostgreSQL 서비스의 **Internal Database URL** 복사
2. Spring Boot 서버의 `DATABASE_URL` 환경 변수에 설정

## 🧪 배포 후 테스트

### **1. Python AI 서버 테스트**
```bash
curl -X POST [Python AI 서버 URL]/recommend \
  -H "Content-Type: application/json" \
  -d '{"gender":"남성","ageGroup":"30대","region":"서울","prefCategory":"한식"}'
```

### **2. Spring Boot 서버 테스트**
```bash
curl [Spring Boot 서버 URL]/
```

## 📝 주의사항

### **1. 파일 업로드 제한**
- Render는 **임시 파일 시스템** 사용
- 모델 파일들은 **GitHub 저장소**에 포함되어야 함

### **2. 메모리 제한**
- **Free Tier**: 512MB RAM
- **Paid Tier**: 1GB+ RAM (권장)

### **3. 환경 변수 보안**
- API 키는 **환경 변수**로만 설정
- `.env` 파일은 **절대 커밋하지 말 것**

## 🔧 문제 해결

### **Python AI 서버 오류**
```bash
# 로그 확인
render logs [서비스명]

# 빌드 오류 시
pip install --upgrade pip
pip install -r requirements.txt
```

### **Spring Boot 서버 오류**
```bash
# 로그 확인
render logs [서비스명]

# 데이터베이스 연결 오류 시
# DATABASE_URL 형식 확인
# jdbc:postgresql://[host]:[port]/[database]
```

## 📊 모니터링

### **Render 대시보드**
- **Metrics**: CPU, 메모리 사용량
- **Logs**: 실시간 로그 확인
- **Health**: 서비스 상태 확인

### **알림 설정**
- **Slack/Discord** 웹훅 연결
- **이메일** 알림 설정
