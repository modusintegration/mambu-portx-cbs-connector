package io.portx.cbs.connector.idempotence;


public class InvalidIdempotenceParametersException extends IdempotenceSessionManagerException {

  public InvalidIdempotenceParametersException() {
  }

  public InvalidIdempotenceParametersException(String message) {
    super(message);
  }

  public InvalidIdempotenceParametersException(Throwable cause) {
    super(cause);
  }

  public InvalidIdempotenceParametersException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidIdempotenceParametersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super();
  }
}