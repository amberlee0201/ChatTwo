package com.ce.chat2.common.config;

import com.ce.chat2.chat.service.ReadCountService;
import com.ce.chat2.chat.service.RedisChatPubSubService;
import com.ce.chat2.room.listener.ParticipationMessageListener;
import com.ce.chat2.room.listener.RoomMessageListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;

    @Value("${redis.topic.user-participation}")
    private String userTopic;
    @Value("${redis.topic.room-update}")
    private String roomTopic;

    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
        @Qualifier("messageListenerAdapter") MessageListenerAdapter messageListenerAdapter,
        @Qualifier("readCountListenerAdapter") MessageListenerAdapter readCountListenerAdapter,
        @Qualifier("participationMessageListenerAdapter") MessageListenerAdapter participationMessageListenerAdapter,
        @Qualifier("roomMessageListenerAdapter") MessageListenerAdapter roomMessageListenerAdapter
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("room*"));
        container.addMessageListener(readCountListenerAdapter, new PatternTopic("readCount*"));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(
        RedisChatPubSubService redisChatPubSubService){
        return new MessageListenerAdapter(redisChatPubSubService, "onMessage");
    }
    @Bean
    public MessageListenerAdapter readCountListenerAdapter(
        ReadCountService readCountService){
        return new MessageListenerAdapter(readCountService, "onMessage");
    }

    @Bean
    public MessageListenerAdapter participationMessageListenerAdapter(ParticipationMessageListener listener){
        return new MessageListenerAdapter(listener, "onMessage");
    }

    @Bean
    public MessageListenerAdapter roomMessageListenerAdapter(RoomMessageListener listener){
        return new MessageListenerAdapter(listener, "onMessage");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
