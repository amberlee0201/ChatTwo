package com.ce.chat2.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/notification-connect") // 알림을 위한 새로운 웹소켓 엔드포인트 추가
                .setAllowedOriginPatterns("*") // allowedOrigins 대신 allowedOriginPatterns 사용
                .withSockJS();

        registry.addEndpoint("/notification-connect-sockjs")
                .setAllowedOriginPatterns("*") // allowedOrigins 대신 allowedOriginPatterns 사용
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/publish", "/app");
//        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/room-pub", "/notification-pub");
        registry.enableSimpleBroker("/room-sub", "/topic", "/queue");
    }
}
