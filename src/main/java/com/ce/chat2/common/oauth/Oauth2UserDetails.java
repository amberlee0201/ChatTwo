package com.ce.chat2.common.oauth;

import com.ce.chat2.user.entity.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
public class Oauth2UserDetails implements OAuth2User, Serializable {

    private User user;
    private Map<String, Object> attributes;

    public Oauth2UserDetails(User user ,Map<String, Object> attributes){
        this.attributes = attributes;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRole()));
    }

    @Override
    public String getName() {
        return attributes.get("sub").toString();
    }
}