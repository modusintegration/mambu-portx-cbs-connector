Feature: Test Person Operations using BDD approach

  Scenario: Create a Person
    Given I have the person payload located at "test-data/json/mambuAPI/create-person/createPersonRequestOBA.json" that I want to create a person
    When I create a person using the API
    Then I check that the status code coming from createPerson from the API response is "202"
    And I validate that the API response from createPerson has the required fields using the validation rules engine
    And I get the person id
    And I check that the status code coming from getPerson from the API response is "200"
    And I validate that the API response from findPerson has the required fields using the validation rules engine
    And I validate that the fields on the createPerson are present on the getPerson

  Scenario: Retrieve a Person by Id
    When I retrieve a person with the id "12345" using the API
    Then I check that the status code coming from getPerson from the API response is "200"
    And I validate that the API response from findPerson has the required fields using the validation rules engine

  Scenario: Retrieve a Person by first name and last name
    When I retrieve a person with the first name "John" and last name "Due" using the API
    Then I check that the status code coming from findPersonByFirstNameAndLastName from the API response is "200"
    And I validate that the API response from findPersonByFirstNameAndLastName has the required fields using the validation rules engine

  Scenario: Test Schema validator Request Related Parties PartyType
    Given I have the person payload located at "test-data/json/mambuAPI/create-person/schema-validator/createPersonRequest_SV_RelatedParty_OBA.json" that I want to create a person
    When I try to create a person using the API
    Then I check that the status code coming from createPerson from the API response is "400"
    And I receive a schema validator exception error for Person scenario like this json response "test-data/json/mambuAPI/create-person/schema-validator/createPersonResponse_SV_RelatedParty_OBA.json"