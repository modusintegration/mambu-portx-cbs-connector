package io.portx.cbs.connector.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ErrorDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class ErrorDTO   {

  @JsonProperty("code")
  private String code;

  @JsonProperty("message")
  private String message;

  @JsonProperty("payload")
  private Object payload;

  public ErrorDTO code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Error code.
   * @return code
  */
  
  @Schema(name = "code", description = "Error code.", required = false)
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ErrorDTO message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Error message text.
   * @return message
  */
  
  @Schema(name = "message", description = "Error message text.", required = false)
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ErrorDTO payload(Object payload) {
    this.payload = payload;
    return this;
  }

  /**
   * Error payload as json object.
   * @return payload
  */
  
  @Schema(name = "payload", description = "Error payload as json object.", required = false)
  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorDTO error = (ErrorDTO) o;
    return Objects.equals(this.code, error.code) &&
        Objects.equals(this.message, error.message) &&
        Objects.equals(this.payload, error.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, payload);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorDTO {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    payload: ").append(toIndentedString(payload)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}