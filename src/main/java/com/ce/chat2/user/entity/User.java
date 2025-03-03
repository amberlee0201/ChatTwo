package com.ce.chat2.user.entity;

import com.ce.chat2.common.entity.BaseEntity;
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

    public static User of(String providerId, String name, String image){
        return User.builder()
            .providerId(providerId)
            .name(name)
            .image(image)
            .build();
    }
}
