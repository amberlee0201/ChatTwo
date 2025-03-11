package com.ce.chat2.notification.event;

import com.ce.chat2.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendFollowedEvent {
    private final User from;
    private final User to;
}