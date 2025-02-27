package com.ce.chat2.user.dto;

import com.ce.chat2.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListResponse {
    private int id;
    private String name;
    private String image;

    public static UserListResponse to(User user) {
        return new UserListResponse(user.getId(), user.getName(), user.getImage());
    }

}
