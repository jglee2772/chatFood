package com.chatfood.controller;

import com.chatfood.dto.*;
import com.chatfood.entity.User;
import com.chatfood.repository.UserRepository;
import com.chatfood.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChatFoodController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @Autowired
    public ChatFoodController(RecommendationService recommendationService, UserRepository userRepository) {
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/chat")
    public ChatResponse handleChat(@RequestBody ChatRequest request, HttpSession session) {
        String userMessage = request.getMessage();
        ChatResponse response = new ChatResponse();
        boolean isFirstMessage = "__INIT__".equals(userMessage);

        if (isFirstMessage) {
            String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

            if (loggedInUserEmail != null) {
                Optional<User> userOptional = userRepository.findByEmail(loggedInUserEmail);
                if (userOptional.isPresent()) {
                    return createRecommendationResponse(userOptional.get());
                } else {
                    response.setReply("안녕하세요! 로그인 정보에 오류가 있습니다. 다시 로그인해주세요.");
                }
            } else {
                response.setReply("안녕하세요! 점심 메뉴를 추천해 드릴까요? 원하시는 음식 종류를 말씀해주세요.");
            }
        } else {
            // TODO: 이어지는 대화에 대한 로직 구현 (GPT 연동 등)
            response.setReply("네, 알겠습니다!");
        }
        return response;
    }

    /**
     * 사용자 정보를 바탕으로 AI 추천을 요청하고, 그 결과를 ChatResponse 객체로 만들어 반환합니다.
     * @param user 로그인한 사용자 엔티티
     * @return 챗봇 응답 및 추천 메뉴 목록이 담긴 ChatResponse 객체
     */
    private ChatResponse createRecommendationResponse(User user) {
        ChatResponse response = new ChatResponse();
        try {
            UserInfo userInfoForAI = convertUserToUserInfo(user);

            // 1. 채팅창에 표시될 기본 응답 메시지를 설정합니다.
            String greeting = String.format(
                    "%s님, 안녕하세요! 🙌\n회원님의 정보를 바탕으로 찾아본 오늘의 추천 메뉴입니다! \n 아니면 다른 메뉴 추천해드릴까요?",
                    user.getName()
            );
            response.setReply(greeting);

            // 2. Python 서버에 추천을 요청합니다.
            FlaskResponse flaskResponse = recommendationService.getRecommendations(userInfoForAI).block();

            // 3. 추천 결과를 문자열이 아닌, 추천 목록(List)에 담아줍니다.
            if (flaskResponse != null && flaskResponse.getRecommendations() != null && !flaskResponse.getRecommendations().isEmpty()) {
                response.setRecommendations(flaskResponse.getRecommendations());
            } else {
                // 추천 메뉴가 없을 경우, 빈 리스트를 설정합니다.
                response.setRecommendations(Collections.emptyList());
            }

        } catch (Exception e) {
            System.err.println("추천 시스템 호출 중 오류 발생: " + e.getMessage());
            response.setReply("죄송합니다, 추천 시스템에 문제가 발생했어요. 잠시 후 다시 시도해주세요.");
        }
        return response;
    }

    /**
     * User 엔티티 객체를 AI 서버 요청에 필요한 UserInfo DTO로 변환합니다.
     * @param user 사용자 엔티티
     * @return AI 모델 입력용 UserInfo DTO
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
}

