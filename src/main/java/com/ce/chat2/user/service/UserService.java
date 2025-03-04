package com.ce.chat2.user.service;

import com.ce.chat2.common.oauth.Oauth2UserDetails;
import com.ce.chat2.common.s3.S3FileDto;
import com.ce.chat2.common.s3.S3Service;
import com.ce.chat2.user.dto.UserFindResponse;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UnAuthorizedUser;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public User updateUserInfo(Oauth2UserDetails loginUser,int id, String name, MultipartFile image)
        throws IOException {
        if(loginUser.getUser().getId() != id) throw new UnAuthorizedUser();
        User savedUser = userRepository.findById(id)
            .orElseThrow(UserNotFound::new);

        S3FileDto dto;
        if(StringUtils.hasText(savedUser.getFileName())){
            dto = s3Service.modifyImage(image, savedUser.getFileName());
        }else{
            dto = s3Service.uploadImage(image);
        }

        savedUser.updateInfo(name, dto);
        return savedUser;
    }

    public void updateOAuth2User(Authentication authentication, User updatedUser) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            if (authentication.getPrincipal() instanceof Oauth2UserDetails customOAuth2User) {
                Map<String, Object> attributes = new HashMap<>(customOAuth2User.getAttributes());

                attributes.put("name", updatedUser.getName());
                attributes.put("picture", updatedUser.getImage());
                Oauth2UserDetails updatedOAuth2User = new Oauth2UserDetails(updatedUser, attributes);

                SecurityContextHolder.getContext().setAuthentication(
                    new OAuth2AuthenticationToken(updatedOAuth2User, customOAuth2User.getAuthorities(), provider)
                );
            }
        }
    }

    public List<UserFindResponse> findUser(String name) {
        return userRepository.findByNameContaining(name).stream()
        .map(UserFindResponse::to).collect(Collectors.toList());
    }
}

