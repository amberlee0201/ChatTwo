package com.ce.chat2.common.oauth;

import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User loginUser = super.loadUser(userRequest);

        String id = loginUser.getAttributes().get("sub").toString();
        User user = userRepository.findByProviderId(id)
            .orElseGet(() ->
                userRepository.save(User.of(loginUser))
            );

        return new Oauth2UserDetails(user, loginUser.getAttributes());
    }
}
