package com.ce.chat2.user.exception;

import com.ce.chat2.common.exception.CustomBaseException;
import com.ce.chat2.common.exception.ErrorCode;

public class UserNotFound extends CustomBaseException {
  public UserNotFound() {
    super(ErrorCode.USER_NOT_FOUND);
  }
}
