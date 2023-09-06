package io.portx.cbs.connector.processor;

import java.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import io.portx.cbs.connector.dto.ErrorDTO;
import io.portx.cbs.connector.idempotence.IdempotenceControllerHelper;
import io.portx.cbs.connector.idempotence.IdempotentSessionManager;
import io.portx.cbs.connector.idempotence.IdempotenceSessionManagerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

public class IdempotenceProcessor implements Processor {

    @Autowired
    IdempotentSessionManager idempotenceManager;    
    @Autowired
    private IdempotenceControllerHelper idempotenceHelper;
    public void process(Exchange exchange) throws Exception {
        // String idempotencyId = exchange.getProperty("personId").toString();
        String idempotencyId = exchange.getIn().getHeader("idempotencyId").toString();
        String operationId = exchange.getIn().getHeader("operationId").toString();
        
        Object body = exchange.getIn().getBody();

        System.out.println("IDEMPOTENCY ID ");
        System.out.println(idempotencyId);
        System.out.println(operationId);


        if (idempotencyId != null) {
            try {
              return idempotenceHelper.sendIdempotenceOperation(operationId, body, idempotencyId, null);
            } catch (IdempotenceSessionManagerException e) {
              throw e;
            }
          } else {
            System.out.println("==========IDEMPOTENCY ID IS NOT SENT======== ");

          }
    }
}