@functional-area-account-opening

Feature: Account opening - Main wizard task list

  Epic: Account opening
  Version: 2.8 (12/11/2019)
  Story: Request to open a registry account - Main wizard (task list)
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=9&modificationDate=1573566227370&api=v2

  Background:
    # sign in
    * I sign in as "senior admin" user
    * I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | primaryContact           | Legal Rep1                 |
      | primaryContact           | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    * I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" link
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button

  @test-case-id-01303113447
  Scenario: As user I can complete ah section for oha
    And I select the "Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    And The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    And The "Installation Information" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    And The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"

  @test-case-id-01303113492  @exec-manual
  Scenario: As user I can complete ah section for aoha
    And I select the "Aircraft Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Aircraft Operator details" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"

  @test-case-id-01303113537 @exec-manual
  Scenario: As user I can fill in primary contact section for oha
    And I select the "Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Installation Information" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And The page "contains" the "Name 1" text
    Then The "Account details" "link" is "enabled"

  @test-case-id-01303113609
  Scenario: As user I can complete Transaction rules section for oha
    And I select the "Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Installation Information" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                          |
      | Section 1 | 1. Add the account holder INCOMPLETE |
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And The "Account details" "link" is "enabled"
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                    |
      | Account type      | ETS - Operator Holding Account |
      | Account name text | account details name 1         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    Then The "Choose the transaction rules" "link" is "enabled"
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                                                                                                                                 |
      | Approval of second authorised representative label | a second authorised representative must approve transfers of units to a trusted account; transfers of units cannot be made to accounts that are not on the trusted account list; surrender transactions or return of excess allocations need approval by a second authorised representative |
    Then The "Installation Information" "link" is "enabled"

  @test-case-id-01303113714 @exec-manual
  Scenario: As user I can complete installation section
    And I select the "Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Installation Information" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                          |
      | Section 1 | 1. Add the account holder INCOMPLETE |
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |
    Then The "Account details" "link" is "enabled"
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                    |
      | Account type      | ETS - Operator Holding Account |
      | Account name text | account details name 1         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |
    Then I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    Then The "Choose the transaction rules" "link" is "enabled"
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                     |
      | Approval of second authorised representative label | A second authorised representative must approve transfers of units to a trusted account; Transfers of units cannot be made to accounts that are not on the trusted account list |
    Then The "Installation Information" "link" is "enabled"
    And I click the "Installation information" link
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
    Then The "Authorised Representative details" "link" is "enabled"
    And The "Continue" "button" is "enabled"

  @test-case-id-01303113853 @exec-manual
  Scenario: As user I can complete aoha section
    And I select the "Aircraft Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Aircraft Operator details" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                          |
      | Section 1 | 1. Add the account holder INCOMPLETE |
    And The page "contains" the "Name 1" text
    Then The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |
    Then The "Account details" "link" is "enabled"
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                             |
      | Account type      | ETS - Aircraft Operator Holding Account |
      | Account name text | account details name 1                  |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |
    Then I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    Then The "Choose the transaction rules" "link" is "enabled"
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                     |
      | Approval of second authorised representative label | A second authorised representative must approve transfers of units to a trusted account; Transfers of units cannot be made to accounts that are not on the trusted account list |
    Then The "Aircraft Operator details" "link" is "enabled"
    And I click the "Aircraft Operator details" link
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
    Then The "Authorised Representative details" "link" is "enabled"
    And The "Continue" "button" is "enabled"

  @exec-manual @test-case-id-348315513879
  Scenario Outline: As user I can see the account type I selected on previous step
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                               | field_value                                            |
      | Request to open a registry header title | Request to open a registry account\nNew <account_type> |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |
      | Trading Account                   |
      | Person Holding Account            |

  @exec-manual @test-case-id-348315513896
  Scenario Outline: As user I can see all common sections relevant to my opening request
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                    | field_value                       |
      | Fill in the account details label name       | Fill in the account details       |
      | Configure the transaction rules label name   | Configure the transaction rules   |
      | Authorised Representative details label name | Authorised Representative details |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |
      | Trading Account                   |
      | Person Holding Account            |

  @exec-manual @test-case-id-348315513916
  Scenario Outline: As user I can complete ah section for Trading Account and Person Holding Account
    And I select the "<account_type>" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Authorised Representative details" "link" is "disabled"
    And The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Organisation" option
    And I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"

    Examples:
      | account_type           |
      | Trading Account        |
      | Person Holding Account |

  @exec-manual @test-case-id-01303114078
  Scenario: As user I can fill in primary contact section for aoha
    And I select the "Aircraft Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Aircraft Operator details" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                          |
      | Section 1 | 1. Add the account holder INCOMPLETE |
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Account details" "link" is "enabled"

  @exec-manual @test-case-id-348315514042
  Scenario Outline: As user I can fill in primary contact section for Trading Account and Person Holding Account
    And I select the "<account_type>" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Authorised Representative details" "link" is "disabled"
    And The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Organisation" option
    And I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | Organisation name                         | Name 1     |
      | Organisation registration number          | 12345      |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"

    Examples:
      | account_type           |
      | Trading Account        |
      | Person Holding Account |

  @exec-manual @test-case-id-01303114199
  Scenario: As user I can complete Transaction rules section for aoha
    And I select the "Aircraft Operator Holding Account" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Aircraft Operator details" "link" is "disabled"
    And The "Authorised Representative details" "link" is "disabled"
    Then The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Individual" option
    And I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Last name  |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And The "Account details" "link" is "enabled"
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                       |
      | Account type      | Aircraft Operator Holding Account |
      | Account name text | account details name 1            |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    Then The "Choose the transaction rules" "link" is "enabled"
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                               |
      | Configure the transaction rules label | Approval of a second authorised representative required, transfer to accounts not on the list not allowed |
    Then The "Aircraft Operator details" "link" is "enabled"

  @exec-manual @test-case-id-348315514189
  Scenario Outline: As user I can complete Transaction rules section for Trading Account and Person Holding Account
    And I select the "<account_type>" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And The "Account Holder details" "link" is "enabled"
    Then The "Account details" "link" is "disabled"
    And The "Choose the transaction rules" "link" is "disabled"
    Then The "Authorised Representative details" "link" is "disabled"
    And The "Continue" "button" is "disabled"
    And I click the "Account holder details" link
    And I select the "Organisation" option
    And I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | Organisation name                         | Name 1     |
      | Organisation registration number          | 12345      |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And The "Add the primary contact" "link" is "enabled"
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |
    Then The "Account details" "link" is "enabled"
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | <account_type>         |
      | Account name text | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    Then The "Choose the transaction rules" "link" is "enabled"
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                               |
      | Configure the transaction rules label | Approval of a second authorised representative required, transfer to accounts not on the list not allowed |
    Then The "Authorised Representative details" "link" is "enabled"

    Examples:
      | account_type           |
      | Trading Account        |
      | Person Holding Account |

  @exec-manual @test-case-id-348315514294
  Scenario Outline: As user I can see all sections relevant to my opening request
    When I select "<account_type>" account in the "Fill account type" screen
    Then the <section_name_visible> is visible
    But the <section_name_not_visible> is not visible

    Examples:
      | account_type                      | section_name_visible                                                                              | section_name_not_visible        |
      | Operator Holding Account          | account-holder, account-details, Transaction-rules, authorised-representatives, Installation      | Aircraft-operator               |
      | Aircraft Operator Holding Account | account-holder, account-details, Transaction-rules, authorised-representatives, Aircraft Operator | Installation                    |
      | Trading Account                   | account-holder, account-details, Transaction-rules, authorised-representatives                    | Installation, Aircraft-operator |
      | Person Holding Account            | account-holder, account-details, Transaction-rules, authorised-representatives                    | Installation, Aircraft-operator |

  @test-case-id-348315514307
  Scenario Outline: As user I can know which sections are OPTIONAL for OHA and AOHA accounts
    And I select the "<account_type>" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                            | field_value                                |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |

  @exec-manual @test-case-id-348315514323
  Scenario Outline: As user I can know what information is missing from opening request
    Given I navigate to the "Request a new account overview" screen
    When I have not have filled in the "<Section-name>" section
    And the "<Section-name>" section is mandatory
    Then the "<link-name>" link is visible in the "<field-name>" field
    And the "<Section-name>" status is "INCOMPLETE"

    Examples:
      | Section-name               | link-name                             | field-name                |
      | account-holder             | Fill-in the account holder details    | account-holder-name       |
      | account-details            | Fill-in the account details           | account-name              |
      | transaction-rules          | Configure the transaction rules       | transaction-rules         |
      | authorised-representatives | Add authorised representative (+)     | authorised-representative |
      | Installation               | Fill-in the installation details      | installation-name         |
      | Aircraft-operator          | Fill-in the aircraft operator details | aircraft-operator-name    |

  @test-case-id-348315514340
  Scenario Outline: As user I cannot submit ars that violate business rules
    And I select the "<account_type>" option
    Then The "Continue" "button" is "enabled"
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                  |
      | Section 4 | 4. Add authorised representatives INCOMPLETE |

    Examples:
      | account_type           |
      | Trading Account        |
      | Person Holding Account |

  @test-case-id-348315514355
  Scenario Outline: As user I cannot submit ars that violate business rules due to 4 eyes principle
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    And I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 1" text
    And I click the "Add the primary contact" link
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
    When I click the "Continue" button
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | <account_type_ui>      |
      | Account name text | account details name 1 |
    When I click the "Continue" button
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                  |
      | Section 4 | 4. Add authorised representatives INCOMPLETE |
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Read only" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                |
      | Section 4 | 4. Add authorised representatives COMPLETE |

    Examples:
      | account_type           | account_type_ui             |
      | Trading Account        | ETS - Trading Account       |
      | Person Holding Account | KP - Person Holding Account |

  @test-case-id-01303114593 @exec-manual
  Scenario: As user I can see oha distinct sections
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And The page "does not contain" the "Aircraft Operator details" text
    Then I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName  | field_value                                                                              |
      | Page title | Request to open an Emissions Trading Registry Account New ETS - Operator Holding Account |

  @exec-manual @test-case-id-01303114602
  Scenario: As user I can see aoha distinct sections
    And I select the "Aircraft Operator Holding Account" option
    And I click the "Continue" button
    And The page "does not contain" the "Fill in the installation details" text
    Then I see the following fields having the values:
      | fieldName                       | field_value               |
      | Aircraft Operator details label | Aircraft Operator details |

  @test-case-id-01303114612
  Scenario: As user I can know which sections are OPTIONAL for my request for oha
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                            | field_value                                                  |
      | Section 1                            | 1. Add the account holder INCOMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details INCOMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                   |
      | Section 6 Check and submit           | 6. Check and submit your request                             |

  @test-case-id-01303114626
  Scenario: As user I can know which sections are INcompleted OPTIONAL for my request for Trading account account type
    And I select the "Trading Account" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                         |
      | Section 1                 | 1. Add the account holder INCOMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details INCOMPLETE |
      | Section 3                 | 3. Set up rules for transactions INCOMPLETE         |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE        |
      | Section 5                 | 5. Check and submit your request                    |

  @test-case-id-01303114639
  Scenario: As user I can know which sections are INcompleted OPTIONAL for my request for aoha
    And I select the "Aircraft Operator Holding Account" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                           |
      | Section 1                                   | 1. Add the account holder INCOMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details INCOMPLETE |
      | Section 3                                   | 3. Set up rules for transactions INCOMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator INCOMPLETE                               |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                            |
      | Section 6 Check and submit                  | 6. Check and submit your request                                      |

  @test-case-id-01303114653
  Scenario: As user I can know which sections are INcompleted OPTIONAL for my request for Person holding account KP account type
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                               |
      | Section 1                        | 1. Add the account holder INCOMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details INCOMPLETE |
      | Section 3                        | 3. Set up rules for transactions INCOMPLETE               |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE              |
      | Section 5                        | 5. Check and submit your request                          |

  @test-case-id-01303114666
  Scenario: As user I can cancel opening request and return to dashboard
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    When I click the "Cancel" button
    Then I am presented with the "Registry dashboard" screen

  @test-case-id-01303114780
  Scenario: As user I can verify that for oha ets account type I see the correct sections status while filling in sections
    # account type: Operator Holding Account
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I select the "Afghanistan" option
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | uk_ets@email.com     |
      | Fill Re-type email address | uk_ets@email.com     |
    And I click the "Continue" button
    And I click the "Back" link
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                  |
      | Section 1                            | 1. Add the account holder INCOMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details INCOMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                   |
      | Section 6 Check and submit           | 6. Check and submit your request                             |
    # Create a primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | legal rep line 1         |
      | Fill Town or city          | legal rep town city      |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12312                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                  |
      | Section 1                            | 1. Add the account holder COMPLETE                           |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details INCOMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                   |
      | Section 6 Check and submit           | 6. Check and submit your request                             |
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    When I click the "Continue" button
    When I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE             |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE             |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # create installation details
    Given I click the "Installation information" link
    And I click the "Is this an installation transfer: No" button
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2051                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Combustion of fuels" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information COMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # create authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information COMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    When I click the "Continue" button
    Then The page "contains" the "Check your answers before submitting your account opening request" text

  @exec-manual @test-case-id-01303114953
  Scenario: As user I can verify that for OHA account type I see the correct sections status when I select an existing ah for individual from proposed list
    # account type: Operator Holding Account
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    When I click the "From the list below" link
    And I click the "Please select" link
    And I select the "Organisation 1" option
    When I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                  |
      | Section 1                            | 1. Add the account holder INCOMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details INCOMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                   |
      | Section 6 Check and submit           | 6. Check and submit your request                             |
    # Create a primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field                  | fieldValue           |
      | First and middle names | legal rep first name |
      | Last name              | legal rep last name  |
      | Also known as          | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | legal rep line 1         |
      | Fill Town or city          | legal rep town city      |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12312                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                  |
      | Section 1                            | 1. Add the account holder COMPLETE                           |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details INCOMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                   |
      | Section 6 Check and submit           | 6. Check and submit your request                             |
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    When I click the "Continue" button
    When I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions INCOMPLETE                |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE             |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information INCOMPLETE             |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # create installation details
    Given I click the "Installation information" link
    And I enter the following values to the fields:
      | field                                      | fieldValue          |
      | Permit ID                                  | 12345               |
      | Installation name                          | Installation name 1 |
      | First year of verified emission submission | 2051                |
      | Permit entry into force: Day               | 01                  |
      | Permit entry into force: Month             | 01                  |
      | Permit entry into force: Year              | 2050                |
    When I select the "Regulator: NRW" option
    And I select the "Installation activity type: Capture of greenhouse gases from other installations" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information COMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |
    # create authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                            | field_value                                                |
      | Section 1                            | 1. Add the account holder COMPLETE                         |
      | Section 2 operator holding account   | 2. Add the ets - operator holding account details COMPLETE |
      | Section 3                            | 3. Set up rules for transactions COMPLETE                  |
      | Section 4 installation information   | 4. Add the installation information COMPLETE               |
      | Section 5 authorised representatives | 5. Add authorised representatives OPTIONAL                 |
      | Section 6 Check and submit           | 6. Check and submit your request                           |

  @test-case-id-01303115102
  Scenario: As user I can verify that for aoha I see the correct sections status while filling in sections
    # account type: Aircraft Operator Holding Account
    And I select the "Aircraft Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Organisation name | account Name 1 |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 00001      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 00002      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Line 1            | line 1 value account type       |
      | Fill Town or city | town city value in account type |
      | dropdown          | Country Greece                  |
    And I click the "Continue" button
    And I click the "Continue" button
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                           |
      | Section 1                                   | 1. Add the account holder INCOMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details INCOMPLETE |
      | Section 3                                   | 3. Set up rules for transactions INCOMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator INCOMPLETE                               |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                            |
      | Section 6 Check and submit                  | 6. Check and submit your request                                      |
    # Create a primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | legal rep line 1         |
      | Fill Town or city          | legal rep town city      |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12312                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                           |
      | Section 1                                   | 1. Add the account holder COMPLETE                                    |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details INCOMPLETE |
      | Section 3                                   | 3. Set up rules for transactions INCOMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator INCOMPLETE                               |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                            |
      | Section 6 Check and submit                  | 6. Check and submit your request                                      |
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    When I click the "Continue" button
    When I click the "Continue" button
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                         |
      | Section 1                                   | 1. Add the account holder COMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details COMPLETE |
      | Section 3                                   | 3. Set up rules for transactions INCOMPLETE                         |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator INCOMPLETE                             |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                          |
      | Section 6 Check and submit                  | 6. Check and submit your request                                    |
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option    
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                         |
      | Section 1                                   | 1. Add the account holder COMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details COMPLETE |
      | Section 3                                   | 3. Set up rules for transactions COMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator INCOMPLETE                             |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                          |
      | Section 6 Check and submit                  | 6. Check and submit your request                                    |
    # fill aircraft operator details
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue               |
      | Fill Monitoring plan ID                    | Monitoring plan ID value |
      | First year of verified emission submission | 2051                     |
    When I select the "Regulator: EA" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                       | field_value              |
      | Monitoring plan ID                              | Monitoring plan ID value |
      | First year of verified emission submission text | 2051                     |
      | Regulator text                                  | EA                       |
    When I click the "Continue" link
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                         |
      | Section 1                                   | 1. Add the account holder COMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details COMPLETE |
      | Section 3                                   | 3. Set up rules for transactions COMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator COMPLETE                               |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                          |
      | Section 6 Check and submit                  | 6. Check and submit your request                                    |
    # create authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Aircraft Operator Holding Account current status
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                         |
      | Section 1                                   | 1. Add the account holder COMPLETE                                  |
      | Section 2 aircraft operator holding account | 2. Add the ets - aircraft operator holding account details COMPLETE |
      | Section 3                                   | 3. Set up rules for transactions COMPLETE                           |
      | Section 4 aircraft operator                 | 4. Add the aircraft operator COMPLETE                               |
      | Section 5 authorised representatives        | 5. Add authorised representatives OPTIONAL                          |
      | Section 6 Check and submit                  | 6. Check and submit your request                                    |

  @test-case-id-01303115271
  Scenario: As user I can verify that for Trading account account type I see the correct sections status while filling in sections
    # account type: Trading Account
    And I select the "Trading Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Organisation name | account Name 1 |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 00001      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 00002      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Line 1            | line 1 value account type       |
      | Fill Town or city | town city value in account type |
      | dropdown          | Country Greece                  |
    And I click the "Continue" button
    And I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                         |
      | Section 1                 | 1. Add the account holder INCOMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details INCOMPLETE |
      | Section 3                 | 3. Set up rules for transactions INCOMPLETE         |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE        |
      | Section 5                 | 5. Check and submit your request                    |
    # Create a primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | legal rep line 1         |
      | Fill Town or city          | legal rep town city      |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12312                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                         |
      | Section 1                 | 1. Add the account holder COMPLETE                  |
      | Section 2 trading account | 2. Add the ets - trading account details INCOMPLETE |
      | Section 3                 | 3. Set up rules for transactions INCOMPLETE         |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE        |
      | Section 5                 | 5. Check and submit your request                    |
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                       |
      | Section 1                 | 1. Add the account holder COMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details COMPLETE |
      | Section 3                 | 3. Set up rules for transactions INCOMPLETE       |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE      |
      | Section 5                 | 5. Check and submit your request                  |
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                       |
      | Section 1                 | 1. Add the account holder COMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details COMPLETE |
      | Section 3                 | 3. Set up rules for transactions COMPLETE         |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE      |
      | Section 5                 | 5. Check and submit your request                  |
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                       |
      | Section 1                 | 1. Add the account holder COMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details COMPLETE |
      | Section 3                 | 3. Set up rules for transactions COMPLETE         |
      | Section 4                 | 4. Add authorised representatives INCOMPLETE      |
      | Section 5                 | 5. Check and submit your request                  |
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Trading Account current status
    Then I see the following fields having the values:
      | fieldName                 | field_value                                       |
      | Section 1                 | 1. Add the account holder COMPLETE                |
      | Section 2 trading account | 2. Add the ets - trading account details COMPLETE |
      | Section 3                 | 3. Set up rules for transactions COMPLETE         |
      | Section 4                 | 4. Add authorised representatives COMPLETE        |
      | Section 5                 | 5. Check and submit your request                  |

  @test-case-id-01303115428
  Scenario: As user I can verify that for Person holding account KP account type I see the correct sections status while filling in sections
    # account type: Person Holding Account
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Organisation name | account Name 1 |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 00001      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 00002      |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Line 1            | line 1 value account type       |
      | Fill Town or city | town city value in account type |
      | dropdown          | Country Greece                  |
    And I click the "Continue" button
    And I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                               |
      | Section 1                        | 1. Add the account holder INCOMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details INCOMPLETE |
      | Section 3                        | 3. Set up rules for transactions INCOMPLETE               |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE              |
      | Section 5                        | 5. Check and submit your request                          |
    # Create a primary contact
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | legal rep line 1         |
      | Fill Town or city          | legal rep town city      |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12312                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                               |
      | Section 1                        | 1. Add the account holder COMPLETE                        |
      | Section 2 person holding account | 2. Add the kp - person holding account details INCOMPLETE |
      | Section 3                        | 3. Set up rules for transactions INCOMPLETE               |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE              |
      | Section 5                        | 5. Check and submit your request                          |
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                             |
      | Section 1                        | 1. Add the account holder COMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details COMPLETE |
      | Section 3                        | 3. Set up rules for transactions INCOMPLETE             |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE            |
      | Section 5                        | 5. Check and submit your request                        |
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                             |
      | Section 1                        | 1. Add the account holder COMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details COMPLETE |
      | Section 3                        | 3. Set up rules for transactions COMPLETE               |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE            |
      | Section 5                        | 5. Check and submit your request                        |
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                             |
      | Section 1                        | 1. Add the account holder COMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details COMPLETE |
      | Section 3                        | 3. Set up rules for transactions COMPLETE               |
      | Section 4                        | 4. Add authorised representatives INCOMPLETE            |
      | Section 5                        | 5. Check and submit your request                        |
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # Person Holding Account current status
    Then I see the following fields having the values:
      | fieldName                        | field_value                                             |
      | Section 1                        | 1. Add the account holder COMPLETE                      |
      | Section 2 person holding account | 2. Add the kp - person holding account details COMPLETE |
      | Section 3                        | 3. Set up rules for transactions COMPLETE               |
      | Section 4                        | 4. Add authorised representatives COMPLETE              |
      | Section 5                        | 5. Check and submit your request                        |