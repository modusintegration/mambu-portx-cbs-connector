package io.portx.cbs.connector;

import javax.annotation.Generated;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

/**
 * Generated from OpenApi specification by Camel REST DSL generator.
 */
@Generated("org.apache.camel.generator.openapi.PathGenerator")
@Component
public final class BankAccountManagement extends RouteBuilder {
    /**
     * Defines Apache Camel routes using REST DSL fluent API.
     */
    public void configure() {

        restConfiguration().component("servlet").contextPath("/");

        rest("/mambu-portx-cbs-connector/1.0")
            .post("/accounts")
                .id("openAccount")
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("idempotencyId")
                    .type(RestParamType.header)
                    .dataType("string")
                    .required(true)
                    .description("Idempotency identifier used by the client when making repeated calls")
                .endParam()
                .param()
                    .name("bankId")
                    .type(RestParamType.header)
                    .dataType("string")
                    .required(true)
                    .description("Bank Identification")
                .endParam()
                .param()
                    .name("body")
                    .type(RestParamType.body)
                    .required(true)
                .endParam()
                .to("direct:openAccount");

        rest("/mambu-portx-cbs-connector/1.0")
            .get("/accounts/{accountId}")
                .id("findAccount")
                .produces("application/json")
                .param()
                    .name("accountId")
                    .type(RestParamType.path)
                    .required(true)
                    .description("Account Identification")
                .endParam()
                .param()
                    .name("bankId")
                    .type(RestParamType.header)
                    .dataType("string")
                    .required(true)
                    .description("Bank Identification")
                .endParam()
                .to("direct:findAccount");

        rest("/mambu-portx-cbs-connector/1.0")
            .get("/banks")
                .id("findBanks")
                .produces("application/json")
                .to("direct:findBanks");

        rest("/mambu-portx-cbs-connector/1.0")
            .get("/branches")
                .id("findBranches")
                .produces("application/json")
                .to("direct:findBranches");

    }
}
