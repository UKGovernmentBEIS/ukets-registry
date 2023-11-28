@functional-area-account-management

Feature: Account management - Update AR request AR change access rights

  Epic: Account Management
  Version: v2.7 (23/04/2020)
  Story: 5.3.2	Request to update the authorised representatives Change AR access rights.

  @test-case-id-95976717
  Scenario Outline: As admin I can CHANGE access rights update request submit of an AR to non CLOSED account
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
    # select CHANGE ACCESS RIGHTS and continue
    When I select the "Change access rights" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Change access rights" text
    # try to continue without user selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select user
    And I click the "Authorised Representative6" button
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without access rights selection
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
    And The page "contains" the "Change authorised representatives access rights" text

    @sampling-smoke
    Examples:
      | account_status | select_new_access_rights |
      | OPEN           | Initiate                 |

    Examples:
      | account_status               | select_new_access_rights |
      | ALL_TRANSACTIONS_RESTRICTED  | Approve                  |
      | SUSPENDED                    | Initiate                 |
      | OPEN                         | Initiate and approve     |
      | OPEN                         | Approve                  |
      | OPEN                         | Read only                |
      | SOME_TRANSACTIONS_RESTRICTED | Read only                |
      | SUSPENDED_PARTIALLY          | Approve                  |

  @sampling-smoke @test-case-id-95976718
  Scenario: As admin I cannot CHANGE access rights update request submit of an AR to Transfer Pending account
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
    # select CHANGE ACCESS RIGHTS and continue
    When I select the "Change access rights" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Change access rights" text
    # try to continue without user selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select user
    And I click the "Authorised Representative6" button
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without access rights selection
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

  @test-case-id-95976808
  Scenario Outline: As AR with correct access rights I can CHANGE access rights of another AR to an non closed account
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
      | ACTIVE | <auth_rep_access_rights> | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    And The page "contains" the "Request update" text
    When I click the "Request update" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Request to update the authorised representatives" text
    And The page "does not contain" the "Suspend representative" text
    And The page "does not contain" the "Restore representative" text
    # try to continue without selection/
    When I click the "Continue" button
    Then I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # select CHANGE ACCESS RIGHTS and continue
    When I select the "Change access rights" option
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And The page "contains" the "Change access rights" text
    # try to continue without user selection
    When I click the "Continue" button
    Then I see an error summary with "Select a user"
    And I see an error detail for field "authorisedRepresentative" with content "Error: Select a user"
    # select user
    And I click the "Authorised Representative6" button
    When I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    # try to continue without access rights selection
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "accessRights" with content "Error: Select the access rights"
    # select access rights
    When I click the "<select_new_access_rights>" button
    And I click the "Continue" button
    Then I am presented with the "Account authorized representatives" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | check and confirm page main content | Request to update the authorised representatives Check the update request and confirm ! Warning This change will only apply to future requests. Type of update Change type of update Change access rights User Change user Name Access rights Work contact details AR status User status Authorised Representative6 Initiate and approve transactions and trusted account list (tal) updates Email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 ACTIVE ENROLLED New access rights Change access rights <access_right_new_info> Submit request |
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
    And The page "contains" the "Change authorised representatives access rights" text

    @sampling-smoke
    Examples:
      | auth_rep_access_rights | account_status | select_new_access_rights | access_right_new_info                                     |
      | INITIATE_AND_APPROVE   | OPEN           | Initiate                 | Initiate transfers and trusted account list (tal) updates |

    Examples:
      | auth_rep_access_rights | account_status               | select_new_access_rights | access_right_new_info                                                    |
      | INITIATE_AND_APPROVE   | OPEN                         | Initiate and approve     | Initiate and approve transactions and trusted account list (tal) updates |
      | INITIATE_AND_APPROVE   | OPEN                         | Approve                  | Approve transfers and trusted account list (tal) updates                 |
      | INITIATE_AND_APPROVE   | OPEN                         | Read only                | Read only                                                                |
      | INITIATE               | OPEN                         | Initiate                 | Initiate transfers and trusted account list (tal) updates                |
      | INITIATE               | OPEN                         | Initiate and approve     | Initiate and approve transactions and trusted account list (tal) updates |
      | INITIATE               | OPEN                         | Approve                  | Approve transfers and trusted account list (tal) updates                 |
      | INITIATE               | OPEN                         | Read only                | Read only                                                                |
      | INITIATE               | ALL_TRANSACTIONS_RESTRICTED  | Approve                  | Approve transfers and trusted account list (tal) updates                 |
      | INITIATE               | SOME_TRANSACTIONS_RESTRICTED | Read only                | Read only                                                                |

  @exec-manual @test-case-id-95976903
  Scenario Outline: As admin I can approve AR Change access rights request when I am NOT initiator and reject even I am initiator
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
      | account_id | claimed_by | status | outcome | type                                                   | initiated_by | completed_by | diff                                                             |
      | 100000001  | <claimant> |        |         | AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST | <initiator>  |              | auth rep:CHANGE_ACCESS_RIGHTS,UK977538690871,<new_access_rights> |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName    | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | page content | Check the update request and select either 'Approve proposal' or 'Reject proposal' History & comments Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Access rights Work contact details AR status User status Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info ACTIVE ENROLLED New access rights <new_access_rights_name> |
    When I click the "<task_action> request" button
    Then I am presented with the "Task confirmation <page_after_action>" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<task_after_action>" text
    Then I see the following fields having the values:
      | fieldName    | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | page content | Check the update request and select either 'Approve proposal' or 'Reject proposal' History & comments Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Access rights Work contact details AR status User status Authorised Representative3 <new_access_name_info> Email dont@care.com Phone number +UK (44)1434634996 more info ACTIVE ENROLLED New access rights <new_access_rights_name> |
    # check in Account > "history & comments"
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
      | sign_in_user | new_access_rights | new_access_rights_name | new_access_name_info              | claimant  | initiator | task_action | page_after_action | task_after_action | authorized_representative_page_content                                                                                                                                                                   |
      # approve as senior admin, claimant, NOT initiator
      | senior admin | INITIATE          | Initiate               | Initiate                          | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate Email dont@care.com Phone number +UK (44)1434634996 more info Request update                          |
      # reject as senior admin, claimant, NOT initiator
      | senior admin | APPROVE           | Approve                | Initiate and approve transactions | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as senior admin, claimant, AND initiator
      | senior admin | READ_ONLY         | Read only              | Initiate and approve transactions | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # approve as junior admin, claimant, NOT initiator
      | junior admin | INITIATE          | Initiate               | Initiate                          | 100000004 | 100000005 | Approve     | approve           | APPROVED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate Email dont@care.com Phone number +UK (44)1434634996 more info Request update                          |
      # reject as junior admin, claimant, NOT initiator
      | junior admin | APPROVE           | Approve                | Initiate and approve transactions | 100000004 | 100000005 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |
      # reject as junior admin, claimant, AND initiator
      | junior admin | READ_ONLY         | Read only              | Initiate and approve transactions | 100000004 | 100000004 | Reject      | reject            | REJECTED          | Active representatives Name Access rights Work contact details Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Request update |

  @exec-manual @test-case-id-95976983
  Scenario Outline: As AR with correct access rights I cannot complete a Change access rights AR request when I am NOT initiator
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
      | account_id | claimed_by | status | outcome | type                                                   | initiated_by | completed_by | diff                                                             |
      | 100000001  | 100000005  |        |         | AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST | 100000005    |              | auth rep:CHANGE_ACCESS_RIGHTS,UK977538690871,<new_access_rights> |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName    | field_value                                                                                                                                                                                                                                                                                                                                                                                 |
      | page content | Account info Account Holder Organisation 1 Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 User Name Access rights Work contact details AR status User status Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info ACTIVE ENROLLED New access rights <access_rights_name> |
    Then The page "does not contain" the "<task_action> request" text

    Examples:
      | access_rights        | new_access_rights    | access_rights_name                | task_action |
      | INITIATE             | INITIATE_AND_APPROVE | Initiate and approve transactions | Approve     |
      | INITIATE_AND_APPROVE | INITIATE             | Initiate                          | Approve     |
      | INITIATE             | INITIATE_AND_APPROVE | Initiate and approve transactions | Reject      |
      | INITIATE_AND_APPROVE | INITIATE             | Initiate                          | Reject      |
      | INITIATE             | APPROVE              | Approve                           | Approve     |
      | INITIATE_AND_APPROVE | APPROVE              | Approve                           | Approve     |
      | INITIATE             | READ_ONLY            | Read only                         | Reject      |
      | INITIATE_AND_APPROVE | READ_ONLY            | Read only                         | Reject      |
