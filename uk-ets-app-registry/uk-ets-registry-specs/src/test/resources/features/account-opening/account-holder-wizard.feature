@functional-area-account-opening

Feature: Account opening - Account holder wizard

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (& 6.1.1.2) as user I can add an Account Holder from scratch to my request for a new account
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  # Screens:
  # "Request a new account overview"		    Is the 6.1.1.1 at pg 21	Screen  3: 	Request to open a registry account
  # "Fill account holder type"					Is the 6.1.1.2 at pg 23	Screen  1: 	Fill-in the account holder - type
  # "Fill account holder person"				Is the 6.1.1.2 at pg 23	Screen 2a: 	Fill-in the account holder - person
  # "Fill account holder individual details"        Is the 6.1.1.2 at pg 24	Screen 3a1: Fill-in the account holder - personal details
  # "Fill account holder individual address"        Is the 6.1.1.2 at pg 25	Screen 3a2: Fill-in the account holder - person address
  # "View account holder individual overview"	    Is the 6.1.1.2 at pg 29	Screen 4a:	Fill-in the account holder - person View

  # "Fill account holder organisation"		    Is the 6.1.1.2 at pg 26	Screen 2b: 	Fill-in the account holder - LE
  # "Fill account holder Organisation details"	Is the 6.1.1.2 at pg 27	Screen 3b1:	Fill-in the account holder - LE details
  # "Fill account holder Organisation address"	Is the 6.1.1.2 at pg 28	Screen 3b2:	Fill-in the account holder - LE address
  # "View account holder organisation overview"	Is the 6.1.1.2 at pg 30	Screen 4b:	Fill-in the account holder - LE View

  # Users:
  # User_Registered    		   for a user having the REGISTERED status,
  # User_Validated             for a user having the VALIDATED status,
  # User_Enrolled              for a user having the ENROLLED status,
  # User_Unenrollment_Pending  for a user having the UNENROLLMENT_PENDING status,
  # User_Unenrolled            for a user having the UNENROLLED status.

  @exec-manual @test-case-id-0355558842
  Scenario Outline: As user I can go back to previous screen Fill ah type
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I click the "Back" link
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName | field_value                          |
      | Section 1 | 1. Add the account holder INCOMPLETE |

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |
      | Trading Account                   |
      | Person Holding Account            |

  @exec-manual @test-case-id-0355558869
  Scenario Outline: As a X user I dont see X screen object
    Given I sign in as "<user_name>" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                       |
      | New Individual label      | <new_individual_label_value>      |
      | From the list below label | <from_the_list_below_label_value> |
      | Search by name label      | <search_by_name_label_value>      |

    Examples:
      | account_type                      | user_name  | new_individual_label_value | from_the_list_below_label_value | search_by_name_label_value |
      # Operator Holding Account
      | Operator Holding Account          | registered | New individual             | undefined                       | undefined                  |
      | Operator Holding Account          | validated  | New individual             | From the list below             | undefined                  |
      | Operator Holding Account          | enrolled   | New individual             | From the list below             | undefined                  |
      # Aircraft Operator Holding Account
      | Aircraft Operator Holding Account | registered | New individual             | undefined                       | undefined                  |
      | Aircraft Operator Holding Account | validated  | New individual             | From the list below             | undefined                  |
      | Aircraft Operator Holding Account | enrolled   | New individual             | From the list below             | undefined                  |
      # Trading Account
      | Trading Account                   | registered | New individual             | undefined                       | undefined                  |
      | Trading Account                   | validated  | New individual             | From the list below             | undefined                  |
      | Trading Account                   | enrolled   | New individual             | From the list below             | undefined                  |
      # Person Holding Account
      | Person Holding Account            | registered | New individual             | undefined                       | undefined                  |
      | Person Holding Account            | validated  | New individual             | From the list below             | undefined                  |
      | Person Holding Account            | enrolled   | New individual             | From the list below             | undefined                  |

  @test-case-id-0355558913 @exec-manual
  Scenario Outline: As enrolled user I dont see X screen object
    Given I sign in as "enrolled" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                    | field_value            |
      | Add a new organisation label | Add a new organisation |
    And The page "does not contain" the "Search by name" text
    And The page "contains" the "From the list below" text

    Examples:
      | account_type                      |
      | Operator Holding Account          |
      | Aircraft Operator Holding Account |
      | Trading Account                   |
      | Person Holding Account            |

  @test-case-id-0355558956
  Scenario Outline: As user when I cannot select an existing ah for individual from proposed list
    Given I sign in as "registered" user
    And I am an AR in the following accounts
      | type                              | state | status | holder type  | holder name    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | INDIVIDUAL   | Individual 1   |
      | OPERATOR_HOLDING_ACCOUNT          | OPEN  | ACTIVE | INDIVIDUAL   | Individual 2   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 1 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "<account_type>" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And The page "does not contain" the "Individual 2" text

    Examples:
      | account_type    |
      | Trading Account |

    @exec-manual
    Examples:
      | account_type           |
      | Person Holding Account |

  @sampling-smoke @test-case-id-732559076
  Scenario: As registered user I cannot create an oha or aoha account
    Given I sign in as "registered" user
    And I am an AR in the following accounts
      | type                              | state | status | holder type  | holder name    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | INDIVIDUAL   | Individual 1   |
      | OPERATOR_HOLDING_ACCOUNT          | OPEN  | ACTIVE | INDIVIDUAL   | Individual 2   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 1 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And The page "does not contain" the "Operator Holding Account" text
    And The page "does not contain" the "Aircraft Operator Holding Account" text

  @test-case-id-0355559002 @exec-manual
  Scenario Outline: As admin when I select an existing ah for individual via Searching I can view his details in readonly mode
    Given I sign in as "<user>" user
    And I am an AR in the following accounts
      | type                              | state | status | holder type  | holder name    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | INDIVIDUAL   | Individual 1   |
      | OPERATOR_HOLDING_ACCOUNT          | OPEN  | ACTIVE | INDIVIDUAL   | Individual 2   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 1 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I click the "Search by name" button
    And I enter value "Individual 2" in "Search by name textbox" field
    And I click the "Available typeahead Individual 2" link
    When I click the "Continue" button
    And The current page "is" in read-only mode

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @exec-manual @test-case-id-0355559037
  Scenario Outline: As admin when I select an existing ah for organisation via Searching I can view his details in readonly mode
    Given I sign in as "<user>" user
    And I am an AR in the following accounts
      | type                              | state | status | holder type  | holder name    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | INDIVIDUAL   | Individual 1   |
      | OPERATOR_HOLDING_ACCOUNT          | OPEN  | ACTIVE | INDIVIDUAL   | Individual 2   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 1 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I click the "Search by name" button
    And I enter value "Organisation 1" in "Search by name textbox" field
    And I click the "Available typeahead Organisation 1" link
    When I click the "Continue" button
    And The current page "is" in read-only mode

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @exec-manual @test-case-id-732559165
  Scenario: As user when I select an existing ah for organisation from proposed list I can view his details in readonly mode
    Given I sign in as "registered" user
    And I am an AR in the following accounts
      | type                              | state | status | holder type  | holder name    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 1 |
      | OPERATOR_HOLDING_ACCOUNT          | OPEN  | ACTIVE | INDIVIDUAL   | Individual 2   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN  | ACTIVE | ORGANISATION | Organisation 2 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "From the list below" option
    When I click the "Please select" link
    And I select the "Organisation 1" option
    When I click the "Continue" button
    And The current page "is" in read-only mode
    # The primary contact section contains the defined authorised representative:
    And The page "contains" the "Organisation 1" text
    And The page "contains" the "Test address 7" text

  @exec-manual @test-case-id-0355559104
  Scenario Outline: As user I can continue to next screen
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "<radio_option_name>" option
    When I click the "Continue" button
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName  | field_value |
      | Page title | title       |

    Examples:
      | radio_option_name | title                |
      | Individual        | Add the individual   |
      | Organisation      | Add the organisation |

  @exec-manual @test-case-id-732559221
  Scenario: As user I can Select the type of ah
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I click the "Back" link
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName  | field_value               |
      | Page title | Choose the account holder |


  @test-case-id-732559243
  Scenario: As user I can Fill ah individual details no name set
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    When I click the "Continue" button
    Then I see an error summary with "Enter the first and middle names.Enter the last name"
    And I see an error detail for field "event-name" with content "Error: Enter the first and middle names."

  @test-case-id-732559271 @exec-manual
  Scenario: As user I can Fill ah individual details no year of birth set
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    When I click the "Continue" button
    Then I see an error summary with "The account holder must be aged 18 or over"

  @test-case-id-732559297 @exec-manual
  Scenario: As user I can Fill ah individual details check edge case of invalid age
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I click the "Continue" button
    Then I see an error summary with "The account holder must be aged 18 or over"

  @exec-manual @test-case-id-732559327
  Scenario: As user I can Fill ah individual details happy path
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I click the "Back" link
    And I am presented with the "Request to open registry account" screen
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Add the individual's details |


  @test-case-id-732559357 @exec-manual
  Scenario: As a user I can fill ah individual address details and leave fist line empty
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    Then I see an error summary with "Enter their address"
    And I see an error detail for field "event-name" with content "Error: Enter their address"

  @exec-manual @test-case-id-732559398
  Scenario: As user I can go Fill ah individual address details leave empty field: Town or city
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Name 1               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    Then I see an error summary with "Enter the town or city"
    And I see an error detail for field "event-name" with content "Error: Enter the town or city"

  @exec-manual @test-case-id-732559439
  Scenario: As user I can go to Fill ah individual address details page leaving empty email field
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Name 1               |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    Then I see an error summary with "Enter the email address"
    And I see an error detail for field "event-name" with content "Error: Enter the email address"

  @test-case-id-732559479
  Scenario: As user I can go Fill ah individual address details leaving empty retype email field
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field               | fieldValue           |
      | Line 1              | Name 1               |
      | Fill Town or city   | Smyrna               |
      | dropdown            | Country Greece       |
      | Fill Postcode       | 12345                |
      | dropdown            | Country code GR (30) |
      | Fill Phone number 1 | 6911111111           |
      | Fill Email address  | test1@test2.com      |
    And I click the "Continue" button
    Then I see an error summary with "Retype the email address"
    And I see an error detail for field "event-name" with content "Error: Retype the email address"

  @exec-manual @test-case-id-732559520
  Scenario: As user I can go Fill ah individual address details leave empty field: Phone number 1
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Name 1               |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    Then I see an error summary with "Enter a telephone number, for example +UK (44) 0808 157 0192"
    And I see an error detail for field "event-name" with content "Error: Enter a telephone number, for example +UK (44) 0808 157 0192"

  @test-case-id-732559560 @exec-manual
  Scenario: As user I can go Fill ah individual address details postcode mandatory for UK only UK case WITHOUT postcode
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue             |
      | Line 1                     | Name 1                 |
      | Fill Town or city          | Smyrna                 |
      | dropdown                   | Country United Kingdom |
      | dropdown                   | Country code GR (30)   |
      | Fill Phone number 1        | 6911111111             |
      | Fill Email address         | test1@test2.com        |
      | Fill Re-type email address | test1@test2.com        |
    And I click the "Continue" button
    Then I see an error summary with "Enter the UK post code"
    And I see an error detail for field "event-name" with content "Error: Enter the UK post code"

  @test-case-id-732559600 @exec-manual
  Scenario: As user I can go Fill ah individual address details postcode mandatory for UK only UK case WITH postcode
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Fill Last name | Name 2     |
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue             |
      | Line 1                     | Name 1                 |
      | Fill Town or city          | Smyrna                 |
      | dropdown                   | Country United Kingdom |
      | dropdown                   | Country code GR (30)   |
      | Fill Phone number 1        | 6911111111             |
      | Fill Postcode              | N13HX                  |
      | Fill Email address         | test1@test2.com        |
      | Fill Re-type email address | test1@test2.com        |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value        |
      | First and middle names | Name 1             |
      | Last name text         | Name 2             |
      | Country of birth       | United Kingdom     |
      | Address                | Name 1             |
      | Town or city           | Smyrna             |
      | Postcode               | N13HX              |
      | Country                | United Kingdom     |
      | Phone number 1         | +GR (30)6911111111 |
      | Email address          | test1@test2.com    |

  @exec-manual @test-case-id-0355559547
  Scenario Outline: As user I can go Fill ah individual address details invalid phone formats
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                 | fieldValue             |
      | Line 1                | Name 1                 |
      | Fill Town or city     | Smyrna                 |
      | dropdown              | Country United Kingdom |
      | dropdown              | Country code GR (30)   |
      | Phone number 1        | <phone_number_value>   |
      | Fill Postcode         | N13HX                  |
      | Email address         | test1@test2.com        |
      | Re-type email address | test1@test2.com        |
    And I click the "Continue" button
    Then I see an error summary with "Enter a telephone number, for example +UK (44) 0808 157 0192"
    And I see an error detail for field "event-name" with content "Error: Enter a telephone number, for example +UK (44) 0808 157 0192"

    Examples:
      | phone_number_value |
      | 691111111+         |
      | 691111111a         |
      | 691111111          |

  @exec-manual @test-case-id-732559690
  Scenario: As user I can go Fill ah individual address details different email and retype email addresses
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    Given I navigate to the "Fill account type" screen
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                 | fieldValue      |
      | Fill Town or city     | Smyrna          |
      | dropdown              | Country Greece  |
      | Line 1                | Name 1          |
      | Phone number 1        | (020) 1111 1111 |
      | Phone number 2        | (020) 1111 1111 |
      | Email address         | test1@test2.com |
      | Re-type email address | test2@test2.com |
    And I click the "Continue" button
    Then I see an error summary with "The emails did not match"
    And I see an error detail for field "event-name" with content "Error: The emails did not match"

  @exec-manual @test-case-id-732559732
  Scenario: As user I can go Fill ah individual address details happy path
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    Given I navigate to the "Fill account type" screen
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                 | fieldValue      |
      | Fill Town or city     | Smyrna          |
      | dropdown              | Country Greece  |
      | Line 1                | Name 1          |
      | Phone number 1        | (020) 1111 1111 |
      | Phone number 2        | (020) 1111 1111 |
      | Email address         | test1@test2.com |
      | Re-type email address | test1@test2.com |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value       |
      | First and middle names | Name 1            |
      | Country of birth       | Greece            |
      | Home address           | Name 1 Smyrna     |
      | Phone number 1         | +GR (020)11111111 |
      | Email address          | test1@test2.com   |

  @exec-manual @test-case-id-732559773
  Scenario: As user I can go back to previous screen Fill ah Organisation details
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value               |
      | Page title | Choose the account holder |


  @exec-manual @test-case-id-0355559701
  Scenario Outline: As user I can continue to next screen organisation
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation name                | Name 1     |
      | Organisation registration number | 12345      |
    And I enter value "<value>" in "<field>" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Add the organisation address |

    Examples:
      | field                                              | value    |
      | VAT registration number with country code          | 67890    |
      | Reason for not providing a VAT registration number | reason 1 |

  @exec-manual @test-case-id-0355559734
  Scenario Outline: As user I cannot submit empty mandatory fields for organisation fill one field only
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field    | fieldValue    |
      | <field1> | <field1value> |
      | <field2> | <field2value> |
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | field1                                                      | field1value | field2                                                      | field2value | error                                                                              |
      | Organisation name                                           | Name 1      | Organisation registration number                            | 11111       | Either specify the VAT Registration number or provide a reason for not having one. |
      | Organisation name                                           | Name 1      | VAT registration number with country code                   | 11111       | Either specify the Registration number or provide a reason for not having one.     |
      | Organisation name                                           | Name 1      | Organisation Reason for not providing a registration number | 11111       | Either specify the VAT Registration number or provide a reason for not having one. |
      | Organisation name                                           | Name 1      | Reason for not providing a VAT registration number          | 11111       | Either specify the Registration number or provide a reason for not having one.     |
      | Organisation registration number                            | 11111       | VAT registration number with country code                   | 22222       | Enter the organisation name.                                                       |
      | Organisation registration number                            | 11111       | Reason for not providing a VAT registration number          | 22222       | Enter the organisation name.                                                       |
      | Organisation Reason for not providing a registration number | 11111       | VAT registration number with country code                   | 22222       | Enter the organisation name.                                                       |
      | Organisation Reason for not providing a registration number | 11111       | Reason for not providing a VAT registration number          | 22222       | Enter the organisation name.                                                       |

  @exec-manual @test-case-id-732559870
  Scenario: As user I can go back to previous screen Fill ah Organisation address
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
    And I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Add the organisation details |

  @exec-manual @test-case-id-732559900
  Scenario: As user I can continue to next screen organisation
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
    And I see the following fields having the values:
      | fieldName  | field_value        |
      | Page title | Check your answers |


  @exec-manual @test-case-id-732559935
  Scenario: As user I cannot submit empty mandatory fields leaving town or city field empty
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
      | field    | fieldValue     |
      | Line 1   | Line 1 value   |
      | dropdown | Country Greece |
    When I click the "Continue" button
    Then I see an error summary with "Enter the town or city"
    And I see an error detail for field "event-name" with content "Error: Enter the town or city."

  @exec-manual @test-case-id-732559970
  Scenario: As user I cannot submit empty mandatory fields leaving line 1 field empty
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    When I click the "Continue" button
    Then I see an error summary with "Enter their address"
    And I see an error detail for field "event-name" with content "Error: Enter their address"

  @test-case-id-7325510004
  Scenario: Postcode is mandatory for UK only organisation section
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
      | field             | fieldValue   |
      | Line 1            | Line 1 value |
      | Fill Town or city | Smyrna       |
    When I click the "Continue" button
    Then I see an error summary with "Enter the UK post code"
    And I see an error detail for field "event-name" with content "Error: Enter the UK post code."
    And I select the "Country United Kingdom" option
    And I enter value "12345" in "Fill Postcode" field
    And I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                         | field_value  |
      | Check your answers: Organisation details: Name                    | Name 1       |
      | Check your answers: Organisation details: Registration number     | 12345        |
      | Check your answers: Organisation details: VAT registration number | 67890        |
      | Check your answers: Organisation details: Organisation address    | Line 1 value |
      | Check your answers: Organisation details: Town or city            | Smyrna       |
      | Check your answers: Organisation details: Country                 | UK           |
      | Check your answers: Organisation details: Postcode                | 12345        |


  @test-case-id-7325510048
  Scenario: Ensure that in overview page for individual we can successfully Change Personal Details and Apply
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
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
      | Fill Last name | Last name  |
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
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName              | field_value        |
      | First and middle names | Name 1             |
      | Last name text         | Last name          |
      | Country of birth       | Afghanistan        |
      | Address                | Line 1 value       |
      | Town or city           | Smyrna             |
      | Postcode               | 12345              |
      | Country                | United Kingdom     |
      | Phone number 1         | +GR (30)6911111111 |
      | Email address          | test1@test2.com    |
    When I click the "Change Personal Details" link
    And I clear the "Fill Full name" field
    And I enter value "Name 2" in "Fill Full name" field
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Continue" button
    And The page "contains" the "Name 2" text

  @exec-manual @test-case-id-7325510113
  Scenario: Ensure that in overview page for individual we can successfully change Address and contact details and Apply
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName        | field_value                  |
      | Country of birth | United Kingdom               |
      | Country          | United Kingdom               |
      | Full name        | Name 1                       |
      | Home address     | Line 1 value Smyrna UK 12345 |
      | Phone number 1   | +GR (30)6911111111           |
      | Email address    | test1@test2.com              |
    When I click the "Change Address and contact details" link
    And I clear the "Line 1" field
    And I enter value "Line 1 second value" in "Line 1" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName    | field_value         |
      | Home address | Line 1 second value |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value        |
      | Page title | Check your answers |

  @exec-manual @test-case-id-7325510180
  Scenario: Ensure that overview page works properly changing Organisation details and Apply
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value            |
      | Name                 | Name 1                 |
      | Organisation address | Line 1 value Smyrna GR |
    When I click the "Change Organisation details" link
    And I clear the "Organisation name" field
    And I enter value "Name 2" in "Organisation name" field
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value |
      | Name      | Name 2      |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value        |
      | Page title | Check your answers |

  @test-case-id-7325510238
  Scenario: Ensure that overview page works properly changing Address and contact details and Apply
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
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
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value  |
      | Name                 | Name 1       |
      | Organisation address | Line 1 value |
      | Town or city         | Smyrna       |
      | Country              | GR           |
    When I click the "Change Address and contact details" link
    And I clear the "Line 1" field
    And I enter value "Line 1 second value" in "Line 1" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value         |
      | Organisation address | Line 1 second value |
      | Town or city         | Smyrna              |
      | Country              | GR                  |
    And I click the "Continue" button
    And The page "contains" the "Name 1" text

  @exec-manual @test-case-id-7325510299
  Scenario: Ensure that overview page works properly upon Apply button without changing fields for individual
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
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
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName            | field_value            |
      | Name                 | Name 1                 |
      | Organisation address | Line 1 value Smyrna GR |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value                                           |
      | Page title | Request to open an Emissions Trading Registry Account |

  @exec-manual @test-case-id-7325510347
  Scenario: Ensure that overview page works properly upon Apply button without changing fields for organisation
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field          | fieldValue |
      | Fill Full name | Name 1     |
      | Day of birth   | 01         |
      | Month of birth | 02         |
      | Year of birth  | 1970       |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field                      | fieldValue           |
      | Line 1                     | Line 1 value         |
      | Fill Town or city          | Smyrna               |
      | dropdown                   | Country Greece       |
      | Fill Postcode              | 12345                |
      | dropdown                   | Country code GR (30) |
      | Fill Phone number 1        | 6911111111           |
      | Fill Email address         | test1@test2.com      |
      | Fill Re-type email address | test1@test2.com      |
    And I click the "Continue" button
    And I click the "Back" link
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName        | field_value                  |
      | Country of birth | United Kingdom               |
      | Country          | United Kingdom               |
      | Full name        | Name 1                       |
      | Home address     | Line 1 value Smyrna UK 12345 |
      | Phone number 1   | +GR (30)6911111111           |
      | Email address    | test1@test2.com              |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value                                           |
      | Page title | Request to open an Emissions Trading Registry Account |
