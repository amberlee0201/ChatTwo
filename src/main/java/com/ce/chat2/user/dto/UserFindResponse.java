package com.ce.chat2.user.dto;

import com.ce.chat2.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserFindResponse {
    private int id;
    private String name;
    private String image;

    public static UserFindResponse to(User user) {
        return new UserFindResponse(user.getId(), user.getName(), user.getImage());
    }
}