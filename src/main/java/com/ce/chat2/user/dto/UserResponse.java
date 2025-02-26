package com.ce.chat2.user.dto;

import java.util.List;

import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private int id;
    private String name;
    private String image;
    private String fileName;
    private UserRole role;

    public static UserResponse to(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getImage(), user.getFileName(), user.getRole());
    }

}
