package io.portx.cbs.connector.idempotence;

public class InvalidIdempotentSessionInitStateException extends IdempotenceSessionManagerException{


  public InvalidIdempotentSessionInitStateException() {
  }

  public InvalidIdempotentSessionInitStateException(String message) {
    super(message);
  }

  public InvalidIdempotentSessionInitStateException(Throwable cause) {
    super(cause);
  }

  public InvalidIdempotentSessionInitStateException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidIdempotentSessionInitStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super();
  }
}