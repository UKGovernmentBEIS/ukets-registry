@functional-area-ets-transactions

Feature: ETS transactions - Deletion reversal

  @sampling-smoke @test-case-id-74956728921
  Scenario: As sra I can apply a deletion reversal transaction after a deletion transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN           | 401 | ALLOWANCE | true       | true      | 10000001100 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | OPEN           | 20  | ALLOWANCE | true       | true      | 10000003000 | 10000003019 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     | UK100000002            |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value            |
      | Originating Country Code | GB                     |
      | Account ID               | 100000002              |
      | Claimed by               | 100000001              |
      | Initiated by             | 100000002              |
      | Transaction type         | Deletion of Allowances |
      | Commitment period        | 2                      |
      | Acquiring account        | UK-100-10000007-0-26   |
      | Transferring account     | UK-100-10000056-0-72   |
      | Unit type                | ALLOWANCE              |
      | Quantity to issue        | 20                     |
      | Task status              | APPROVED               |
      | Transaction status       | Completed              |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    When I click the "transaction row result 1" link
    And I click the "Reverse transaction" button
    And I click the "Continue" button
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    And The page "contains" the "You have submitted a transaction proposal" text
    And I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    #ensure that the correct units (20) are not added to the transferring account because task is not approved yet
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                 |
      | Available quantity | Total available quantity: 0 |
    # ensure that the correct units (381) are not removed from the deletion account because task is not approved yet
    When I access "10000007" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                              |
      | Available quantity | Total available quantity: 381 Allowances |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    Then I am presented with the "Task List" screen
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    And I get a new otp based on the existing secret for the "test_senior_admin 2_user" user
    And I enter value "correct otp for user test_senior_admin 2_user" in "otp" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    # ensure that the correct units (20) are added back to the account because task is now approved
    And I see the following fields having the values:
      | fieldName          | field_value                             |
      | Available quantity | Total available quantity: 20 Allowances |
    # ensure that the initial units (381) are displayed to the deletion account because task is now approved
    When I access "10000007" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                              |
      | Available quantity | Total available quantity: 381 Allowances |

  @exec-manual @test-case-id-74956728922
  Scenario Outline: As not sra I cannot apply a deletion reversal transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN           | 401 | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | OPEN           | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "does not contain" the "Reverse transaction" text

    Examples:
      | user                      |
      | junior admin              |
      | read only admin admin     |
      | authority                 |
      | authorised representative |

  @exec-manual @test-case-id-74956728923
  Scenario Outline: As sra I cannot apply a second reversal deletion upon another pending or complete request for this transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN           | 401 | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | OPEN           | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When There is an already "<status>" deletion reversal request for the same transaction
    Then A second reversal deletion is not available

    Examples:
      | status    |
      | pending   |
      | completed |
