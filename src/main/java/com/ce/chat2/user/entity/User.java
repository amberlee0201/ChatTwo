package com.ce.chat2.user.entity;

import com.ce.chat2.common.entity.BaseEntity;

import com.ce.chat2.common.s3.S3FileDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    int id;

    @Column(nullable = false, name = "user_provider_id")
    String providerId;

    @Column(nullable = false, name = "user_name", length = 50)
    String name;

    @Column(nullable = false, name = "user_image")
    String image;

    @Column(name = "user_fileName")
    String fileName;

    UserRole role;

    public static User of(String providerId, String name, String image, UserRole userRole) {
        return User.builder()
                .providerId(providerId)
                .name(name)
                .image(image)
                .role(userRole)
                .build();
    }

    public static User of(OAuth2User loginUser) {

        return User.of(loginUser.getAttribute("sub"),
                loginUser.getAttribute("name"),
                loginUser.getAttribute("picture"),
                UserRole.USER);
    }

    public void updateInfo(String username, S3FileDto dto) {
        this.name = username;
        this.image = dto.getFilePath();
        this.fileName = dto.getFileName();
    }
}
