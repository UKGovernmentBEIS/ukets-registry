@functional-area-user-registration
@exec-manual

Feature: User registration - Personal details registration

  Epic: User registration and sign-in
  Version: v2.2 (06/12/2019)
  Story: (& 4.1.4) Register personal and work contact details
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=16&modificationDate=1575643746000&api=v2

  @test-case-id-50861929960
  Scenario: As user I can successfully enter my personal details
    Given I navigate to the "Enter Personal Details" screen
    And I accept all cookies
    Then I am presented with the "Enter Personal Details" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Enter Work Contact Details" screen

  @test-case-id-50861929969
  Scenario: As user I can register only if I am over 18 years old
    Given I navigate to the "Enter Personal Details" screen
    And I accept all cookies
    Then I am presented with the "Enter Personal Details" screen
    When I enter the mandatory fields leaving the mandatory field "permit-year" empty
    And I enter value "2003" in "permit-year" field
    And I click the "Continue" button
    Then I see an error summary with "You have to be 18 years of age or older to register"
    And I see an error detail for field "permit-date" with content "Error: You have to be 18 years of age or older to register."

  @test-case-id-26484429779
  Scenario Outline: As user I cannot submit empty mandatory fields for country uk
    Given I navigate to the "Enter Personal Details" screen
    And I accept all cookies
    Then I am presented with the "Enter Personal Details" screen
    When I enter the mandatory fields leaving the mandatory field "<fieldName>" empty
    And I click the "Continue" button
    Then I see an error summary with "<errorMessage>"
    And I see an error detail for field "<field>" with content "Error: <errorMessage>"

    Examples:
      | fieldName         | field       | errorMessage               |
      | firstName         | event-name  | Enter your first name.     |
      | lastName          | event-name  | Enter your last name.      |
      | buildingAndStreet | event-name  | Enter your address line 1. |
      | townOrCity        | event-name  | Enter your town or city.   |
      | postCode          | event-name  | Enter your UK post code.   |
      | permit-year       | permit-date | Enter your birth date.     |
      | permit-month      | permit-date | Enter your birth date.     |
      | permit-day        | permit-date | Enter your birth date.     |

  @test-case-id-50861930002
  Scenario: As user I can submit empty postcode field for country not equals to uk
    Given I navigate to the "Enter Personal Details" screen
    And I accept all cookies
    Then I am presented with the "Enter Personal Details" screen
    And I enter the mandatory fields which are
      | fieldId           | field_value   |
      | firstName         | Nikos         |
      | lastName          | Nikolaou      |
      | buildingAndStreet | Nikou str. 42 |
      | townOrCity        | Athens        |
      | country           | Greece        |
      | permit-day        | 01            |
      | permit-month      | 02            |
      | permit-year       | 1970          |
      | countryOfBirth    | Greece        |
    And I click the "Continue" button
    Then I am presented with the "Enter Work Contact Details" screen

  @test-case-id-50861930021
  Scenario: As user when I come back from Enter Work Contact Details screen I see data retained
    Given I navigate to the "Enter Personal Details" screen
    And I accept all cookies
    Then I am presented with the "Enter Personal Details" screen
    And I enter the mandatory fields which are
      | fieldId           | field_value   |
      | firstName         | Nikos         |
      | lastName          | Nikolaou      |
      | buildingAndStreet | Nikou str. 42 |
      | townOrCity        | Athens        |
      | country           | Greece        |
      | permit-day        | 01            |
      | permit-month      | 02            |
      | permit-year       | 1970          |
      | countryOfBirth    | Greece        |
    When I click the "Continue" button
    Then I am presented with the "Enter Work Contact Details" screen
    When I click the "Back" link
    Then I am presented with the "Enter Personal Details" screen
    And I see the following values in the fields
      | fieldId           | field_value   |
      | firstName         | Nikos         |
      | lastName          | Nikolaou      |
      | buildingAndStreet | Nikou str. 42 |
      | townOrCity        | Athens        |
      | country           | Greece        |
      | permit-day        | 01            |
      | permit-month      | 02            |
      | permit-year       | 1970          |
      | countryOfBirth    | Greece        |
