@functional-area-account-opening

Feature: Account opening - Account details trading account

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: Request to open a registry account - Account details wizard
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
    # start of prerequisite 2: open account type: "Trading Account"
    * I select the "Trading Account" option
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
    * I click the "Continue" button
    # end of prerequisite 2: open account type: "Trading Account"
    # start of prerequisite 3: create a primary contact
    * I click the "Add the primary contact" link
    * I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    * I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    * I click the "Continue" button
    * I enter the following values to the fields:
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
    * I click the "Continue" button
    * I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # access Fill account details:
    * I click the "Account details" link

  @exec-manual @test-case-id-1629888695
  Scenario: As user I can go back to new account overview page
    When I click the "Back" link
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |

  @exec-manual @test-case-id-0970178615
  Scenario Outline: As user I cannot submit empty mandatory fields for Trading Account
    And I enter the following values to the fields:
      | field     | fieldValue             |
      | dropdown  | Country United Kingdom |
      | <field_1> | <value_1>              |
      | <field_2> | <value_2>              |
      | <field_3> | <value_3>              |
    When I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | value_1               | field_1      | value_2               | field_2       | value_3   | field_3           | error                      |
      | georges lammam        | Account name | Line 1 Nahawand Maqam | Line 1        | Ano Syros | Fill Town or city | Enter your UK post code.   |
      | georges lammam        | Account name | 12345                 | Fill Postcode | Ano Syros | Fill Town or city | Enter your Address line 1. |
      | georges lammam        | Account name | Line 1 Nahawand Maqam | Line 1        | 12345     | Fill Postcode     | Enter your Town or City.   |
      | Line 1 Nahawand Maqam | Line 1       | 12345                 | Fill Postcode | Ano Syros | Fill Town or city | Enter the Account name.    |

  @exec-manual @test-case-id-1629888722
  Scenario: As user when I navigate to previous screen I see data retained
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Account name      | georges lammam |
      | Line 1            | line 1 value   |
      | Fill Town or city | Ano Syros      |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value     |
      | Account type | Trading Account |
      | Account name | georges lammam  |
      | Address      | line 1 value    |
      | Town or city | Ano Syros       |
      | Country      | Greece          |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName      | field_value    |
      | Account name   | georges lammam |
      | Line 1         | line 1 value   |
      | Town or city   | Ano Syros      |
      | Country Greece | Greece         |

  @test-case-id-1629888748 @exec-manual
  Scenario: As user I can submit account details for Trading Account
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Account name      | georges lammam |
      | Line 1            | line 1 value   |
      | Fill Town or city | Ano Syros      |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value           |
      | Account type      | ETS - Trading Account |
      | Account name text | georges lammam        |
      | Address           | line 1 value          |
      | Town or city      | Ano Syros             |
      | Country           | GR                    |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value    |
      | Account details label | georges lammam |

  @exec-manual @test-case-id-1629888770
  Scenario: As user I can change account details for Trading Account before apply
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Account name      | georges lammam |
      | Line 1            | line 1 value   |
      | Fill Town or city | Ano Syros      |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value     |
      | Account type | Trading Account |
      | Account name | georges lammam  |
      | Address      | line 1 value    |
      | Town or city | Ano Syros       |
      | Country      | Greece          |
    When I click the "Change" button
    And I see the following fields having the values:
      | fieldName      | field_value    |
      | Account name   | georges lammam |
      | Line 1         | line 1 value   |
      | Town or city   | Ano Syros      |
      | Country Greece | Greece         |
    And I clear the "Account name" field
    And I enter value "account details name 2" in "Account name" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value            |
      | Account type | Trading Account        |
      | Account name | account details name 2 |
      | Address      | line 1 value           |
      | Town or city | Ano Syros              |
      | Country      | Greece                 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

  @exec-manual @test-case-id-1629888813
  Scenario: As user I can edit account details for Trading Account after apply
    And I enter the following values to the fields:
      | field             | field_value    |
      | Account name      | georges lammam |
      | Line 1            | line 1 value   |
      | Fill Town or city | Ano Syros      |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value     |
      | Account type | Trading Account |
      | Account name | georges lammam  |
      | Address      | line 1 value    |
      | Town or city | Ano Syros       |
      | Country      | Greece          |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value    |
      | Account details label | georges lammam |
    When I click the "Account details label" link
    When I click the "Edit" button
    And I see the following fields having the values:
      | fieldName      | field_value    |
      | Account name   | georges lammam |
      | Line 1         | line 1 value   |
      | Town or city   | Ano Syros      |
      | Country Greece | Greece         |
    And I clear the "Account name" field
    And I enter value "account details name 2" in "Account name" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value            |
      | Account type | Trading Account        |
      | Account name | account details name 2 |
      | Address      | line 1 value           |
      | Town or city | Ano Syros              |
      | Country      | Greece                 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

  @exec-manual @test-case-id-1629888863
  Scenario: As user I can delete account details from request
    And I enter the following values to the fields:
      | field             | field_value    |
      | Account name      | georges lammam |
      | Line 1            | line 1 value   |
      | Fill Town or city | Ano Syros      |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value     |
      | Account type | Trading Account |
      | Account name | georges lammam  |
      | Address      | line 1 value    |
      | Town or city | Ano Syros       |
      | Country      | Greece          |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value    |
      | Account details label | georges lammam |
    When I click the "Account details label" link
    And I see the following fields having the values:
      | fieldName    | field_value     |
      | Account type | Trading Account |
      | Account name | georges lammam  |
      | Address      | line 1 value    |
      | Town or city | Ano Syros       |
      | Country      | Greece          |
    When I click the "Delete" button
    And I see the following fields having the values:
      | fieldName             | field_value                 |
      | Account details label | Fill in the account details |
