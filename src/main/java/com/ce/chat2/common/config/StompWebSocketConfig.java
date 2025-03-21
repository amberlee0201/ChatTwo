package com.ce.chat2.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            .setAllowedOrigins(url);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/room-pub", "/chat-pub", "/notification-pub");
        registry.enableSimpleBroker("/room-sub", "/chat-sub", "/topic");
    }

    // ✅ 사용자 인증 정보를 WebSocket 세션에 연동
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                assert accessor != null;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                    System.out.println("Auth at CONNECT: " + auth);

                    if (auth != null) {
                        accessor.setUser(auth); // 🔐 사용자 정보 세션에 주입
                    }
                }
                return message;
            }
        });
    }
}
