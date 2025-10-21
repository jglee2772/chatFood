package com.chatfood.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 대화 컨텍스트를 관리하는 DTO
 * 사용자의 대화 히스토리와 상태를 추적합니다.
 */
public class ConversationContext {
    
    private String sessionId;
    private String userId;
    private LocalDateTime startTime;
    private List<String> conversationHistory;
    private String currentTopic;
    private boolean hasRecommendations;
    private List<Recommendation> lastRecommendations;
    private String lastUserMessage;
    private int conversationTurn;
    
    public ConversationContext() {
        this.conversationHistory = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.conversationTurn = 0;
        this.hasRecommendations = false;
    }
    
    public ConversationContext(String sessionId, String userId) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
    }
    
    /**
     * 대화 히스토리에 메시지 추가
     */
    public void addToHistory(String message) {
        this.conversationHistory.add(message);
        this.conversationTurn++;
    }
    
    /**
     * 대화 상태 초기화
     */
    public void reset() {
        this.conversationHistory.clear();
        this.currentTopic = null;
        this.hasRecommendations = false;
        this.lastRecommendations = null;
        this.conversationTurn = 0;
    }
    
    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public List<String> getConversationHistory() {
        return conversationHistory;
    }
    
    public void setConversationHistory(List<String> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }
    
    public String getCurrentTopic() {
        return currentTopic;
    }
    
    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }
    
    public boolean isHasRecommendations() {
        return hasRecommendations;
    }
    
    public void setHasRecommendations(boolean hasRecommendations) {
        this.hasRecommendations = hasRecommendations;
    }
    
    public List<Recommendation> getLastRecommendations() {
        return lastRecommendations;
    }
    
    public void setLastRecommendations(List<Recommendation> lastRecommendations) {
        this.lastRecommendations = lastRecommendations;
    }
    
    public String getLastUserMessage() {
        return lastUserMessage;
    }
    
    public void setLastUserMessage(String lastUserMessage) {
        this.lastUserMessage = lastUserMessage;
    }
    
    public int getConversationTurn() {
        return conversationTurn;
    }
    
    public void setConversationTurn(int conversationTurn) {
        this.conversationTurn = conversationTurn;
    }
}
