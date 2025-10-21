package com.chatfood.controller;

import com.chatfood.dto.*;
import com.chatfood.service.GPTConversationService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ChatFoodController {

    private static final Logger logger = LoggerFactory.getLogger(ChatFoodController.class);
    private final GPTConversationService gptConversationService;

    @Autowired
    public ChatFoodController(GPTConversationService gptConversationService) {
        this.gptConversationService = gptConversationService;
    }

    @GetMapping("/initial-recommendations")
    public ChatResponse getInitialRecommendations(HttpSession session) {
        // 요청 ID 생성 및 MDC 설정
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        String sessionId = session.getId();
        String userEmail = (String) session.getAttribute("loggedInUserEmail");
        
        logger.info("초기 추천 요청 수신 - 세션ID: {}, 사용자: {}", 
                   sessionId, userEmail != null ? userEmail : "비로그인");
        
        try {
            // GPT 기반 초기 추천 생성
            ConversationResponse conversationResponse = gptConversationService.getInitialRecommendations(sessionId, userEmail);
            
            // ConversationResponse를 ChatResponse로 변환
            ChatResponse response = convertToChatResponse(conversationResponse);
            
            logger.info("초기 추천 생성 완료 - 응답타입: {}, 추천수: {}", 
                       conversationResponse.getConversationType(), 
                       response.getRecommendations().size());
            
            return response;
            
        } catch (Exception e) {
            logger.error("초기 추천 생성 중 오류 발생", e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setReply("죄송합니다, 초기 추천 생성 중 오류가 발생했습니다. 다시 시도해주세요.");
            return errorResponse;
        } finally {
            // MDC 정리
            MDC.clear();
        }
    }

    @PostMapping("/chat")
    public ChatResponse handleChat(@RequestBody ChatRequest request, HttpSession session) {
        // 요청 ID 생성 및 MDC 설정
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);
        
        String userMessage = request.getMessage();
        String sessionId = session.getId();
        String userEmail = (String) session.getAttribute("loggedInUserEmail");
        
        logger.info("채팅 요청 수신 - 메시지: {}, 세션ID: {}, 사용자: {}", 
                   userMessage, sessionId, userEmail != null ? userEmail : "비로그인");
        
        try {
                    // GPT 기반 대화 시스템 사용
                    ConversationResponse conversationResponse = gptConversationService.processConversation(
                        userMessage, sessionId, userEmail
                    );
            
            // ConversationResponse를 ChatResponse로 변환
            ChatResponse response = convertToChatResponse(conversationResponse);
            
            logger.info("대화 처리 완료 - 응답타입: {}, 추천수: {}", 
                       conversationResponse.getConversationType(), 
                       response.getRecommendations().size());
            
            return response;
            
        } catch (Exception e) {
            logger.error("채팅 처리 중 오류 발생", e);
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setReply("죄송합니다, 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            return errorResponse;
        } finally {
            // MDC 정리
            MDC.clear();
        }
    }

    /**
     * ConversationResponse를 ChatResponse로 변환
     */
    private ChatResponse convertToChatResponse(ConversationResponse conversationResponse) {
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setReply(conversationResponse.getReply());
        chatResponse.setRecommendations(conversationResponse.getRecommendations());
        chatResponse.setPythonRecommendations(conversationResponse.getPythonRecommendations());
        
        
        // 대화 옵션을 추천에 추가 (프론트엔드에서 처리할 수 있도록)
        if (!conversationResponse.getOptions().isEmpty()) {
            // 옵션들을 추천 목록에 추가
            for (ConversationOption option : conversationResponse.getOptions()) {
                if ("food".equals(option.getType())) {
                    // 음식 옵션은 추천으로 추가
                    Recommendation rec = new Recommendation();
                    rec.setFoodName(option.getText());
                    rec.setPriceMin(0);
                    rec.setPriceMax(0);
                    chatResponse.getRecommendations().add(rec);
                }
            }
        }
        
        return chatResponse;
    }


}

