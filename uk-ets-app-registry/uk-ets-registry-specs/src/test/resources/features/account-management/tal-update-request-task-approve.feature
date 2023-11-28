@functional-area-account-management

Feature: Account management - TAL update request task approve

  Epic: Account Management
  Version: 2.2 (19/03/2020)
  Story: (5.2.1) Request to update the trusted account list
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @test-case-id-3942983237
  Scenario Outline: As senior admin I can assign and claim add or remove TAL task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000008    |              | tal ids:100000001 |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    # ensure that I can assign successfully the task to another user
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    When I enter value "SENIOR" in "User" field
    And I click the "SENIOR ADMIN 0" button
    And I enter value "assign comment" in "Enter some comments" field
    And I click the "Assign" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And The page "contains" the "<account_action>" text
    And The page "contains" the "100000001" text
    And The page "contains" the "SENIOR ADMIN 0" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "CLAIMED" text

    Examples:
      | task_type                      | account_action |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |

  @test-case-id-3942983292
  Scenario Outline: As senior admin I can complete add or remove update TAL request with comment when I am NOT initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000008    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment and add comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I enter value "history comment 2" in "comment area" field
    And I click the "Add Comment" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # approve/reject action (setting optional comment):
    Then I am presented with the "Task Details" screen
    When I click the "<action>" button
    Then I am presented with the "Task Details" screen
    When I enter value "comment 1" in "comment area" field
    # ensure data is correct in Task confirmation approve/reject screen after approve/reject action
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve/reject action
    Then I am presented with the "Task List" screen
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "<account_action>" text
    And The page "contains" the "100000001" text
    And The page "contains" the "JUNIOR ADMIN 0" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "COMPLETED" text

    Examples:
      | task_type                      | account_action | action  |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Approve |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Reject  |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Reject  |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Approve |

  @exec-manual @test-case-id-3942983365
  Scenario Outline: As senior admin I can APPROVE an add or remove update TAL request without comment when I am NOT initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000001  |        |         | <task_type> | 100000002    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment - no comment add
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Add comment" link
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # approve/reject action without setting optional comment:
    Then I am presented with the "Task Details" screen
    When I click the "Approve request" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this request" text
    # ensure data is correct in Task confirmation approve screen after approve action
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve action
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "100000001" text
    And The page "contains" the "JUNIOR ADMIN 0" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "COMPLETED" text
    And The page "contains" the "<account_action>" text

    Examples:
      | task_type                      | account_action |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |

  @test-case-id-3942983431
  Scenario Outline: As senior admin I cannot REJECT an add or remove update TAL request when I am NOT initiator without entering comment
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000008    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment without adding a comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # try to approve/reject action without setting comment:
    Then I am presented with the "Task Details" screen
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Why are you rejecting this request ?" text
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And I see an error summary with "Enter a reason for rejecting the request"

    Examples:
      | task_type                      | account_action |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |

  @test-case-id-3942983487
  Scenario Outline: As senior admin I cannot APPROVE an add or remove TAL request when I am BOTH claimant and initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000007    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment and add comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Add comment" link
    And I enter value "history comment 2" in "comment area" field
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName             | field_value                       |
      | comment table headers | When Who What Request ID Comments |
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    Then I am presented with the "Task Details" screen
    # ensure that I CANNOT approve a TAL add/remove request:
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I click the "Complete task" button
    And I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

    Examples:
      | task_type                      | account_action |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |

  @test-case-id-3942983546
  Scenario Outline: As senior admin I can REJECT an add or remove TAL request when I am BOTH claimant and initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000007    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment and add comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I enter value "history comment 2" in "comment area" field
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName             | field_value                       |
      | comment table headers | When Who What Request ID Comments |
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # ensure that I CAN reject a TAL add/remove request:
    Then I am presented with the "Task Details" screen
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve action
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "<account_action>" text
    And The page "contains" the "COMPLETED" text
    And The page "contains" the "SENIOR ADMIN USER" text

    Examples:
      | task_type                      | account_action |
      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |
      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |

  @test-case-id-3942983614
  Scenario Outline: As ar with correct access rights I can APPROVE an add or remove update TAL request without comment when I am NOT initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000008  |        |         | <task_type> | 100000001    |              | tal ids:100000001 |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment - no comment add
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # approve/reject action without setting optional comment:
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this request" text
    # ensure data is correct in Task confirmation approve screen after approve action
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve action
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "<account_action>" text
    And The page "contains" the "COMPLETED" text

    Examples:
      | access_rights        | task_type                      | account_action |
      | INITIATE_AND_APPROVE | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |
      | INITIATE_AND_APPROVE | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |
      | APPROVE              | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         |
      | APPROVE              | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            |

  @test-case-id-230958701
  Scenario Outline: As a user I can cancel a tal addition request of status pending activation
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    Given I sign in as "<user>" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                        | initiated_by | completed_by | diff              |
      | 100000001  | 100000008  |        |         | ADD_TRUSTED_ACCOUNT_REQUEST | 100000001    |              | tal ids:100000001 |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # check data before adding history comment - no comment add
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Add trusted account" text
    When I click the "Add comment" link
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # approve/reject action without setting optional comment:
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this request" text
    # ensure data is correct in Task confirmation approve screen after approve action
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve action
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "Add" text
    And The page "contains" the "COMPLETED" text
    # ensure that that i can cancel the operation
    When I access "1002" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And I click the "Cancel addition" button
    And I click the "Cancel addition" button    
    Then The page "contains" the "You can successfully canceled the addition of the account to the trusted account list." text

    @exec-manual
    Examples:
      | user                      |
      | authorized representative |
      | senior admin              |
      | junior admin              |

  @test-case-id-3942983690
  Scenario Outline: As AR with correct access rights I can REJECT an add or remove TAL request
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000008  |        |         | <task_type> | 100000001    |              | tal ids:100000001 |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And The page "contains" the "Request ID: 100000001" text
    Then I see the following fields having the values:
      | fieldName           | field_value                                                                                                            |
      | account info        | Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
      | update details info | <update_info_details>                                                                                                  |
    # check data before adding history comment and add comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I enter value "history comment 2" in "comment area" field
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName             | field_value                       |
      | comment table headers | When Who What Request ID Comments |
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # ensure that I CAN reject a TAL add/remove request:
    Then I am presented with the "Task Details" screen
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" button
    # ensure data is correct in Task List screen after approve action
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then The page "contains" the "<account_action>" text
    And The page "contains" the "COMPLETED" text

    @sampling-smoke
    Examples:
      | access_rights | task_type                      | account_action | update_info_details                                                                                                        |
      | APPROVE       | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |

    Examples:
      | access_rights        | task_type                      | account_action | update_info_details                                                                                                        |
      | INITIATE_AND_APPROVE | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |
      | INITIATE_AND_APPROVE | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |
      | APPROVE              | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |

  @test-case-id-3942983774
  Scenario Outline: As ar with INCORRECT access rights I cannot APPROVE an add or remove update TAL request when I am NOT initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000008    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # approve/reject action without setting optional comment
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this request" text
    # ensure data is correct in Task confirmation approve screen after approve action
    When I click the "Complete task" button
    Then I see an error summary with "User can approve tasks only for the account he/she is the AR with 'Approve' or 'initiate & approve access rights"

    @sampling-smoke
    Examples:
      | access_rights | task_type                      | account_action | update_info_details                                                                                                        |
      | INITIATE      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |

    Examples:
      | access_rights | task_type                      | account_action | update_info_details                                                                                                        |
      | READ_ONLY     | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |
      | READ_ONLY     | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |
      | INITIATE      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |

  @exec-manual @test-case-id-3942983848
  Scenario Outline: As ar with INCORRECT access rights I cannot REJECT an add or remove TAL request
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | OPERATOR_HOLDING_ACCOUNT          |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
      | trustedAccount           | GB-100-1002-1-84 PENDING_APPROVAL |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000001 |
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type        | initiated_by | completed_by | diff              |
      | 100000001  | 100000007  |        |         | <task_type> | 100000001    |              | tal ids:100000001 |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And The page "contains" the "Request ID: 100000001" text
    Then I see the following fields having the values:
      | fieldName           | field_value                                                                                                                         |
      | account info        | Account info Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
      | update details info | <update_info_details>                                                                                                               |
    # check data before adding history comment and add comment
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<account_action> trusted account" text
    When I click the "Add comment" link
    And I enter value "history comment 2" in "comment area" field
    And I click the "Add Comment" button
    # check data after adding history comment
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName             | field_value                       |
      | comment table headers | When Who What Request ID Comments |
    And The page "contains" the "Request ID: 100000001" text
    When I click the "Back" link
    # ensure that I CAN reject a TAL add/remove request:
    Then I am presented with the "Task Details" screen
    When I click the "Reject request" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Reject" button
    Then I see an error summary with "User can reject tasks only for the account he/she is the AR with 'approve' or 'initiate & approve' access rights"

    Examples:
      | access_rights | task_type                      | account_action | update_info_details                                                                                                        |
      | INITIATE      | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |
      | INITIATE      | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |
      | READ_ONLY     | ADD_TRUSTED_ACCOUNT_REQUEST    | Add            | Update details The following account will be added to the trusted account list Account Description GB-100-1002-1-84        |
      | READ_ONLY     | DELETE_TRUSTED_ACCOUNT_REQUEST | Remove         | Update details The following account(s) will be removed from the trusted account list Account Description GB-100-1002-1-84 |
