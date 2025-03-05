package com.ce.chat2.notification;

import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NotificationController {

    @Data
    public static class NotificationMessage {
        private String message;

        public NotificationMessage(String message) {
            this.message = message;
        }
    }

    @MessageMapping("/send")
    @SendTo("/topic/notification-sub")
    public NotificationMessage handleNotification(NotificationMessage message) {
        log.info("서버에서 메시지 받음: " + message.getMessage());

        NotificationMessage response = new NotificationMessage("서버에서 전달: " + message.getMessage());

        log.info("클라이언트로 전달할 메시지: " + response.getMessage());
        return response;
    }
}