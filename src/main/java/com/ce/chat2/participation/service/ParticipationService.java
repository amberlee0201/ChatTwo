package com.ce.chat2.participation.service;

import com.ce.chat2.participation.entity.Participation;
import com.ce.chat2.participation.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    public List<String> sendInitialRooms(String userId) {
        List<String> roomList = participationRepository.findAllRoomsByUserId(userId)
                .stream()
                .map(Participation::getRoomId)
                .toList();
        log.info("roomList: {}", roomList);
        return roomList;
    }
}
