package com.ce.chat2.user.service;

import com.ce.chat2.common.s3.S3FileDto;
import com.ce.chat2.common.s3.S3Service;
import com.ce.chat2.user.entity.User;
import com.ce.chat2.user.exception.UnAuthorizedUser;
import com.ce.chat2.user.exception.UserNotFound;
import com.ce.chat2.user.repository.UserRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public User updateUserInfo(User loginUser,int id, String name, MultipartFile image)
        throws IOException {
        if(loginUser.getId() != id) throw new UnAuthorizedUser();
        User savedUser = userRepository.findById(id)
            .orElseThrow(UserNotFound::new);

        S3FileDto dto;
        if(StringUtils.hasText(savedUser.getFileName())){
            dto = s3Service.modifyImage(image, savedUser.getFileName());
        }else{
            dto = s3Service.uploadImage(image);
        }

        savedUser.updateInfo(name, dto);
        return savedUser;
    }
}

