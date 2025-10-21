# ChatFood

Spring Boot 기반의 ChatFood 애플리케이션입니다.

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Gradle 8.5
- Spring Data JPA
- H2 Database (개발용)
- Lombok

## 프로젝트 실행 방법

### 1. 빌드

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

### 2. 실행

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

또는 빌드 후 jar 파일 직접 실행:

```bash
java -jar build/libs/ChatFood-0.0.1-SNAPSHOT.jar
```

### 3. 접속

- 애플리케이션: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/chatfood`
  - Username: `sa`
  - Password: (비어있음)

### 4. 데이터베이스

개발 환경에서는 H2 파일 기반 데이터베이스를 사용합니다:
- 데이터는 `./data/chatfood.mv.db` 파일에 저장됩니다
- 서버를 재시작해도 데이터가 유지됩니다
- H2 콘솔을 통해 데이터를 직접 조회/수정할 수 있습니다

## 프로젝트 구조

```
ChatFood/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── chatfood/
│   │   │           ├── ChatFoodApplication.java
│   │   │           └── controller/
│   │   │               └── HomeController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── chatfood/
│                   └── ChatFoodApplicationTests.java
├── build.gradle
├── settings.gradle
└── gradlew
```

## 개발 환경 설정

### 필수 요구사항

- JDK 17 이상
- Gradle 8.5 (wrapper 포함)

### API 키 설정

1. **OpenAI API 키 설정**
   ```bash
   # src/main/resources/application-local.properties 파일 생성
   openai.api.key=your-actual-openai-api-key-here
   ```

2. **Kakao API 키 설정** (선택사항)
   ```bash
   # src/main/resources/application-local.properties 파일에 추가
   kakao.rest.api.key=your-kakao-api-key
   kakao.javascript.key=your-kakao-javascript-key
   ```

3. **로컬 프로파일로 실행**
   ```bash
   # Windows
   gradlew.bat bootRun --args='--spring.profiles.active=local'
   
   # Linux/Mac
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

### 보안 주의사항

- `application-local.properties` 파일은 `.gitignore`에 포함되어 Git에 커밋되지 않습니다
- 실제 API 키는 절대 공개 저장소에 커밋하지 마세요
- 프로덕션 환경에서는 환경 변수나 보안 관리 시스템을 사용하세요

## API 엔드포인트

- `GET /` - 웰컴 메시지

## 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다.

