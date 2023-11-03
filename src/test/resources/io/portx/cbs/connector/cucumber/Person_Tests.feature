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
