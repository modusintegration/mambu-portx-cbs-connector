package io.portx.cbs.connector.idempotence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.portx.cbs.connector.mapper.JsonMapper;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.http.ResponseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
 * An IdempotentSession records each client invocation of a particular idempotence operation.
 * It builds the response information of the registered operation. Is unique for a client request id.
 */
@Entity
@Table(
    name = "IdempotentSession",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "client_request_id", name = IdempotentSession.CLIENT_REQUEST_ID_CONSTRAINT_NAME)}

)
@NamedQuery(name = "IdempotentSession.findAll",
    query = "from IdempotentSession")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)

public class IdempotentSession implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String CLIENT_REQUEST_ID_CONSTRAINT_NAME = "client_request_id_un";

  public static final String STATUS_INI_STRING = "INI";
  public static final String STATUS_SAVED_STRING = "SAVED";
  public static final String IDEMPOTENCE_IS_AT_INIT_ERROR_MESSAGE_PREFIX = "idempotence is at INIT. Attempt to initialize another idempotence session for preexistent request id ";
  public static final String IDEMPOTENCE_ID_CONFLICT_ERROR_MESSAGE_PREFIX = "Idempotence conflict. Attempt to initialize another idempotence session for preexistent request id ";
  public static final String IDEMPOTENCE_OPERATION_NAME_CONFLICT_ERROR_MESSAGE_PREFIX = "The same client request cannot be bound to different operation names.";
  public static final String IDEMPOTENCE_PARAMETERS_CONFLICT_ERROR_MESSAGE_PREFIX = "The same client request cannot be bound to different parameters.";


  public enum IdempotentSessionStatus {

    // Indicates that the IdempotenceSession has been started and initialized on the database.
    INI,
    // Indicates that the IdempotenceSession has been finished with the operation result saved on the database.
    SAVED,
    // Indicates that the IdempotenceSession could not be started because there is another with same idempotence id and INI status on the database.
    INI_CONFLICT,
    // Indicates that the IdempotenceSession could not be started because there is another with same idempotence id on the database.
    ID_CONFLICT,
    // Indicates that the IdempotenceSession result cannot be retrieved because the session found on the database, has different operation name.
    OPERATION_CONFLICT,
    // Indicates that the IdempotenceSession result cannot be retrieved because the session found on the database, has different parameters.
    PARAMETER_CONFLICT,
    // Indicates that the IdempotenceSession tried to save the result, but it could not because of a transactional error.
    SAVE_PENDING;

  }


  private IdempotentSessionStatus sessionStatus;

  @Transient
  public IdempotentSessionStatus getSessionStatus() {
    return sessionStatus;
  }

  public void setSessionStatus(IdempotentSessionStatus sessionStatus) {

    this.sessionStatus = sessionStatus;

  }


  private Long id;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
  @SequenceGenerator(name = "sequenceGenerator", sequenceName = "sequenceGenerator", allocationSize = 1)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  private UUID clientRequestId;

  @NotNull
  @Column(name = "client_request_id", nullable = false, unique = true)
  public UUID getClientRequestId() {
    return this.clientRequestId;
  }

  public void setClientRequestId(UUID clientCallId) {
    this.clientRequestId = clientCallId;
  }


  private String operationName;

  @NotNull
  @Column(name = "operation_name", nullable = false, columnDefinition = "text")
  @Type(type = "text")
  public String getOperationName() {
    return this.operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }


  private String parameters;

  @NotNull
  @Column(name = "parameters", nullable = false, columnDefinition = "jsonb")
  @Type(type = "jsonb")
  public String getParameters() {
    return this.parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }


  private Date requestTime;

  @Column(name = "request_time")
  public Date getRequestTime() {
    return this.requestTime;
  }

  public void setRequestTime(Date requestTime) {
    this.requestTime = requestTime;
  }


  private int responseCode;

  @NotNull
  @Column(name = "response_code")
  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  private String responseMessage;

  @Column(name = "response_message", columnDefinition = "text")
  @Type(type = "text")
  public String getResponseMessage() {
    return responseMessage;
  }

  public void setResponseMessage(String responseMessage) {
    this.responseMessage = responseMessage;
  }


  private String responseEntity;

  @Column(name = "response_entity", columnDefinition = "jsonb")
  @Type(type = "jsonb")
  public String getResponseEntity() {
    return responseEntity;
  }

  public void setResponseEntity(String responseEntity) {
    this.responseEntity = responseEntity;
  }


  private String clientRequestIDStatus;

  @Column(name = "client_request_id_status", nullable = false)
  public String getClientRequestIdStatus() {
    return clientRequestIDStatus;
  }

  public void setClientRequestIdStatus(String clientIDStatus) {
    this.clientRequestIDStatus = clientIDStatus;
    if (clientIDStatus.equalsIgnoreCase(STATUS_INI_STRING)) this.sessionStatus = IdempotentSessionStatus.INI;
    if (clientIDStatus.equalsIgnoreCase(STATUS_SAVED_STRING)) this.sessionStatus = IdempotentSessionStatus.SAVED;
  }


  // TODO: Delete this attribute
  private boolean isValid;

  @Column(name = "isvalid", nullable = false)
  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }


  public IdempotentSession() {
  }

  public IdempotentSession(UUID clientRequestId,
                           String operationName,
                           String parameters) {

    this.clientRequestId = clientRequestId;
    this.operationName = operationName;
    this.parameters = parameters;
    this.requestTime = new Date();
    //delete:
    this.isValid = true;
  }


  // Returns true, if the client request id of the receiver was already initialized on the database
  @Transient
  public boolean hasBeenInitialized() {
    switch (sessionStatus) {
      case INI_CONFLICT:
      case ID_CONFLICT:
        return true;
      default:
        return false;
    }
  }

  // Returns true, if the client request id of the receiver was already initialized on the database
  @Transient
  public boolean hasPendingSave() {
    return sessionStatus == IdempotentSessionStatus.SAVE_PENDING;
  }


  // Returns true, if the receiver was recovered from database with status SAVED.
  public boolean hasResponse() {
    return sessionStatus == IdempotentSessionStatus.SAVED;
  }


  public void setResponseEntityAsObject(ResponseEntity responseEntity) throws InvalidIdempotenceParametersException {

    ObjectMapper mapper = JsonMapper.INSTANCE.objectMapper;
    try {
      mapper.addMixIn(ResponseEntity.class, ResponseEntityMixin.class);
      String entityAsString = mapper.writeValueAsString(responseEntity);
      this.responseEntity = entityAsString;
    } catch (JsonProcessingException e) {
      throw new InvalidIdempotenceParametersException(e);
    }

  }

  // Change the ResponseEntity representation from String to Object .
  @Transient
  public ResponseEntity getResponseEntityAsObject() throws InvalidIdempotenceParametersException {

    ObjectMapper mapper = JsonMapper.INSTANCE.objectMapper;
    try {
      mapper.addMixIn(ResponseEntity.class, ResponseEntityMixin.class);
      ResponseEntity result = mapper.readValue(this.getResponseEntity(), ResponseEntity.class);
      return result;
    } catch (JsonProcessingException e) {
      throw new InvalidIdempotenceParametersException(e);
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof IdempotentSession)) {
      return false;
    }
    return id != null && id.equals(((IdempotentSession) o).id);
  }

  @Override
  public int hashCode() {
    // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "IdempotentSession{" +
        "id=" + getId() +
        ", clientRequestId='" + getClientRequestId() + "'" +
        ", operationName='" + getOperationName() + "'" +
        ", parameters='" + getParameters() + "'" +
        ", requestTime='" + getRequestTime() + "'" +
        ", resultCode='" + getResponseCode() + "'" +
        ", clientIDStatus='" + getClientRequestIdStatus() + "'" +
        "}";
  }


  // Returns true, if the session has same parameters than the receiver.
  public boolean hasSameParametersThan(IdempotentSession session) throws InvalidIdempotenceParametersException {

    ObjectMapper mapper = JsonMapper.INSTANCE.objectMapper;
    try {
      JsonNode actualObj1 = mapper.readTree(this.parameters);
      JsonNode actualObj2 = mapper.readTree(session.parameters);

      TextNodeComparator cmp = new TextNodeComparator();
      return actualObj1.equals(cmp, actualObj2);

    } catch (JsonProcessingException e) {
      throw new InvalidIdempotenceParametersException();

    }
  }

  /**
   * Returns true if the session has same operation name than the receiver.
   *
   * @param session
   * @return
   * @throws InvalidIdempotenceOperationException
   */
  public boolean hasSameOperationNameThan(IdempotentSession session) throws InvalidIdempotenceOperationException {
    return operationName.equals(session.getOperationName());
  }

  @Transient
  public boolean isSaved() {

    return sessionStatus == IdempotentSessionStatus.SAVED;
  }


  public void setSaved() {

    sessionStatus = IdempotentSessionStatus.SAVED;
    clientRequestIDStatus = STATUS_SAVED_STRING;
  }

  public void setPendingSave() {

    sessionStatus = IdempotentSessionStatus.SAVE_PENDING;

  }

  @Transient
  public boolean isInitialized() {

    return sessionStatus == IdempotentSessionStatus.INI;
  }

  public void setInitialized() {
    sessionStatus = IdempotentSessionStatus.INI;
    clientRequestIDStatus = STATUS_INI_STRING;
  }


  /**
   * Returns true if the receiver has same (client request id, operation name, parameters) values. If the receiver has the same
   * client request id, throws and exception when either the operation name or the parameters are different.
   * This method could be extended using the session request time.
   **/
  public boolean isIdempotentToSession(IdempotentSession recoveredSession) throws IdempotenceSessionManagerException {

    if (recoveredSession.getClientRequestId().equals(this.getClientRequestId())) {
      if ((this.hasSameParametersThan(recoveredSession)) && (this.hasSameOperationNameThan(recoveredSession))) {
        return true;
      }
      if (!this.hasSameParametersThan(recoveredSession))
        throw new InvalidIdempotenceParametersException(IDEMPOTENCE_PARAMETERS_CONFLICT_ERROR_MESSAGE_PREFIX);
      if (!this.hasSameOperationNameThan(recoveredSession))
        throw new InvalidIdempotenceOperationException(IDEMPOTENCE_OPERATION_NAME_CONFLICT_ERROR_MESSAGE_PREFIX);
    }
    return false;
  }

  /**
   * Sets a conflict status response when the same client is bound to different parameters
   *
   * @param recoveredSession
   */
  public void setConflictedParametersResponse(IdempotentSession recoveredSession) {
    {
      this.sessionStatus = IdempotentSessionStatus.PARAMETER_CONFLICT;
      this.setResponseMessage(IDEMPOTENCE_PARAMETERS_CONFLICT_ERROR_MESSAGE_PREFIX +
          " Operation Name:'" + this.getOperationName() + "'" +
          " Client Request ID:'" + this.getClientRequestId() + "'" +
          " Parameteres Received:'" + this.getParameters() + "'" +
          " Parameteres Expected:'" + recoveredSession.getParameters() + "'"
      );
    }


  }

  /**
   * Sets a conflict status response when the same client is bound to different parameters
   *
   * @param recoveredSession
   */
  public void setConflictedOperationResponse(IdempotentSession recoveredSession) {

    this.sessionStatus = IdempotentSessionStatus.OPERATION_CONFLICT;
    this.setResponseMessage(IDEMPOTENCE_OPERATION_NAME_CONFLICT_ERROR_MESSAGE_PREFIX +
        " Operation Name:'" + this.getOperationName() + "'" +
        " Client Request ID:'" + this.getClientRequestId() + "'" +
        " Parameteres Received:'" + this.getParameters() + "'" +
        " Parameteres Expected:'" + recoveredSession.getParameters() + "'"
    );


  }

  /**
   * Sets a conflict status response, if more than one invocation for the same request ids arrives.
   */
  public void setExistingSessionAtInitResponse() {

    this.sessionStatus = IdempotentSessionStatus.INI_CONFLICT;
    this.setResponseMessage(IDEMPOTENCE_IS_AT_INIT_ERROR_MESSAGE_PREFIX +
        " Operation Name:'" + this.getOperationName() + "'" +
        " Client Request ID:'" + this.getClientRequestId() + "'" +
        " Parameteres Received:'" + this.getParameters() + "'"
    );


  }

  /**
   * Sets a conflict status response, if more than one invocation for the same request ids arrives.
   */
  public void setExistingSessionConflictResponse() {

    this.sessionStatus = IdempotentSessionStatus.ID_CONFLICT;
    this.setResponseMessage(IDEMPOTENCE_ID_CONFLICT_ERROR_MESSAGE_PREFIX +
        " Operation Name:'" + this.getOperationName() + "'" +
        " Client Request ID:'" + this.getClientRequestId() + "'" +
        " Parameteres Received:'" + this.getParameters() + "'"
    );


  }


}
