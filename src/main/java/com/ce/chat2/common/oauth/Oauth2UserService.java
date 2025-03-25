package com.ce.chat2.common.oauth;

import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        Optional<User> optionalUser = userRepository.findByProviderId(id);

        if(optionalUser.isPresent()){
            Map<String, Object> attributes = new HashMap<>(loginUser.getAttributes());
            User user = optionalUser.get();
            attributes.put("name", optionalUser.get().getName());
            attributes.put("picture", optionalUser.get().getImage());

            return new Oauth2UserDetails(user, attributes);
        }

        return new Oauth2UserDetails(userRepository.save(User.of(loginUser)), loginUser.getAttributes());
    }
}
