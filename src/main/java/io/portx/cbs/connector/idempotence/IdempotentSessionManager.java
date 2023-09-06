package io.portx.cbs.connector.idempotence;

import java.util.UUID;
//import javax.enterprise.context.ApplicationScoped;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import javax.transaction.*;

import io.portx.cbs.connector.mapper.JsonMapper;
import io.portx.cbs.connector.repository.IdempotentSessionRepository;

import javax.persistence.PersistenceException;


import com.fasterxml.jackson.core.JsonProcessingException;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/**
 * An IdempotentSessionManager is a singleton that manages idempotence sessions in order to provide the same
 * semantic response for the same request (client id invocation, operation, parameters).
 * <p>
 */
@Service
public class IdempotentSessionManager {

  private static final Logger logger = Logger.getLogger(IdempotentSessionManager.class);

  @Autowired
  IdempotentSessionRepository sessionRepository;

  /**
   * Returns the proper session to build the response from.
   * <p>
   * It first looks for an existing session that matches the parameters ( operationName, clientRequestID and parameters ).
   * It there's no session, it creates one and returns it
   * If there's already a session, it checks that is idempotent to the request; if not it returns an errored session
   *
   * @param operationName
   * @param clientRequestID must not be null
   * @param parameter
   * @return null if there's no existing session, or the session to use if there's one ( either OK or on a conflicted state )
   * @throws IdempotenceSessionManagerException if the parameters can't be serialized to JSON, which this class uses
   *                                            to compare parameters
   */
  public IdempotentSession createOrRecoverSession(String operationName, String clientRequestID, Object parameter) throws IdempotenceSessionManagerException {
    logger.debugf("recoverSession operationName {} clientRequestID {}", operationName, clientRequestID);
    if (clientRequestID == null) {
      throw new IdempotenceSessionManagerException("clientRequestID must not be null");
    }
    try {
      int maxAttempts = 10;
      String parameterAsString = JsonMapper.INSTANCE.objectMapper.writeValueAsString(parameter);
      IdempotentSession newSession = new IdempotentSession(UUID.fromString(clientRequestID), operationName, parameterAsString);

      IdempotentSession resultSession = this.recoverIdempotentSession(newSession);
      for (int attempt = 0; resultSession.hasBeenInitialized() && attempt < maxAttempts; attempt++) {
        // If the previous attempt failed, wait a bit to have a better change that the existing transaction to finishes
        // before the next attempt
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // Explicitly ignored
        }
        logger.debug("retrying " + clientRequestID + "; attempt: " + attempt);
        newSession = new IdempotentSession(UUID.fromString(clientRequestID), operationName, parameterAsString);
        resultSession = this.recoverIdempotentSession(newSession);
      }

      if (resultSession.hasBeenInitialized())
        throw new InvalidIdempotentSessionInitStateException(resultSession.getResponseMessage());
      return resultSession;
    } catch (JsonProcessingException e) {
      throw new IdempotenceSessionManagerException("Couldn't serialize the operation parameters");
    }
  }

  /**
   * Analyses the invocations conditions and returns the proper session that will build the response.
   * <p>
   * It first tries to find an existent session with the clientRequestId, and then analyses it
   * - If the existing session is at the INIT state, sets the currentSession as conflicted and returns it
   * - If the existing session is at the SAVE state and the same parameters, then returns it
   * - If the existing session is at the SAVE state but different parameters, sets the currentSession as conflicted and returns it
   * - If there's no existent session, then persist the current one and return it
   *
   * @return null if there's no existing session, or the session to use if there's one ( either OK or on a conflicted state )
   */
  @Transactional
  protected IdempotentSession recoverIdempotentSession(IdempotentSession newSession) throws IdempotenceSessionManagerException {
    IdempotentSession recoveredSession = sessionRepository.findIdempotentSessionByClientRequestId(newSession.getClientRequestId());
    if (recoveredSession != null) {
      if (recoveredSession.isInitialized()) {
        // An idempotence session is found in an INIT state, meaning it's in progress and the response hasn't been saved yet
        // Returns a session duplicate invocation response
        newSession.setExistingSessionAtInitResponse();
        logger.debug("Found an in-progress session: INI. " + newSession.getResponseMessage());


        return newSession;
      } else if (recoveredSession.isSaved()) {
        //An idempotence session with the response is found.
        try {
          if (newSession.isIdempotentToSession(recoveredSession)) {
            logger.debug("Found an idempotent session");
            return recoveredSession;
          } else {
            throw new RuntimeException("Unexpected condition: newSession.isIdempotentToSession(recoveredSession) returned false " +
                " for newSession: " + newSession + " and recoveredSession: " + recoveredSession + " and they should have " +
                " the same clientRequestId");
          }
          // if the new session has conflicted parameters.
        } catch (InvalidIdempotenceParametersException e) {
          // sets the current session with the proper response and returns it.
          newSession.setConflictedParametersResponse(recoveredSession);
          logger.debug("Found an invalid idempotent session (InvalidIdempotenceParametersException): " + newSession.getResponseMessage());

          //newSession.setIsFailed(true);

          throw e;
          // if the new session has conflicted operation name.
        } catch (InvalidIdempotenceOperationException e) {
          // sets the current session with the proper response and returns it.
          newSession.setConflictedOperationResponse(recoveredSession);
          logger.debug("Found an invalid idempotent session (InvalidIdempotenceOperationException): " + newSession.getResponseMessage());

          //newSession.setIsFailed(true);
          throw e;
        }
      } else {
        throw new RuntimeException("Unexpected condition: recoveredSession is not at isInitialized or at isSaved. " +
            " recoveredSession: " + recoveredSession);
      }
    }

    //There isn't any idempotence session for the client request id.
    try {
      //Initializes the session for the client id and updates the repository.
      logger.debug("Initializing and saving the currentSession");
      newSession.setInitialized();
      sessionRepository.saveAndFlush(newSession);
      logger.debugf("Session not found, created a new one and returning it");
      return newSession;
      // It may be that between we looked for a session and saved this one another thread or process
      // won the race and saved its session first
      // This would happen when using a SERIALIZABLE isolation level
    } catch (LockAcquisitionException | OptimisticLockException e) {
      logger.debug("LockException " + e.getMessage());
      //sets a conflict invocation response and returns the current session.
      newSession.setExistingSessionConflictResponse();
      logger.debug("DuplicatedSession " + newSession.getResponseMessage());

      return newSession;
      // This would happen when not using a SERIALIZABLE isolation level, so the inserts will be rejected
    } catch (PersistenceException | DataIntegrityViolationException e) {
      logger.warn("PersistenceException when trying to save a new newSession. " + e.getMessage());
      logger.debug("PersistenceException when trying to save a new newSession. cause: " + e.getCause(), e);
      if (e.getCause() instanceof ConstraintViolationException &&
          this.isIdempotentSessionConstraintName(((ConstraintViolationException) e.getCause()).getConstraintName())) {
        //sets a conflict invocation response and returns the current session.
        newSession.setExistingSessionConflictResponse();
        logger.debug("DuplicatedSession " + newSession.getResponseMessage());
        return newSession;
      } else {
        logger.warn("Unhandled PersistenceException when trying to save a new newSession.", e);
        throw e;
      }
    } catch (Throwable t) {
      logger.warn("Throwable when trying to save a new newSession. " + t.getMessage());
      logger.debug("Throwable when trying to save a new newSession. cause: " + t.getCause(), t);
      throw t;
    }
  }

  private boolean isIdempotentSessionConstraintName(String constraintName) {
    return (IdempotentSession.CLIENT_REQUEST_ID_CONSTRAINT_NAME.equals(constraintName) ||
        (constraintName != null && constraintName.toLowerCase().startsWith("uk_")));
  }

  /**
   * Saves the current session with a responseEntity and status. Will be used to build the response on future invocations.
   *
   * @param sessionId
   * @param responseStatusCode
   * @param responseEntity
   * @throws InvalidIdempotenceParametersException
   */
  @Transactional
  public IdempotentSession saveSession(String sessionId, int responseStatusCode, ResponseEntity responseEntity) throws IdempotenceSessionManagerException {
    IdempotentSession session = null;
    if (sessionId != null) {
      session = sessionRepository.findIdempotentSessionByClientRequestId(UUID.fromString(sessionId));
      if (!session.isInitialized()) {
        throw new InvalidIdempotentSessionInitStateException("saveSession called with a session which is not on an INIT state, it's at: " + session.getSessionStatus());
      }

      session.setResponseCode(responseStatusCode);
      session.setResponseEntityAsObject(responseEntity);
      session.setSaved();
      logger.debugf("About to save: {}", session);

      sessionRepository.save(session);
      sessionRepository.flush();
    } else {
      throw new IdempotenceSessionManagerException("saveSession called with a null sessionId");
    }
    logger.debug("saveCurrentSession - COMMITTED");
    return session;
  }


  /**
   * Saves the current session with a responseEntity and status. Will be used to build the response on future invocations.
   *
   * @param sessionId
   * @param responseStatusCode
   * @param responseEntity
   * @throws IdempotenceSessionManagerException
   */
  public void saveSessionResponse(String sessionId, int responseStatusCode, ResponseEntity responseEntity) throws IdempotenceSessionManagerException {


    int maxAttempts = 5;
    IdempotentSession resultSession = null;


    try {
      resultSession = this.saveSession(sessionId, responseStatusCode, responseEntity);
    } catch (RollbackException e) {
      resultSession.setPendingSave();
    }


    for (int attempt = 0; resultSession.hasPendingSave() && attempt < maxAttempts; attempt++) {
      // If the previous attempt failed, wait a bit to have a better change that the existing transaction to finishes
      // before the next attempt
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // Explicitly ignored
      }
      logger.debug("retrying save" + sessionId + "; attempt: " + attempt);

      try {
        resultSession = this.saveSession(sessionId, responseStatusCode, responseEntity);
      } catch (RollbackException e) {
        resultSession.setPendingSave();
      }
    }

    if (resultSession.hasPendingSave())
      logger.error("Save retries have failed for operation:" + resultSession.getOperationName() + "session id:" + sessionId);
  }

  @Transactional
  public void deleteSession(UUID sessionId) {
    if (sessionId != null) {
      sessionRepository.deleteByClientRequestId(sessionId);
      logger.debugf("Session with id {} deleted", sessionId);
    } else {
      throw new RuntimeException("deleteSession called with a null sessionId");
    }
  }
}