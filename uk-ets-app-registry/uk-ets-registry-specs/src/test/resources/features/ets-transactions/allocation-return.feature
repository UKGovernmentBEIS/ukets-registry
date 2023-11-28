@functional-area-ets-transactions

Feature: ETS transactions - Return excess allocation & Tasks

  Epic: ETS Transactions
  Version: v1.7 (04/03/2021)
  Story: 5.6.2 (Extension) Transaction - Return excess allocation

  # Information for Return excess allocation:
  # From any ETS OHA and AOHA Account
  # The acquiring account will always be ‘UK Allocation Account’ (Gov account) Or "UK New Entrants Reserve Account"
  # Transaction can be carried out by ARs & SRAs for non SUSPENDED accounts.
  # Transaction can be carried out by SRAs in case of SUSPENDED accounts.
  # BR3015 The returned quantity cannot be more than the over-allocated amount  (for the given Year and NAT table)
  # Delays never apply to transactions other than transfers

  @test-case-id-8652394
  Scenario Outline: As SRA user I can trigger a Return excess allocation transaction task and units are correctly transferred upon task approval
  # party holding account (e.g. oha / aoha) with unit type ALLOWANCE and balance 1100
    Given I have created an account with the following properties
      | property                 | value                                                                     |
      | accountType              | <registry_account_type>                                                   |
      | accountIndex             | 5                                                                         |
      | accountStatus            | <acc_status>                                                              |
      | holderType               | ORGANISATION                                                              |
      | holderName               | Organisation 1                                                            |
      | legalRepresentative      | Legal Rep1a                                                               |
      | legalRepresentative      | Legal Rep2b                                                               |
      | authorisedRepresentative | Authorised Representative1                                                |
      | unitsInformation         | 1100,ALLOWANCE,10000002000,10000003099,10000019,CP0,CP0,empty,empty,false |
      | commitmentPeriod         | 0                                                                         |
      | transfersOutsideTal      | true                                                                      |
      | approveSecondAr          | true                                                                      |
    # UK Allocation Account with registry_account_type UK_ALLOCATION_ACCOUNT
    And the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | accountHolderType | typeLabel             | sop | reservedForTransaction |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT | UK Allocation Account | OPEN           | 500 | ALLOWANCE | true       | true      | 10000003100 | 10000003599 | CP0      | CP0      |             | ALLOWANCE | 10000002  | 0             | GOVERNMENT        | UK Allocation Account |     |                        |
    # create allocation table for the oha/aoha account created and also allocate allocate allowances of 150 value for current scenario
    And There are the following allocated allowances for the compliant entity "100000001" of "<table_type>" allocation table of "ALLOWED" status
      | allocation_year_id | entitlement | allocated | returned | reversed |
      | 100000001          | 100         | 150       | [null]   | [null]   |
      | 100000002          | 100         | [null]    | [null]   | [null]   |
      | 100000003          | 100         | [null]    | [null]   | [null]   |
      | 100000004          | 100         | [null]    | [null]   | [null]   |
      | 100000005          | 100         | [null]    | [null]   | [null]   |
      | 100000006          | 100         | [null]    | [null]   | [null]   |
      | 100000007          | 100         | [null]    | [null]   | [null]   |
      | 100000008          | 100         | [null]    | [null]   | [null]   |
      | 100000009          | 100         | [null]    | [null]   | [null]   |
      | 100000010          | 100         | [null]    | [null]   | [null]   |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000019" in "account-details"
    Then I am presented with the "View account" screen
    And The page "contains" the "Allocation status for UK allowances" text
    When I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    # Return allowances
    And The page "contains" the "Return allowances" text
    When I click the "Return allowances" button
    Then I am presented with the "View account" screen
    And The page "contains" the "Choose the number of units to return" text
    # there is already a default quantity set to "50" so there is no need for clear and reset value
    When I click the "Continue" button
    Then I am presented with the "View account" screen
    When I click the "Continue" button
    Then I am presented with the "View account" screen
    And The page "contains" the "Check and sign your proposal" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "submit" button
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName            | field_value                               |
      | submit title         | You have submitted a transaction proposal |
      | request Id value     | [not empty nor null value]                |
      | transaction Id value | [not empty nor null value]                |
    # manually executed step: ensure warning is presented:
    # When I click the "Allocation status for UK allowances" button
    # Then The page "contains" the "There are transactions for return of excess allocation pending approval." text
    # as seconds sra claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And The page "contains" the "Claimed" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    And I get a new otp based on the existing secret for the "test_senior_admin 2_user" user
    And I enter value "correct otp for user test_senior_admin 2_user" in "otp" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    When I click the "Back to task list" link
    Then I am presented with the "Task List" screen
    # the task is now APPROVED, check that the units were removed from the account: ensure new amount is 1050 instead of 1100
    When I access "10000019" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                                |
      | Total available quantity content | Total available quantity: 1,050 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0                 |
    # check that the units were added to the UK Surrender Account: ensure new amount is 550 instead of 500
    When I access "10000002" in "account-details"
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value                              |
      | Total available quantity content | Total available quantity: 550 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0               |

    @sampling-smoke
    Examples:
      | registry_account_type    | acc_status | table_type |
      | OPERATOR_HOLDING_ACCOUNT | OPEN       | NAT        |

    Examples:
      | registry_account_type    | acc_status          | table_type |
      | OPERATOR_HOLDING_ACCOUNT | SUSPENDED           | NAT        |
      | OPERATOR_HOLDING_ACCOUNT | SUSPENDED_PARTIALLY | NAT        |

    @exec-manual
    Examples:
      | registry_account_type             | acc_status          | table_type |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN                | NAVAT      |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | SUSPENDED           | NAVAT      |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | SUSPENDED_PARTIALLY | NAVAT      |

  @test-case-id-8652306 @exec-manual
  Scenario Outline: As SRA user I cannot trigger a Return excess allocation transaction under a ALL TRANSACTIONS RESTRICTED accounts
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    When I sign in as "senior admin" user
      | ACTIVE | INITIATE_AND_APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "does not contains" the "Return allowances" button

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type    | account_name     | acc_status                  | balance_ui | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label               | NAT_table |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | OHA Test Account | ALL_TRANSACTIONS_RESTRICTED | 2,000      | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account | NAT       |

  @test-case-id-8652391 @exec-manual
  Scenario Outline: As AR user I cannot trigger a Return excess allocation transaction under a SUSPENDED or PARTIALLY SUSPENDED or ALL_TRANSACTIONS_RESTRICTED accounts
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    And I sign in as "enrolled AR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "does not contains" the "Return allowances" button

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status                  | balance_ui | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED                   | 2,000      | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED_PARTIALLY         | 1,000      | 1000 | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | ALL_TRANSACTIONS_RESTRICTED | 2,000      | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NER       |

  @test-case-id-8652392 @exec-manual
  Scenario Outline: As JRA user I cannot trigger a Return excess allocation transaction
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "does not contains" the "Return allowances" button

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status          | bal  | balance_ui | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED           | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED_PARTIALLY | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN                | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | TRANSFER_PENDING    | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       |

  @test-case-id-8652393 @exec-manual
  Scenario Outline: As AR user I can trigger a Return excess allocation transaction
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following system accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type           | account_name                    | acc_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                      |
      | UK-100-10000002-0-51 | PARTY_HOLDING_ACCOUNT | UK_ALLOCATION_ACCOUNT           | UK Allocation Account           | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000002  | 0             | GOVERNMENT      | UK Allocation Account           |
      | UK-100-10000003-0-46 | PARTY_HOLDING_ACCOUNT | UK_NEW_ENTRANTS_RESERVE_ACCOUNT | UK New Entrants Reserve Account | OPEN       | 0   |       | true       | true      |          |        |          |          |             |          | 10000003  | 0             | GOVERNMENT      | UK New Entrants Reserve Account |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
	# Check here also: Delays never apply to transactions other than transfers
	# --> We should execute the transaction out of the transaction window and it has to be completed without delay.
    When the time is out of the transaction window
    And I sign in as "enrolled AR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button
    When I click the "Year 2021 Return allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Choose the number of units" text
    When I enter value "<Qty>" in "UKA Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                     |
      | Transferring account details                | Account holder <acc_holder> Account number <account_id> Account name <account_name> Allocation type <NAT_table> |
      | Year of return                              | 2021                                                                                                            |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances <Qty>                                                                 |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Allocation Account                                 |
    When I get a new otp based on the existing secret for the "enrolled AR" user
    And I enter value "correct otp for user enrolledAR" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
	# A 2nd Return overallocated transaction cannot started if there is already another one pending.
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                        |
      | Account name label | Account name: <account_name>  <acc_status>         |
      | Available quantity | Total available quantity: 10 Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button
    When I click the "Year 2021 Return allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Choose the number of units" text
    When I enter value "10" in "UKA Enter quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                     |
      | Transferring account details                | Account holder <acc_holder> Account number <account_id> Account name <account_name> Allocation type <NAT_table> |
      | Year of return                              | 2021                                                                                                            |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances 10                                                                    |
      | acquiring account details                   | Account is not on the trusted account list Account number <return_account>                                      |
    When I get a new otp based on the existing secret for the "enrolled AR" user
    And I enter value "correct otp for user enrolledAR" in "otp" field
    And I click the "Submit" button
    Then I see an error summary with "Transactions to return excess allocations cannot be made while a return for the same year and allocation table is pending"
	# As 2nd AR, Locate the task and approve
    When I sign in as "enrolled AR2" user with the following status and access rights to accounts:
      | ACTIVE | APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I enter value "<account_id>" in "Account number" field
    And I click the "Search" button
    Then I am presented with the "Task List" screen
    And I see the following tasks
      | task type            | name of initiator | account number | authorised representative | task status |
      | Transaction Proposal | enrolled AR       | <account_id>   |                           | UNCLAIMED   |
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
    And I see the following fields having the values:
      | fieldName            | field_value              |
      | Transferring account | <account_id>             |
      | Transaction type     | Return excess allocation |
      | Total quantity       | <balance_ui>             |
      | Acquiring account    | UK Allocation Account    |
      | Allocation table     | <NAT_table>              |
      | Year of return       | 2021                     |
      | Unit type            | Allowances               |
      | Quantity to transfer | <Qty>                    |
    And The page "contain" the "Approve" text
    When I click the "Approve" button
    Then I am presented with the "Task Details approve request" screen
    When I click the "Complete task" button
	# the task is now APPROVED, check that the units were removed from the account
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Available quantity | Total available quantity: <Qty> Assigned Amount Units |

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status | bal  | balance_ui | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table | Qty | New_Bal | return_account       |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    | UK-100-10000002-0-51 |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     | UK-100-10000002-0-51 |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NER       | 40  | 1960    | UK-100-10000003-0-46 |

  @test-case-id-8652395 @exec-manual
  Scenario Outline: As AR user I cannot trigger a Return excess allocation transaction when I set invalid quantity values or leave mandatory fields empty
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    And I sign in as "enrolled AR" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | <account_id> |
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button
    When I click the "Year 2021 Return allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Choose the number of units" text
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
    Then I see an error summary with "The proposed quantity is more than the overallocated units"

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status | bal  | balance_ui | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table | Qty | New_Bal |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN       | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN       | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     |

  @test-case-id-8652396 @exec-manual
  Scenario Outline: As SRA user while implementing a Return excess allocation transaction I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button
    When I click the "Year 2021 Return allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Choose the number of units" text
    When I enter value "<Qty>" in "UKA Enter quantity" field
    And I click the "continue" button
	#
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                     |
      | Transferring account details                | Account holder <acc_holder> Account number <account_id> Account name <account_name> Allocation type <NAT_table> |
      | Year of return                              | 2021                                                                                                            |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances <Qty>                                                                 |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Allocation Account                                 |
	#
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                    | field_value |
      | Enter the quantity to return | <Qty>       |
    When I click the "Back" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status          | bal  | balance_ui | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table | Qty | New_Bal |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN                | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN                | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED_PARTIALLY | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED           | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     |

  @test-case-id-8652397 @exec-manual
  Scenario Outline: As SRA user I can cancel a Return excess allocation transaction triggering
    Given the following accounts have been created
      | account_id   | kyoto_account_type   | registry_account_type   | account_name   | acc_status   | bal   | un_ty   | tr_out_tal   | ap_sec_ar   | ub_start   | ub_end   | ub_or_cp   | ub_ap_cp   | ub_activity   | ub_un_ty   | ub_acc_id   | commit_period   | acc_holder_type   | type_label   |
      | <account_id> | <kyoto_account_type> | <registry_account_type> | <account_name> | <acc_status> | <bal> | <un_ty> | <tr_out_tal> | <ap_sec_ar> | <ub_start> | <ub_end> | <ub_or_cp> | <ub_ap_cp> | <ub_activity> | <ub_un_ty> | <ub_acc_id> | <commit_period> | <acc_holder_type> | <type_label> |
    And the following allowances were set
      | account_id   | allocation_table | allocation_year | entitlement | allocated | remaining |
      | <account_id> | <NAT_table>      | 2021            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2022            | 100         | 150       | -50       |
      | <account_id> | <NAT_table>      | 2023            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2024            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2025            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2026            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2027            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2028            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2029            | 100         | 0         | 100       |
      | <account_id> | <NAT_table>      | 2030            | 100         | 0         | 100       |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000052" in "account-details"
    Then I am presented with the "View account overview" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                  |
      | Account name label | Account name: <account_name>  <acc_status>                   |
      | Available quantity | Total available quantity: <balance_ui> Assigned Amount Units |
    When I click the "Allocation status for UK allowances" link
    Then I am presented with the "Account Overview Allocation" screen
    And The page "contains" the "Allocation status for UK allowances" text
    And The page "contains" the "Return allowances" button
    When I click the "Year 2021 Return allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Choose the number of units" text
    When I enter value "<Qty>" in "UKA Enter quantity" field
    And I click the "continue" button
	#
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction to return overallocated allowances" text
    And The page "contains" the "Check and sign your proposal" text
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                     |
      | Transferring account details                | Account holder <acc_holder> Account number <account_id> Account name <account_name> Allocation type <NAT_table> |
      | Year of return                              | 2021                                                                                                            |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowances <Qty>                                                                 |
      | acquiring account details                   | Account is not on the trusted account list Account number UK Allocation Account                                 |
	#
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account overview" screen

    Examples:
      | account_id           | kyoto_account_type    | registry_account_type             | account_name      | acc_status          | bal  | balance_ui | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                        | NAT_table | Qty | New_Bal |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | OPEN                | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | OPEN                | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT          | OHA Test Account  | SUSPENDED_PARTIALLY | 2000 | 2,000      | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 10000052  | 0             | ORGANISATION    | Operator holding account          | NAT       | 40  | 1960    |
      | UK-100-10000052-0-92 | PARTY_HOLDING_ACCOUNT | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | AOHA Test Account | SUSPENDED           | 1000 | 1,000      | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 10000072  | 0             | ORGANISATION    | Aircraft operator holding account | NAVAT     | 40  | 960     |
