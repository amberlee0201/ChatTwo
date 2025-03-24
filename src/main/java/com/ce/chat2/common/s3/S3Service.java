package com.ce.chat2.common.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ce.chat2.common.exception.UnavailableS3;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.cdn}")
    private String cdn;

    private final AmazonS3 amazonS3;

    public S3FileDto uploadImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try{
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
            return S3FileDto.of(cdn+fileName, fileName);
        }catch (AmazonServiceException e){
            throw new UnavailableS3(e.getMessage());
        }
    }

    public S3FileDto modifyImage(MultipartFile file, String preFileName) throws IOException {
        //S3 이미지 삭제
        try {
            amazonS3.deleteObject(bucket, preFileName);
            return uploadImage(file);
        }catch (AmazonServiceException e){
            throw new UnavailableS3(e.getMessage());
        }
    }

    public void deleteImage(String fileName) {
        try {
            amazonS3.deleteObject(bucket, fileName);
        }catch (AmazonServiceException e){
            throw new UnavailableS3(e.getMessage());
        }
    }
}
