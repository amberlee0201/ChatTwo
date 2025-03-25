package com.ce.chat2.follow.dto;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseData {
    public static final ResponseData SUCCESS_RESPONSE = new ResponseData("SUCCESS", "성공");

    @NonNull
    String code;

    @NonNull
    String message;

}
