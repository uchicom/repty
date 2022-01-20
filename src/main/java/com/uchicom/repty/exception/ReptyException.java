// (C) 2022 uchicom
package com.uchicom.repty.exception;

public class ReptyException extends RuntimeException {

  public ReptyException(Throwable cause) {
    super(cause);
  }

  public ReptyException(String message, Throwable cause) {
    super(message, cause);
  }
}
