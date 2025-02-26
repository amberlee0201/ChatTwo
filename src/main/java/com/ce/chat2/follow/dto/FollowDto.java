package com.ce.chat2.follow.dto;

import com.ce.chat2.follow.entity.Follow;
import com.ce.chat2.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {
    private int id;
    private User from;
    private User to;
    private boolean isBreak;

    public static FollowDto to(Follow follow) {
        return FollowDto.builder()
                .id(follow.getId())
                .from(follow.getFrom())
                .to(follow.getTo())
                .isBreak(follow.isBreak())
                .build();
    }

}