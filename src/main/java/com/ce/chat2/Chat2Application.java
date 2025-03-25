package com.ce.chat2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableJpaAuditing
@SpringBootApplication
@EnableRedisHttpSession
public class Chat2Application {

	public static void main(String[] args) {
		SpringApplication.run(Chat2Application.class, args);
	}

}
