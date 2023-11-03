Feature: Test Account Operations using BDD approach

  Scenario: Create an Account
    Given I have the account payload located at "test-data/json/mambuAPI/create-account/createAccountRequestOBA.json" that I want to create an account
    When I create an account using the API
    Then I check that the status code coming from createAccount from the API response is "202"
    And I validate that the API response from createAccount has the required fields using the validation rules engine
    And I get the account id
    And I check that the status code coming from getAccount from the API response is "200"
    And I validate that the API response from getAccount has the required fields using the validation rules engine
    And I validate that the fields on the createAccount are present on the getAccount

  Scenario: Retrieve an account by Id
    When I retrieve an account with the id "99999999" using the API
    Then I check that the status code coming from getAccount from the API response is "200"
    And I validate that the API response from getAccount has the required fields using the validation rules engine

  Scenario: Test Schema validator BR Account 009 SingleOwnerAccountParties
    Given I have the account payload located at "test-data/json/mambuAPI/create-account/schema-validator/createAccountRequest_SV_009_SingleOwnerAccountParties_OBA.json" that I want to create an account
    When I try to create an account using the API
    Then I check that the status code coming from createAccount from the API response is "400"
    And I receive a schema validator exception error for Account scenario like this json response "test-data/json/mambuAPI/create-account/schema-validator/createAccountResponse_SV_009_SingleOwnerAccountParties_OBA.json"