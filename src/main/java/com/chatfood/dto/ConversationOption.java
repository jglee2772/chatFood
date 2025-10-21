package com.chatfood.dto;

/**
 * 대화 옵션을 위한 DTO
 * 사용자가 선택할 수 있는 대화 옵션들을 정의합니다.
 */
public class ConversationOption {
    
    private String text;
    private String action;
    private String value;
    private String type; // "food", "continue", "restart", "other"
    
    public ConversationOption() {}
    
    public ConversationOption(String text, String action, String value, String type) {
        this.text = text;
        this.action = action;
        this.value = value;
        this.type = type;
    }
    
    // Getters and Setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
