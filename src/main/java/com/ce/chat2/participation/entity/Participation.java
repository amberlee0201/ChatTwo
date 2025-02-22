package com.ce.chat2.participation.entity;

import com.ce.chat2.common.entity.BaseEntity;
import com.ce.chat2.room.entity.Room;
import com.ce.chat2.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participation extends BaseEntity {
    int id;
    Room room;
    User user;
    boolean isOut;
}
