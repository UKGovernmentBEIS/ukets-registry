@functional-area-kp-transactions

Feature: KP transactions - Transfer to SOP for first external transfer

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.5) As a Senior-Admin, I can execute a transaction to SOP for first external transfer.

  #
  # Information for Transfer to SOP for first external transfer:
  #
  # a. from transferring account:
  #   Party holding account (100)
  #   having AAU subject to SOP,  CP2 only
  #
  # b. to acquiring account:
  #   FIXED, "Transfer to SOP for first external transfer" account (280) for the CP of the chosen units
  #
  # c. Once "Transfer to SOP for First External Transfer of AAU" option is selected, registry should recognise the account CDM_SOP_ACCOUNT
  #

  @exec-manual @test-case-id-03957824150
  Scenario: As senior admin user I cannot trigger a transfer to SOP for first external transfer when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
    Then I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    # try to continue without select an option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"
    #
    When I select the "Transfer to SOP for First External Transfer of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    # try to continue without selection a unit type
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # try to continue without setting a quantity amount
    When I click the "AAU (Subject to SOP)" button
    And I click the "continue" button
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # set zero amount and try to continue
    When I enter value "0" in "Enter the quantity to transfer" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # set negative amount and try to continue
    And I clear the "Enter the quantity to transfer" field
    When I enter value "-100" in "Enter the quantity to transfer" field
    And I click the "continue" button
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (symbol)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "!" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (float number - 1 decimal point precision)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "1.1" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (float number - 3 decimal point precision)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "1.001" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (letter)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "a" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity (numbers with letter)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "1a1" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"
    # enter an invalid quantity amount (negative edge case - greater than the available one)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "1501" in "Enter the quantity to transfer" field
    When I click the "continue" button
    And I see an error summary with "The requested quantity exceeds the current account balance for the unit type being transferred"
    # enter an invalid quantity amount (positive edge case - equals the available one)
    And I clear the "Enter the quantity to transfer" field
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                  | field_value                                                                                                            |
      | Transferring account details               | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer detail | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU Subject to SOP CP2 CP2 1500           |
      | acquiring account details                  | No acquiring account is available for this transaction. Please contact the Help Desk.                                  |

  @sampling-smoke @test-case-id-03957824249
  Scenario: As senior admin user I can trigger a transfer to SOP for first external transfer
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period | acc_holder_type | typeLabel             | sop  |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   | INDIVIDUAL      | Party Holding Account | true |
    Then I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Transfer to SOP for First External Transfer of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Subject to SOP)" button
    And I enter value "900" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Subject to SOP) CP2 CP2 900          |
      | acquiring account details                   | No acquiring account is available for this transaction. Please contact the Help Desk.                                  |
    # continue with Check and sign your proposal optional comment:
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then The page "contains" the "TransferToSOPforFirstExtTransferAAU" text
    And The page "contains" the "Awaiting approval" text
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
    # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @exec-manual @test-case-id-03957824305
  Scenario: As senior admin user I cannot propose a transfer to SOP for first external transfer while another transaction of the same type is pending
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
    Then I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Transfer to SOP for First External Transfer of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Subject to SOP)" button
    And I enter value "1000" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                  | field_value                                                                                                            |
      | Transferring account details               | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer detail | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP2 CP2 1000                          |
      | acquiring account details                  | No acquiring account is available for this transaction. Please contact the Help Desk.                                  |
    # continue with Check and sign your proposal optional comment:
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                 | field_value                                                                 |
      | sign proposal information | By signing this proposal you confirm that the information above is correct. |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    When I enter value "My comment" in "proposal comment" field
    # proceed with final submission:
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen
    When I click the "Transactions item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                            |
      | Page main content | Total available quantity: Total reserved quantity: 1,000 AAU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity AAU 2 2 500 Reserved quantity AAU 2 2 1000 |
    # ensure that I cannot re-submit a transaction of the same type as long as the current task is not completed
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Transfer to SOP for First External Transfer of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Subject to SOP)" button
    And I enter value "100" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                  | field_value                                                                                                            |
      | Transferring account details               | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer detail | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP2 CP2 100                           |
      | acquiring account details                  | No acquiring account is available for this transaction. Please contact the Help Desk.                                  |
    # continue with Check and sign your proposal optional comment:
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                 | field_value                                                                 |
      | sign proposal information | By signing this proposal you confirm that the information above is correct. |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    When I enter value "My comment 2" in "proposal comment" field
    # proceed with final submission
    And I click the "Submit" button
    Then I see an error summary with "Transactions from accounts with other pending transactions of the same type are not allowed, with the exception of transfers"
