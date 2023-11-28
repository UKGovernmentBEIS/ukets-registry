@functional-area-account-management

Feature: Account management - Update AR request AR restore

  Epic: Account Management
  Version: 2.7 (27/04/2020)
  Story: (5.3.2) Request to update the authorised representatives
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949&preview=/124686949/129021021/UK%20Registry%20-%20Account%20Management.docx

  @test-case-id-5760936303
  Scenario Outline: As admin I can Restore update request submit of an AR to non CLOSED account
    # create first account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | accountStatus            | <account_status>                     |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative6 SUSPENDED |
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
   # select Restore and continue
    When I select the "Restore representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select auth rep for Restore
    When I click the "Authorised Representative6" button
    Then I am presented with the "Account authorized representatives" screen
    And I click the "Continue" button
    And I enter value "comment 1" in "comment area" field
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
    And The page "contains" the "Restore authorised representative" text

    @sampling-smoke
    Examples:
      | account_status               |
      | OPEN                         |
      
      Examples:
      | account_status               |
      | ALL_TRANSACTIONS_RESTRICTED  |
      | SOME_TRANSACTIONS_RESTRICTED |
      | SUSPENDED                    |
      | SUSPENDED_PARTIALLY          |

  @sampling-smoke @test-case-id-5760936304
  Scenario: As admin I cannot Restore update request submit of an AR to Transfer Pending account
    # create first account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | accountStatus            | TRANSFER_PENDING                     |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative6 SUSPENDED |
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
   # select Restore and continue
    When I select the "Restore representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select auth rep for Restore
    When I click the "Authorised Representative6" button
    Then I am presented with the "Account authorized representatives" screen
    And I click the "Continue" button
    And I enter value "comment 1" in "comment area" field
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    Then I see an error summary with "Requests cannot be made for accounts with a Transfer Pending status"

  @exec-manual @test-case-id-5760936383
  Scenario Outline: As admin I can approve AR Restore request when I am NOT initiator and reject even I am initiator
     # create first account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | accountStatus            | OPEN                                 |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative3 SUSPENDED |
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
      | account_id | claimed_by | status | outcome | type                                      | initiated_by | completed_by | diff                            |
      | 100000001  | <claimant> |        |         | AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST | <initiator>  |              | auth rep:RESTORE,UK977538690871 |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "<task_action> request" button
    Then I am presented with the "Task confirmation <page_after_action>" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Restore authorised representative" text
    And The page "contains" the "<task_after_action>" text
    # check in Account > "history & comments"
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # check in Account > "authorized representatives"
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                              |
      | Page main content | <authorized_representative_page_content> |

    Examples:
      | sign_in_user | claimant  | initiator | task_action | page_after_action | task_after_action | authorized_representative_page_content                                                                                                                                                                                                 |
      # approve as senior admin, claimant, NOT initiator
      | senior admin | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update    |
      # reject as senior admin, claimant, NOT initiator
      | senior admin | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Authorised representatives Suspended representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as senior admin, claimant, AND initiator
      | senior admin | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Authorised representatives Suspended representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # approve as junior admin, claimant, NOT initiator
      | junior admin | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update    |
      # reject as junior admin, claimant, NOT initiator
      | junior admin | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Authorised representatives Suspended representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as junior admin, claimant, AND initiator
      | junior admin | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Authorised representatives Suspended representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |

  @exec-manual @test-case-id-5760936454
  Scenario Outline: As AR with correct access rights I cannot complete Restore AR request when I am NOT initiator
   # create first account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | accountStatus            | OPEN                                 |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative3 SUSPENDED |
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
      | account_id | claimed_by | status | outcome | type                                      | initiated_by | completed_by | diff                            |
      | 100000001  | 100000005  |        |         | AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST | 100000005    |              | auth rep:RESTORE,UK977538690871 |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And The page "does not contain" the "<task_action> request" text

    Examples:
      | access_rights        | task_action |
      | INITIATE             | Approve     |
      | INITIATE_AND_APPROVE | Approve     |
      | INITIATE             | Reject      |
      | INITIATE_AND_APPROVE | Reject      |
