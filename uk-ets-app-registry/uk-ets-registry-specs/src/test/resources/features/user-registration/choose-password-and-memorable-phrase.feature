@functional-area-user-registration

Feature: User registration - Choose password and memorable phrase
  Epic: User registration and sign-in
  Version: 1.0 (19/09/2019)
  Story: Register personal and work contact details
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=12&amp;modificationDate=1568901007000&amp;api=v2

  @test-case-id-95982529326
  Scenario: As user I can successfully set a memorable phrase
    Given I navigate to the "Enter Work Contact Details" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
      | workTownOrCity               | Smyrna        |
    And I enter value "2101010200" in "workPhone-phone-number" field
    And I enter value "GR" in "workPhone-country-code" field
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I enter value "What a beautiful day" in "Memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "Choose Password" screen

  @test-case-id-95982529345
  Scenario: As user I cannot continue without having set a memorable phrase
    Given I navigate to the "Enter Work Contact Details" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
      | workTownOrCity               | Smyrna        |
    And I enter value "2101010200" in "workPhone-phone-number" field
    And I enter value "GR" in "workPhone-country-code" field
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I see an error summary with "Enter a memorable phrase"
    And I see an error detail with id "event-name-error" and content "Enter a memorable phrase"

  @test-case-id-09716629171
  Scenario Outline: As user I cannot continue when I set invalid characters in memorable phrase
    Given I navigate to the "Enter Work Contact Details" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
      | workTownOrCity               | Smyrna        |
    And I enter value "2101010200" in "workPhone-phone-number" field
    And I enter value "GR" in "workPhone-country-code" field
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I enter value "<text>" in "Memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I see an error summary with "The memorable phrase cannot contain any special characters"
    And I see an error detail with id "event-name-error" and content "The memorable phrase cannot contain any special characters"

    Examples:
      | text      |
      | @         |
      | *         |
      | @@#%^*$%# |

  @sampling-smoke @test-case-id-09716629198
  Scenario Outline: As user I can submit a valid password
    Given I navigate to the "Choose Password" screen
    And I accept all cookies
    When I enter value "<password>" in "password" field
    And I enter value "<password>" in "pconfirm" field
    And I click the "Continue" button
    Then I am presented with the "Check answers and submit" screen

    Examples:
      | password                   |
      | MyVerySecretPasswordIsThis |
      | AnotherWayT0ProperlyT3st   |

  @test-case-id-09716629212
  Scenario Outline: As user I can select passwords that are at least 8 characters long
    Given I navigate to the "Choose Password" screen
    And I accept all cookies
    When I enter value "<password>" in "password" field
    And I enter value "<password>" in "pconfirm" field
    And I click the "Continue" button
    Then I see an error summary with "password and confirm password should be 10 characters or more"
    And I see an error detail with id "event-name-error" and content "password and confirm password should be 10 characters or more"

    Examples:
      | password |
      | passwor  |
      | pass     |

  @test-case-id-09716629227
  Scenario Outline: As user I should not be allowed to use weak or invalid characters passwords
    Given I navigate to the "Choose Password" screen
    And I accept all cookies
    When I enter value "<password>" in "password" field
    And I enter value "<password>" in "pconfirm" field
    And I click the "Continue" button
    Then I see an error summary with "<error_content>"

    @sampling-smoke
    Examples:
      | password | error_content                                               |
      | 12345678 | Enter a strong password. Password is contained in deny list |

    Examples:
      | password | error_content                                               |
      | Password | Enter a strong password. Password is contained in deny list |
      | P@ssw0rd | Enter a strong password. Password is contained in deny list |

    @exec-manual
    Examples:
      | password | error_content           |
      | #$%^&*#$ | Enter a strong password |

  @exec-manual @test-case-id-95982529444
  Scenario: As user I must verify password that I have already provided by typing it again
    Given I navigate to the "Choose Password" screen
    And I accept all cookies
    When I enter value "MyVerySecretPasswordIsThis" in "password" field
    And I enter value "_MyVerySecretPasswordIsThis" in "pconfirm" field
    And I click the "Continue" button
    Then I see an error summary with "Password and re-typed password do not match. Please enter both passwords again"
    And I see an error detail with id "event-name-error" and content "Password and re-typed password do not match. Please enter both passwords again."
