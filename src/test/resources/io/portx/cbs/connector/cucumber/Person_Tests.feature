Feature: Test Person Operations using BDD approach

  Scenario: Create a Person
    Given I have the person payload located at "test-data/json/mambuAPI/create-person/createPersonRequestOBA.json" that I want to create a person
    When I create a person using the API
    Then I check that the status code coming from createPerson from the API response is "202"
    And I validate that the API response from createPerson has the required fields using the validation rules engine
    And I get the person id
    And I check that the status code coming from getPerson from the API response is "200"
    And I validate that the API response from findPerson has the required fields using the validation rules engine
    And I validate that the fields on the requests are present on the response

  Scenario: Retrieve a Person
    When I retrieve a person with the id "12345" using the API
    Then I check that the status code coming from getPerson from the API response is "200"
    And I validate that the API response from findPerson has the required fields using the validation rules engine