@functional-area-kp-transactions

Feature: KP transactions - KP Units transfer

  Epic: KP Transactions and Transactions (v1.4, 20/02/2020)
  Version: 1.0 (31/01/2020)
  Story: (KP Transactions, &5.2.1) As a AR or Admin, I can transfer KP units. (Transactions, &5.1.1) Propose transaction

  # Screens:
  # "Propose transaction"  				Is the Transactions 5.1.1  at pg 22  Screen  1: 	Propose transaction
  # "Propose transaction, quantity"  	Is the Transactions 5.1.1  at pg 23  Screen  2: 	Propose transaction - Specify quantity
  # "Propose transaction, acquiring"  	Is the Transactions 5.1.1  at pg 25  Screen  3: 	Propose transaction - Specify acquiring account
  # "Propose transaction, review"		Is the Transactions 5.1.1  at pg 27  Screen  4: 	Propose transaction - Review transaction details
  # "Propose transaction, sign"      	Is the Transactions 5.1.1  at pg 29  Screen  5: 	Propose transaction - Sign and submit this proposal
  # "Propose transaction, done"      	Is the Transactions 5.1.1  at pg 31  Screen  6: 	Propose transaction - Done & acknowledge
  # "Propose transaction, cancel"      	Is the Transactions 5.1.1  at pg 32  Screen  7: 	Propose transaction - Cancel

  #  Information: TAL: (trusted account boolean options):
  #  a1. TAL mandatory: transaction outside the tal: false.
  #  a2. TAL optional: transaction outside the tal: true. ( as a result it creates a transaction proposal task in awaiting approval status when I is proposed)
  #  b. second approve:  It is set to true in all scenarios below (as a result 4 eyes principle applied)

  # Information: Internal and External transfers:
  # Internal transfers: from uk-registry to uk-registry (UK, not e.g. JP [japanese]). The acquiring account should be KP, not person holding.
  # External transfer: from uk-registry to non uk-registry (not UK, e.g. JP). The acquiring account should be KP, not person holding.

  # Information: Enrolled AR (authorized representative): should be able to see: the "Propose transaction" button only:
  # if acquiring account in PERSON HOLDING ACCOUNT
  # not the PARTY HOLDING ACCOUNT

  @exec-manual @test-case-id-669754424880
  Scenario Outline: As SRA user I can ensure that I see correct data regarding Inbound transactions
    # create account
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat             | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | NONE                  | Party Holding 2 | <account_status> | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity | consumed |
      | 1      | AAU       | 80       |          | 10       |
      | 1      | AAU       | 50       |          | 20       |
    # create/try to create COMPLETED INBOUND transaction for the account GB-100-1001-1-89 (id 1001)
    Given I have the following Inbound transaction
      | fieldName                          | field_value |
      | acquiringRegistryAccountIdentifier | 1001        |
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                      |
      | Account name label | Account name: Party Holding Account 1001 <acc_info>Change status |
      | Available quantity | Total available quantity: <amount> Assigned Amount Units         |
    # ensure that the transaction has been successfully created
    When I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    And The page "<data_contained>" the "GR200001" text

    Examples:
      | account_status               | amount | data_contained   | acc_info                     |
      | OPEN                         | 1,510  | contains         | OPEN                         |
      | ALL_TRANSACTIONS_RESTRICTED  | 1,510  | contains         | ALL TRANSACTIONS RESTRICTED  |
      | SOME_TRANSACTIONS_RESTRICTED | 1,510  | contains         | SOME TRANSACTIONS RESTRICTED |
      | SUSPENDED_PARTIALLY          | 1,510  | contains         | SUSPENDED PARTIALLY          |
      | SUSPENDED                    | 1,500  | does not contain | SUSPENDED                    |

  @exec-manual @test-case-id-97547925086
  Scenario: As senior admin user I cannot implement a internal transfer proposal when I have zero balance and TAL is optional for Outbound transaction
    # info: balance calculated: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 0    | RMU   | true       | true      | 7000     | 6999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I see an error summary with "The requested quantity exceeds the current account balance for the unit type being transferred"
    When I click the "Back" link
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Back" link
    Then I am presented with the "View account" screen
    When I click the "Back to account list" link
    Then I am presented with the "Account list" screen
    And The page "does not contain" the "Transfer" text

  @test-case-id-97547925124 @exec-manual
  Scenario: As senior admin I cannot implement an internal transfer without Transfer KP units option when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    # no selection for "Transfer Kyoto units" option
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error detail for field "transactionType" with content "Error: Select a transaction type"

  @test-case-id-97547925151
  Scenario: As senior admin I cannot do an internal transfer proposal if I dont click unit type checkbox when TAL is optional for Outbound transaction

    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "At least one non-zero quantity must be specified"

  @test-case-id-97547925181
  Scenario: As senior admin I cannot implement an internal transfer proposal if I dont enter a unit type value when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"

  @test-case-id-669754425053
  Scenario: As senior admin I cannot do an internal transfer proposal if I enter a negative unit type value or a symbol when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "-1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "The quantity must be a positive number without decimal places"

  @test-case-id-97547925249
  Scenario: As senior admin I can continue to Choose acquiring account of internal transfer if I enter correct quantity when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text

  @test-case-id-669754425121
  Scenario Outline: As senior admin I have to complete ALL acquiring account fields for internal transfer of account when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter the following values to the fields:
      | field     | fieldValue      |
      | <field_1> | <field_1_value> |
      | <field_2> | <field_2_value> |
      | <field_3> | <field_3_value> |
      | <field_4> | <field_4_value> |
      | <field_5> | <field_5_value> |
    When I click the "continue" button
    Then I see an error summary with "<error_summary>"

    Examples:
      | field_1_value | field_2_value | field_3_value | field_4_value | field_5_value | field_1                | field_2                | field_3              | field_4           | field_5                | error_summary                                                                                 |
      # empty fields
      | [empty]       | [empty]       | [empty]       | [empty]       | [empty]       | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Enter account number                                                                          |
      | [empty]       | 100           | 1002          | 1             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The country code must be 2 letters long and valid             |
      | GB            | [empty]       | 1002          | 1             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The account type must be 3 digits long and a valid Kyoto type |
      | GB            | 100           | [empty]       | 1             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Enter account number                                                                          |
      | GB            | 100           | 1002          | [empty]       | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The period must be 1 digit long                               |
      | GB            | 100           | 1002          | 1             | [empty]       | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format – The check digits must be specified for UK registry account    |
      # invalid values
      | *             | 100           | 1002          | 1             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The country code must be 2 letters long and valid             |
      | GB            | 100           | @             | 1             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The account ID must be numeric                                |
      | GB            | 100           | 1002          | .             | 84            | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The period must be 1 digit long                               |
      | GB            | 100           | 1002          | 1             | ,             | userDefinedCountryCode | userDefinedAccountType | userDefinedAccountId | userDefinedPeriod | userDefinedCheckDigits | Invalid account number format - The check digits must be up to 2 digits long                  |

  @test-case-id-669754425177 @sampling-smoke
  Scenario: The transaction cannot be completed when transfers outside trusted account list config is false
    # try to transfer from 1001 (with false value of tr_out_tal) account to 1002 account
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    #
    # For GB-100-1002-1-84, set parameter "tr_out_tal" to "false"
    #
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | false      | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | false      | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And I select the "Transfer KP units" option
    When I click the "Continue" button
    Then I see an error summary with "you cannot make transfers to an account that is not on the trusted account list. you can update the transaction rule to allow transfers to accounts that are not on the trusted account list"

  @sampling-smoke @test-case-id-97547925382
  Scenario: As senior admin I cannot choose an acquiring account of PROPOSED account status
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat     | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | PROPOSED | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN     | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I see an error summary with "The acquiring account number does not exist in the registry"

  @test-case-id-97547925422
  Scenario: As senior admin I can specify acquiring account of internal transfer while trying to propose a transaction when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account content                | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001         |
      | Transaction type details                    | Transfer KP units                                                                                                              |
      | unit types and quantity to transfer header  | Unit types and quantity to transfer                                                                                            |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1                                     |
      | acquiring account content                   | Account not in the trusted account list Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
      | Transaction ID value                        | [not empty nor null value]                                                                                                     |

  @exec-manual @test-case-id-97547925479
  Scenario: As senior admin user I can cancel proposal for a transaction proposal of an internal transfer while trying to propose a transaction when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | page main content | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Change transaction type Transfer KP units Unit types and quantity to transfer Change unit types and quantity Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1 Acquiring account Change Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Continue Cancel |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another Registry Administrator |
    When I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Cancel transaction proposal" text

  @exec-manual @test-case-id-97547925538
  Scenario: As senior admin user I can cancel a transaction proposal of an internal transfer while trying to propose a transaction when TAL is optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | page main content | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Change transaction type Transfer KP units Unit types and quantity to transfer Change unit types and quantity Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1 Acquiring account Change Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Continue Cancel |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another Registry Administrator |
    And The page "contains" the "Enter comment" text
    When I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Cancel proposal" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | Propose transaction |

  @exec-manual @test-case-id-97547925601
  Scenario: As senior admin user I can see already set data being retained while trying to implement a internal transaction proposal for TAL optional for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | page main content | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Change transaction type Transfer KP units Unit types and quantity to transfer Change unit types and quantity Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1 Acquiring account Change Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Continue Cancel |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    # back check for data retain
    When I click the "Back" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | page main content | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Change transaction type Transfer KP units Unit types and quantity to transfer Change unit types and quantity Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1 Acquiring account Change Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Continue Cancel |
    When I click the "Back" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName              | field_value |
      | userDefinedCountryCode | GB          |
      | userDefinedAccountType | 100         |
      | userDefinedAccountId   | 1002        |
      | userDefinedPeriod      | 1           |
      | userDefinedCheckDigits | 84          |
    When I click the "Back" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                      | field_value |
      | Enter the quantity to transfer | 1           |

  @test-case-id-669754425513
  Scenario Outline: As senior admin user I can propose an internal kp transfer for TAL optional or mandatory for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal      | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | <tal_mandatory> | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | true            | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Transaction details" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account content                | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001         |
      | Transaction type details                    | Transfer KP units                                                                                                              |
      | unit types and quantity to transfer header  | Unit types and quantity to transfer                                                                                            |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1                                     |
      | acquiring account content                   | Account not in the trusted account list Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
      | Transaction ID value                        | [not empty nor null value]                                                                                                     |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And The page "contains" the "Sign proposal" text
    Then I see the following fields having the values:
      | fieldName                 | field_value                                                                 |
      | sign proposal information | By signing this proposal you confirm that the information above is correct. |
    And I enter the following values to the fields:
      | field            | fieldValue |
      | proposal comment | comment 1  |
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen

    Examples:
      | tal_mandatory |
      | true          |
      | false         |

  @test-case-id-97547925753
  Scenario: As AR enrolled I can implement transaction proposal of internal transfer for TAL optional or mandatory for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    # info: enrolled ar can only see acquiring account of person holding account, not party holding account
    Given the following accounts have been created
      | account_id        | kyoto_account_type     | registry_account_type | account_name     | stat | bal  | un_ty | transfers_outside_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | commitment_period_code | account_holder_id | type_label             |
      | GB-100-1002-1-84  | PARTY_HOLDING_ACCOUNT  | Null                  | Party Holding 1  | OPEN | 1199 | RMU   | true                  | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                      | GOVERNMENT        | Party Holding Account  |
      | GB-121-50032-2-58 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | OPEN | 1500 | RMU   | true                  | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50032     | 1                      | Null              | Person Holding Account |
    # info: the access rights for the authorized representative user are defined in the examples of the current scenario
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "50032" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                    |
      | Transferring account content                | Account Holder Test Holder Name Account number GB-121-50032-2-58 Account name or description Person Holding Account 50032      |
      | Transaction type details                    | Transfer KP units                                                                                                              |
      | unit types and quantity to transfer header  | Unit types and quantity to transfer                                                                                            |
      | unit types and quantity to transfer details | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1                                     |
      | acquiring account content                   | Account not in the trusted account list Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 |
      | Transaction ID value                        | [not empty nor null value]                                                                                                     |
    And I enter the following values to the fields:
      | field            | fieldValue |
      | proposal comment | comment 1  |
    And I get a new otp based on the existing secret for the "test_authorized_representative_user" user
    When I enter value "correct otp for user test_authorized_representative_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "You have submitted a transaction proposal" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "View account" screen

  @test-case-id-669754425660
  Scenario Outline: As senior admin I can implement a transaction proposal of external transfer with or without comment and TAL optional or mandatory for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal    | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | <tal_outside> | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 1500 | RMU   | <tal_outside> | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName                    | field_value                                                                                                            |
      | Transferring account content | Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And The page "contains" the "Check and sign your proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another registry administrator |
    And I enter the following values to the fields:
      | field            | fieldValue |
      | proposal comment | <comment>  |
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "Account Overview Transactions" screen

    @defect-UKETS-4792
    Examples:
      | tal_outside | comment   |
      | true        | [empty]   |
      
    @exec-manual
    Examples:
      | tal_outside | comment   |
      | true        | comment 1 |

  @exec-manual @test-case-id-669754425733
  Scenario Outline: As AR enrolled I can implement transaction proposal of external transfer with or without comment when TAL optional or mandatory for Outbound transaction
    # info: balance calculation: unit_block_end - unit black start + 1.
    # info: unit blocks values ranges should not overlap each other.
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT  | Null                  | Party Holding 1  | OPEN | 1199 | RMU   | <tal_out>  | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | OPEN | 1500 | RMU   | <tal_out>  | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    # info: the access rights for the authorized representative user are defined in the examples of the current scenario
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000002 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    And I select the "Transfer KP units" option
    And I click the "Continue" button
    And The page "contains" the "Select unit type(s) and specify quantity" text
    And I click the "RMU checkbox" button
    And I enter value "1" in "Enter the quantity to transfer" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And I enter the following values to the fields:
      | field                  | fieldValue |
      | userDefinedCountryCode | GB         |
      | userDefinedAccountType | 100        |
      | userDefinedAccountId   | 1002       |
      | userDefinedPeriod      | 1          |
      | userDefinedCheckDigits | 84         |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    And The page "contains" the "Propose transaction for transfer KP units" text
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | page main content | Transferring account Account Holder Test Holder Name Account number GB-100-1001-1-89 Account name or description Party Holding Account 1001 Transaction type Change transaction type Transfer KP units Unit types and quantity to transfer Change unit types and quantity Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 1 Acquiring account Change Acquiring account Account not in the trusted account list Account Holder Test Holder Name Account number GB-100-1002-1-84 Account name or description Party Holding Account 1002 Continue |
    When I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Propose transaction for transfer KP units" text
    And The page "contains" the "Sign and submit this proposal" text
    Then I see the following fields having the values:
      | fieldName                     | field_value                                                              |
      | proposal approval information | Warning This proposal must be approved by another registry administrator |
    And I enter the following values to the fields:
      | field            | fieldValue |
      | proposal comment | comment 1  |
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    When I click the "Back to account details" link
    Then I am presented with the "Account Overview Transactions" screen

    Examples:
      | access_rights        | tal_out |
      | INITIATE_AND_APPROVE | true    |
      | INITIATE_AND_APPROVE | false   |
      | INITIATE             | false   |
      | INITIATE             | true    |

  @test-case-id-669754425809
  Scenario Outline: As senior admin I have correct visibility of Propose transaction button as per account status case of Outbound transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat             | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN             | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | <account_status> | 1500 | RMU   | true       | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    Then I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I am presented with the "Registry dashboard" screen
    When I access "1001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Account name label | Account name: Party Holding Account 1001      |
      | Account status     | <acc_info>                                    |
      | Available quantity | Total available quantity: 1,500 Removal Units |
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Propose transaction | <visibility> |

    Examples:
      | account_status               | acc_info                                   | visibility          |
      | SOME_TRANSACTIONS_RESTRICTED | SOME TRANSACTIONS RESTRICTED Change status | Propose transaction |
      | OPEN                         | OPEN Change status                         | Propose transaction |
      | SUSPENDED_PARTIALLY          | SUSPENDED PARTIALLY Change status          | Propose transaction |
      | SUSPENDED                    | SUSPENDED Change status                    | Propose transaction |
      | TRANSFER_PENDING             | TRANSFER PENDING                           | Propose transaction |
      | ALL_TRANSACTIONS_RESTRICTED  | ALL TRANSACTIONS RESTRICTED Change status  | [null]              |
      | CLOSED                       | CLOSED                                     | [null]              |

  # info: balance calculation: unit_block_end - unit black start + 1.
  # info: unit blocks values ranges should not overlap each other.
  # person holding account is used to test access rights for AR enrolled because enrolled AR cannot see the PARTY HOLDING ACCOUNTS

  @test-case-id-669754425850
  Scenario Outline: As AR enrolled I have correct visibility of Propose transaction button as per account status case of Outbound transaction
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type | account_name     | stat             | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT  | Null                  | Party Holding 1  | OPEN             | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | <account_status> | 1500 | RMU   | <tal_out>  | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000002 |
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Account name label | Account name: Party Holding Account 1000      |
      | Account status     | <acc_info>                                    |
      | Available quantity | Total available quantity: 1,500 Removal Units |
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Propose transaction | <visibility> |

    Examples:
      | account_status               | acc_info                     | visibility          | access_rights        |
      | ALL_TRANSACTIONS_RESTRICTED  | ALL TRANSACTIONS RESTRICTED  | [null]              | INITIATE_AND_APPROVE |
      | SOME_TRANSACTIONS_RESTRICTED | SOME TRANSACTIONS RESTRICTED | [null]              | INITIATE_AND_APPROVE |
      | OPEN                         | OPEN                         | Propose transaction | INITIATE_AND_APPROVE |
