package com.ce.chat2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Chat2Application {

	public static void main(String[] args) {
		SpringApplication.run(Chat2Application.class, args);
	}

}
