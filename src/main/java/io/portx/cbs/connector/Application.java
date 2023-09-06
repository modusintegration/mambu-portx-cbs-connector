package io.portx.cbs.connector;

import io.portx.camel.PortXCamelServletApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
//
// @CamelOpenTelemetry
// @ComponentScan("com.baeldung.autowire.sample")
// @ComponentScan(basePackages = {"io.portx.cbs.connector", "io.portx.cbs.connector.repository"})
@EntityScan(basePackages = {"io.portx.cbs.connector.idempotence"})
@EnableJpaRepositories(basePackages = "io.portx.cbs.connector.repository")
public class Application extends PortXCamelServletApplication {
    public static void main(String... args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public String getCamelLogEipName() {
        return "mambu-portx-cbs-connector";
    }
}
