package io.portx.cbs.connector.idempotence;

import io.portx.cbs.connector.dto.ErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;


/**
 * An IdempotenceControllerHelper is a singleton that interacts with the idempotence session manager in order to
 * provide idempotence to an operation. The message sendIdempotenceOperation ensures same response on retries with same id.
 * <p>
 * * (idempotenceHelper.sendIdempotenceOperation(createOutboundWireDTO,idempotencyId,
 * <p>
 * () -> service.createWire(createOutboundWireDTO)
 * <p>
 * ))
 */

@Service
public class IdempotenceControllerHelper<T> {

  @Autowired
  IdempotentSessionManager idempotenceManager;

  //Ensures a unique invocation of the impotent operator for same id.
  public ResponseEntity<T> sendIdempotenceOperation(String operationName, Object dto, String idempotencyId, Supplier<ResponseEntity<T>> idempotentOperator) throws IdempotenceSessionManagerException {

    IdempotentSession idempotenceSession;

    HttpHeaders headerFound = new HttpHeaders();
    headerFound.add("PORTX-Idempotency", "FOUND");

    HttpHeaders headerConflict = new HttpHeaders();
    headerConflict.add("PORTX-Idempotency", "CONFLICT");

    HttpHeaders headerNew = new HttpHeaders();
    headerNew.add("PORTX-Idempotency", "NEW");

    //Recovers the operation result and returns it
    try {
      idempotenceSession = this.idempotenceManager.createOrRecoverSession(operationName, idempotencyId, dto);
     // idempotenceSession = this.idempotenceManager.createOrRecoverSession("createOutboundWire", idempotencyId, dto);
      if (idempotenceSession.hasResponse()) {
        ResponseEntity response = idempotenceSession.getResponseEntityAsObject();
        HttpHeaders headers = createHeaders(headerFound, response);
        return new ResponseEntity(response.getBody(), headers, response.getStatusCode().value());
      }
    } catch (InvalidIdempotenceOperationException | InvalidIdempotenceParametersException |
             InvalidIdempotentSessionInitStateException e) {
      return new ResponseEntity(this.createConflictResponseEntity(e.getMessage()), headerConflict, HttpStatus.CONFLICT);
    } catch (IdempotenceSessionManagerException e) {
      return new ResponseEntity(this.createErrorIdempotencyRecovery(e.getMessage(), idempotencyId), headerConflict, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Performs the idempotent operation y saves the result for future invocations
    try {
      ResponseEntity savedEntity = idempotentOperator.get();
      this.idempotenceManager.saveSessionResponse(idempotencyId, savedEntity.getStatusCode().value(), savedEntity);
      HttpHeaders headers = createHeaders(headerNew, savedEntity);
      return new ResponseEntity(savedEntity.getBody(), headers, savedEntity.getStatusCode().value());
    } catch (IdempotenceSessionManagerException e) {
      this.idempotenceManager.deleteSession(idempotenceSession.getClientRequestId());
      throw e;
    } catch (Throwable e) { // We also catch any other exception, clean up the session and throw a higher exception
      this.idempotenceManager.deleteSession(idempotenceSession.getClientRequestId());
      throw new IdempotenceSessionManagerException(e);
    }
  }

  /**
   * Return an HttpHeaders that will contain the newHeader and all headers from the ResponseEntity
   *
   * @param newHeader
   * @param savedEntity
   * @return
   */
  private HttpHeaders createHeaders(HttpHeaders newHeader, ResponseEntity savedEntity) {
    HttpHeaders headers = new HttpHeaders();
    headers.addAll(newHeader);
    headers.addAll(savedEntity.getHeaders());
    return headers;
  }

  private Object createConflictResponseEntity(String message) {
    ErrorDTO dto = new ErrorDTO()
        .code(Integer.toString(HttpStatus.CONFLICT.value()))
        .message(message);
    return dto;
  }

  private Object createErrorIdempotencyRecovery(String message, String idempotencyId) {
    ErrorDTO errorDto = new ErrorDTO()
        .code("500")
        .message("Internal error while trying to recover the idempotence session data. IdempotencyId: " + idempotencyId)
        .payload(message);
    return errorDto;
  }

  private Object createInternalError(String message, String idempotencyId) {
    ErrorDTO errorDto = new ErrorDTO()
        .code("500")
        .message("Internal error. IdempotencyId: " + idempotencyId)
        .payload(message);
    return errorDto;
  }

}