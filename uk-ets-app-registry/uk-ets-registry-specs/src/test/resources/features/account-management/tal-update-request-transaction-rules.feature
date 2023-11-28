@functional-area-account-management

Feature: Account management - TAL update request transaction rules

  Epic: Account Management
  Version: 2.2 (19/03/2020)
  Story: 5.2.7	Request to update the TAL transaction rules
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @exec-manual @test-case-id-5017213987
  Scenario: As admin I can cancel an update of TAL transaction rules of account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account who has the first account in tal
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                    |
      | Page main content | Transactions Rules for transactions Do you want a second authorised representative to approve transfers of units to a trusted account No Are transfers to accounts not on the trusted account list allowed? Yes Request update |
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And I see the following fields having the values:
      | fieldName                                            | field_value                                                                                                                                 |
      | Is the approval of a second authorised question info | Request to update rules for transactions\nDo you want a second authorised representative to approve transfers of units to a trusted account |
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # click cancel
    And The page "contains" the "Cancel" text
    When I click the "Cancel" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen

  @exec-manual @test-case-id-5017214044
  Scenario: As admin I can navigate back during TAL transaction rules update of account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account who has the first account in tal
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # click back
    When I click the "Back" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And I see the following fields having the values:
      | fieldName                               | field_value                                                                                                  |
      | Are transfers to accounts question info | Request to update rules for transactions\nAre transfers to accounts not on the trusted account list allowed? |
    And The page "contains" the "Select one option" text
    # click back
    When I click the "Back" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And I see the following fields having the values:
      | fieldName                                            | field_value                                                                                                                                 |
      | Is the approval of a second authorised question info | Request to update rules for transactions\nDo you want a second authorised representative to approve transfers of units to a trusted account |
    And The page "contains" the "Select one option" text

  @sampling-smoke @test-case-id-5017214111
  Scenario: As admin I can update TAL transaction rules of account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account who has the first account in tal
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # change second option
    When I click the "Change second approval" link
    Then I am presented with the "Account overview tal transaction rules" screen
    And I click the "Yes" button
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # ensure data contains the old and new values
    Then I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                 |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Yes Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes Change                                                               |
    # change first option
    When I click the "Change account not in tal" link
    Then I am presented with the "Account overview tal transaction rules" screen
    And I click the "No" button
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # ensure data contains the old and new values for both two options
    Then I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                 |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Yes Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes No Change                                                            |
    When I click the "Submit request" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # navigate Go back to the account
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                  |
      | Page main content | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update |
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"

  @sampling-smoke @test-case-id-6893024164
  Scenario Outline: As AR enrolled with the correct access rights I can update TAL transaction rules of my account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 1                        |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account who has the first account in tal
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | accountIndex             | 0                          |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000002 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And I see the following fields having the values:
      | fieldName                               | field_value                                                                                                                                                                    |
      | Are transfers to accounts question info | Request to update rules for transactions Are transfers to accounts not on the trusted account list allowed? Are transfers to accounts not on the trusted account list allowed? |
    And The page "contains" the "Select one option" text
    When I click the "Continue" button
    # check data before changes
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                             |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes Change                                                           |
    # change second option
    When I click the "Change second approval" link
    Then I am presented with the "Account overview tal transaction rules" screen
    And I click the "Yes" button
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # ensure data contains the old and new values
    Then I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                 |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Yes Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes Change                                                               |
    # change first option
    When I click the "Change account not in tal" link
    Then I am presented with the "Account overview tal transaction rules" screen
    And I click the "No" button
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Continue" button
    Then I am presented with the "Account overview tal transaction rules" screen
    # ensure data contains the old and new values for both two options
    Then I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                 |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Yes Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes No Change                                                            |
    When I click the "Change surrender transaction or a return of excess allocation" link
    Then I am presented with the "Account overview tal transaction rules" screen
    And I click the "No" button
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                 |
      | seconds approval info  | Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? No Yes Change |
      | transfers allowed info | Are transfers to accounts not on the trusted account list allowed? Yes No Change                                                            |
      | surrender allowed info | Is the approval of a second AR necessary to execute a surrender transaction or a return of excess allocation? Yes No Change                 |
    When I click the "Submit request" button
    Then I am presented with the "Account overview tal transaction rules" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # navigate Go back to the account
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                  |
      | Page main content | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update |
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And The page "contains" the "Update transaction rules" text

    Examples:
      | access_rights        |
      | INITIATE             |
      | INITIATE_AND_APPROVE |

  @test-case-id-6893024264
  Scenario Outline: As AR with incorrect access rights I cannot update TAL transaction rules of my account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 1                        |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account who has the first account in tal
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | accountIndex             | 0                          |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000002 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then The page "does not contain" the "Request update" text
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                   |
      | Page main content | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes |

    Examples:
      | access_rights |
      | APPROVE       |
      | READ_ONLY     |
