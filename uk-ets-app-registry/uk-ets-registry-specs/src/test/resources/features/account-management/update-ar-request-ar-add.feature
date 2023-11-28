@functional-area-account-management

Feature: Account management - Update AR request AR add

  Epic: Account Management
  Version: 2.7 (27/04/2020)
  Story: (5.3.2) Request to update the authorised representatives
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949&preview=/124686949/129021021/UK%20Registry%20-%20Account%20Management.docx

  @test-case-id-06478925057
  Scenario: As admin I can cancel an update request submit of an AR
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                               |
      | Page main content | Authorised representatives No items added. Request update |
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Request to update the authorised representatives" text
    And The page "contains" the "Select type of update" text
    # cancel the request and ensure that no authorized representative has been added
    When I click the "Cancel" link
    Then I am presented with the "Account authorized representatives" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                               |
      | Page main content | Authorised representatives No items added. Request update |
    And The page "contains" the "Request update" text
    # ensure request update is available again after cancellation
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen

  @test-case-id-5506435072
  Scenario Outline: As a admin I cannot approve an authorized update request when I am both initiator and claimant due to 4 eyes principle
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
    # create task when the sign in user is both the claimant and the initiator
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                                       | initiated_by | completed_by | diff                                                   |
      | 100000001  | 100000003  |        |         | AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST | 100000003    |              | auth rep:ADD,UK88299344979,<access_rights_of_ar_added> |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | authorized representatives content | Check the update request and select either "Approve" or "Reject" History and comments Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Work contact details Authorised Representative2 Email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Request documents Access rights for the new user <access_rights> and trusted account list (tal) updates Back to top |
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

    Examples:
      | sign_in_user | access_rights_of_ar_added | access_rights                     |
      | senior admin | INITIATE_AND_APPROVE      | Initiate and approve transactions |
      | senior admin | INITIATE                  | Initiate transfers                |
      | junior admin | INITIATE_AND_APPROVE      | Initiate and approve transactions |
      | junior admin | INITIATE                  | Initiate transfers                |

  @test-case-id-06478925175
  Scenario: As admin I cannot update request submit of an AR to a CLOSED account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | CLOSED                   |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "does not contain" the "Request update" text

  @test-case-id-5506435168
  Scenario Outline: As enrolled AR with incorrect access rights APPROVE or READONLY I cannot submit AR update requests for any account
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "<should_contain>" the "Request update" text

    Examples:
      | access_rights | should_contain   |
      | APPROVE       | contains         |
      | READ_ONLY     | does not contain |

  @test-case-id-5506435210
  Scenario Outline: As AR with incorrect access rights I cannot complete AR request
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                                       | initiated_by | completed_by | diff                                            |
      | 100000001  | 100000003  |        |         | AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST | 100000004    |              | auth rep:ADD,UK88299344979,INITIATE_AND_APPROVE |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    #Check task is not available
    Then The page "does not contain" the "100000001" text

    Examples:
      | access_rights |
      | APPROVE       |
      | READ_ONLY     |

  @test-case-id-5506435254
  Scenario Outline: As AR I can ADD update request submit of an AR to non CLOSED account
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
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    And I have created 1 "senior" administrators
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
    # select add and continue
    When I select the "Add representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Add representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Please specify the representative"
    # option 1: select from the list
    When I click the "From the list below" button
    And I click the "Please select" button
    And I click the "Authorised Representative1" button
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Add representative" text
    And The page "contains" the "New user" text
    # option 2: navigate back and then select using user id
    And I click the "Back" link
    Then I am presented with the "Account authorized representatives" screen
    When I click the "By user ID" button
    Then The page "contains" the "Your representative must create a registry sign in for the UK Emissions Trading Registry and tell you their user ID" text
    And I enter value "UK88299344979" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Add representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights and continue
    When I click the "Approve" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure that Go back to the accountworks as expected
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Add authorised representative" text

    @sampling-smoke @sampling-mvp-smoke
    Examples:
      | account_status |
      | OPEN           |

    Examples:
      | account_status               |
      | ALL_TRANSACTIONS_RESTRICTED  |
      | SOME_TRANSACTIONS_RESTRICTED |

  @test-case-id-5506435372
  Scenario Outline: As AR with correct access rights I can add another AR to an non closed account
    # create first account
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | <account_status>         |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
      | authorisedRepresentative | Authorised Representative6 |
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
    And The page "does not contain" the "Suspend representative" text
    And The page "does not contain" the "Restore representative" text
    # try to continue without selection/
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # select add and continue
    When I select the "Add representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Add representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Please specify the representative"
    # option 1: select from the list - in case of authorized representative sign in, even with correct access rights,
    # the list should not show to authorized representative
    When I click the "From the list below" button
    And I click the "Please select" button
    And The page "does not contain" the "UK88299344979" text
    # option 2: navigate back and then select using user id
    And I click the "Back" link
    Then I am presented with the "Account authorized representatives" screen
    When I click the "Continue" button
    And I click the "By user ID" button
    Then The page "contains" the "Your representative must create a registry sign in for the UK Emissions Trading Registry and tell you their user ID" text
    And I enter value "UK88299344979" in "authorized representative user id" field
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Add representative" text
    And The page "contains" the "Select the access rights" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights and continue
    When I click the "Approve" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | check and confirm page main content | Request to update the authorised representatives Check the update request and confirm Type of update Change type of update Add representative User Change user You are adding the following authorised representative Name Work contact details Authorised Representative2 Email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Access rights for the new user Change access rights Approve transfers and trusted account list (tal) updates Submit request |
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure that Go back to the accountworks as expected
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And The page "contains" the "Add authorised representative" text

    Examples:
      | account_status               | access_rights        |
      | OPEN                         | INITIATE             |
      | OPEN                         | INITIATE_AND_APPROVE |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE             |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE_AND_APPROVE |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE             |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE_AND_APPROVE |

  @exec-manual @test-case-id-5506435502
  Scenario Outline: As AR with correct access rights I CANNOT add another AR to an non closed account as I cannot even search for account
    # create first account
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | <account_status>         |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
      | authorisedRepresentative | Authorised Representative6 |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    When I enter value "UK-100-50001-2-11" in "Account name or ID" field
    And I click the "Search" button
    Then The page "does not contain" the "Operator holding account 50001" text
    And The page "does not contain" the "Organisation 1" text

    Examples:
      | account_status      | access_rights        |
      | SUSPENDED           | INITIATE_AND_APPROVE |
      | SUSPENDED           | INITIATE             |
      | TRANSFER_PENDING    | INITIATE             |
      | TRANSFER_PENDING    | INITIATE_AND_APPROVE |
      | SUSPENDED_PARTIALLY | INITIATE             |
      | SUSPENDED_PARTIALLY | INITIATE_AND_APPROVE |

  @exec-manual @test-case-id-5506435550
  Scenario Outline: As admin I can approve AR addition request when I am NOT initiator and reject even I am initiator
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
      | account_id | claimed_by | status | outcome | type                                       | initiated_by | completed_by | diff                                            |
      | 100000001  | <claimant> |        |         | AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST | <initiator>  |              | auth rep:ADD,UK88299344979,INITIATE_AND_APPROVE |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | authorized representatives content | Check the update request and select either "Approve proposal" or "Reject proposal" History and comments Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Work contact details Authorised Representative2 Email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Access rights for the new user Initiate and approve transactions Back to top |
    When I click the "<task_action> request" button
    Then I am presented with the "Task confirmation <page_after_action>" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Add authorised representative" text
    And The page "contains" the "<task_after_action>" text
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                                                                                                                                                                                                                                                                       |
      | authorized representatives content | History and comments Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Work contact details Authorised Representative2 Email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Access rights for the new user Initiate and approve transactions Back to top |
    # check in Account > "history and comments"
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "History and comments item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                      |
      | Page main content | History and comments Information This page is under construction |
    # check in Account > "authorized representatives"
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                         |
      | Page main content | Authorised representatives <authorized_representative_page_content> |

    Examples:
      | sign_in_user | claimant  | initiator | task_action | page_after_action | task_after_action | authorized_representative_page_content                                                                                                                                                                   |
      # approve as senior admin, claimant, NOT initiator
      | senior admin | 100000003 | 100000004 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as senior admin, claimant, NOT initiator
      | senior admin | 100000003 | 100000004 | Reject      | reject            | REJECTED          | No items added. Request update                                                                                                                                                                           |
      # reject as senior admin, claimant, AND initiator
      | senior admin | 100000003 | 100000003 | Reject      | reject            | REJECTED          | No items added. Request update                                                                                                                                                                           |
      # approve as junior admin, claimant, NOT initiator
      | junior admin | 100000003 | 100000004 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as junior admin, claimant, NOT initiator
      | junior admin | 100000003 | 100000004 | Reject      | reject            | REJECTED          | No items added. Request update                                                                                                                                                                           |
      # reject as junior admin, claimant, AND initiator
      | junior admin | 100000003 | 100000003 | Reject      | reject            | REJECTED          | No items added. Request update                                                                                                                                                                           |

  @test-case-id-5506435635
  Scenario Outline: As AR with correct access rights I cannot complete add AR request
    # create first account
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                                       | initiated_by | completed_by | diff                                            |
      | 100000001  | 100000004  |        |         | AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST | 100000004    |              | auth rep:ADD,UK88299344979,INITIATE_AND_APPROVE |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    #Check task is not available
    Then The page "does not contain" the "100000001" text

    @sampling-smoke
    Examples:
      | access_rights |
      | INITIATE      |

    Examples:
      | access_rights        |
      | INITIATE_AND_APPROVE |
