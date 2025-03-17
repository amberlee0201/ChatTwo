package com.ce.chat2.notification.event;

import com.ce.chat2.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendFollowedEvent {
    private final User from;
    private final User to;

    public String getMessage() {
        return from.getName() + "님이 친구 요청을 보냈습니다.";
    }
}
