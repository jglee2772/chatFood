package com.chatfood.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 로깅 관련 설정 및 모니터링
 */
@Configuration
@EnableScheduling
public class LoggingConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);
    
    // 로그 통계를 위한 카운터들
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private final AtomicLong aiRecommendationCount = new AtomicLong(0);
    private final AtomicLong userLoginCount = new AtomicLong(0);
    
    /**
     * 로그 통계를 주기적으로 출력하는 스케줄러
     * 매 5분마다 실행
     */
    @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
    public void logStatistics() {
        long total = totalRequests.get();
        long errors = errorCount.get();
        long aiRecs = aiRecommendationCount.get();
        long logins = userLoginCount.get();
        
        if (total > 0) {
            double errorRate = (double) errors / total * 100;
            logger.info("=== 로그 통계 (5분간) ===");
            logger.info("총 요청 수: {}", total);
            logger.info("에러 수: {} (에러율: {:.2f}%)", errors, errorRate);
            logger.info("AI 추천 요청 수: {}", aiRecs);
            logger.info("사용자 로그인 수: {}", logins);
            logger.info("========================");
        }
    }
    
    /**
     * 애플리케이션 시작 시 로그 설정 정보 출력
     */
    @Bean
    public String logStartupInfo() {
        logger.info("=== ChatFood 애플리케이션 시작 ===");
        logger.info("로깅 시스템: SLF4J + Logback");
        logger.info("로그 레벨: INFO (개발환경), WARN (프로덕션)");
        logger.info("로그 파일: logs/chatfood.log");
        logger.info("에러 로그: logs/chatfood-error.log");
        logger.info("JSON 로그: logs/chatfood-json.log");
        logger.info("================================");
        return "Logging initialized";
    }
    
    // 통계 카운터 getter/setter 메서드들
    public void incrementTotalRequests() {
        totalRequests.incrementAndGet();
    }
    
    public void incrementErrorCount() {
        errorCount.incrementAndGet();
    }
    
    public void incrementAiRecommendationCount() {
        aiRecommendationCount.incrementAndGet();
    }
    
    public void incrementUserLoginCount() {
        userLoginCount.incrementAndGet();
    }
    
    public long getTotalRequests() {
        return totalRequests.get();
    }
    
    public long getErrorCount() {
        return errorCount.get();
    }
    
    public long getAiRecommendationCount() {
        return aiRecommendationCount.get();
    }
    
    public long getUserLoginCount() {
        return userLoginCount.get();
    }
}
