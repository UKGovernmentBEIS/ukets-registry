@functional-area-account-opening

Feature: Account opening - Aircraft operator wizard

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (& 6.1.3.1) as user I can add an Aircraft Operator from scratch to my request for a new account
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
    # start of prerequisite 2: open account type: "Aircraft Operator Holding Account"
    * I select the "Aircraft Operator Holding Account" option
    * I click the "Continue" button
    * I click the "Account holder details" link
    * I select the "Organisation" option
    * I click the "Continue" button
    * I select the "Add a new organisation" option
    * I click the "Continue" button
    * I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
    # end of prerequisite 2: open account type: "Aircraft Operator Holding Account"
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
    * I click the "Continue" button
  # end of prerequisite 5: add transaction rules

  @exec-manual @test-case-id-602917710507
  Scenario: As user I can go back to previous screen navigating from details
    Given I click the "Aircraft Operator details" link
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                                 |
      | Page title | New ETS - Aircraft Operator Holding Account |


  @exec-manual @test-case-id-7120538810418
  Scenario Outline: As user I can continue to next screen
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Monitoring plan ID                         | abc        |
      | First year of verified emission submission | 2050       |
    And I click the "Regulator" button
    When I select the "<selection_option>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value        |
      | Page title | Check your answers |

    Examples:
      | selection_option      |
      | Regulator: EA         |
      | Regulator: NRW        |
      | Regulator: SEPA       |
      | Regulator: DAERA      |
      | Regulator: BEIS_OPRED |

  @exec-manual @test-case-id-602917710538
  Scenario: As user I cannot submit empty regulation option
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Monitoring plan ID                         | abc        |
      | First year of verified emission submission | 2050       |
    And I click the "Continue" button
    Then I see an error summary with "Select the Regulator"
    And I see an error detail for field "event-name" with content "Error: Select the Regulator."

  @test-case-id-7120538810452
  Scenario Outline: As user I cannot submit empty textbox fields option
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field     | fieldValue |
      | <field_1> | <value_1>  |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | field_1                                    | value_1 | error                                                |
      | Fill Monitoring plan ID                    | abc     | Enter the first year of verified emission submission |
      | First year of verified emission submission | 2050    | Enter the Monitoring plan ID                         |

  @test-case-id-602917710569
  Scenario: First year of verification cannot be equal to or lower than 2020
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2000       |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    Then I see an error summary with "First year of verified emission submission cannot be before 2021"
    And I see an error detail for field "event-name" with content "Error: First year of verified emission submission cannot be before 2021"

  @exec-manual @test-case-id-602917710583
  Scenario: First year of verification must be 2021 or later
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2021       |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value |
      | First year of verified emission submission text | 2021        |

  @exec-manual @test-case-id-602917710597
  Scenario: As user I can go back to previous screen navigating from details view to details
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2050       |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                       |
      | Page title | Add the Aircraft Operator details |

  @exec-manual @test-case-id-602917710613
  Scenario: As user filling screens and then coming back and forth I see my filling data preserved
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2050       |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value |
      | Monitoring plan ID                              | abc         |
      | First year of verified emission submission text | 2050        |
      | Regulator text                                  | Gibraltar   |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName                                       | field_value |
      | Monitoring plan ID                              | abc         |
      | First year of verified emission submission text | 2050        |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value |
      | Monitoring plan ID                              | abc         |
      | First year of verified emission submission text | 2050        |
      | Regulator text                                  | Gibraltar   |

  @exec-manual @test-case-id-602917710645
  Scenario: An account with the same monitoring plan ID already exists under same ah then a second account is not permitted
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | 1234567890 |
      | First year of verified emission submission | 2050       |
    And I click the "Regulator" button
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    Then The page "contains" the "An account with the same monitoring plan ID already exists.  A second account is not permitted." text

  @test-case-id-602917710658 @exec-manual
  Scenario: As user I can apply for Specific aoha
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2050       |
    And I click the "Regulator" button
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value |
      | Monitoring plan ID                              | abc         |
      | First year of verified emission submission text | 2050        |
      | Regulator text                                  | EA          |
    When I click the "Continue" link
    And I see the following fields having the values:
      | fieldName                       | field_value |
      | Aircraft Operator details label | abc         |

  @test-case-id-602917710681
  Scenario: As user I can change the information before apply for Specify aoha
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue               |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2050                     |
    And I click the "Regulator" button
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value              |
      | Monitoring plan ID                              | Monitoring plan ID value |
      | First year of verified emission submission text | 2050                     |
      | Regulator text                                  | EA                       |
    When I click the "Change aircraft operator details" link
    Then I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value              |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2050                     |
    And I clear the "Fill Monitoring plan ID" field
    And I enter value "Monitoring plan ID value 2" in "Fill Monitoring plan ID" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value                |
      | Monitoring plan ID                              | Monitoring plan ID value 2 |
      | First year of verified emission submission text | 2050                       |

  @test-case-id-602917710713
  Scenario: As user I can edit aoha after apply
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue               |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2050                     |
    And I click the "Regulator" button
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value              |
      | Monitoring plan ID                              | Monitoring plan ID value |
      | First year of verified emission submission text | 2050                     |
      | Regulator text                                  | EA                       |
    When I click the "Continue" link
    And I see the following fields having the values:
      | fieldName                       | field_value              |
      | Aircraft Operator details label | Monitoring plan ID value |
    When I click the "Aircraft Operator details label" link
    And I click the "Edit" button
    Then I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value              |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2050                     |
    And I clear the "Fill Monitoring plan ID" field
    And I enter value "Monitoring plan ID value 2" in "Fill Monitoring plan ID" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value                |
      | Monitoring plan ID                              | Monitoring plan ID value 2 |
      | First year of verified emission submission text | 2050                       |
    When I click the "Continue" link
    And I see the following fields having the values:
      | fieldName                       | field_value                |
      | Aircraft Operator details label | Monitoring plan ID value 2 |

  @test-case-id-602917710757
  Scenario: As user I can delete aoha after apply
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue               |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2050                     |
    And I click the "Regulator" button
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value              |
      | Monitoring plan ID                              | Monitoring plan ID value |
      | First year of verified emission submission text | 2050                     |
      | Regulator text                                  | EA                       |
    When I click the "Continue" link
    And I see the following fields having the values:
      | fieldName                       | field_value              |
      | Aircraft Operator details label | Monitoring plan ID value |
    When I click the "Aircraft Operator details label" link
    And I click the "Delete" button
    And I see the following fields having the values:
      | fieldName                       | field_value               |
      | Aircraft Operator details label | Aircraft Operator details |
