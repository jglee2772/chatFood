package com.chatfood.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * 대화 응답을 위한 DTO
 * AI 대화와 음식 추천을 통합하여 응답합니다.
 */
public class ConversationResponse {
    
    private String reply;
    private List<Recommendation> recommendations;
    private List<Recommendation> pythonRecommendations; // Python AI 개인화 추천
    private List<ConversationOption> options;
    private String conversationType; // "greeting", "recommendation", "follow_up", "food_selection"
    private boolean isEndOfConversation;
    private String nextAction; // "continue", "food_map", "restart"
    
    public ConversationResponse() {
        this.recommendations = new ArrayList<>();
        this.pythonRecommendations = new ArrayList<>();
        this.options = new ArrayList<>();
        this.conversationType = "greeting";
        this.isEndOfConversation = false;
        this.nextAction = "continue";
    }
    
    // Getters and Setters
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
    
    public List<Recommendation> getPythonRecommendations() {
        return pythonRecommendations;
    }
    
    public void setPythonRecommendations(List<Recommendation> pythonRecommendations) {
        this.pythonRecommendations = pythonRecommendations;
    }
    
    public List<ConversationOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<ConversationOption> options) {
        this.options = options;
    }
    
    public String getConversationType() {
        return conversationType;
    }
    
    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }
    
    public boolean isEndOfConversation() {
        return isEndOfConversation;
    }
    
    public void setEndOfConversation(boolean endOfConversation) {
        isEndOfConversation = endOfConversation;
    }
    
    public String getNextAction() {
        return nextAction;
    }
    
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
}
