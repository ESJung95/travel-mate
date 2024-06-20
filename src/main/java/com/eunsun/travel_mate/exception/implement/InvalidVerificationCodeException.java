package com.eunsun.travel_mate.exception.implement;

import com.eunsun.travel_mate.enums.ErrorCode;
import com.eunsun.travel_mate.exception.AbstractException;
import lombok.Getter;

@Getter
public class InvalidVerificationCodeException extends AbstractException {

  private final ErrorCode errorCode;

  public InvalidVerificationCodeException (ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}

