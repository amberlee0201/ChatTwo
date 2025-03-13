package com.ce.chat2.notification.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.notification.entity.Notification;
import com.ce.chat2.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String getNotifications(Model model, @AuthenticationPrincipal Oauth2UserDetails oAuth2User) {
        if (oAuth2User != null) {
            model.addAttribute("user", oAuth2User.getUser());
            List<Notification> notifications = notificationService.getNotifications(oAuth2User.getUser().getId());
            model.addAttribute("notifications", notifications);
            log.info("🔵 알림 페이지 접속 - 로그인된 사용자 ID: {}", oAuth2User.getUser().getId());
        } else {
            log.warn("🟠 알림 페이지 접속 - OAuth2 인증 정보 없음!");
        }
        return "notification";
    }
}