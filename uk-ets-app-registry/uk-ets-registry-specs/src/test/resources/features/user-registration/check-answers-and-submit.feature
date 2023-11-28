@functional-area-user-registration

Feature: User registration - Check answers and submit

  Epic: User registration and sign-in
  Version: 1.0 (19/09/2019)
  Story: Register personal and work contact details
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=12&amp;modificationDate=1568901007000&amp;api=v2

  @test-case-id-84724329042
  Scenario Outline: As user I would like to see my phone number in international format
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
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
      | workTownOrCity               | Smyrna        |
    And I enter value "<phone_number>" in "workPhone-phone-number" field
    And I enter value "<country_code>" in "workPhone-country-code" field
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I enter value "What a beautiful day" in "Memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "Choose Password" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Check answers and submit" screen
    Then I see the following fields having the values:
      | fieldName | field_value |
      | phone     | <value>     |

    Examples:
      | country_code | phone_number    | value                   |
      | UK           | (020) 1234 1234 | +UK (44)(020) 1234 1234 |
      | GR           | 2101010200      | +GR (30)2101010200      |

  @exec-manual @test-case-id-84724329071
  Scenario Outline: As user when I click on link Change I navigate to respective screen
    Given I navigate to the "Check answers and submit" screen
    And I accept all cookies
    Then I am presented with the "Check answers and submit" screen
    When I click the "Change" link that navigates to "<screen_name>"
    Then I am presented with the "<screen_name>" screen

    Examples:
      | screen_name                |
      | Enter Personal Details     |
      | Enter Work Contact Details |
      | Choose Password            |

  @deleteUnregisteredUser @test-case-id-333821329280
  Scenario: As a not registered user I can register
    Given I navigate to the "Email Address" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                |
      | email   | user_unregistered@test.com |
    And I click the "Continue" button
    Then I am presented with the "Email Address" screen
    And I receive an "Confirm your work email address - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Email Confirmed" screen
    When I click the "Continue" button
    Then I am presented with the "Enter Personal Details" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Enter Work Contact Details" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I enter value "What a beautiful day" in "Memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "Choose Password" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Check answers and submit" screen
    And I click the "Submit" button
    Then I am presented with the "Registered" screen

  @exec-manual @test-case-id-333821329310
  Scenario: As user I can be notified when I cannot submit due to technical issues
    Given I navigate to the "Check answers and submit" screen
    And I accept all cookies
    And I do not want to make further changes
    When I click the "Submit" button
    And the backend server reports a technical issue
    And A message is provided stating 'We apologize for the inconvenience. The system is currently experiencing technical difficulties. Please try again. If the problem persists come back later and start the registration process again from the beginning by entering an email address. If the problem still persists please contact the help desk.'
