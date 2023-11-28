@functional-area-kp-transactions
@exec-manual

Feature: KP transactions - tCER or lCER Replacement

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.16) As a Senior-Admin, I can execute a Replacement of tCER or lCER.

  # Information for Replacement:
  #
  # From Accounts:
  #   - Former operator holding account	120
  #   - Party holding account 	100
  #   - Person holding account	121
  # having units as below.
  #
  #
  # This transaction is selected ONLY as a response to an ITL notification of the following type:
  # - Type 3 (impending tCER or lCER expiry)
  #     Replacing   : AAU, RMU, ERU, CER, tCER, lCER  for the given block within the type-3 notification.
  #     By          : tCER,lCER
  #     To   Account: tCER replacement account for expiry	411
  #                   lCER replacement account for expiry	421
  #
  # - Type 4 (reversal of storage for CDM project)
  #     Replacing   : AAU, RMU, ERU, CER, lCER    for the given project  within the type-4 notification.
  #     By          : lCER
  #     To   Account: lCER replacement account for reversal of storage	422
  #
  # - Type 5 (non-submission of certification report for CDM project)
  #     Replacing   : AAU, RMU, ERU, CER, lCER    for the given project  within the type-5 notification.
  #     By          : lCER
  #     To   Account: lCER replacement account for non-submission of certification report	423
  #

  @test-case-id-3022321705
  Scenario: As senior admin user I cannot trigger a Replacement transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following notification exists
      | notification_id | type | project_id | block_start | block_end |
      | 1001            | 3    |            | 1           | 100       |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 3 | OPEN | 100 | AAU   |            | false      | true      | 1        | 100    | CP1      | CP1      |             | AAU      | 1002      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1002" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1004 OPEN Change status |
      | Available quantity | Total available quantity: 100 Assigned Amount Units         |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Replacement of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    # ITL notification is MANDATORY for this case.
    When I click the "Continue" button
    Then I see an error detail with content "Error: Invalid notification ID - please try again "
    #
    When I enter value "1001" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    # try to continue without selection a unit type
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # try to continue without setting a quantity amount
    When I click the "AAU" button
    And I click the "continue" button
    Then I see an error summary with "The quantity must be a positive number without decimal places"
    # set zero amount and try to continue
    When I enter value "0" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # set negative amount and try to continue
    When I clear the "Enter the quantity to transfer" field
    And I enter value "-100" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (symbol)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "!" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (float number - 1 decimal point precision)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "1.1" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (float number - 3 decimal point precision)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "1.001" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (letter)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "a" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (numbers with letter)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "1a1" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity amount (negative edge case - greater than the available one)
    When I clear the "Enter the quantity to transfer" field
    And I enter value "200000" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I see an error summary with "The requested quantity exceeds the current account balance for the unit type being transferred"

  @test-case-id-3022321801
  Scenario: As senior admin user I can trigger a Replacement transaction
    Given the following notification exists
      | notification_id | type | project_id | block_start | block_end |
      | 1001            | 3    |            | 1           | 100       |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 3 | OPEN | 100 | AAU   |            | false      | true      | 1        | 100    | CP1      | CP1      |             | AAU      | 1002      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1002" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1002 OPEN Change status |
      | Available quantity | Total available quantity: 100 Assigned Amount Units         |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Replacement of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "1001" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    When I enter value "100" in "AAU Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                 |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 100                                |
      | acquiring account details                   | Trusted account Account number GB-421-10000019-1-81 Account name or description lCER Replacement account for expiry for CP1 |
    #
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # ensure that holdings item is created
    When I click the "Back to account details" link
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                          |
      | Holdings main content | Holdings Total available quantity: 0 Total reserved quantity: 100 AAU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity AAU 1 1 0 100 |
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @test-case-id-3022321871 @exec-manual
  Scenario: As senior admin user while implementing a Replacement transaction I see data retained while navigating backwards
    Given the following notification exists
      | notification_id | type | project_id | block_start | block_end |
      | 1001            | 3    |            | 1           | 100       |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 3 | OPEN | 100 | AAU   |            | false      | true      | 1        | 100    | CP1      | CP1      |             | AAU      | 1002      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1002" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1002 OPEN Change status |
      | Available quantity | Total available quantity: 100 Assigned Amount Units         |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Replacement of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "1001" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    When I enter value "100" in "AAU Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                 |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 100                                |
      | acquiring account details                   | Trusted account Account number GB-421-10000019-1-81 Account name or description lCER Replacement account for expiry for CP1 |
    #
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | checkbox button AAU checkbox   | selected    |
      | Enter the quantity to transfer | 100         |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see data retained

  @test-case-id-3022321934
  Scenario: As senior admin user I can cancel a Replacement transaction triggering
    Given the following notification exists
      | notification_id | type | project_id | block_start | block_end |
      | 1001            | 3    |            | 1           | 100       |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 3 | OPEN | 100 | AAU   |            | false      | true      | 1        | 100    | CP1      | CP1      |             | AAU      | 1002      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1002" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1002 OPEN Change status |
      | Available quantity | Total available quantity: 100 Assigned Amount Units         |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Replacement of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "1001" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    When I enter value "100" in "AAU Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                 |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 100                                |
      | acquiring account details                   | Trusted account Account number GB-421-10000019-1-81 Account name or description lCER Replacement account for expiry for CP1 |
    #
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account overview" screen
    # propose transaction should be available again
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    # ensure that I can navigate to holdings even I cancelled the procedure:
    When I click the "Holdings item" link
    Then I am presented with the "View account overview" screen
    And The page "contains" the "GB-100-1002-1-84 " text
    And The page "contains" the "Total available quantity:" text
    And The page "contains" the "100 AAU" text

## end ##
