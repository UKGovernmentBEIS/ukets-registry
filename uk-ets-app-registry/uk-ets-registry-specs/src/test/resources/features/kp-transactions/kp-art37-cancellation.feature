@functional-area-kp-transactions

Feature: KP transactions - Art37Cancellation

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.4) As a Senior-Admin, I can execute a Art. 3.7ter cancellation transaction.

  # Information for Art37Cancellation:
  #
  # a. from transferring account:
  #   Party Holding account (100)
  #   having AAU CP2 units
  #
  # b. to acquiring account:
  #   FIXED, Art37ter cancellation account (200) for the CP of the chosen units

  @test-case-id-43671919409
  Scenario: As senior admin user I cannot trigger an Art37Cancellation transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id       | kyoto_account_type            | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT         | NONE                  | Party Holding 2  | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-270-1017-2-28 | ART37TER_CANCELLATION_ACCOUNT | NONE                  | Art37ter Account | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1003              |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    # try to continue without select an option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"
    # continue
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    # try to continue without selection a unit type
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # try to continue without setting a quantity amount
    When I click the "AAU (Not Subject to SOP)" button
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

  @sampling-smoke @test-case-id-43671919502
  Scenario: As senior admin user I can trigger an Art37 Cancellation transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type            | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT         | NONE                  | Party Holding 2  | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-270-1017-2-28 | ART37TER_CANCELLATION_ACCOUNT | NONE                  | Art37ter Account | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1003              |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003         |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1,500            |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Party Holding Account 1017 |
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # ensure that holdings item is created
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                                                          |
      | Holdings main content | Total available quantity: 0 Total reserved quantity: 1,500 Assigned Amount Units Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity AAU (Not Subject to SOP) 2 2 1,500 |
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @exec-manual @test-case-id-43671919567
  Scenario: As senior admin user while implementing Art37ter cancellation triggering I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id       | kyoto_account_type            | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT         | NONE                  | Party Holding 2  | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-270-1017-2-28 | ART37TER_CANCELLATION_ACCOUNT | NONE                  | Art37ter Account | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1,500    |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Not Available      |
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                     |
      | proposal approval information | This proposal must be approved by another user. |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    # backwards data retain check:
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1,500    |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Not Available      |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | checkbox button AAU checkbox   | selected    |
      | Enter the quantity to transfer | 1500        |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    Then I see data retained

  @test-case-id-43671919644
  Scenario: As senior admin user I can cancel a Art37ter cancellation transaction triggering
    Given the following accounts have been created
      | account_id       | kyoto_account_type            | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT         | NONE                  | Party Holding 2  | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-270-1017-2-28 | ART37TER_CANCELLATION_ACCOUNT | NONE                  | Art37ter Account | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Party Holding Account 1003              |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003         |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1,500            |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Party Holding Account 1017 |
    # proceed with cancellation:
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account" screen
    # propose transaction should be available again
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    # ensure that I can navigate to holdings even I cancelled the procedure:
    When I click the "Holdings item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "GB-100-1003-2-76" text
    And The page "contains" the "1,500 Assigned Amount Units" text
    And The page "contains" the "AAU (Not Subject to SOP)" text

  @exec-manual @test-case-id-43671919706
  Scenario: As senior admin user I cannot propose an Art37ter cancellation transaction while another transaction of the same type is pending
    Given the following accounts have been created
      | account_id       | kyoto_account_type            | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT         | NONE                  | Party Holding 2  | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-270-1017-2-28 | ART37TER_CANCELLATION_ACCOUNT | NONE                  | Art37ter Account | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "1000" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP2 CP2 1,000                         |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Not Available      |
    # continue with Check and sign your proposal optional comment:
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
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
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName             | field_value                                                                                                                                                                                                     |
      | Holdings main content | Holdings Total available quantity: Total reserved quantity: 1,000 AAU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity AAU 2 2 500 Reserved quantity AAU 2 2 1000 |
    # ensure that I cannot re-submit a transaction of the same type as long as the current task is not completed
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Art. 3.7ter cancellation of AAU" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "100" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP2 CP2 100                           |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Not Available      |
    # continue with Check and sign your proposal optional comment:
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                 | field_value                                                                 |
      | sign proposal information | By signing this proposal you confirm that the information above is correct. |
    And The page "contains" the "Enter comment" text
    And The page "contains" the "Visible to authorised representatives of both acquiring and transferring accounts" text
    When I enter value "My comment 2" in "proposal comment" field
    # proceed with final submission:
    And I click the "Submit" button
    Then I see an error summary with "Transactions from accounts with other pending transactions of the same type are not allowed, with the exception of transfers"
