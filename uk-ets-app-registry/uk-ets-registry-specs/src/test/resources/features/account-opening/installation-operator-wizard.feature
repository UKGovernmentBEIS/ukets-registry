@functional-area-account-opening

Feature: Account opening - Installation operator wizard

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (& 6.1.2.1) as user I can add an Installation Operator from scratch to my request for a new account
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  # Screens:
  # "Request a new account overview"	Is the 6.1.1.1	at pg 21 Screen 3: Request to open a registry account
  # "Fill-in installation details"		Is the 6.1.2.1	at pg 52 Screen 1: Fill-in installation details
  # "Fill-in installation details view" Is the 6.1.2.1	at pg 54 Screen 2: Fill-in installation details view

  Background:
    # start of prerequisite 1: login
    * I sign in as "senior admin" user
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" button
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    * I select the "Operator Holding Account" option
    * I click the "Continue" button
    * I click the "Account holder details" link
    * I select the "Organisation" option
    * I click the "Continue" button
    * I select the "Add a new organisation" option
    * I click the "Continue" button
    #* I am presented with the "Fill account holder Organisation details" screen
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
    * I am presented with the "Request to open registry account" screen
    * I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
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
    * I am presented with the "Request to open registry account" screen
    * I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    * I click the "Account details" link
    * I enter value "georges lammam" in "Account name" field
    * I click the "Continue" button
    * I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    * I click the "Choose the transaction rules" link
    * I select the "Yes" option
    * I click the "Continue" button
    * I select the "Yes" option
    * I click the "Continue" button
    * I select the "Yes" option
    * I click the "Continue" button
    #* I am presented with the "Configure the transaction rules overview" screen
    * I click the "Continue" button
  # end of prerequisite 5

  @exec-manual @test-case-id-66131712346
  Scenario: As user I can go back to previous screen
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                       |
      | Page title | Is this an installation transfer? |

  @exec-manual @test-case-id-66131712354
  Scenario: As user I can continue to next screen
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    And I select the "Installation activity type: Combustion of fuels" option
    And I select the "Regulator: NRW" option
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value        |
      | Page title | Check your answers |

  @test-case-id-66131712371
  Scenario: As user I cannot submit without having selected Installation activity type field
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I click the "Continue" button
    Then I see an error summary with "Enter the installation activity type"
    And I see an error detail for field "event-name" with content "Error: Enter the installation activity type"

  @test-case-id-66131712388
  Scenario: As user I cannot submit form without having selected regulator field
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I see an error summary with "Enter the regulator"
    And I see an error detail for field "event-name" with content "Error: Enter the regulator"

  @exec-manual @test-case-id-673252612303
  Scenario Outline: As user I cannot submit form without having inserted a textbox date field
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
    And I enter value "<value_1>" in "<field_1>" field
    And I enter value "<value_2>" in "<field_2>" field
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I see an error summary with "Enter all the fields of the permit entry into force"
    And I see an error detail for field "permit-date" with content "Error: Enter all the fields of the permit entry into force."

    Examples:
      | field_1                        | value_1 | field_2                        | value_2 |
      | Permit entry into force: Day   | 01      | Permit entry into force: Month | 01      |
      | Permit entry into force: Month | 01      | Permit entry into force: Year  | 2050    |
      | Permit entry into force: Year  | 2050    | Permit entry into force: Day   | 01      |

  @test-case-id-673252612326 @exec-manual
  Scenario Outline: As user I cannot submit form without having inserted a textbox fields
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                          | fieldValue |
      | Permit entry into force: Day   | 01         |
      | Permit entry into force: Month | 01         |
      | Permit entry into force: Year  | 2050       |
    And I enter value "<value_1>" in "<field_1>" field
    And I enter value "<value_2>" in "<field_2>" field
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | field_1           | value_1             | field_2                                    | value_2 | error                       |
      | Installation name | Installation name 1 | First year of verified emission submission | 2050    | Enter the permit ID         |
      | Permit ID         | 12345               | First year of verified emission submission | 2050    | Enter the installation name |

  @test-case-id-66131712453 @exec-manual
  Scenario: First year of Verified Emission submit must be greater or equal to 2021 negative edge case
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2020                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I see an error summary with "First year of verified emission submission cannot be before 2021"
    And I see an error detail for field "event-name" with content "Error: First year of verified emission submission cannot be before 2021"

  @test-case-id-66131712471 @exec-manual
  Scenario: First year of Verified Emission submit must be greater or equal to 2021 positive edge case
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2021                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2021                |

  @exec-manual @test-case-id-66131712489
  Scenario: As user I can go back and forth maintaining data
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I am presented with the "Fill-in installation details view" screen
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force: Day                    | 01                  |
      | Permit entry into force: Month                  | 01                  |
      | Permit entry into force: Year                   | 2050                |
      | First year of verified emission submission text | 2050                |
    And I click the "Continue" button
    Then I am presented with the "Fill-in installation details view" screen
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |

  @test-case-id-66131712533 @exec-manual
  Scenario: As user I can apply installation
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                      | field_value               |
      | Installation information label | 12345 Installation name 1 |
    And The page "does not contain" the "Apply" text

  @test-case-id-66131712563
  Scenario: As user I can edit installation after apply
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                      | field_value               |
      | Installation information label | 12345 Installation name 1 |
    When I click the "Installation information label" link
    And I click the "Edit" button
    And I clear the "Installation name" field
    And I enter value "Installation name 2" in "Installation name" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 2 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                      | field_value               |
      | Installation information label | 12345 Installation name 2 |

  @exec-manual @test-case-id-66131712614
  Scenario: As user I cannot submit an installation of account with the same permit ID already existed under same ah
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 1234567890          |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    Then I see an error summary with "An account with the same permit ID already exists under the same account holder. A second account is not permitted"
    And I see an error detail for field "permit-id" with content "An account with the same permit ID already exists under the same account holder. A second account is not permitted"
    Then The page "contains" the "An account with the same permit ID already exists.  A second account is not permitted." text

  @test-case-id-66131712633
  Scenario: As user I can change installation before apply
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    And I click the "Change installation details" link
    Then I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value         |
      | Installation name                          | Installation name 1 |
      | Permit ID                                  | 12345               |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
      | First year of verified emission submission | 2050                |
    And I clear the "Installation name" field
    And I enter value "Installation name 2" in "Installation name" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 2 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                      | field_value               |
      | Installation information label | 12345 Installation name 2 |

  @test-case-id-66131712683
  Scenario: As user I can delete installation after apply
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2050                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value         |
      | Installation name text                          | Installation name 1 |
      | Permit ID text                                  | 12345               |
      | Permit entry into force text                    | 1 January 2050      |
      | Regulator text                                  | NRW                 |
      | First year of verified emission submission text | 2050                |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                      | field_value               |
      | Installation information label | 12345 Installation name 1 |
    When I click the "Installation information label" link
    And I click the "Delete" button
    And I see the following fields having the values:
      | fieldName                      | field_value              |
      | Installation information label | Installation Information |
