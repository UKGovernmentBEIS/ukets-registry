@functional-area-account-opening

Feature: Account opening - Account details OHA AOHA KP Accounts

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: Request to open a registry account - Account details wizard
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  Background:
    # start of prerequisite: login
    * I sign in as "senior admin" user
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" link
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button
  # end of prerequisite.

  @exec-manual @test-case-id-69598427057
  Scenario Outline: As user I can go back to new account overview page
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the " Registration number " option
    And I enter the following values to the fields:
      | Organisation registration number | 12345 |
    And I select the " VAT Registration number " option
    And I enter the following values to the fields:
      | VAT registration number with country code | 67890 |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    When I click the "Back" link
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName | field_value                        |
      | Section 1 | 1. Add the account holder COMPLETE |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |
      | Person Holding Account            |

  @exec-manual @test-case-id-1761477210
  Scenario: As user when I refresh page data is retained
    # open account type:
    And I select the "Operator holding account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue        |
      | Account name | Markos Vamvakaris |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value       |
      | Account type | <account_type>    |
      | Account name | Markos Vamvakaris |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName    | field_value       |
      | Account name | Markos Vamvakaris |
    Then I refresh the current page
    Then I see the following fields having the values:
      | fieldName    | field_value       |
      | Account name | Markos Vamvakaris |

  @exec-manual @test-case-id-69598427214
  Scenario Outline: As user I cannot submit empty name line or town field for PHA accounts
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field     | fieldValue     |
      | dropdown  | Country Greece |
      | <field_1> | <value_1>      |
      | <field_2> | <value_2>      |
    When I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | account_type           | value_1        | field_1      | value_2               | field_2           | error                      |
      | Person Holding Account | georges lammam | Account name | Line 1 Nahawand Maqam | Line 1            | Enter your Town or City.   |
      | Person Holding Account | georges lammam | Account name | Downtown              | Fill Town or city | Enter your Address line 1. |

  @exec-manual @test-case-id-69598427290
  Scenario Outline: As user I cannot submit empty name line or town field for OHA and AOHA accounts
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    When I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | account_type                      | error                   |
      | Operator Holding Account          | Enter the Account name. |
      | Aircraft Operator holding account | Enter the Account name. |

  @exec-manual @test-case-id-69598427361
  Scenario Outline: As user when I navigate to previous screen I see data retained for OHA and AOHA accounts
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue        |
      | Account name | Markos Vamvakaris |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value       |
      | Account type | <account_type>    |
      | Account name | Markos Vamvakaris |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName    | field_value       |
      | Account name | Markos Vamvakaris |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |

  @exec-manual @test-case-id-1761477524
  Scenario: As user when I navigate to previous screen I see data retained for PHA accounts
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field          | fieldValue |
      | First name     | Giannis    |
      | Last name      | Dragatsis  |
      | Also known as  | Ogdontakis |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
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
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field             | fieldValue        |
      | dropdown          | Country Greece    |
      | Account name      | Markos Vamvakaris |
      | Line 1            | line 1 value      |
      | Line 2            | line 2 value      |
      | Line 3            | line 3 value      |
      | Fill Town or city | Ano Syros         |
      | Fill Postcode     | 12345             |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | Markos Vamvakaris                        |
      | Town or city | Ano Syros                                |
      | Postcode     | 12345                                    |
      | Country      | Greece                                   |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName      | field_value       |
      | Account name   | Markos Vamvakaris |
      | Line 1         | line 1 value      |
      | Line 2         | line 2 value      |
      | Line 3         | line 3 value      |
      | Town or city   | Ano Syros         |
      | Postcode       | 12345             |
      | Country Greece | Greece            |

  @exec-manual @test-case-id-69598427536
  Scenario: As user when I navigate to previous screen I see data retained using case of Billing address the same with one of AH
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue                      |
      | Organisation name                         | Organisation maqam al saba name |
      | Organisation registration number          | 12345                           |
      | VAT registration number with country code | 67890                           |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter value "account details name 1" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    Then I see the following fields having the values:
      | fieldName    | field_value            |
      | Account name | account details name 1 |
      | Line 1       | line 1 value           |
      | Line 2       | line 2 value           |
      | Line 3       | line 3 value           |
      | Town or city | Smyrna                 |
      | Postcode     | 12345                  |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | account details name 1                   |
      | Town or city | Smyrna                                   |
      | Postcode     | 12345                                    |
      | Country      | United Kingdom                           |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName    | field_value            |
      | Account name | account details name 1 |
      | Line 1       | line 1 value           |
      | Line 2       | line 2 value           |
      | Line 3       | line 3 value           |
      | Town or city | Smyrna                 |
      | Postcode     | 12345                  |

  @test-case-id-69598427633 @exec-manual
  Scenario Outline: As user I can submit account details for OHA and AOHA accounts
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | ETS - <account_type>   |
      | Account name text | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |

  @test-case-id-1761477804 @exec-manual
  Scenario: As user I can submit account details for PHA accounts
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field             | fieldValue                        |
      | Account name      | account details name 1            |
      | Line 1            | Line 1 Nahawand Maqam             |
      | Line 2            | line value maqam al hijaz 2 value |
      | Line 3            | line value maqam al kurdi 2 value |
      | Fill Town or city | Ano Syros                         |
      | Fill Postcode     | 12345                             |
      | dropdown          | Country Greece                    |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                                                                               |
      | Address           | Line 1 Nahawand Maqam line value maqam al hijaz 2 value line value maqam al kurdi 2 value |
      | Town or city      | Ano Syros                                                                                 |
      | Postcode          | 12345                                                                                     |
      | Country           | GR                                                                                        |
      | Account type      | KP - Person Holding Account                                                               |
      | Account name text | account details name 1                                                                    |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |

  @exec-manual @test-case-id-1761477895
  Scenario: As user I can submit the account details for PHA accounts using case of Billing address the same with one of AH
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue                      |
      | Organisation name                         | Organisation maqam al saba name |
      | Organisation registration number          | 12345                           |
      | VAT registration number with country code | 67890                           |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter value "account details name 1" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    Then I see the following fields having the values:
      | fieldName    | field_value            |
      | Account name | account details name 1 |
      | Line 1       | line 1 value           |
      | Line 2       | line 2 value           |
      | Line 3       | line 3 value           |
      | Town or city | Smyrna                 |
      | Postcode     | 12345                  |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | account details name 1                   |
      | Town or city | Smyrna                                   |
      | Postcode     | 12345                                    |
      | Country      | United Kingdom                           |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |

  @test-case-id-69598427900
  Scenario Outline: As user I can edit account details for OHA and AOHA accounts after apply
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | ETS - <account_type>   |
      | Account name text | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    When I click the "Account details label" link
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | ETS - <account_type>   |
      | Account name text | account details name 1 |
    When I click the "Edit" button
    And I clear the "Account name" field
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 2 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | ETS - <account_type>   |
      | Account name text | account details name 2 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

    Examples:
      | account_type             |
      | Operator Holding Account |

    @exec-manual
    Examples:
      | account_type                      |
      | Aircraft Operator Holding Account |

  @exec-manual @test-case-id-1761478100
  Scenario: As user I can edit account details for PHA accounts after apply
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue                      |
      | Organisation name                         | Organisation maqam al saba name |
      | Organisation registration number          | 12345                           |
      | VAT registration number with country code | 67890                           |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Account name      | account details name 1 |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Ano Syros              |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country Greece         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | account details name 1                   |
      | Town or city | Ano Syros                                |
      | Postcode     | 12345                                    |
      | Country      | Greece                                   |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    When I click the "Account details label" link
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | account details name 1                   |
      | Town or city | Ano Syros                                |
      | Postcode     | 12345                                    |
      | Country      | Greece                                   |
    When I click the "Edit" button
    And I clear the "Account name" field
    And I clear the "Town or city" field
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Account name      | account details name 2 |
      | Fill Town or city | city 2                 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value                              |
      | Address      | line 1 value\nline 2 value\nline 3 value |
      | Account type | Person Holding Account                   |
      | Account name | account details name 2                   |
      | Town or city | city 2                                   |
      | Postcode     | 12345                                    |
      | Country      | Greece                                   |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

  @exec-manual @test-case-id-69598428137
  Scenario Outline: As user I can change account details for OHA and AOHA accounts before submit
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue                      |
      | Organisation name                         | Organisation maqam al saba name |
      | Organisation registration number          | 12345                           |
      | VAT registration number with country code | 67890                           |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account name text | account details name 1 |
      | Account type      | <account_type>         |
    When I click the "Change" button
    And I clear the "Account name" field
    And I enter value "account details name 2" in "Account name" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account name text | account details name 2 |
      | Account type      | <account_type>         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |

  @test-case-id-1761478314
  Scenario: As user I can change account details for PHA accounts before submit
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Account name      | account details name 1 |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Ano Syros              |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country Greece         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                            |
      | Account name text | account details name 1                 |
      | Account type      | KP - Person Holding Account            |
      | Address           | line 1 value line 2 value line 3 value |
      | Town or city      | Ano Syros                              |
      | Postcode          | 12345                                  |
      | Country           | GR                                     |
    When I click the "Change" button
    And I clear the "Account name" field
    And I enter value "account details name 2" in "Account name" field
    And I clear the "Fill Town or city" field
    And I enter value "city 2" in "Fill Town or city" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                            |
      | Account name text | account details name 2                 |
      | Account type      | KP - Person Holding Account            |
      | Address           | line 1 value line 2 value line 3 value |
      | Town or city      | city 2                                 |
      | Postcode          | 12345                                  |
      | Country           | GR                                     |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 2 |

  @exec-manual @test-case-id-69598428333
  Scenario Outline: As user I can delete account details from OHA and AOHA accounts request
    # open account type:
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue                      |
      | Organisation name                         | Organisation maqam al saba name |
      | Organisation registration number          | 12345                           |
      | VAT registration number with country code | 67890                           |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field        | fieldValue             |
      | Account name | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | <account_type>         |
      | Account name text | account details name 1 |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    When I click the "Account details label" link
    And I see the following fields having the values:
      | fieldName         | field_value            |
      | Account type      | <account_type>         |
      | Account name text | account details name 1 |
    When I click the "Delete" button
    And The page "does not contain" the "account details name 1" text

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |

  @test-case-id-1761478510
  Scenario: As user I can delete account details from PHA accounts request
    # open account type:
    And I select the "Person Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
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
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    And I click the "Continue" button
    And I click the "Continue" button
    # create a primary contact:
    Given I click the "Add the primary contact" link
    And I enter the following values to the fields:
      | field         | fieldValue  |
      | First name    | Giannis     |
      | Last name     | Papaioannou |
      | Also known as | Psilos      |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 legal rep value   |
      | Fill Town or city          | Tsesme                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 00000                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    When I click the "Continue" button
    When I click the "Continue" button
    # access Fill account details:
    And I click the "Account details" link
    And I enter the following values to the fields:
      | field             | fieldValue             |
      | Account name      | account details name 1 |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Ano Syros              |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country Greece         |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName         | field_value                            |
      | Account name text | account details name 1                 |
      | Account type      | KP - Person Holding Account            |
      | Address           | line 1 value line 2 value line 3 value |
      | Town or city      | Ano Syros                              |
      | Postcode          | 12345                                  |
      | Country           | GR                                     |
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName             | field_value            |
      | Account details label | account details name 1 |
    When I click the "Account details label" link
    And I see the following fields having the values:
      | fieldName         | field_value                            |
      | Address           | line 1 value line 2 value line 3 value |
      | Town or city      | Ano Syros                              |
      | Postcode          | 12345                                  |
      | Country           | GR                                     |
      | Account type      | KP - Person Holding Account            |
      | Account name text | account details name 1                 |
    When I click the "Delete" button
    And The page "does not contain" the "account details name 1" text
