@functional-area-kp-transactions
@exec-manual

Feature: KP transactions - Expiry Date Change of tCER or lCER

  Epic: KP Transactions
  Version: v1.5 (09/04/2020)
  Story: (KP Transactions, &5.1.14) As a Senior-Admin, I can execute an Expiry Date Change of tCER or lCER

  # Information for Expiry Date Change of tCER or lCER:
  #
  # a. from transferring account:
  #   Former operator holding account	(120)
  #   Party holding account (including Scottish and Welsh Net Emissions)	(100)
  #   Person holding account	(121)
  #   Retirement account	(300)
  #   tCER replacement account for expiry	(411)
  #   lCER replacement account for expiry	(421)
  #   lCER replacement account for reversal of storage	(422)
  #   lCER replacement account for non-submission of certification report	(423)
  #
  #   having tCER, lCER  units
  #
  # b. to acquiring account:
  #   FIXED, the transferring account itself.
  #
  #  This transaction is to be selected by the registry administrator ONLY as a response to an ITL notification of the following type:
  #        Type 9 (Expiry Date Change)
  #   The tCER, lCER units will extend their validity by X months, as specified within the ITL notification message triggering this transaction.

  @test-case-id-25423220807
  Scenario: As senior admin user I cannot trigger an Expiry Date Change transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following notification exists
      | notification_id | type | project_id | target | due_to    | CP  |
      | 41              | 9    | GB5690     | 30     | today+30d | CP2 |
    And the following project exists
      | project_id |
      | GB5690     |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | tCER  | GB5690     | false      | true      | 2500     | 3999   | CP2      | CP2      |             | tCER     | 1003      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Expiry Date Change of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    # ITL notification is MANDATORY for this case.
    When I click the "Continue" button
    Then I see an error detail with id "event-name-error" and content "Invalid notification ID - please try again"
    When I enter value "41" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Select unit type(s) and specify quantity" text
    # try to continue without selection a unit type
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"
    # try to continue without setting a quantity amount
    When I click the "tCER" button
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

  @test-case-id-25423220905
  Scenario: As senior admin user I can trigger an Expiry Date Change transaction
    Given the following notification exists
      | notification_id | type | project_id | target | due_to    | CP  |
      | 41              | 9    | GB5690     | 30     | today+30d | CP2 |
    And the following project exists
      | project_id |
      | GB5690     |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | tCER  | GB5690     | false      | true      | 2500     | 3999   | CP2      | CP2      |             | tCER     | 1003      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Expiry Date Change of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "41" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "20" in "tCER Enter quantity" field
    And I select "GB5690" in "tCER Select project" false
    And I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity tCER  CP2 CP2 20                          |
      | acquiring account details                   | Trusted account Account number GB-100-1003-2-76 Account name or description Party Holding Account                      |
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
      | fieldName             | field_value                                                                                                                                                                                              |
      | Holdings main content | Holdings Total available quantity: 1,480 Total reserved quantity: 20 tCER Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity tCER 2 2 1480 |
    # ensure that the task is now created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text
  # [Manually executed step] And After task "approval" I see the correct values in "Account" areas of "Holdings" and "Transactions"

  @test-case-id-25423220977 @exec-manual
  Scenario: As senior admin user while implementing an Expiry Date Change transaction I see data retained while navigating backwards
    Given the following notification exists
      | notification_id | type | project_id | target | due_to    | CP  |
      | 41              | 9    | GB5690     | 30     | today+30d | CP2 |
    And the following project exists
      | project_id |
      | GB5690     |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | tCER  | GB5690     | false      | true      | 2500     | 3999   | CP2      | CP2      |             | tCER     | 1003      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Expiry Date Change of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "41" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "20" in "tCER Enter quantity" field
    And I select "GB5690" in "tCER Select project" false
    And I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity tCER  CP2 CP2 20                          |
      | acquiring account details                   | Trusted account Account number GB-100-1003-2-76 Account name or description Party Holding Account                      |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | checkbox button lCER checkbox  | selected    |
      | Enter the quantity to transfer | 20          |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see data retained

  @test-case-id-25423221042
  Scenario: As senior admin user I can cancel an Expiry Date Change transaction triggering
    Given the following notification exists
      | notification_id | type | project_id | target | due_to    | CP  |
      | 41              | 9    | GB5690     | 30     | today+30d | CP2 |
    And the following project exists
      | project_id |
      | GB5690     |
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | un_project | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1003-2-76 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | OPEN | 1500 | tCER  | GB5690     | false      | true      | 2500     | 3999   | CP2      | CP2      |             | tCER     | 1003      | 2                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1003" in "account-details"
    Then I am presented with the "View account overview" screen
    # overview left side screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1003 OPEN Change status |
      | Available quantity | Total available quantity: 1,500 Assigned Amount Units       |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I select the "Expiry Date Change of tCER or lCER" option
    Then The page "contains" the "Enter ITL notification ID" text
    When I enter value "41" in "ITL notification ID" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "20" in "tCER Enter quantity" field
    And I select "GB5690" in "tCER Select project" false
    And I click the "continue" button
    # ensure that information is correct
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                            |
      | Transferring account details                | Account Holder Test Holder Name Account number GB-100-1003-2-76 Account name or description Party Holding Account 1003 |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity tCER  CP2 CP2 20                          |
      | acquiring account details                   | Trusted account Account number GB-100-1003-2-76 Account name or description Party Holding Account                      |
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
    And The page "contains" the "Total available quantity:" text
    And The page "contains" the "1500 tCER" text
