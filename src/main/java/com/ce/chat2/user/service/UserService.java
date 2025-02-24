package com.ce.chat2.user.service;

import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UnAuthorizedUser;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User updateUserInfo(User loginUser,int id, String name, MultipartFile image) {
        if(loginUser.getId() != id) throw new UnAuthorizedUser();
        User savedUser = userRepository.findById(id)
            .orElseThrow(UserNotFound::new);

        //TODO S3연동 이후 Image 처리 로직 추가
        log.info("image name= {}", image.getOriginalFilename());

        savedUser.updateInfo(name);
        return savedUser;
    }
}

