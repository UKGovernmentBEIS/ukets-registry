@functional-area-ets-transactions
@exec-manual

Feature: ETS transactions - Reverse allocation & Tasks

  Epic: ETS Transactions
  Version: v1.8 (08/03/2021)
  Story: 5.7.2 (Extension) Transaction - Reverse allocation

  # Information for Reverse allocation:
  #
  # From any ETS OHA and AOHA Account, SRA can revert an existing Allocation transaction.
  #    Acquiring account will be the UK Allocation Account.
  #    No delay applies for this transaction.
  #
  #    Reversal of allocation transaction to acquiring account with status CLOSED and TRANSFER PENDING are not allowed
  #    Reverse allocation button will disappear from the Transaction details page as soon as a pending reversal exists.

  @test-case-id-6239057623
  Scenario Outline: As SRA user I can trigger a Reverse Allocation transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When the time is out of the transaction window
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "contains" the "Reverse transaction" button
    When I click the "Reverse transaction" button
    Then I am presented with the "Overview Transaction" screen
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                              |
      | Transferring account details                | Account holder AccHolder Account number UK-100-10000052-0-92 Account name <account_name> |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 100                                            |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Allocation Account          |
    When I get a new otp based on the existing secret for the "senior admin" user
    And I enter value "correct otp for user senior admin" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
	# Check that Reverse transaction button will disappear from the Transaction details page as soon as a pending reversal exists.
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "SurrenderAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button
	# As 2nd SRA, Locate the task and approve
    When I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I enter value "UK-100-10000052-0-92" in "Account number" field
    And I click the "Search" button
    Then I am presented with the "Task List" screen
    When I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve"	button
    Then I am presented with the "Task Details approve request" screen
    When I click the "Complete task" button
	# the task is now APPROVED, check that were added to the UK Allocation Account
    When I access "10000002" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Available quantity | Available quantity: 100 Assigned Amount Units |
	# and check that the units the units were removed from the account
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                         |
      | Available quantity | Available quantity: <new_bal> Assigned Amount Units |

    Examples:
      | registry_account_type             | account_name      | acc_status                   | bal  | ub_start | ub_end | ub_acc_id | type_label                        | new_bal |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN                         | 2000 | 1        | 2000   | 10000052  | Operator holding account          | 1900    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN                         | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account | 900     |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED_PARTIALLY          | 2000 | 1        | 2000   | 10000052  | Operator holding account          | 1900    |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED                    | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account | 900     |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SOME_TRANSACTIONS_RESTRICTED | 2000 | 1        | 2000   | 10000052  | Operator holding account          | 1900    |

  @test-case-id-6239053059 @exec-manual
  Scenario Outline: As SRA user I cannot trigger a Reverse Allocation transaction of ALL TRANSACTIONS RESTRICTED
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    And I sign in as "senior admin" user
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

    Examples:
      | registry_account_type    | account_name     | acc_status                  | bal  | ub_start | ub_end | ub_acc_id | type_label               |
      | OPERATOR_HOLDING_ACCOUNT | OHA Test Account | ALL_TRANSACTIONS_RESTRICTED | 2000 | 1        | 2000   | 10000052  | Operator holding account |

  @test-case-id-6239057622
  Scenario Outline: As AR user I cannot trigger a Reverse Allocation transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

    Examples:
      | registry_account_type             | account_name      | acc_status | bal  | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |

  @test-case-id-6239057621
  Scenario Outline: As JRA user I cannot trigger a Reverse Allocation transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

    Examples:
      | registry_account_type             | account_name      | acc_status | bal  | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |

  @test-case-id-6239057620
  Scenario: As SRA user I cannot trigger a Reverse Allocation transaction under a Trading or KP account
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name        | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label      |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | TRADING_ACCOUNT       | Trader Account Test | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Trading account |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000052-0-92 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

  @test-case-id-6239057624
  Scenario Outline: As SRA user I cannot trigger a Reverse Allocation transaction to acquiring account with status CLOSED TRANSFER PENDING are not allowed
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

    Examples:
      | registry_account_type             | account_name      | acc_status       | bal  | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | CLOSED           | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | CLOSED           | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | TRANSFER_PENDING | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | TRANSFER_PENDING | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |

  @test-case-id-6239057625
  Scenario Outline: As SRA user I cannot trigger a Reverse Allocation transaction after 14 days
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -15 days | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "does not contains" the "Reverse transaction" button

    Examples:
      | registry_account_type             | account_name      | acc_status | bal  | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |

  @test-case-id-6239057626
  Scenario Outline: As SRA user I can cancel a Reverse Allocation transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label            |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account |
    And the following transaction exists
      | trans_id | trans_type         | From_account         | To_account           | Quantity | Execution_date | trans_status |
      | UK10001  | AllocateAllowances | UK-100-10000002-0-51 | UK-100-10000025-0-33 | 100      | Today -5 days  | Completed    |
	# Check that the transaction can be executed out of the transaction window hours
    When the time is out of the transaction window
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Transactions" link
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "UK10001" link
    And The page "contains" the "AllocateAllowances" text
    When I click the "Transaction UK10001" link
    Then I am presented with the "Transaction detail" screen
    And The page "contains" the "Transaction ID:" text
    And The page "contains" the "COMPLETED" text
    And I see the following fields having the values:
      | fieldName           | field_value              |
      | Transaction account | UK Allocation Account    |
      | Transaction type    | Allocation of allowances |
      | Total quantity      | 100 Allowances           |
      | Acquiring account   | UK-100-10000052-0-92     |
    And The page "contains" the "Reverse transaction" button
    When I click the "Reverse transaction" button
    Then I am presented with the "Overview Transaction" screen
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                              |
      | Transferring account details                | Account holder AccHolder Account number UK-100-10000052-0-92 Account name <account_name> |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 100                                            |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Allocation Account          |
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account overview" screen

    Examples:
      | registry_account_type             | account_name      | acc_status          | bal  | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN                | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN                | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED_PARTIALLY | 2000 | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED           | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account |