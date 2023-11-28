@functional-area-account-opening

Feature: Account opening - Check information before submit and success page

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (&6.1.1.7) Check information provided before submitting
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

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
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    * I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" link
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button

  @exec-manual @test-case-id-555517411332
  Scenario: As user I can navigate back and forth
    # specify account type
    And I select the "Trading Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue     |
      | Organisation name                         | account Name 1 |
      | Organisation registration number          | 00001          |
      | VAT registration number with country code | 00002          |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Line 1            | line 1 value account type       |
      | Fill Town or city | town city value in account type |
      | dropdown          | Country Greece                  |
    And I click the "Continue" button
    And I click the "Continue" button
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK977538690871" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    # check back and forth buttons
    When I click the "Continue" button
    When I click the "Upper Back" link
    When I click the "Continue" button
    When I click the "Information is incomplete or wrong Back" link
    When I click the "Continue" button
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |

  @test-case-id-555517411450
  Scenario: As user I can see all sections relevant to my opening request having correct data for Trading ah with Organisation
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
    # Create a primary contact
    When I click the "Add the primary contact" link
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    When I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" button
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    Then I see the following fields having the values:
      | fieldName                                | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | check your answers before submit content | request to open an emissions trading registry account check your answers before submitting your account opening request close all sections 1account holder organisation details change name account name 1 registration number 00001 vat number 00002 organisation address change address line 1 value account type town city value in account type greece primary contact legal rep first name legal rep last name primary contact details change first and middle names legal rep first name last name legal rep last name also known as legal rep aka i confirm that the primary contact is aged 18 or over yes primary contact work details change company position consultant address legal rep line 1 legal rep town city uk 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com 2account details account type ets - trading account account name giovan tsaous change billing details address line 1 value account type town city value in account type greece 3default rules for the trusted account list do you want a second authorised representative to approve transfers of units to a trusted account? yes change do you want to allow transfers of units to accounts that are not on the trusted account list? no change 4authorised representatives authorised representative1 change name authorised representative1 user id uk88299344979 permissions approve transfers and trusted account list (tal) updates authorised representative2 change name authorised representative2 user id uk689820232063 permissions initiate and approve transactions and trusted account list (tal) updates 5 now submit your request by submitting this request you confirm that all the information you have given is correct and up to date confirm and submit |
    # submit
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 2" link
    Then I am presented with the "View account" screen

  @exec-manual @test-case-id-555517411630
  Scenario: As user I can submit an Account Type Trading account ETS with 1 AR selected
    # account type: Trading Account
    And I select the "Trading Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Organisation" option
    When I click the "Continue" button
    And I select the "Add a new organisation" option
    When I click the "Continue" button
    And I enter the following values to the fields:
      | field                                     | fieldValue     |
      | Organisation name                         | account Name 1 |
      | Organisation registration number          | 00001          |
      | VAT registration number with country code | 00002          |
    And I click the "Continue" button
    And I enter the following values to the fields:
      | field             | fieldValue                      |
      | Line 1            | line 1 value account type       |
      | Fill Town or city | town city value in account type |
      | dropdown          | Country Greece                  |
    And I click the "Continue" button
    And I click the "Continue" button
    # Create a primary contact
    When I click the "Add the primary contact" link
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    When I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    Then I see the following fields having the values:
      | fieldName                                | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | check your answers before submit content | Request to open an Emissions Trading Registry account Check your answers before submitting your account opening request Close all sections 1Account holder Organisation details Change Name account Name 1 Registration number 00001 VAT number 00002 Organisation address Change Address line 1 value account type town city value in account type Greece Primary contact legal rep first name legal rep last name Primary contact details Change First and middle names legal rep first name Last name legal rep last name Also known as legal rep aka I confirm that the primary contact is aged 18 or over Yes Primary contact work details Change Company position Consultant Address legal rep line 1 legal rep town city UK 12312 Phone number 1 +GR (30)6911111111 Email address estoudiantina@smyrna.com 2Account details Account type ETS - Trading Account Account name giovan tsaous Change Billing address Address line 1 value account type town city value in account type Greece 3Default rules for the trusted account list Do you want a second authorised representative to approve transfers of units to a trusted account? Yes Change Do you want to allow transfers of units to accounts that are not on the trusted account list? No Change 4Authorised representatives Authorised Representative1 Change Name Authorised Representative1 User ID UK88299344979 Permissions Approve Authorised Representative2 Change Name Authorised Representative2 User ID UK6 89820232063 Permissions Initiate and approve transactions and trusted account list (tal) updates 5 Now submit your request By submitting this request you confirm that all the information you have given is correct and up to date Confirm and submit |
    # submit
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |

  @test-case-id-555517411785
  Scenario: As user I can see all sections relevant to my Person holding kp opening request having correct data for Organisation
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
    And I enter value "line 1 value account type" in "Line 1" field
    And I enter value "town city value in account type" in "Fill Town or city" field
    And I select the "Country Greece" option
    And I click the "Continue" button
    And I click the "Continue" button
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    Then I see the following fields having the values:
      | fieldName                                | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | check your answers before submit content | request to open an emissions trading registry account check your answers before submitting your account opening request close all sections 1account holder organisation details change name account name 1 registration number 00001 vat number 00002 organisation address change address line 1 value account type town city value in account type greece primary contact legal rep first name legal rep last name primary contact details change first and middle names legal rep first name last name legal rep last name also known as legal rep aka i confirm that the primary contact is aged 18 or over yes primary contact work details change company position consultant address legal rep line 1 legal rep town city uk 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com 2account details account type kp - person holding account account name giovan tsaous change billing details address line 1 value account type town city value in account type greece 3default rules for the trusted account list do you want a second authorised representative to approve transfers of units to a trusted account? yes change do you want to allow transfers of units to accounts that are not on the trusted account list? no change 4authorised representatives authorised representative1 change name authorised representative1 user id uk88299344979 permissions approve transfers and trusted account list (tal) updates authorised representative2 change name authorised representative2 user id uk689820232063 permissions initiate and approve transactions and trusted account list (tal) updates 5 now submit your request by submitting this request you confirm that all the information you have given is correct and up to date confirm and submit |
    # submit
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 2" link
    Then I am presented with the "View account" screen

  @test-case-id-872905212 @sampling-smoke
  Scenario: As user I can see that after account opening task approval a new open account is created
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
    And I enter value "line 1 value account type" in "Line 1" field
    And I enter value "town city value in account type" in "Fill Town or city" field
    And I select the "Country Greece" option
    And I click the "Continue" button
    And I click the "Continue" button
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    And I "check" the "Billing address is the same as account holder address" checkbox
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another sra, claim and approve the account opening task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Claimed" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    When I am presented with the "Task Details" screen
    # ensure that open account is created
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 1" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | Account name: giovan tsaous |
      | Account status     | OPEN Change status          |

  @test-case-id-555517411966
  Scenario: As user I can see all sections relevant to my oha opening request having correct data for individual
    # account type: Operator holding account (ETS)
    And I select the "Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I select the "Afghanistan" option
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Continue" button
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
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    Then I see the following fields having the values:
      | fieldName                                | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | check your answers before submit content | request to open an emissions trading registry account check your answers before submitting your account opening request close all sections 1account holder individual's details change first and middle names legal rep first name last name legal rep last name country of birth afghanistan i confirm that the account holder is aged 18 or over yes individual's contact details change home address legal rep line 1 legal rep town city united kingdom 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com primary contact legal rep first name legal rep last name primary contact details change first and middle names legal rep first name last name legal rep last name also known as legal rep aka i confirm that the primary contact is aged 18 or over yes primary contact work details change company position consultant address legal rep line 1 legal rep town city uk 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com 2account details account type ets - operator holding account account name giovan tsaous change 3default rules for the trusted account list do you want a second authorised representative to approve transfers of units to a trusted account? yes change do you want to allow transfers of units to accounts that are not on the trusted account list? no change do you want a second authorised representative to approve a surrender transaction or a return of excess allocation? yes change 4installation installation name not updated installation name 1 change installation and permit details installation name installation activity type combustion of fuels permit id 12345 permit entry into force 1 january 2050 regulator nrw first year of verified emission submission 2051 last year of verified emission submission 5authorised representatives authorised representative1 change name authorised representative1 user id uk88299344979 permissions approve transfers and trusted account list (tal) updates authorised representative2 change name authorised representative2 user id uk689820232063 permissions initiate and approve transactions and trusted account list (tal) updates 6 now submit your request by submitting this request you confirm that all the information you have given is correct and up to date confirm and submit |
    # submit
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 2" link
    Then I am presented with the "View account" screen

  @test-case-id-555517411967
  Scenario: As user I can see all sections relevant to my oha opening request having correct data for individual
    # account type: Operator holding account (ETS)
    And I select the "Aircraft Operator Holding Account" option
    And I click the "Continue" button
    And I click the "Account holder details" link
    And I select the "Individual" option
    When I click the "Continue" button
    And I select the "Add a new individual" option
    When I click the "Continue" button
    And I select the "Afghanistan" option
    And I enter the following values to the fields:
      | field         | fieldValue           |
      | First name    | legal rep first name |
      | Last name     | legal rep last name  |
      | Also known as | legal rep aka        |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
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
    # Fill account details
    And I click the "Account details" link
    And I enter value "giovan tsaous" in "Account name" field
    When I click the "Continue" button
    When I click the "Continue" button
    # transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Continue" button
    Given I click the "Aircraft Operator details" link
    And I enter the following values to the fields:
      | field                                      | fieldValue |
      | Fill Monitoring plan ID                    | abc        |
      | First year of verified emission submission | 2021       |
    And I click the "Regulator" button
    When I select the "Regulator: DAERA" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I click the "Continue" button
    When I click the "Continue" button
    # account holder checks
    When I click the "Account holder" link
    And I click the "Open all" link
    Then I see the following fields having the values:
      | fieldName                                | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | check your answers before submit content | request to open an emissions trading registry account check your answers before submitting your account opening request close all sections 1account holder individual's details change first and middle names legal rep first name last name legal rep last name country of birth afghanistan i confirm that the account holder is aged 18 or over yes individual's contact details change home address legal rep line 1 legal rep town city united kingdom 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com primary contact legal rep first name legal rep last name primary contact details change first and middle names legal rep first name last name legal rep last name also known as legal rep aka i confirm that the primary contact is aged 18 or over yes primary contact work details change company position consultant address legal rep line 1 legal rep town city uk 12312 phone number 1 +gr (30)6911111111 email address estoudiantina@smyrna.com 2account details account type ets - aircraft operator holding account account name giovan tsaous change 3default rules for the trusted account list do you want a second authorised representative to approve transfers of units to a trusted account? yes change do you want to allow transfers of units to accounts that are not on the trusted account list? no change do you want a second authorised representative to approve a surrender transaction or a return of excess allocation? yes change 4aircraft operator monitoring plan id not updated abc change installation and permit details monitoring plan id regulator daera first year of verified emission submission 2021 last year of verified emission submission 5authorised representatives authorised representative1 change name authorised representative1 user id uk88299344979 permissions approve transfers and trusted account list (tal) updates authorised representative2 change name authorised representative2 user id uk689820232063 permissions initiate and approve transactions and trusted account list (tal) updates 6 now submit your request by submitting this request you confirm that all the information you have given is correct and up to date confirm and submit |
    # submit
    When I click the "Confirm and submit" button
    And I get a Request Id
    Then I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 2" link
    Then I am presented with the "View account" screen

  @exec-manual @test-case-id-555517412169
  Scenario: As user I can view the details of a section of opening request
    When I click on the triangle button
    Then the section expands

  @exec-manual @test-case-id-555517412175
  Scenario: As user I can be notified when I cannot submit my request due to technical issues
    And I click the "Submit" button
    And the backend server reports a technical issue
    Then I see an error summary with 'Sorry, the system is currently experiencing technical difficulties. Submit again in a couple of minutes before leaving this page. If the problem continues, contact the help desk first before re-entering the necessary information'
