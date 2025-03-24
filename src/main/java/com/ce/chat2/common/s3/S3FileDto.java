package com.ce.chat2.common.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3FileDto {
    private String filePath; // 전체 경로
    private String fileName; // UUID+OriginalFileName
    private String fileType;

    public static S3FileDto of(String filePath, String fileName, String fileType){
        return S3FileDto.builder()
            .filePath(filePath)
            .fileName(fileName)
            .fileType(fileType)
            .build();
    }
}
