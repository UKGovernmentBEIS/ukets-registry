@functional-area-kp-transactions

Feature: KP transactions - KP Carryover AAU

  Epic: KP Transactions and Transactions (v1.9 (26/03/2020))
  Version: v1.4 (26/03/2020)
  Story: (KP Transactions, &5.1.11) As a Senior-Admin, I can carry-over CP1 AAu units to CP2.

  # Screens:
  # "Propose transaction"  				Is the Transactions 5.1.1  at pg 22  Screen  1: 	Propose transaction
  # "Propose transaction, quantity"  	Is the Transactions 5.1.1  at pg 23  Screen  2: 	Propose transaction - Specify quantity
  # "Propose transaction, acquiring"  	Is the Transactions 5.1.1  at pg 25  Screen  3: 	Propose transaction - Specify acquiring account
  # "Propose transaction, review"		Is the Transactions 5.1.1  at pg 27  Screen  4: 	Propose transaction - Review transaction details
  # "Propose transaction, sign"      	Is the Transactions 5.1.1  at pg 29  Screen  5: 	Propose transaction - Sign and submit this proposal
  # "Propose transaction, done"      	Is the Transactions 5.1.1  at pg 31  Screen  6: 	Propose transaction - Done & acknowledge
  # "Propose transaction, cancel"      	Is the Transactions 5.1.1  at pg 32  Screen  7: 	Propose transaction - Cancel

  # Information for Carryover AAU:
  #
  # a. from transferring account:
  # account type: PARTY_HOLDING_ACCOUNT
  # commitment period: 1
  # registry_account_type: NONE
  #
  # b. to acquiring account:
  # account type: PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT (instead of PARTY_HOLDING_ACCOUNT)
  # commitment period: 2
  # registry_account_type: NONE

  @sampling-smoke @test-case-id-6261919837
  Scenario: As senior admin user I cannot trigger carryover AAU transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1001              |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    # try to continue without select an option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"
    # select carry over aau option and continue
    When I select the "Carry-over AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    # try to continue without selection a unit type
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # try to continue without setting a quantity amount
    When I click the "AAU checkbox" button
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
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                         |

  @test-case-id-6261919947
  Scenario: As senior admin user I can trigger carryover AAU transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1001              |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Carry-over AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU checkbox" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    And I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account content                | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001         |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                                 |
      | acquiring account content                   | Account not in the trusted account list Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
    # continue with Check and sign your proposal optional comment:
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                 | field_value                                                                 |
      | sign proposal information | By signing this proposal you confirm that the information above is correct. |
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    # proceed with final submission:
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    # overview left side screen after the carryover
    Then I am presented with the "View account" screen
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                                     |
      | Holdings main content | Total available quantity: 0 Total reserved quantity: 1,500 Assigned Amount Units Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity AAU 1 1 1,500 |
    # ensure that the task is now created:
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And The page "contains" the "Transaction Proposal" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @exec-manual @test-case-id-6261920025
  Scenario: As senior admin user while implementing carryover AAU triggering I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1001              |
      | Account status     | OPEN                                                  |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Carry-over AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU checkbox" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                                                                      |
      | Transferring account content                | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001                                      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                                                                                   |
      | acquiring account content                   | Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                     |
      | proposal approval information | This proposal must be approved by another user. |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    # backwards data retain check:
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                                                                      |
      | Transferring account content                | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001                                      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                                                                                   |
      | acquiring account content                   | Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | checkbox button AAU checkbox   | selected    |
      | Enter the quantity to transfer | 1500        |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    Then I see data retained

  @exec-manual @test-case-id-6261920107
  Scenario: As senior admin user I can cancel a carryover AAU transaction triggering
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1001              |
      | Account status     | OPEN                                                  |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Carry-over AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU checkbox" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                                                                      |
      | Transferring account content                | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001                                      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                                                                                   |
      | acquiring account content                   | Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another registry administrator |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    # proceed with cancellation:
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Cancel proposal" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    # ensure that I can navigate to holdings even I cancelled the procedure:
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                         |
      | Holdings main content | Holdings Total available quantity: Total reserved quantity: 1,500 AAU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity AAU 1 1 1500 |

  @exec-manual @test-case-id-6261920177
  Scenario: As senior admin user propose transaction button is available after a cancel
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1001              |
      | Account status     | OPEN                                                  |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Carry-over AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU checkbox" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                                                                      |
      | Transferring account content                | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001                                      |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 1,500                                                                                   |
      | acquiring account content                   | Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another registry administrator |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    # proceed with cancellation:
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Cancel proposal" button
    Then I am presented with the "Account Overview Transactions" screen
    # ensure that "Propose transaction" button is still available and functional:
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Back" link
    # ensure that I can navigate to holdings even I cancelled the procedure:
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                                                      |
      | Holdings main content | Holdings Total Quantity available: 0 multiple unit types Total reserved quantity: 1500 multiple unit types Click on the unit to view more details. Unit type Original CP Applicable CP Quantity available Reserved quantity 1500 |

  @test-case-id-6261920249
  Scenario: As AR enrolled with INITIATE AND APPROVE access rights I cannot see PARTY HOLDING ACCOUNT accounts and subsequently Propose transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type                      | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT | NONE                  | Party Holding 1 | OPEN | 1199 | AAU   | false      | true      | 1        | 1199   | CP1      | CP1      |             | AAU      | 1002      | 2                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT                   | NONE                  | Party Holding 2 | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                                                                 |
      | table rows results | GB-100-1002-1-84 Party Holding Account 1002 Party Holding Account individual first name 1 individual last name 1 Open 1,199 |
    And The page "does not contain" the "GB-100-1001-1-89" text

  @exec-manual @test-case-id-6261920271
  Scenario: As AR enrolled with INITIATE AND APPROVE access rights I cannot carryover AAU regarding non gov accounts
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | AAU   | true       | true      | 1        | 2000   | CP2      | CP2      |             | AAU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | AAU   | true       | true      | 1300     | 1400   | CP2      | CP2      |             | AAU      | 1001      | 1                   |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "contains" the "Transfer Kyoto units" text
