@functional-area-account-management

Feature: Account management - Update AR request AR replace

  Epic: Account Management
  Version: v2.7 (23/04/2020)
  Story: 5.3.2	Request to update the authorised representatives, Replace AR

  @test-case-id-4490775981
  Scenario Outline: As admin I can REPLACE update request submit of an AR to non CLOSED account
    # create first account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Request to update the authorised representatives" text
    And The page "contains" the "Select type of update" text
    # try to continue without selection/
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # select replace and continue
    When I select the "Replace representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Replace representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Please select one from the list.Please specify the representative"
    And I select the "Authorised - Representative6" option
    And I click the "By user ID" button
    And I enter value "UK689820232063" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "New user" text
    And The page "contains" the "Authorised Representative2" text
    And I click the "Back" link
    And I select the "Authorised - Representative6" option
    And I click the "By user ID" button
    And I enter value "UK689820232063" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights
    When I click the "<select_access_rights>" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure that Go back to the account works as expected
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Replace authorised representative" text

    @sampling-smoke
    Examples:
      | account_status | select_access_rights |
      | OPEN           | Initiate             |

    Examples:
      | account_status               | select_access_rights |
      | ALL_TRANSACTIONS_RESTRICTED  | Approve              |
      | SOME_TRANSACTIONS_RESTRICTED | Read only            |
      | SUSPENDED                    | Initiate             |
      | SUSPENDED_PARTIALLY          | Approve              |

  @sampling-smoke @test-case-id-4490775982
  Scenario: As admin I cannot REPLACE update request submit of an AR to Transfer Pending account
    # create first account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | TRANSFER_PENDING           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Request to update the authorised representatives" text
    And The page "contains" the "Select type of update" text
    # try to continue without selection/
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # select replace and continue
    When I select the "Replace representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Replace representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Please select one from the list.Please specify the representative"
    And I select the "Authorised - Representative6" option
    And I click the "By user ID" button
    And I enter value "UK689820232063" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "New user" text
    And The page "contains" the "Authorised Representative2" text
    And I click the "Back" link
    And I select the "Authorised - Representative6" option
    And I click the "By user ID" button
    And I enter value "UK689820232063" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights
    When I click the "Read only" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    Then I see an error summary with "Requests cannot be made for accounts with a Transfer Pending status"

  @test-case-id-4490776075
  Scenario Outline: As AR with correct access rights I can REPLACE another AR to an non closed account
    # create first account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Request to update the authorised representatives" text
    And The page "contains" the "Select type of update" text
    # authorized representatives, even with correct access rights, do not see the Suspend, Restore options
    # ARs cannot see the suspend and restore options
    And The page "does not contain" the "Suspend representative" text
    And The page "does not contain" the "Restore representative" text
    # try to continue without selection/
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # select replace and continue
    When I select the "Replace representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Replace representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Please select one from the list.Please specify the representative"
    # only option available: replace using option "With a User ID" (ARs cannot select from list)
    And I click the "select list ar" button
    And I select the "Authorised - Representative6" option
    And I click the "By user ID" button
    And I enter value "UK689820232063" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "New user" text
    And The page "contains" the "Authorised Representative2" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights
    When I click the "<select_new_access_rights>" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure that Go back to the account works as expected
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Replace authorised representative" text

    Examples:
      | account_status               | access_rights        | select_new_access_rights |
      | OPEN                         | INITIATE             | Initiate and approve     |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE             | Initiate and approve     |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE_AND_APPROVE | Initiate                 |
      
    @exec-manual
    Examples:
      | account_status               | access_rights        | select_new_access_rights |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE_AND_APPROVE | Initiate                 |
      | OPEN                         | INITIATE_AND_APPROVE | Initiate                 |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE             | Initiate and approve     |

  @exec-manual @test-case-id-4490776169
  Scenario Outline: As admin I can approve AR REPLACE request when I am NOT initiator and reject even I am initiator
    # create first account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative3 |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<sign_in_user>" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                                          | initiated_by | completed_by | diff                                                               |
      | 100000001  | <claimant> |        |         | AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST | <initiator>  |              | auth rep:REPLACE,UK689820232063,UK977538690871,<new_access_rights> |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "<task_action> request" button
    Then I am presented with the "Task confirmation <page_after_action>" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Replace authorised representative" text
    And The page "contains" the "<task_after_action>" text
    # check in Account > "history & comments"
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # check in Account > "authorized representatives"
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                         |
      | Page main content | Authorised representatives <authorized_representative_page_content> |

    Examples:
      | sign_in_user | new_access_rights | claimant  | initiator | task_action | page_after_action | task_after_action | authorized_representative_page_content                                                                                                                                                                   |
      # approve as senior admin, claimant,
      | senior admin | INITIATE          | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative2 Initiate Email dont@care.com Phone number +UK (44)1434634996 more info Request update                          |
      # reject as senior admin, claimant,
      | senior admin | APPROVE           | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as senior admin, claimant,
      | senior admin | READ_ONLY         | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # approve as junior admin, claimant,
      | junior admin | INITIATE          | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative2 Initiate Email dont@care.com Phone number +UK (44)1434634996 more info Request update                          |
      # reject as junior admin, claimant,
      | junior admin | APPROVE           | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as junior admin, claimant,
      | junior admin | READ_ONLY         | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |

  @exec-manual  @test-case-id-4490776240
  Scenario Outline: As AR with correct access rights I cannot complete REPLACE AR request when I am NOT initiator
    # create first account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative3 |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                                          | initiated_by | completed_by | diff                                                               |
      | 100000001  | 100000005  |        |         | AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST | 100000005    |              | auth rep:REPLACE,UK689820232063,UK977538690871,<new_access_rights> |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName    | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | page content | Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Replace the following authorised representative Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info with the one below Name Work contact details Authorised Representative2 Email dont@care.com Phone number +UK (44)1434634996 more info Access rights for the new user <access_rights_name> |
    Then The page "does not contain" the "<task_action> request" text

    Examples:
      | access_rights        | new_access_rights    | access_rights_name                | task_action |
      | INITIATE             | INITIATE_AND_APPROVE | Initiate and approve transactions | Approve     |
      | INITIATE_AND_APPROVE | INITIATE             | Initiate transfers                | Approve     |
      | INITIATE             | INITIATE_AND_APPROVE | Initiate and approve transactions | Reject      |
      | INITIATE_AND_APPROVE | INITIATE             | Initiate transfers                | Reject      |
      | INITIATE             | APPROVE              | Approve                           | Approve     |
      | INITIATE_AND_APPROVE | APPROVE              | Approve                           | Approve     |
      | INITIATE             | READ_ONLY            | Read only                         | Reject      |
      | INITIATE_AND_APPROVE | READ_ONLY            | Read only                         | Reject      |
