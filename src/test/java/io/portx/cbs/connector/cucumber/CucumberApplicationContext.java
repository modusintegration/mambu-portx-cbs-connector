package io.portx.cbs.connector.cucumber;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import io.portx.cbs.connector.Application;
import io.portx.cbs.connector.cucumber.definitions.AccountCucumberTestDefinition;
import io.portx.cbs.connector.cucumber.definitions.OrganizationCucumberTestDefinition;
import io.portx.cbs.connector.cucumber.definitions.PersonCucumberTestDefinition;
import io.portx.cbs.connector.cucumber.util.WiremockFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8042"
        }, classes = {Application.class})
@ActiveProfiles("cucumber")
public class CucumberApplicationContext {

    private final OrganizationCucumberTestDefinition organizationCucumberTestDefinition;
    private final PersonCucumberTestDefinition personCucumberTestDefinition;
    private final AccountCucumberTestDefinition accountCucumberTestDefinition;

    public CucumberApplicationContext() {
        this.accountCucumberTestDefinition = new AccountCucumberTestDefinition();
        this.organizationCucumberTestDefinition = new OrganizationCucumberTestDefinition();
        this.personCucumberTestDefinition = new PersonCucumberTestDefinition();
    }

    @BeforeAll
    public static void beforeAll() {
        WiremockFactory.init();
    }

    @AfterAll
    public static void afterAll() {
        WiremockFactory.stop();
    }
}
