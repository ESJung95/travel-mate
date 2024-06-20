package com.eunsun.travel_mate.exception.implement;

import com.eunsun.travel_mate.enums.ErrorCode;
import com.eunsun.travel_mate.exception.AbstractException;
import lombok.Getter;

@Getter
public class EmailDuplicateException extends AbstractException {

  private final ErrorCode errorCode;

  public EmailDuplicateException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

}