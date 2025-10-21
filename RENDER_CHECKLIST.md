# 🚀 Render 배포 체크리스트

## ✅ **배포 전 준비사항**

### **1. GitHub 저장소 설정**
- [ ] 모든 파일이 GitHub에 푸시됨
- [ ] `.gitignore`에 민감한 정보 제외됨
- [ ] AI 모델 파일들이 저장소에 포함됨

### **2. 환경 변수 준비**
- [ ] OpenAI API 키 준비
- [ ] 카카오 API 키 준비
- [ ] PostgreSQL 데이터베이스 정보 준비

### **3. 파일 구조 확인**
```
ChatFood/
├── foodchat/                 # Python AI 서버
│   ├── app.py
│   ├── requirements.txt
│   ├── Procfile
│   ├── *.h5                 # AI 모델 파일
│   └── *.joblib            # 전처리기 파일
├── src/                     # Spring Boot 서버
│   └── main/java/com/chatfood/
├── build.gradle
├── Dockerfile
└── DEPLOYMENT.md
```

## 🎯 **배포 순서**

### **Step 1: PostgreSQL 데이터베이스 생성**
1. [Render 대시보드](https://dashboard.render.com) → **New** → **PostgreSQL**
2. **Database Name**: `foodchat`
3. **User**: 자동 생성
4. **Password**: 자동 생성
4. **Internal Database URL** 복사

### **Step 2: Python AI 서버 배포**
1. **New** → **Web Service**
2. **Repository**: GitHub 저장소 연결
3. **Root Directory**: `foodchat`
4. **Build Command**: `pip install -r requirements.txt`
5. **Start Command**: `gunicorn app:app --bind 0.0.0.0:$PORT`
6. **Environment Variables**:
   ```
   PORT=5000
   ```
7. **Service URL** 복사

### **Step 3: Spring Boot 서버 배포**
1. **New** → **Web Service**
2. **Repository**: GitHub 저장소 연결
3. **Root Directory**: `/` (루트)
4. **Build Command**: `./gradlew build -x test`
5. **Start Command**: `java -jar build/libs/*.jar`
6. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=[PostgreSQL Internal Database URL]
   DB_USERNAME=[PostgreSQL 사용자명]
   DB_PASSWORD=[PostgreSQL 비밀번호]
   PYTHON_AI_URL=[Python AI 서버 URL]
   OPENAI_API_KEY=[OpenAI API 키]
   KAKAO_REST_API_KEY=[카카오 REST API 키]
   KAKAO_JAVASCRIPT_KEY=[카카오 JavaScript 키]
   ```

## 🧪 **배포 후 테스트**

### **1. Python AI 서버 테스트**
```bash
curl -X POST [Python AI 서버 URL]/recommend \
  -H "Content-Type: application/json" \
  -d '{"gender":"남성","ageGroup":"30대","region":"서울","prefCategory":"한식"}'
```

**예상 응답:**
```json
{
  "status": "success",
  "recommendations": [
    {"foodName": "김치찌개", "priceMin": 8000, "priceMax": 10000},
    {"foodName": "비빔밥", "priceMin": 9000, "priceMax": 11000},
    {"foodName": "제육볶음", "priceMin": 10000, "priceMax": 12000}
  ]
}
```

### **2. Spring Boot 서버 테스트**
```bash
curl [Spring Boot 서버 URL]/
```

**예상 응답:** HTML 페이지

### **3. 통합 테스트**
1. [Spring Boot 서버 URL] 접속
2. 로그인/회원가입
3. 채팅 기능 테스트
4. 음식 추천 기능 테스트

## 🔧 **문제 해결**

### **Python AI 서버 오류**
- **빌드 실패**: `pip install --upgrade pip`
- **모델 로딩 실패**: 모델 파일이 저장소에 포함되었는지 확인
- **메모리 부족**: Paid Tier로 업그레이드

### **Spring Boot 서버 오류**
- **데이터베이스 연결 실패**: `DATABASE_URL` 형식 확인
- **Python AI 서버 연결 실패**: `PYTHON_AI_URL` 확인
- **빌드 실패**: Java 17 지원 확인

### **공통 오류**
- **환경 변수 누락**: 모든 필수 환경 변수 설정 확인
- **포트 충돌**: Render에서 자동 할당된 포트 사용
- **CORS 오류**: `WebConfig.java`에서 CORS 설정 확인

## 📊 **모니터링**

### **Render 대시보드**
- **Metrics**: CPU, 메모리 사용량
- **Logs**: 실시간 로그 확인
- **Health**: 서비스 상태 확인

### **알림 설정**
- **Slack/Discord** 웹훅 연결
- **이메일** 알림 설정

## 💰 **비용 예상**

### **Free Tier**
- **Python AI 서버**: 무료 (제한적)
- **Spring Boot 서버**: 무료 (제한적)
- **PostgreSQL**: 무료 (제한적)

### **Paid Tier (권장)**
- **Python AI 서버**: $7/월
- **Spring Boot 서버**: $7/월
- **PostgreSQL**: $7/월
- **총 비용**: 약 $21/월

## 🎉 **배포 완료 후**

1. **도메인 설정**: Custom Domain 연결
2. **SSL 인증서**: 자동 발급
3. **백업 설정**: PostgreSQL 자동 백업
4. **모니터링**: 알림 설정
5. **성능 최적화**: 필요시 스케일링
