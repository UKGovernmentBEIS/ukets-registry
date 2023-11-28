@functional-area-kp-transactions

Feature: KP transactions - Retirement

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.5) As a Senior-Admin, I can execute a retirement transaction.

  # Information for retirement:
  #
  # a. from transferring account:
  #   Party holding account (100)
  #   having AAU CP1 or CP2
  #
  # b. to acquiring account:
  #   FIXED, retirement account (280) for the CP of the chosen units

  @exec-manual @test-case-id-463382222019
  Scenario: As senior admin user I cannot trigger an Retirement transaction when I set invalid quantity values or leave mandatory fields empty
    Given I have the following registry levels
      | Type                  | AAU_TO_RETIRE |
      | UnitType              | AAU           |
      | CommitmentPeriod      | CP2           |
      | EnvironmentalActivity | not defined   |
      | Initial               | 10000000      |
      | Consumed              | 0             |
      | Pending               | 0             |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2            | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-300-1022-2-24 | RETIREMENT_ACCOUNT    | NONE                  | Retirement account for CP2 | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1018      | 2                   |
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
    # try to continue without select an option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"
    When I select the "Retirement" option
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
      | unit types and quantity to transfer detail | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU (Not Subject to SOP) CP2 CP2 1500     |
      | acquiring account details                  | Account not in the trusted account list Account number GB-280-1018-2-30 Account name or description Not Available      |

  @sampling-smoke @test-case-id-463382222134
  Scenario: As senior admin user I can trigger an retirement transaction
    Given I have the following registry levels
      | Type                  | AAU_TO_RETIRE |
      | UnitType              | AAU           |
      | CommitmentPeriod      | CP2           |
      | EnvironmentalActivity | not defined   |
      | Initial               | 10000000      |
      | Consumed              | 0             |
      | Pending               | 0             |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2            | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-300-1022-2-24 | RETIREMENT_ACCOUNT    | NONE                  | Retirement account for CP2 | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1022      | 2                   |
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
    When I select the "Retirement" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "900" in "Enter the quantity to transfer" field
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "AAU (Not Subject to SOP)" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    Then The page "contains" the "You have submitted a transaction proposal" text
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    And I see the following fields having the values:
      | fieldName            | field_value                |
      | request Id value     | [not empty nor null value] |
      | transaction Id value | [not empty nor null value] |
    And I click the "Back to account details" button
    Then I am presented with the "View account" screen
    When I click the "Transactions" link
    Then The page "contains" the "Party Holding Account 1003" text
    And The page "contains" the "Party Holding Account 1022" text
    And The page "contains" the "900" text
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @test-case-id-463382222203
  Scenario: As senior admin user I can propose an Retirement transaction while another transaction of the same type is pending
    Given I have the following registry levels
      | Type                  | AAU_TO_RETIRE |
      | UnitType              | AAU           |
      | CommitmentPeriod      | CP2           |
      | EnvironmentalActivity | not defined   |
      | Initial               | 10000000      |
      | Consumed              | 0             |
      | Pending               | 0             |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name               | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2            | OPEN | 1500 | AAU   | false      | true      | 2500     | 3999   | CP2      | CP2      |             | AAU      | 1003      | 2                   |
      | GB-300-1022-2-24 | RETIREMENT_ACCOUNT    | NONE                  | Retirement account for CP2 | OPEN | 100  | AAU   | false      | true      | 4000     | 4099   | CP2      | CP2      |             | AAU      | 1022      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Retirement" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "900" in "Enter the quantity to transfer" field
    When I click the "continue" button
    And I click the "continue" button
    Then The page "contains" the "AAU (Not Subject to SOP)" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    Then The page "contains" the "You have submitted a transaction proposal" text
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # ensure that transactions item is created
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Retirement" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "AAU (Not Subject to SOP)" button
    And I enter value "10" in "Enter the quantity to transfer" field
    When I click the "continue" button
    And I click the "continue" button
    Then The page "contains" the "AAU (Not Subject to SOP)" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    # proceed with final submission
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    Then The page "contains" the "You have submitted a transaction proposal" text
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
