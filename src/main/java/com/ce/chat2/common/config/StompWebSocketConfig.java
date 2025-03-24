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

        registry.addEndpoint("/notification-connect")
                .setAllowedOrigins(url)  // 또는 .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/chat-connect")
            .setAllowedOrigins(url);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/room-pub", "/chat-pub", "/notification-pub");
        registry.enableSimpleBroker("/room-sub", "/chat-sub", "/topic");
    }

}
