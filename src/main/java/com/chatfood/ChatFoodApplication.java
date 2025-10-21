package com.chatfood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class  ChatFoodApplication {

    private static final Logger logger = LoggerFactory.getLogger(ChatFoodApplication.class);

    public static void main(String[] args) {
        var app = SpringApplication.run(ChatFoodApplication.class, args);
        Environment env = app.getEnvironment();
        
        logger.info("ChatFood 애플리케이션 시작 - 포트: {}", env.getProperty("server.port"));
    }

}

