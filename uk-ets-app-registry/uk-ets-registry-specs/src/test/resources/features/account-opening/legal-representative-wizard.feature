@functional-area-account-opening

Feature: Account opening - Legal representative wizard

  Version: 2.8 (12/03/2020)
  Story: (&6.1.1.3) Request to open a registry account - primary contact wizard
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  Background:
    # start of prerequisite 1: login
    * I sign in as "senior admin" user
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" link
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open aoha ets account type
    * I select the "Operator Holding Account" option
    * I click the "Continue" button
    * I click the "Account holder details" link
    * I select the "Organisation" option
    * I click the "Continue" button
    * I select the "Add a new organisation" option
    * I click the "Continue" button
    * I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    * I select the "Fill Registration number" option
    * I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    * I select the "Fill VAT Registration number" option
    * I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    * I click the "Continue" button
    * I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    * I click the "Continue" button
    * I click the "Back" link
    * I click the "Continue" button
    * I click the "Continue" button
  # end of prerequisite 2.

  @test-case-id-55125412775  @exec-manual
  Scenario: As user I can submit personal details for 1 primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name text         | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Postcode               | 12345                    |
      | Country                | UK                       |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value       |
      | primary contact text | Giannis Dragatsis |

  @exec-manual @test-case-id-55125412815
  Scenario: As user I can submit personal details for 2 primary contacts
    # submit for 1st primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name              | Dragatsis                |
      | Also known as          | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | United Kingdom           |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value                         |
      | primary contact text | Giannis Dragatsis (primary contact) |
    # submit for 2nd alternative primary contact
    When I click the "Add the (alternative) primary contact" button
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Dimitris   |
      | Last name              | Semsis     |
      | Also known as          | Salonikios |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | dummy line value         |
      | Fill Town or city          | Columbia                 |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 67890                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6922222222               |
      | Fill Email address         | smyrna.violin@violin.com |
      | Fill Re-type email address | smyrna.violin@violin.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Dimitris                 |
      | Last name              | Semsis                   |
      | Also known as          | Salonikios               |
      | Work address           | dummy line value         |
      | Town or city           | Columbia                 |
      | Country                | United Kingdom           |
      | Postcode               | 67890                    |
      | Work phone number 1    | +306922222222            |
      | Email address          | smyrna.violin@violin.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                     | field_value                                   |
      | primary contact text          | Giannis Dragatsis (primary contact)           |
      | (alternative) primary contact | Dimitris Semsis (alternative primary contact) |

  @exec-manual @test-case-id-76946212795
  Scenario Outline: As user I cannot submit empty mandatory fields in legal representative wizard
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | <field_1>      | <value_1>  |
      | <field_2>      | <value_2>  |
      | Day of birth   | 01         |
      | Month of birth | 02         |
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "<error_field>" with content "Error: <error>"

    Examples:
      | value_1  | field_1                | value_2 | field_2       | error                 | error_field |
      | Dimitris | First and middle names | Semsis  | Last name     | Enter the birth date. | permit-date |
      | 1970     | Year of birth          | Semsis  | Last name     | Enter the first name. | event-name  |
      | Dimitris | First and middle names | 1970    | Year of birth | Enter the last name.  | event-name  |

  @exec-manual @test-case-id-55125412922
  Scenario: As user I cannot register a primary contact of age under 18 years old
    Given I click the "Add the primary contact" link
    * I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    Then I see an error summary with "The legal representative must be 18 years of age or older to open an account"
    And I see an error detail for field "permit-date" with content "Error: The legal representative must be 18 years of age or older to open an account."

  @exec-manual @test-case-id-55125412937
  Scenario: As user I can bypass the postcode fill if primary contact resides out of UK
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country Greece           |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name              | Dragatsis                |
      | Also known as          | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | Greece                   |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value                         |
      | primary contact text | Giannis Dragatsis (primary contact) |

  @test-case-id-55125412977
  Scenario: As user I must submit a postcode if the primary contact resides in UK
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1                   |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    Then I see an error summary with "Enter the UK post code"
    And I see an error detail for field "event-name" with content "Error: Enter the UK post code."

  @test-case-id-76946212898
  Scenario Outline: As user I cannot submit empty Fill primary contact address fields Line 1 or Town City
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter value "<value1>" in "<field1>" field
    And I enter value "<value2>" in "<field2>" field
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>."

    Examples:
      | value1     | field1            | value2     | field2            | error                                    |
      | line 1     | Line 1            | Consultant | Position          | Enter the town or city                   |
      | Smyrna     | Fill Town or city | line 1     | Line 1            | Please enter the position in the company |
      | Consultant | Position          | Smyrna     | Fill Town or city | Enter the address line 1                 |

  @exec-manual @test-case-id-55125413038
  Scenario: As user I cannot submit empty Phone field in Fill primary contact address screen
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    Then I see an error summary with "Please enter the phone number"
    And I see an error detail for field "event-name" with content "Error: Please enter the phone number."

  @test-case-id-76946212957
  Scenario Outline: As user I cannot submit empty Email or Retype email field in Fill primary contact address screen
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field               | fieldValue              |
      | Position            | Consultant              |
      | Line 1              | line 1 value            |
      | Fill Town or city   | Smyrna                  |
      | dropdown            | Country United Kingdom  |
      | Fill Postcode       | 12345                   |
      | dropdown            | Country code 1: GR (30) |
      | Fill Phone number 1 | 6911111111              |
    And I enter value "<value>" in "<field>" field
    When I click the "Continue" button
    Then I see an error summary with "<error>"
    #Need to implement code for multiple error details. In case of multiple errors, with the current implementation, only the first error detail is evaluated
    And I see an error detail for field "event-name" with content "Error: <error_detail>."

    Examples:
      | value                    | field                      | error                                                                                                                 | error_detail             |
      | estoudiantina@smyrna.com | Fill Email address         | Retype the email address                                                                                              | Retype the email address |
      | estoudiantina@smyrna.com | Fill Re-type email address | Enter the email address.Invalid re-typed email address. The email address and the re-typed email address should match | Enter the email address  |

  @test-case-id-55125413098
  Scenario: As user I cannot enter an email address using incorrect format
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue                                  |
      | Position                   | Consultant                                  |
      | Line 1                     | line 1 value                                |
      | Fill Town or city          | Smyrna                                      |
      | dropdown                   | Country United Kingdom                      |
      | Fill Postcode              | 12345                                       |
      | dropdown                   | Country code 1: GR (30)                     |
      | Fill Phone number 1        | 6911111111                                  |
      | Fill Email address         | lsjkhbrfvukygdkjhtvbjkhbrfvukygdksmyrna.com |
      | Fill Re-type email address | lsjkhbrfvukygdkjhtvbjkhbrfvukygdksmyrna.com |
    When I click the "Continue" button
    Then I see an error summary with "Enter an email address in the correct format, like name@example.comEnter an email address in the correct format, like name@example.com"
    And I see an error detail for field "event-name" with content "Error: Enter an email address in the correct format, like name@example.com"

  @exec-manual @test-case-id-55125413126
  Scenario: As user I can enter a long email address
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue                                                                |
      | Position                   | Consultant                                                                |
      | Line 1                     | line 1 value                                                              |
      | Fill Town or city          | Smyrna                                                                    |
      | dropdown                   | Country United Kingdom                                                    |
      | Fill Postcode              | 12345                                                                     |
      | dropdown                   | Country code 1: GR (30)                                                   |
      | Fill Phone number 1        | 6911111111                                                                |
      | Fill Email address         | ukygxgbxhtvbkyhdbgblsjkhbrfvukygdkjhtvbkyhdbgblsjkhbrfvukygdb@sdvfsdc.com |
      | Fill Re-type email address | ukygxgbxhtvbkyhdbgblsjkhbrfvukygdkjhtvbkyhdbgblsjkhbrfvukygdb@sdvfsdc.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | Email address | ukygxgbxhtvbkyhdbgblsjkhbrfvukygdkjhtvbkyhdbgblsjkhbrfvukygdb@sdvfsdc.com |

  @exec-manual
    @test-case-id-76946213047
  Scenario Outline: As user I must verify primary contacts email address using same value in Email and Retype email fields
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue              |
      | Position                   | Consultant              |
      | Line 1                     | line 1 value            |
      | Fill Town or city          | Smyrna                  |
      | dropdown                   | Country United Kingdom  |
      | Fill Postcode              | 12345                   |
      | dropdown                   | Country code 1: GR (30) |
      | Fill Phone number 1        | 6911111111              |
      | Fill Email address         | prefix1@infix1.postfix1 |
      | Fill Re-type email address | <retype_email_value>    |
    When I click the "Continue" button
    Then I see an error summary with "Invalid re-typed email address. The email address and the re-typed email address should match"
    And I see an error detail for field "event-name" with content "Error: Invalid re-typed email address. The email address and the re-typed email address should match."

    Examples:
      | retype_email_value       |
      | prefix2@infix1.postfix1  |
      | prefix1@infix2.postfix1  |
      | prefix1@infix1.postfix2  |
      | prefix1@infix1.postfix11 |

  @exec-manual
  @test-case-id-55125413191
  Scenario: As user I can change primary contacts personal details before apply
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name              | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | United Kingdom           |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Change personal details" button
    And I clear the "First and middle names" field
    And I enter value "Second edited name" in "First and middle names" field
    When I click the "Continue" button
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Second edited name       |
      | Last name              | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | United Kingdom           |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName       | field_value                                    |
      | primary contact | Second edited name Dragatsis (primary contact) |

  @test-case-id-55125413252
  Scenario: As user I can change primary contacts address and contact details before apply
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name text         | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | UK                       |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Change address and contact details legal representative" button
    And I clear the "Line 1" field
    And I enter value "line 1 second value" in "Line 1" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name text         | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 second value      |
      | Town or city           | Smyrna                   |
      | Country                | UK                       |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value       |
      | primary contact text | Giannis Dragatsis |

  @test-case-id-55125413305
  Scenario: As user I can remove primary contact from request
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name text         | Dragatsis                |
      | Also known as text     | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | UK                       |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value       |
      | primary contact text | Giannis Dragatsis |
    When I click the "primary contact text" link
    When I click the "Delete" button
    And The page "does not contain" the "Giannis Dragatsis" text

  @exec-manual @test-case-id-55125413350
  Scenario: As user I can edit primary contacts details before submit
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | First and middle names | Giannis    |
      | Last name              | Dragatsis  |
      | Also known as          | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Giannis                  |
      | Last name              | Dragatsis                |
      | Also known as          | Ogdontakis               |
      | Work address           | line 1 value             |
      | Town or city           | Smyrna                   |
      | Country                | United Kingdom           |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value       |
      | primary contact text | Giannis Dragatsis |
    When I click the "primary contact link" link
    When I click the "Edit" button
    And I clear the "First and middle names" field
    And I enter value "Second edited name" in "First and middle names" field
    When I click the "Continue" button
    And I clear the "Line 1" field
    And I enter value "Line 1 second value" in "Line 1" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value              |
      | First and middle names | Second edited name       |
      | Last name              | Dragatsis                |
      | Also known as          | Ogdontakis               |
      | Work address           | Line 1 second value      |
      | Town or city           | Smyrna                   |
      | Country                | United Kingdom           |
      | Postcode               | 12345                    |
      | Work phone number 1    | +GR (30)6911111111       |
      | Email address          | estoudiantina@smyrna.com |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName       | field_value                  |
      | primary contact | Second edited name Dragatsis |