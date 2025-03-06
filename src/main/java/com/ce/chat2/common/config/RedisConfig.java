package com.ce.chat2.common.config;

import com.ce.chat2.chat.service.ReadCountService;
import com.ce.chat2.chat.service.RedisChatPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);
        return new LettuceConnectionFactory(configuration);
    }
    //    publish객체
    @Bean
    @Qualifier("chatPubSub")
//    일반적으로 RedisTemplate<key데이터타입, value데이터타입>을 사용
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory){
        return  new StringRedisTemplate(redisConnectionFactory);
    }

    //    subscribe객체
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
        @Qualifier("messageListenerAdapter") MessageListenerAdapter messageListenerAdapter,
        @Qualifier("readCountListenerAdapter") MessageListenerAdapter readCountListenerAdapter
    ){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("room*"));
        container.addMessageListener(readCountListenerAdapter, new PatternTopic("readCount*"));
        return container;
    }

    //    redis에서 수신된 메시지를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter messageListenerAdapter(
        RedisChatPubSubService redisChatPubSubService){
//        RedisMessagePubSubService 특정 메서드가 수신된 메시지를 처리할수 있도록 지정
        return new MessageListenerAdapter(redisChatPubSubService, "onMessage");
    }
    //    redis에서 수신된 읽음처리 요청를 처리하는 객체 생성
    @Bean
    public MessageListenerAdapter readCountListenerAdapter(
        ReadCountService readCountService){
        return new MessageListenerAdapter(readCountService, "onMessage");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
