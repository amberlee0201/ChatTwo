package com.ce.chat2.user.controller;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UnAuthorizedUser;
import com.ce.chat2.user.service.UserService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/chats")
    String toMain(){
        return "main";
    }

    @GetMapping("/users/{userId}")
    String toMyPage(@PathVariable("userId") int userId,
        @AuthenticationPrincipal Oauth2UserDetails oAuth2User,
        Model model
    ){
        if(userId != oAuth2User.getUser().getId()) throw new UnAuthorizedUser();
        log.info("imagePath = {}", oAuth2User.getUser().getImage());
        model.addAttribute("user", oAuth2User.getUser());
        return "myPage";
    }

    @PutMapping(value="/users/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String modifyUser(
        Authentication authentication,
        @RequestParam("id") int id,
        @RequestParam("name") String name,
        @RequestParam("image") MultipartFile image,
        Model model
    ) throws IOException {
        if (authentication.getPrincipal() instanceof Oauth2UserDetails oauthUser) {
            User savedUser = userService.updateUserInfo(oauthUser, id, name, image);
            userService.updateOAuth2User(authentication, savedUser);
            model.addAttribute("user", savedUser);
            return "myPage";
        } else {
            throw new UnAuthorizedUser();
        }
    }
}
