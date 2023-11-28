@functional-area-kp-transactions

Feature: KP transactions - KP Transaction proposal approval

  Epic: Transactions
  Version: 1.5 (27/02/2020)
  Story: (Task) Approve transaction proposal
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949&preview=/124686949/132293756/UK%20Registry%20-%20Transactions.docx

  @exec-manual @test-case-id-08904123323
  Scenario: As a Junior Admin I dont have permission to claim KP Issuance transaction proposal task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 101  | RMU   | true       | true      | 2100     | 2200   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               |                   |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "You do not have permission to perform this action"

  @test-case-id-5623923208
  Scenario Outline: As a non admin user I have correct transaction tasks visibility according to my access rights
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <acq_account_ac_right> | 100000001 |
      | ACTIVE | <tr_account_ac_right>  | 100000002 |
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "<exp_approve_button>" the "Approve" text
    And The page "<exp_reject_button>" the "Reject" text

    Examples:
      | acq_account_ac_right | tr_account_ac_right  | exp_approve_button | exp_reject_button |
      | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | contains           | contains          |
      | INITIATE             | READ_ONLY            | contains           | contains          |
      | APPROVE              | INITIATE_AND_APPROVE | contains           | contains          |
      | READ_ONLY            | APPROVE              | contains           | contains          |

    @exec-manual
    Examples:
      | acq_account_ac_right | tr_account_ac_right  | exp_approve_button | exp_reject_button |
      | INITIATE_AND_APPROVE | INITIATE             | contains           | contains          |
      | INITIATE_AND_APPROVE | APPROVE              | contains           | contains          |
      | INITIATE_AND_APPROVE | READ_ONLY            | does not contain   | does not contain  |
      | INITIATE             | INITIATE_AND_APPROVE | contains           | contains          |
      | INITIATE             | INITIATE             | contains           | contains          |
      | INITIATE             | APPROVE              | contains           | contains          |
      | APPROVE              | INITIATE             | contains           | contains          |
      | APPROVE              | APPROVE              | contains           | contains          |
      | APPROVE              | READ_ONLY            | does not contain   | does not contain  |
      | READ_ONLY            | INITIATE_AND_APPROVE | contains           | contains          |
      | READ_ONLY            | INITIATE             | contains           | contains          |
      | READ_ONLY            | READ_ONLY            | does not contain   | does not contain  |

  @exec-manual @test-case-id-5623923268
  Scenario Outline: As admin user I have correct transaction tasks visibility according to my access rights
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "<sign_in_user>" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "<exp_approve_button>" the "Approve" text
    And The page "<exp_reject_button>" the "Reject" text

    Examples:
      | sign_in_user    | exp_approve_button | exp_reject_button |
      | junior admin    | contains           | contains          |
      | read only admin | contains           | contains          |
      | senior admin    | contains           | contains          |

  @sampling-smoke @test-case-id-08904123461 @sampling-mvp-smoke
  Scenario: As a Junior Admin I can access a KP Issuance proposal task but I cannot complete it
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               |                   |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    # the approve and reject texts below correspond to "Approve", "Reject" buttons
    And I see "0" elements of "Approve"
    And I see "0" elements of "Reject"

  @test-case-id-08904123496 @exec-manual
  Scenario: As senior admin I can assign task to another admin
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SEN" in "User" field
    And I click the "SENIOR ADMIN 0" link
    When I enter value "comment 1" in "Enter some comments" field
    And I click the "Assign" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "more info" link
    Then The page "contains" the "Initiated by SENIOR ADMIN 0" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @exec-manual @test-case-id-08904123537
  Scenario: As senior admin I can view more info about kp task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    When I click the "more info" link
    Then The page "contains" the "Initiated by SENIOR ADMIN 0" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @exec-manual @test-case-id-08904123576
  Scenario: As senior admin I can view task history and comments
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    # assign task (type ahead value selection)
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SEN" in "User" field
    And I click the "SENIOR ADMIN 0" link
    When I enter value "comment 1" in "Enter some comments" field
    And I click the "Assign" button
    Then I am presented with the "Task List" screen
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Request ID: 100000001" text
    And I click the "more info" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | page main content | History & comments Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Transfer KP units Unit types and quantity to transfer Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1000-1-94 Account name or description Party Holding Account 1000 Back to top |
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName             | field_value                       |
      | comment table headers | When Who What Request ID Comments |
    And The page "contains" the "TASK_ASSIGNED" text
    And The page "contains" the "comment 1" text

  @exec-manual @test-case-id-5623923477
  Scenario Outline: As senior admin I can APPROVE KP issuance transaction proposal with or without a comment
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "more info" link
    And The page "contains" the "Initiated by SENIOR ADMIN 0" text
    And The page "contains" the "Claimed by Registry Administrator" text
    And I enter value "<comment>" in "comment area" field
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed transaction has been approved" text

    Examples:
      | comment   |
      # comment not empty
      | comment 1 |
      # comment empty
      |           |

  @sampling-smoke @test-case-id-08904123680
  Scenario: As AR enrolled I can APPROVE KP issuance transaction proposal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    And I have created 1 "enrolled" users
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    # use the CORRECT USERNAME so as to retrieve the otp for the correct user
    And I get a new otp based on the existing secret for the "test_authorized_representative_user" user
    When I enter value "correct otp for user test_authorized_representative_user" in "otp" field
    And I click the "Complete task" button
    And The page "contains" the "You have approved the proposed transaction" text

  @exec-manual @test-case-id-08904123723
  Scenario: As an ar I can cancel a delayed kp transfer
    Given There is a "delayed" kp transfer
    And the correct units are reserved from the transferring account
    When I navigate to the transaction details
    And I click the "Cancel transaction" button
    And I confirm the cancellation process
    Then the transaction is "cancelled"
    And the correct units are released back to the transferring account

  @test-case-id-08904123733
  Scenario: As senior admin I cannot approve task when I am both claimant and initiator due to 4 eyes security principle
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000001         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    When I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Complete task" button
    Then I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

  @exec-manual @test-case-id-5623923620
  Scenario Outline: As senior admin I must enter the correct password to complete task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "<action>" button
    Then I am presented with the "<screen>" screen
    And I enter value "<comment>" in "comment area" field
    When I complete the task of the <action>
    And the password I set is <password>
    Then I see the <message> message

    Examples:
      | password  | action  | screen                    | message                           |
      | correct   | Approve | Task confirmation approve | the task is completed             |
      | incorrect | Approve | Task confirmation approve | Invalid password or one time code |
      | blank     | Approve | Task confirmation approve | Enter password                    |
      | correct   | Reject  | Task confirmation reject  | the task is completed             |
      | incorrect | Reject  | Task confirmation reject  | Invalid password or one time code |
      | blank     | Reject  | Task confirmation reject  | Enter password                    |

  @exec-manual @test-case-id-08904123821
  Scenario: As senior admin I cannot REJECT KP issuance transaction proposal without setting a reject comment
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Reject request" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And The page "contains" the "Initiated by SENIOR ADMIN 0" text
    And The page "contains" the "Claimed by Registry Administrator" text
    # no comment set, only reject button clicked
    And I click the "Reject" button
    Then I see an error summary with "Empty comment"

  @sampling-smoke @test-case-id-08904123861
  Scenario: As senior admin I can REJECT KP issuance transaction proposal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Rejected" text

  @exec-manual @test-case-id-08904123899
  Scenario: As senior admin I can reject MY OWN task as when I am both claimant and initiator no 4 eyes principle applied
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000001         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Reject request" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And The page "contains" the "Initiated by SENIOR ADMIN USER" text
    And The page "contains" the "Claimed by Registry Administrator" text
    And I enter value "comment 1" in "comment area" field
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Rejected" text
