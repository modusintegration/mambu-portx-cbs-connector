package io.portx.cbs.connector.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated // CBIS-1247 Refactor this to inject ObjectMapper that comes from ObjectMapperConfiguration
public class JsonMapper {

  public static final JsonMapper INSTANCE = new JsonMapper();
  public final ObjectMapper objectMapper;

  public JsonMapper() {
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}