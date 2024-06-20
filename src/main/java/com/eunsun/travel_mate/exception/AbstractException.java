package com.eunsun.travel_mate.exception;

import com.eunsun.travel_mate.enums.ErrorCode;

public abstract class AbstractException extends RuntimeException {

  public abstract ErrorCode getErrorCode();

  public AbstractException (String message) {
    super(message);
  }

}
