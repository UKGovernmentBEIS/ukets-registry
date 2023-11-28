@functional-area-ets-transactions

Feature: ETS transactions - Surrender & Tasks

  Epic: ETS Transactions
  Version: v1.7 (04/03/2021)
  Story: 5.6.3 (Extension) Transaction - Surrender allowances

  # Information for Surrenders:
  # From any ETS OHA and AOHA Account
  # The acquiring account will always be ‘UK Surrender Account’ (Gov account).
  # Transaction can be carried out by ARs & SRAs for non SUSPENDED accounts.
  # Transaction can be carried out by SRAs in case of SUSPENDED accounts.
  # Delays never apply to transactions where the acquiring account is a government account, with the exception of the allocation.

  @test-case-id-8652326
  Scenario Outline: As SRA user I can successfully propose a Surrender transaction and then a correct Transaction Proposal task is created
    # ETS UNITS (aka non kp units) need a. CP0 only (not CP1 or CP2) and b. ub_activity empty (not e.g. AFFORESTATION_AND_REFORESTATION)
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name         | account_status   | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      # party holding account (e.g. oha / aoha) with unit type ALLOWANCE
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Party Holding 1      | <account_status> | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
      # UK Surrender Account with registry_account_type UK_SURRENDER_ACCOUNT
      | UK-100-10000025-0-33 | PARTY_HOLDING_ACCOUNT | UK_SURRENDER_ACCOUNT     | UK Surrender Account | OPEN             | 500 | ALLOWANCE | true       | true      | 10000003100 | 10000003599 | CP0      | CP0      |             | ALLOWANCE | 10000025  | 0                   | GOVERNMENT        | UK Surrender Account     |     |                        |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "100" in "quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Transaction Proposal" text

    @sampling-smoke
    Examples:
      | account_status |
      | OPEN           |

    Examples:
      | account_status      |
      | SUSPENDED_PARTIALLY |
      | SUSPENDED           |

  @test-case-id-865213261234
  Scenario Outline: As AR I can execute surrender process without task approval when the oha/aoha account has enabled single person surrender config
    # set singlePersonApproval to true in order to enabled single person surrender config:
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name         | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction | singlePersonApproval |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | <registry_acc_type>   | Party Holding 1      | OPEN           | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        | false                |
      | UK-100-10000025-0-33 | PARTY_HOLDING_ACCOUNT | UK_SURRENDER_ACCOUNT  | UK Surrender Account | OPEN           | 500 | ALLOWANCE | true       | true      | 10000003100 | 10000003599 | CP0      | CP0      |             | ALLOWANCE | 10000025  | 0                   | GOVERNMENT        | UK Surrender Account     |     |                        | true                 |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "100" in "quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I get a new otp based on the existing secret for the "test_authorized_representative_user" user
    And I enter value "correct otp for user test_authorized_representative_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "You have approved the proposed transaction" text
    And The page "contains" the "The transaction ID is" text
    # ensure party holder account balance equals the current one minus 100
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                 |
      | Total available quantity content | Total available quantity: 0 |
      | Total reserved quantity content  | Total reserved quantity: 0  |
    # ensure surrender account balance equals the current one plus 100
    When I access "10000025" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                              |
      | Total available quantity content | Total available quantity: 600 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0               |

    @sampling-smoke
    Examples:
      | registry_acc_type        |
      | OPERATOR_HOLDING_ACCOUNT |

    Examples:
      | registry_acc_type                 |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |

  @test-case-id-8652197 @exec-manual
  Scenario Outline: As a SRA user I cannot trigger a Surrender transaction of ALL TRANSACTIONS RESTRICTED
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | ORGANISATION    | <type_label> |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "does not contains" the "Surrender allowances" text

    Examples:
      | account_get | account_id           | kyoto_account_type    | registry_account_type    | account_name        | acc_status                  | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | type_label      |
      | 10000052    | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Trader Account Test | ALL_TRANSACTIONS_RESTRICTED | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | Trading account |

  @test-case-id-862327
  Scenario Outline: As SRA user I can successfully see that correct units are transferred to UK Surrender Account after Surrender transaction proposal task approval
    # ETS UNITS (aka non kp units) need a. CP0 only (not CP1 or CP2) and b. ub_activity empty (not e.g. AFFORESTATION_AND_REFORESTATION)
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name         | account_status   | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      # party holding account (e.g. oha / aoha) with unit type ALLOWANCE
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Party Holding 1      | <account_status> | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     | UK100000001            |
      # UK Surrender Account with registry_account_type UK_SURRENDER_ACCOUNT
      | UK-100-10000025-0-33 | PARTY_HOLDING_ACCOUNT | UK_SURRENDER_ACCOUNT     | UK Surrender Account | OPEN             | 500 | ALLOWANCE | true       | true      | 10000003100 | 10000003599 | CP0      | CP0      |             | ALLOWANCE | 10000025  | 0                   | GOVERNMENT        | UK Surrender Account     |     |                        |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value          |
      | Originating Country Code | GB                   |
      | Account ID               | 100000001            |
      | Claimed by               | 100000001            |
      | Initiated by             | 100000002            |
      | Transaction type         | Surrender Allowances |
      | Commitment period        | 0                    |
      | Acquiring account        | UK-100-10000025-0-33 |
      | Transferring account     | UK-100-10000056-0-72 |
      | Unit type                | ALLOWANCE            |
      | Quantity to issue        | 100                  |
      | Environmental activity   |                      |
    # approve transaction
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    # the task is now APPROVED, check that the units were removed from the account: ensure new amount is 0 instead of 100
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                 |
      | Total available quantity content | Total available quantity: 0 |
      | Total reserved quantity content  | Total reserved quantity: 0  |
    # check that the units were added to the UK Surrender Account: ensure new amount is 600 instead of 500
    When I access "10000025" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                              |
      | Total available quantity content | Total available quantity: 600 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0               |

    @sampling-smoke
    Examples:
      | account_status |
      | OPEN           |

    Examples:
      | account_status      |
      | SUSPENDED_PARTIALLY |
      | SUSPENDED           |

  @test-case-id-8652325 @exec-manual
  Scenario Outline: As AR user I can trigger a Surrender transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name         | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label           |
      | UK-100-10000025-0-33 | PARTY_HOLDING_ACCOUNT | UK_SURRENDER_ACCOUNT  | UK Surrender Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000025  | 0             | GOVERNMENT      | UK Surrender Account |
    And "enrolledAR2" user has the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    # Check here also: Delays never apply to transactions where the acquiring account is a government account
    # --> We should execute the transaction out of the transaction window and it has to be completed without delay.
    When the time is out of the transaction window
    And I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to surrender allowances" text
    When I enter value "100" in "UKA Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                              |
      | Transferring account details                | Account holder AccHolder Account number UK-100-10000052-0-92 Account name <account_name> |
      | Transaction type details                    | Surrender allocation                                                                     |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 100                                            |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Surrender Account           |
    When I get a new otp based on the existing secret for the "enrolledAR" user
    And I enter value "correct otp for user enrolledAR" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # As 2nd AR, Locate the task and approve
    When I sign in as "enrolledAR2" user
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
    When I click the "Approve" button
    Then I am presented with the "Task Details approve request" screen
    When I click the "Complete task" button
    # the task is now APPROVED, check that the units were removed from the account
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                               |
      | Available quantity | Total available quantity: <new_bal> Assigned Amount Units |
    # and check that the units were added to the UK Surrender Account
    When I access "10000025" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                         |
      | Available quantity | Total available quantity: 100 Assigned Amount Units |

    Examples:
      | registry_account_type             | account_name      | acc_status | bal  | ub_start | ub_end | ub_acc_id | type_label                        | new_bal |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 1        | 2000   | 10000052  | Operator holding account          | 1,900   |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 3001     | 4000   | 10000072  | Aircraft operator holding account | 900     |

  @test-case-id-8652321 @exec-manual
  Scenario Outline: As a AR user I cannot trigger a Surrender transaction under a Trading or KP account
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | ORGANISATION    | <type_label> |
    And I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "does not contains" the "Surrender allowances" text

    Examples:
      | account_get | account_id           | kyoto_account_type     | registry_account_type | account_name        | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | type_label             |
      | 10000052    | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT  | TRADING_ACCOUNT       | Trader Account Test | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | Trading account        |
      | 10000072    | GB-121-10000072-0-94 | PERSON_HOLDING_ACCOUNT | NONE                  | EP Account          | OPEN       | 1000 | AAU       | true       | true      | 3001     | 4000   | CP2      | CP2      |             | AAU       | 10000072  | 0             | Person holding account |

  @test-case-id-8652322 @exec-manual
  Scenario Outline: As a SRA user I cannot trigger a Surrender transaction under a Trading or KP account
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | ORGANISATION    | <type_label> |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "does not contains" the "Surrender allowances" text

    Examples:
      | account_get | account_id           | kyoto_account_type     | registry_account_type | account_name        | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | type_label             |
      | 10000052    | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT  | TRADING_ACCOUNT       | Trader Account Test | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | Trading account        |
      | 10000072    | GB-121-10000072-0-94 | PERSON_HOLDING_ACCOUNT | NONE                  | EP Account          | OPEN       | 1000 | AAU       | true       | true      | 3001     | 4000   | CP2      | CP2      |             | AAU       | 10000072  | 0             | Person holding account |

  @test-case-id-8652323 @exec-manual
  Scenario Outline: As AR user I cannot trigger a Surrender transaction under a SUSPENDED or PARTIALLY SUSPENDED accounts
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "does not contains" the "Surrender allowances" text

    Examples:
      | registry_account_type             | account_name      | acc_status          | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED           | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED_PARTIALLY | 1000 | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account |

  @test-case-id-8652324 @exec-manual
  Scenario Outline: As JRA user I cannot trigger a Surrender transaction
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And The page "does not contains" the "Surrender allowances" text

    Examples:
      | registry_account_type             | account_name      | acc_status | bal                 | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label   |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | OPEN                | 2000  | ALLOWANCE  | true      | true     | 1      | 2000     | CP0      | CP0         |          | ALLOWANCE | 10000052      | 0               | ORGANISATION |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | OPEN                | 1000  | ALLOWANCE  | true      | true     | 3001   | 4000     | CP0      | CP0         |          | ALLOWANCE | 10000072      | 0               | ORGANISATION |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | SUSPENDED_PARTIALLY | 2000  | ALLOWANCE  | true      | true     | 1      | 2000     | CP0      | CP0         |          | ALLOWANCE | 10000052      | 0               | ORGANISATION |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | SUSPENDED_PARTIALLY | 1000  | ALLOWANCE  | true      | true     | 3001   | 4000     | CP0      | CP0         |          | ALLOWANCE | 10000072      | 0               | ORGANISATION |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | SUSPENDED           | 2000  | ALLOWANCE  | true      | true     | 1      | 2000     | CP0      | CP0         |          | ALLOWANCE | 10000052      | 0               | ORGANISATION |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | SUSPENDED           | 1000  | ALLOWANCE  | true      | true     | 3001   | 4000     | CP0      | CP0         |          | ALLOWANCE | 10000072      | 0               | ORGANISATION |

  @test-case-id-8652327 @exec-manual
  Scenario: As AR user I cannot trigger a Surrender transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name     | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label               |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | OHA Test Account | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account |
    And I sign in as "enrolledAR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | UK-100-10000052-0-92 |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Enter the quantity to surrender" text
    # try to continue without setting a quantity amount
    When I click the "continue" button
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

  @test-case-id-8652328 @exec-manual
  Scenario: As SRA user while implementing a Surrender transaction I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Available quantity | Total available quantity: 1,000 Assigned Amount Units |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to surrender allowances" text
    When I enter value "100" in "UKA Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                 |
      | Transferring account details                | Account holder AccHolder Account number UK-100-10000052-0-92 Account name AOHA Test Account |
      | Transaction type details                    | Surrender allocation                                                                        |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 100                                               |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Surrender Account              |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                       | field_value |
      | Enter the quantity to surrender | 100         |
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    Then I see data retained

  @test-case-id-8652329 @exec-manual
  Scenario Outline: As SRA user I can cancel a Surrender transaction triggering
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name   | acc_status   | bal   | un_ty     | tr_out_tal | ap_sec_ar | ub_start   | ub_end   | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id   | commit_period | acc_holder_type | type_label   |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | <account_name> | <acc_status> | <bal> | ALLOWANCE | true       | true      | <ub_start> | <ub_end> | CP0      | CP0      |             | ALLOWANCE | <ub_acc_id> | 0             | ORGANISATION    | <type_label> |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Surrender allowances" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to surrender allowances" text
    When I enter value "100" in "UKA Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                 |
      | Transferring account details                | Account holder <acc_holder> Account number UK-100-10000052-0-92 Account name <account_name> |
      | Transaction type details                    | Surrender allocation                                                                        |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 100                                               |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Surrender Account              |
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |

    Examples:
      | registry_account_type             | account_name      | acc_holder | acc_status          | bal  | balance_ui | ub_start | ub_end | ub_acc_id | type_label                        |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | OPEN                | 2000 | 2,000      | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | OPEN                | 1000 | 1,000      | 3001     | 4000   | 10000072  | Aircraft operator holding account |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | SUSPENDED_PARTIALLY | 2000 | 2,000      | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | SUSPENDED_PARTIALLY | 1000 | 1,000      | 3001     | 4000   | 10000072  | Aircraft operator holding account |
      | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | AccHolder  | SUSPENDED           | 2000 | 2,000      | 1        | 2000   | 10000052  | Operator holding account          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | AccHolder  | SUSPENDED           | 1000 | 1,000      | 3001     | 4000   | 10000072  | Aircraft operator holding account |
