package io.portx.cbs.connector.idempotence;

public class IdempotenceSessionManagerException extends RuntimeException {

  public IdempotenceSessionManagerException() {
  }

  public IdempotenceSessionManagerException(String message) {
    super(message);
  }

  public IdempotenceSessionManagerException(Throwable cause) {
    super(cause);
  }

  public IdempotenceSessionManagerException(String message, Throwable cause) {
    super(message, cause);
  }

  public IdempotenceSessionManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}