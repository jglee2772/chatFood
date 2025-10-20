package com.chatfood.dto;

import java.util.List;

// chat.html에서 서버로 보내는 JSON 데이터를 받기 위한 클래스
public class ChatRequest {
    private String message;
    private UserInfo userInfo;
    private String sessionId;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
