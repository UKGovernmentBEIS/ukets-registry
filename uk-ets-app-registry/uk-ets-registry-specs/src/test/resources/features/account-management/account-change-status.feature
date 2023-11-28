@functional-area-account-management

Feature: Account management - Account Change Status

  Epic: Account Management
  Version: 2.0 (05/03/2020)
  Story: As a senior registry administrator I can change the status of account in the registry
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20Management.docx?version=12&modificationDate=1583406966000&api=v2

  #  Account Status:
  #  open
  #  [blocked (system)] - new name: ALL_TRANSACTIONS_RESTRICTED
  #  [blocked (administrator)] - new name: SOME_TRANSACTIONS_RESTRICTED
  #  partially suspended
  #  suspended
  #  transfer pending
  #  closed
  #  proposed

  @test-case-id-93150821
  Scenario Outline: As a junior or read only admin I must not see the account change status link
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "<user>" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And The page "does not contain" the "Change status" text

    Examples:
      | user            |
      | junior admin    |
      | read only admin |

  @test-case-id-132743660
  Scenario: As an AR user I must not see the account change status link
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And The page "does not contain" the "Change status" text

  @test-case-id-132743694
  Scenario: As senior admin I cannot change account status of CLOSED account
    Given the following accounts have been created
      # create CLOSED account KP:
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | CLOSED         | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And The page "does not contain" the "Change status" text

  @exec-manual @test-case-id-1327436129
  Scenario: As senior admin I can change account status of TRANSFER PENDING account and then navigate to user details via Who link
    Given the following accounts have been created
      # create TRANSFER_PENDING account KP:
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status   | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | TRANSFER_PENDING | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    # change status:
    When I select the "Unsuspend account" option
    And I click the "Continue" button
    Then I am presented with the "Change account status confirm KP" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                              |
      | page main content | Current account status Transfer pending Action Unsuspend account New account status Open |
    # try to click Apply without having set a comment:
    And I enter value "comment 1" in "comment area" field
    When I click the "Apply" button
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                 |
      | Account name label | Account name: Party Holding Account 1000 OPEN Change status |
      | Available quantity | Total available quantity: 1,199 Removal Units               |
    # check history and comments area:
    And I click the "History and comments item" link
    Then The page "contains" the "Change account status (comment)" text
    And The page "contains" the "SENIOR ADMIN USER" text
    # ensure that "Who" hyperlink redirects to user details page
    When I click the "Who" link
    Then I am presented with the "User details" screen

  @exec-manual @test-case-id-931508187
  Scenario Outline: As senior admin during account status change I can navigate back or cancel the process
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    When I click the "<action_button>" link
    Then I am presented with the "View account" screen

    Examples:
      | action_button |
      | Back          |
      | Cancel        |

  @test-case-id-1327436230
  Scenario: As senior admin I cannot continue to next change account status screen if no choice is selected
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    # NO change status selection:
    And I click the "Continue" button
    Then I see an error summary with "You must select an action"
    And I see an error detail for field "accountStatusAction" with content "Error: You must select an action"

  @exec-manual @test-case-id-1327436269
  Scenario: As senior admin I must provide comment in order to change account status
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    When I select the "Suspend account (fully)" option
    And I click the "Continue" button
    Then I am presented with the "Change account status confirm KP" screen
    When I click the "Apply" button
    Then I see an error summary with "You must enter a comment"
    And I see an error detail for field "event-name" with content "Error: You must enter a comment"

  @test-case-id-931508307
  Scenario Outline: As senior admin I can change non closed KP account status
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status           | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | <initial_account_status> | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value      |
      | Propose transaction | <contain_status> |
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    # change status:
    When I select the "<account_action>" option
    And I click the "Continue" button
    Then I am presented with the "Change account status confirm KP" screen
    And I see the following fields having the values:
      | fieldName            | field_value                                                                                             |
      | Account name content | Account name: Party Holding Account 1000                                                                |
      | page main content    | Current account status <current_acc_status> Action <account_action> New account status <new_acc_status> |
    When I enter value "comment 1" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Account name label | Account name: Party Holding Account 1000      |
      | Account status     | <new_account_status> Change status            |
      | Available quantity | Total available quantity: 1,199 Removal Units |
    # check history and comments area:
    And I click the "History and comments item" link
    Then The page "contains" the "Change account status (comment)" text
    And The page "contains" the "SENIOR ADMIN USER" text

    @sampling-smoke
    Examples:
      | initial_account_status | account_action          | current_acc_status | new_acc_status | new_account_status | contain_status      |
      | OPEN                   | Suspend account (fully) | Open               | Suspended      | SUSPENDED          | Propose transaction |

    Examples:
      | initial_account_status       | account_action              | current_acc_status           | new_acc_status      | new_account_status  | contain_status      |
      | SOME_TRANSACTIONS_RESTRICTED | Remove restrictions         | Some transactions restricted | Open                | OPEN                | Propose transaction |
      | SUSPENDED_PARTIALLY          | Unsuspend account           | Suspended partially          | Open                | OPEN                | Propose transaction |
      | OPEN                         | Suspend account (partially) | Open                         | Suspended partially | SUSPENDED PARTIALLY | Propose transaction |
      | ALL_TRANSACTIONS_RESTRICTED  | Suspend account (partially) | All transactions restricted  | Suspended partially | SUSPENDED PARTIALLY | [null]              |
      | ALL_TRANSACTIONS_RESTRICTED  | Suspend account (fully)     | All transactions restricted  | Suspended           | SUSPENDED           | [null]              |
      | SOME_TRANSACTIONS_RESTRICTED | Suspend account (partially) | Some transactions restricted | Suspended partially | SUSPENDED PARTIALLY | Propose transaction |
      | SOME_TRANSACTIONS_RESTRICTED | Suspend account (fully)     | Some transactions restricted | Suspended           | SUSPENDED           | Propose transaction |
      | SUSPENDED                    | Unsuspend account           | Suspended                    | Open                | OPEN                | Propose transaction |

  @test-case-id-1327436383
  Scenario: As senior admin I cannot remove transaction restrictions from ALL TRANSACTION RESTRICTED accounts
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status              | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | ALL_TRANSACTIONS_RESTRICTED | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value |
      | Propose transaction | [null]      |
    When I click the "Change status" link
    Then I am presented with the "Change account status KP" screen
    And I see the following fields having the values:
      | fieldName            | field_value                 |
      | Account name content | Party Holding Account 1000  |
      | Account status       | ALL TRANSACTIONS RESTRICTED |
    Then The page "does not contain" the "Remove restrictions" text

  @test-case-id-931508420 @exec-manual
  Scenario Outline: As senior admin I can change non closed ets account status
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | INDIVIDUAL                 |
      | holderName               | Individual name 1          |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | state                    | <initial_account_status>   |
      | status                   | ACTIVE                     |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value |
      | Propose transaction | [null]      |
    When I click the "Change status" link
    Then I am presented with the "Change account status ETS" screen
    # change status:
    When I select the "<account_action>" option
    And I click the "Continue" button
    Then I am presented with the "Change account status confirm ETS" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                             |
      | page main content | Current account status <current_acc_status> Action <account_action> New account status <new_acc_status> |
    When I enter value "comment 1" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | <new_account_status> Change status           |
    # check history and comments area:
    And I click the "History and comments item" link
    Then The page "contains" the "Change account status (comment)" text
    And The page "contains" the "SENIOR ADMIN USER" text

    Examples:
      | initial_account_status       | account_action              | current_acc_status           | new_acc_status               | new_account_status           |
      | OPEN                         | Suspend account (partially) | Open                         | Suspended partially          | SUSPENDED PARTIALLY          |
      | OPEN                         | Suspend account (fully)     | Open                         | Suspended                    | SUSPENDED                    |
      | OPEN                         | Restrict some transactions  | Open                         | Some transactions restricted | SOME TRANSACTIONS RESTRICTED |
      | ALL_TRANSACTIONS_RESTRICTED  | Suspend account (partially) | All transactions restricted  | Suspended partially          | SUSPENDED PARTIALLY          |
      | ALL_TRANSACTIONS_RESTRICTED  | Suspend account (fully)     | All transactions restricted  | Suspended                    | SUSPENDED                    |
      | ALL_TRANSACTIONS_RESTRICTED  | Restrict some transactions  | All transactions restricted  | Some transactions restricted | SOME TRANSACTIONS RESTRICTED |
      | SOME_TRANSACTIONS_RESTRICTED | Suspend account (partially) | Some transactions restricted | Suspended partially          | SUSPENDED PARTIALLY          |
      | SOME_TRANSACTIONS_RESTRICTED | Suspend account (fully)     | Some transactions restricted | Suspended                    | SUSPENDED                    |
      | SOME_TRANSACTIONS_RESTRICTED | Remove restrictions         | Some transactions restricted | Open                         | OPEN                         |
      | SUSPENDED_PARTIALLY          | Unsuspend account           | Suspended partially          | Open                         | OPEN                         |
      | SUSPENDED                    | Unsuspend account           | Suspended                    | Open                         | OPEN                         |

  @exec-manual @test-case-id-1327436479
  Scenario: As senior admin I cannot partially suspend or restrict or remove transaction restrictions on an account with outstanding closure requests
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    # create a closure request task:
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_CLOSURE_REQUEST | 100000001    |              |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |
    When I click the "Change status" button
    Then I am presented with the "Change account status KP" screen
    When I select the "Suspend account (partially)" option
    Then I am presented with the "Change your update and confirm" screen
    When I click the "Apply" button
    And I enter text on "Enter some comments" box
    But there is an outstanding closure request for this <Account ID>
    Then I am presented with the error summary "You cannot partially suspend an account with outstanding closure requests"
