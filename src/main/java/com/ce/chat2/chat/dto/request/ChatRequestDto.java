package com.ce.chat2.chat.dto.request;

import com.ce.chat2.common.s3.S3FileDto;
import lombok.Data;

@Data
public class ChatRequestDto {
    private String roomId;
    private String content;
    private String senderName;
    private String fileData;
    private String fileName;
    private String fileType;
    private String filePath;
    private long sendTimeStamp;
    private int userId;

    public void withRoomId(String roomId){
        this.roomId=roomId;
    }
    public void withFile(S3FileDto fileDto){
        this.filePath = fileDto.getFilePath();
    }
}
