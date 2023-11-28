@functional-area-kp-transactions
@exec-manual

Feature: KP transactions - Mandatory Cancellation

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.3) As a Senior-Admin, I can execute a mandatory cancellation transaction.

  # Information for mandatory Cancellation:
  #
  # a. from transferring account:
  # 	Former operator holding account (120)
  # 	Party holding account (100, including Scottish and Welsh Net Emissions)
  # 	Person holding account (121)
  #   having AAU, RMU, ERU, CER, tCER, lCER  units
  #
  # b. to acquiring account:
  #   FIXED, Mandatory cancellation account (250) for the CP of the chosen units
  #
  # This transaction may be selected by the registry administrator as a response to an ITL notification of the following type:
  #   Type 4 (reversal of storage for CDM project)
  #   Type 5 (non-submission of certification report for CDM project)

  @test-case-id-10308521311
  Scenario: As senior admin user I cannot trigger a mandatory cancellation transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id       | kyoto_account_type             | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT          | NONE                  | Party Holding 2            | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-250-1024-2-76 | MANDATORY_CANCELLATION_ACCOUNT | NONE                  | Mandatory cancellation CP2 | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    # try to continue without select an option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"
    # continue
    When I select the "Mandatory cancellation of Kyoto units" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Mandatory cancellation" text
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

  @test-case-id-10308521404
  Scenario: As senior admin user I can trigger a mandatory cancellation transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type             | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT          | NONE                  | Party Holding 2            | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-250-1024-2-76 | MANDATORY_CANCELLATION_ACCOUNT | NONE                  | Mandatory cancellation CP2 | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Mandatory cancellation of Kyoto units" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Mandatory cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | Transaction type details                    | Mandatory cancellation of Kyoto units                                                                                  |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1500     |
      | acquiring account details                   | Account not in the trusted account list Account number GB-250-1024-2-76 Account name or description Not Available      |
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
      | fieldName             | field_value                                                                                                                                                                                                                |
      | Holdings main content | Holdings Total available quantity: 0 Total reserved quantity: 1,500 AAU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity AAU (Not Subject to SOP) 2 2 1500 |
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @test-case-id-10308521468 @exec-manual
  Scenario: As senior admin user while implementing a mandatory cancellation transaction I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id       | kyoto_account_type             | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT          | NONE                  | Party Holding 2            | OPEN | 1500 | CER   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | CER      | 1003      | 2                   |
      | GB-250-1024-2-76 | MANDATORY_CANCELLATION_ACCOUNT | NONE                  | Mandatory cancellation CP2 | OPEN | 100  | CER   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | CER      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Mandatory cancellation of Kyoto units" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "CER" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Mandatory cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | Transaction type details                    | Mandatory cancellation of Kyoto units                                                                                  |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity CER CP2 CP2 1500                          |
      | acquiring account details                   | Account not in the trusted account list Account number GB-250-1024-2-76 Account name or description Not Available      |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | checkbox button CER checkbox   | selected    |
      | Enter the quantity to transfer | 1500        |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    Then I see data retained

  @test-case-id-10308521525
  Scenario: As senior admin user I can cancel a mandatory cancellation transaction triggering
    Given the following accounts have been created
      | account_id       | kyoto_account_type             | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT          | NONE                  | Party Holding 2            | OPEN | 1500 | ERU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | ERU      | 1003      | 2                   |
      | GB-250-1024-2-76 | MANDATORY_CANCELLATION_ACCOUNT | NONE                  | Mandatory cancellation CP2 | OPEN | 100  | ERU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | ERU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Mandatory cancellation of Kyoto units" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "ERU" button
    And I enter value "1500" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | Transaction type details                    | Mandatory cancellation of Kyoto units                                                                                  |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity ERU CP2 CP2 1500                          |
      | acquiring account details                   | Account not in the trusted account list Account number GB-250-1024-2-76 Account name or description Not Available      |
    # proceed with cancellation:
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
    And The page "contains" the "GB-100-1003-2-76" text
    And The page "contains" the "1500 ERU" text

  @test-case-id-10308521586
  Scenario: As senior admin user I cannot propose a mandatory cancellation transaction while another transaction of the same type is pending
    Given the following accounts have been created
      | account_id       | kyoto_account_type             | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT          | NONE                  | Party Holding 2            | OPEN | 1500 | RMU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | RMU      | 1003      | 2                   |
      | GB-250-1024-2-76 | MANDATORY_CANCELLATION_ACCOUNT | NONE                  | Mandatory cancellation CP2 | OPEN | 100  | RMU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | RMU      | 1017      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Mandatory cancellation of Kyoto units" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "RMU" button
    And I enter value "1000" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | Transaction type details                    | Mandatory cancellation of Kyoto units                                                                                  |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1500                          |
      | acquiring account details                   | Account not in the trusted account list Account number GB-250-1024-2-76 Account name or description Not Available      |
    # continue with Check and sign your proposal optional comment:
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
      | fieldName             | field_value                                                                                                                                                                                                     |
      | Holdings main content | Holdings Total available quantity: Total reserved quantity: 1,000 RMU Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity RMU 2 2 500 Reserved quantity RMU 2 2 1000 |
    # ensure that I cannot re-submit a transaction of the same type as long as the current task is not completed
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Mandatory cancellation of Kyoto units" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "RMU" button
    And I enter value "100" in "Enter the quantity to transfer" field
    When I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for Art. 3.7ter cancellation" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | Transaction type details                    | Mandatory cancellation of Kyoto units                                                                                  |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 100                           |
      | acquiring account details                   | Account not in the trusted account list Account number GB-270-1017-2-28 Account name or description Not Available      |
    # continue with Check and sign your proposal optional comment:
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I see an error summary with "Transactions from accounts with other pending transactions of the same type are not allowed, with the exception of transfers"
