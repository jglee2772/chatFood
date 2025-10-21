package com.chatfood.service;

import com.chatfood.dto.*;
import com.chatfood.entity.User;
import com.chatfood.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GPT API를 활용한 간결한 대화 서비스
 * 복잡한 Java 로직을 GPT의 자연스러운 대화 능력으로 대체
 */
@Service
public class GPTConversationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GPTConversationService.class);
    
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final RecommendationService recommendationService;
    
    // 세션별 대화 컨텍스트 저장
    private final Map<String, ConversationContext> conversationContexts = new ConcurrentHashMap<>();
    
    // 음식 이름 추출용 패턴
    private final Pattern foodPattern = Pattern.compile("(김치찌개|비빔밥|제육볶음|불고기|갈비|삼겹살|냉면|물냉면|비빔냉면|막국수|국수|김밥|떡볶이|된장찌개|순두부찌개|부대찌개|삼계탕|닭볶음탕|찜닭|닭갈비|갈비탕|설렁탕|곰탕|육개장|추어탕|해장국|뼈해장국|회덮밥|냉국수|밀면|콩국수|오이냉국|시원한 국수|초밥|사시미|연어덮밥|참치회|라멘|우동|돈까스|스테이크|파스타|피자|치킨|햄버거|짜장면|짬뽕|탕수육|라면|만두|호떡|붕어빵|순대|튀김)");
    
    @Autowired
    public GPTConversationService(UserRepository userRepository, 
                                 RecommendationService recommendationService,
                                 @Value("${openai.api.key:}") String openaiApiKey) {
        this.userRepository = userRepository;
        this.recommendationService = recommendationService;
        
        // API 키 검증
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty() || openaiApiKey.equals("your-openai-api-key-here")) {
            logger.warn("OpenAI API 키가 설정되지 않았습니다. GPT 기능이 제한될 수 있습니다.");
        }
        
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    /**
     * GPT API를 활용한 대화 처리 - 간결한 메인 메서드
     */
    public ConversationResponse processConversation(String message, String sessionId, String userEmail) {
        logger.info("GPT 대화 처리 시작 - 메시지: {}, 세션: {}", message, sessionId);
        
        try {
            // 대화 컨텍스트 가져오기
            ConversationContext context = getOrCreateContext(sessionId, userEmail);
            context.addToHistory("사용자: " + message);
            
            
            // 사용자 정보 가져오기
            String userProfile = getUserProfile(userEmail);
            
            // GPT API 호출
            String gptResponse = callGPTAPI(message, userProfile, context);
            
            // GPT 응답에서 음식 추천 추출
            List<String> foodRecommendations = extractFoodFromGPT(gptResponse);
            
            // 응답 생성
            ConversationResponse response = createResponse(gptResponse, foodRecommendations, context);
            
            // 컨텍스트에 저장
            context.addToHistory("AI: " + gptResponse);
            
            logger.info("GPT 대화 처리 완료 - 추천수: {}", foodRecommendations.size());
            return response;
            
        } catch (Exception e) {
            logger.error("GPT 대화 처리 중 오류", e);
            return createErrorResponse();
        }
    }
    
    /**
     * 초기 진입 시 Python AI 개인화 추천 + GPT 대화
     */
    public ConversationResponse getInitialRecommendations(String sessionId, String userEmail) {
        logger.info("초기 추천 생성 - 세션: {}, 사용자: {}", sessionId, userEmail);
        
        try {
            String userProfile = getUserProfile(userEmail);
            ConversationResponse response = new ConversationResponse();
            
            // 1단계: Python AI 개인화 추천 가져오기
            List<Recommendation> pythonRecommendations = new ArrayList<>();
            
            if (userEmail != null) {
                try {
                    Optional<User> userOpt = userRepository.findByEmail(userEmail);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        UserInfo userInfo = convertUserToUserInfo(user);
                        FlaskResponse flaskResponse = recommendationService.getRecommendations(userInfo).block();
                        
                        if (flaskResponse != null && !flaskResponse.getRecommendations().isEmpty()) {
                            pythonRecommendations = flaskResponse.getRecommendations();
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Python AI 추천 실패, 기본 추천 사용", e);
                }
            }
            
            // 기본 추천 생성 (Python AI 실패 시)
            if (pythonRecommendations.isEmpty()) {
                pythonRecommendations.add(new Recommendation("김치찌개", 8000, 10000));
                pythonRecommendations.add(new Recommendation("비빔밥", 9000, 11000));
                pythonRecommendations.add(new Recommendation("제육볶음", 10000, 12000));
            }
            
            // 2단계: GPT 대화 응답 생성
            String initialPrompt = "안녕하세요! 오늘의 점심은 어떤 걸로 정해볼까요?";
            String gptResponse = callGPTAPI(initialPrompt, userProfile, null);
            List<String> gptFoodRecommendations = extractFoodFromGPT(gptResponse);
            
            // 3단계: 응답 구성
            response.setReply(gptResponse);
            response.setConversationType("initial_hybrid");
            
            // GPT 대화 추천 (오른쪽 영역용)
            if (!gptFoodRecommendations.isEmpty()) {
                response.setRecommendations(convertToRecommendations(gptFoodRecommendations));
            }
            
            // Python AI 개인화 추천 (하단 고정 영역용) - 별도 필드로 전달
            response.setPythonRecommendations(pythonRecommendations);
            
            // 대화 옵션 추가
            response.getOptions().add(new ConversationOption("대화하기", "start_conversation", "대화하기", "conversation"));
            response.getOptions().add(new ConversationOption("다른 음식 보기", "more_recommendations", "다른 음식 보기", "continue"));
            
            // 컨텍스트에 저장
            ConversationContext context = getOrCreateContext(sessionId, userEmail);
            context.setHasRecommendations(true);
            context.setLastRecommendations(convertToRecommendations(gptFoodRecommendations));
            
            return response;
            
        } catch (Exception e) {
            logger.error("초기 추천 생성 중 오류", e);
            return createErrorResponse();
        }
    }
    
    /**
     * GPT API 호출
     */
    private String callGPTAPI(String message, String userProfile, ConversationContext context) {
        // 대화 히스토리 구성
        StringBuilder conversationHistory = new StringBuilder();
        if (context != null && context.getConversationHistory() != null && !context.getConversationHistory().isEmpty()) {
            conversationHistory.append("이전 대화:\n");
            for (String history : context.getConversationHistory()) {
                conversationHistory.append(history).append("\n");
            }
        }
        
        // GPT 프롬프트 구성
        String systemPrompt = """
            당신은 친근하고 자연스러운 음식 추천 챗봇입니다.
            
            대화 스타일:
            - 짧고 간결하게 대화하세요 (1-2문장 이내)
            - 자연스럽고 친근한 톤으로 말하세요
            - 질문은 하나씩만 하세요
            - 구체적인 음식 이름을 언급하세요
            - 최종적으로 3가지 음식을 추천하세요
            
            중요 규칙:
            - 이전 대화 내용을 기억하고 참고하세요
            - 같은 질문을 반복하지 마세요
            - 사용자가 이미 답변한 내용을 다시 묻지 마세요
            - 대화가 진행되면 새로운 정보를 바탕으로 다음 질문을 하세요
            - 사용자의 선호도나 요구사항이 바뀌면 그에 맞게 대화를 이어가세요
            
            예시 대화:
            사용자: "가볍게 먹고싶어"
            AI: "가벼운 음식 좋네요! 샐러드, 순두부찌개, 비빔국수 중에 어떤 게 끌리시나요?"
            
            사용자: "밥류가 먹고싶어"  
            AI: "밥류로는 비빔밥, 김치볶음밥, 주먹밥이 가볍게 먹기 좋을 것 같아요. 어떤 걸 시도해보고 싶으세요?"
            
            사용자 정보: %s
            
            이전 대화:
            %s
            """.formatted(userProfile, conversationHistory.toString());
        
        // GPT API 요청 - 대화 히스토리 포함
        List<GPTMessage> messages = new ArrayList<>();
        messages.add(new GPTMessage("system", systemPrompt));
        
        // 이전 대화 히스토리 추가
        if (context != null && context.getConversationHistory() != null && !context.getConversationHistory().isEmpty()) {
            for (String history : context.getConversationHistory()) {
                if (history.startsWith("사용자:")) {
                    messages.add(new GPTMessage("user", history.substring(4).trim()));
                } else if (history.startsWith("AI:")) {
                    messages.add(new GPTMessage("assistant", history.substring(3).trim()));
                }
            }
        }
        
        // 현재 사용자 메시지 추가
        messages.add(new GPTMessage("user", message));
        
        GPTRequest request = new GPTRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);
        
        try {
            GPTResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GPTResponse.class)
                    .block();
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            } else {
                return "죄송합니다, 답변을 생성할 수 없습니다.";
            }
        } catch (Exception e) {
            logger.error("GPT API 호출 실패", e);
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                return "OpenAI API 키가 올바르지 않습니다. 설정을 확인해주세요.";
            } else if (e.getMessage() != null && e.getMessage().contains("403")) {
                return "OpenAI API 사용 권한이 없습니다. 계정을 확인해주세요.";
            } else {
                return "죄송합니다, AI 서비스에 연결할 수 없습니다.";
            }
        }
    }
    
    /**
     * GPT 응답에서 음식 이름 추출
     */
    private List<String> extractFoodFromGPT(String gptResponse) {
        List<String> foods = new ArrayList<>();
        Matcher matcher = foodPattern.matcher(gptResponse);
        
        while (matcher.find()) {
            String food = matcher.group(1);
            if (!foods.contains(food)) {
                foods.add(food);
            }
        }
        
        // 최대 3개까지만 반환
        return foods.subList(0, Math.min(foods.size(), 3));
    }
    
    /**
     * 사용자 프로필 정보 생성
     */
    private String getUserProfile(String userEmail) {
        if (userEmail == null) {
            return "비로그인 사용자";
        }
        
        try {
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return String.format("이름: %s, 나이: %s, 성별: %s, 지역: %s, 선호카테고리: %s", 
                    user.getName(), user.getAge(), user.getGender(), 
                    user.getRegion(), user.getOftenCategory());
            }
        } catch (Exception e) {
            logger.error("사용자 프로필 생성 중 오류", e);
        }
        
        return "비로그인 사용자";
    }
    
    /**
     * 응답 생성
     */
    private ConversationResponse createResponse(String gptResponse, List<String> foodRecommendations, ConversationContext context) {
        ConversationResponse response = new ConversationResponse();
        response.setReply(gptResponse);
        response.setConversationType("gpt_conversation");
        
        if (!foodRecommendations.isEmpty()) {
            response.setRecommendations(convertToRecommendations(foodRecommendations));
        }
        
        // 대화 옵션 추가
        response.getOptions().add(new ConversationOption("다른 음식 추천", "continue", "다른 음식 추천", "continue"));
        response.getOptions().add(new ConversationOption("대화 이어가기", "continue", "대화 이어가기", "continue"));
        
        return response;
    }
    
    /**
     * 음식 이름을 Recommendation 객체로 변환
     */
    private List<Recommendation> convertToRecommendations(List<String> foodNames) {
        List<Recommendation> recommendations = new ArrayList<>();
        int[] prices = {8000, 10000, 12000, 15000};
        
        for (String foodName : foodNames) {
            int price = prices[new Random().nextInt(prices.length)];
            recommendations.add(new Recommendation(foodName, price, price + 2000));
        }
        
        return recommendations;
    }
    
    /**
     * 에러 응답 생성
     */
    private ConversationResponse createErrorResponse() {
        ConversationResponse response = new ConversationResponse();
        response.setReply("죄송합니다, 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        response.setConversationType("error");
        return response;
    }
    
    /**
     * UserInfo 변환 (Python AI 서비스용)
     */
    private UserInfo convertUserToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName(user.getName());
        userInfo.setGender(user.getGender());
        userInfo.setAgeGroup(user.getAge());
        userInfo.setRegion(user.getRegion());
        userInfo.setPrefCategory(user.getOftenCategory());
        if (user.getLikeCategory() != null && !user.getLikeCategory().isEmpty()) {
            userInfo.setFavCategories(Arrays.asList(user.getLikeCategory().split(",")));
        }
        return userInfo;
    }
    
    /**
     * 대화 컨텍스트 가져오기 또는 생성
     */
    private ConversationContext getOrCreateContext(String sessionId, String userEmail) {
        return conversationContexts.computeIfAbsent(sessionId, 
            k -> new ConversationContext(sessionId, userEmail));
    }
    
    // GPT API 요청/응답 DTO 클래스들
    public static class GPTRequest {
        private String model;
        private List<GPTMessage> messages;
        
        // getters and setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<GPTMessage> getMessages() { return messages; }
        public void setMessages(List<GPTMessage> messages) { this.messages = messages; }
    }
    
    public static class GPTMessage {
        private String role;
        private String content;
        
        public GPTMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        // getters and setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class GPTResponse {
        private List<GPTChoice> choices;
        
        // getters and setters
        public List<GPTChoice> getChoices() { return choices; }
        public void setChoices(List<GPTChoice> choices) { this.choices = choices; }
    }
    
    public static class GPTChoice {
        private GPTMessage message;
        
        // getters and setters
        public GPTMessage getMessage() { return message; }
        public void setMessage(GPTMessage message) { this.message = message; }
    }
}
