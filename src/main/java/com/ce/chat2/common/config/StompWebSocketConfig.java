package com.ce.chat2.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cloud.aws.url}")
    private String url;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins(url) //.setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/notification-connect") // 알림을 위한 새로운 웹소켓 엔드포인트 추가
                .setAllowedOrigins(url) // allowedOrigins 대신 allowedOriginPatterns 사용
                .withSockJS();

        registry.addEndpoint("/notification-connect-sockjs")// allowedOrigins 대신 allowedOriginPatterns 사용
                .setAllowedOrigins(url) //.setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/chat-connect")
            .setAllowedOrigins(url)
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/publish", "/app");
//        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/room-pub", "/chat-pub", "/notification-pub");
        registry.enableSimpleBroker("/room-sub", "/chat-sub", "/topic", "/queue");
    }
}
