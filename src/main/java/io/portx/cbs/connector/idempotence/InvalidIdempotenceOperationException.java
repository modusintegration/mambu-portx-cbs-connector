package io.portx.cbs.connector.idempotence;

public class InvalidIdempotenceOperationException extends IdempotenceSessionManagerException{

  public InvalidIdempotenceOperationException() {
  }

  public InvalidIdempotenceOperationException(String message) {
    super(message);
  }

  public InvalidIdempotenceOperationException(Throwable cause) {
    super(cause);
  }

  public InvalidIdempotenceOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidIdempotenceOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super();
  }
}