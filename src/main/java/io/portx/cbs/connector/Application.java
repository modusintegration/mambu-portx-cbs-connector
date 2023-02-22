package io.portx.cbs.connector;

import io.portx.camel.PortXCamelServletApplication;
import org.springframework.boot.SpringApplication;
// import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
//
// @CamelOpenTelemetry
public class Application extends PortXCamelServletApplication {
    public static void main(String... args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public String getCamelLogEipName() {
        return "mambu-portx-cbs-connector";
    }
}
