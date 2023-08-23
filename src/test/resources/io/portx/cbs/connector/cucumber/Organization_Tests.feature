Feature: Test Organization Operations using BDD approach

  Scenario: Create an Organization
    Given I have the organization payload located at "test-data/json/mambuAPI/create-organization/createOrganizationRequestOBA.json" that I want to create an organization
    When I create an organization using the API
    Then I check that the status code coming from createOrganization operation response is "202"
    And I validate that the API response for createOrganization has the required fields using the validation rules engine
    And I get the organization id
    And I check that the status code coming from getOrganization from the API response is "200"
    And I validate that the API response from findOrganization has the required fields using the validation rules engine
    And I validate that the fields on the createOrganization are present on the getOrganization

  Scenario: Retrieve an Organization
    When I retrieve an organization with the id "12345" using the API
    Then I check that the status code coming from getOrganization from the API response is "200"
    And I validate that the API response from findOrganization has the required fields using the validation rules engine