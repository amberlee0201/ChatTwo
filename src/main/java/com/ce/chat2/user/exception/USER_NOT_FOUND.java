package com.ce.chat2.user.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class USER_NOT_FOUND extends CustomBaseException {
  public USER_NOT_FOUND(String message) {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
