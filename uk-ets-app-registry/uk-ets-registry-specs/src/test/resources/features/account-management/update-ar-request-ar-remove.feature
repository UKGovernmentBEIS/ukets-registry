@functional-area-account-management

Feature: Account management - Update AR request AR remove

  Epic: Account Management
  Version: v2.7 (23/04/2020)
  Story: 5.3.2	Request to update the authorised representatives, Remove AR

  @sampling-smoke @test-case-id-1754075688
  Scenario Outline: As admin I can REMOVE update request submit of an AR to non CLOSED account
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
    # select remove and continue
    When I select the "Remove representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Remove representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select auth rep for removal
    When I click the "Authorised Representative6" button
    Then I am presented with the "Account authorized representatives" screen
    And I click the "Continue" button
    And The page "contains" the "You will remove the following authorised representative from the account" text
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
    And The page "contains" the "Remove authorised representative" text

    Examples:
      | account_status               |
      | OPEN                         |
      | ALL_TRANSACTIONS_RESTRICTED  |
      | SOME_TRANSACTIONS_RESTRICTED |
      | SUSPENDED                    |
      | SUSPENDED_PARTIALLY          |

  @sampling-smoke @test-case-id-1754075689
  Scenario: As admin I can REMOVE update request submit of an AR to non CLOSED account
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
    # select remove and continue
    When I select the "Remove representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Remove representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select auth rep for removal
    When I click the "Authorised Representative6" button
    Then I am presented with the "Account authorized representatives" screen
    And I click the "Continue" button
    And The page "contains" the "You will remove the following authorised representative from the account" text
    # ensure that submit has been applied
    When I click the "Submit request" button
    Then I am presented with the "Account authorized representatives" screen
    Then I see an error summary with "Requests cannot be made for accounts with a Transfer Pending status"

  @test-case-id-1754075769
  Scenario Outline: As AR with correct access rights I can REMOVE another AR to an NON CLOSED account
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
    And The page "does not contain" the "Suspend representative" text
    And The page "does not contain" the "Restore representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
   # select remove and continue
    When I select the "Remove representative" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Remove representative" text
    # try to continue without selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    And The page "contains" the "Authorised Representative6" text
    # select auth rep for removal
    When I click the "Authorised Representative6" button
    Then I am presented with the "Account authorized representatives" screen
    And I click the "Continue" button
    And The page "contains" the "You will remove the following authorised representative from the account" text
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
    And The page "contains" the "Remove authorised representative" text

    Examples:
      | account_status               | access_rights        |
      | OPEN                         | INITIATE             |
      | OPEN                         | INITIATE_AND_APPROVE |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE             |
      | ALL_TRANSACTIONS_RESTRICTED  | INITIATE_AND_APPROVE |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE             |
      | SOME_TRANSACTIONS_RESTRICTED | INITIATE_AND_APPROVE |

  @exec-manual @test-case-id-1754075852
  Scenario Outline: As admin I can approve AR REMOVE request when I am NOT initiator and reject even I am initiator
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
      | account_id | claimed_by | status | outcome | type                                      | initiated_by | completed_by | diff                           |
      | 100000001  | <claimant> |        |         | AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST | <initiator>  |              | auth rep:REMOVE,UK977538690871 |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "<task_action> request" button
    Then I am presented with the "Task confirmation <page_after_action>" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Remove authorised representative" text
    And The page "contains" the "<task_after_action>" text
    # check in Account > "history & comments"
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "History and comments item" link
    # check in Account > "authorized representatives"
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                              |
      | Page main content | <authorized_representative_page_content> |

    Examples:
      | sign_in_user | claimant  | initiator | task_action | page_after_action | task_after_action | authorized_representative_page_content                                                                                                                                                                                              |
      # approve as senior admin, claimant, NOT initiator
      | senior admin | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Authorised representatives No items added. Request update                                                                                                                                                                           |
      # reject as senior admin, claimant, NOT initiator
      | senior admin | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as senior admin, claimant, AND initiator
      | senior admin | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # approve as junior admin, claimant, NOT initiator
      | junior admin | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Authorised representatives No items added. Request update                                                                                                                                                                           |
      # reject as junior admin, claimant, NOT initiator
      | junior admin | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as junior admin, claimant, AND initiator
      | junior admin | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |

  @exec-manual @test-case-id-1754075924
  Scenario Outline: As AR with correct access rights I cannot complete REMOVE AR request when I am NOT initiator
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
      | account_id | claimed_by | status | outcome | type                                      | initiated_by | completed_by | diff                           |
      | 100000001  | 100000005  |        |         | AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST | 100000005    |              | auth rep:REMOVE,UK977538690871 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName    | field_value                                                                                                                                                                                                                                                                                                                                          |
      | page content | Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Access rights Work contact details AR status User status Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info ACTIVE ENROLLED |
    Then The page "does not contain" the "<task_action> request" text

    Examples:
      | access_rights        | task_action |
      | INITIATE             | Approve     |
      | INITIATE_AND_APPROVE | Approve     |
      | INITIATE             | Reject      |
