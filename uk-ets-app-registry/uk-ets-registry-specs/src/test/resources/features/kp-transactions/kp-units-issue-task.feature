@functional-area-kp-transactions

Feature: KP transactions - KP Units issue Task

  Epic: KP Transactions
  Version: 1.0 (31/01/2020)
  Story: (& 5.1.2) As a Senior-Admin, I can approve a KP issuance proposal
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20KP%20Transactions.docx?version=4&modificationDate=1580481329000&api=v2

  @exec-manual @test-case-id-81892724259
  Scenario Outline: As a admin I can access the Internal Transfer task ensuring that data shown is correct when I am or I am not initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Given I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | <initiator>       |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1002-1-84  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "more info" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | page main content | Check the transaction proposal History & comments Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Transfer KP units Unit types and quantity to transfer Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Back to top |
    And The page "contains" the "Initiated by <initiator_name>" text
    And The page "contains" the "Claimed by Registry Administrator" text
    When I click the "Back to task list" link
    Then I am presented with the "Task List" screen

    Examples:
      | initiator | initiator_name    |
      | 100000001 | SENIOR ADMIN USER |
      | 100000002 | SENIOR ADMIN 0    |

  @sampling-smoke @test-case-id-05832524452
  Scenario: As a admin I cannot access a task that I do not have access to
    Given I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 2                   |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000001         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1002-1-84  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    Then I see "0" elements of "Task list returned result rows"

  @sampling-smoke @test-case-id-05832524481
  Scenario: As senior admin I can complete Internal Transfer task when I am NOT initiator
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1002-1-84  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    When I click the "Back to task list" link
    Then I am presented with the "Task List" screen
    # access task again to ensure that the approval is implemented and the task is now completed
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName                                       | field_value                                                                                                                 |
      | Task details type                               | Transaction Proposal                                                                                                        |
      | Task details status                             | APPROVED                                                                                                                    |
      | transferring account information                | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001      |
      | transaction type information                    | Transfer KP units                                                                                                           |
      | Unit types and quantity to transfer information | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation |
    When I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    And The page "contains" the "Transaction proposal task completed." text

  @sampling-smoke @test-case-id-05832524482
  Scenario: As senior admin I can complete Issue KP Units task and successfully receive approval email
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 2                   |
    And I have the following registry levels
      | Type                  | ISSUANCE_KYOTO_LEVEL            |
      | UnitType              | RMU                             |
      | CommitmentPeriod      | CP2                             |
      | EnvironmentalActivity | AFFORESTATION_AND_REFORESTATION |
      | Initial               | 0                               |
      | Consumed              | 0                               |
      | Pending               | 0                               |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value      |
      | Originating Country Code | GB               |
      | Account ID               | 100000001        |
      | Claimed by               | 100000001        |
      | Initiated by             | 100000002        |
      | Transaction type         | Issue KP units   |
      | Commitment period        | 2                |
      | Acquiring account        | GB-100-1002-1-84 |
      | Transferring account     | GB-100-1001-1-89 |
      | Unit type                | RMU              |
      | Quantity to issue        | 40               |
      | Environmental activity   | Afforestation    |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    And I receive an "Notification of UK Registry transaction approval - UK Emissions Trading Registry" email message regarding the "dont@care.com" email address