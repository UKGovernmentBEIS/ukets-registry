@functional-area-ets-transactions

Feature: ETS transactions - Request transfers

  Epic: ETS Transactions
  Version: v1.2 (01/07/2020)
  Story: 5.6.1 (Extension) Transaction - Transfer allowances

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"

  @test-case-id-57075517992
  Scenario: As SRA I can request a Transfer of allowances
    # issuance caps = issuance limits for the ETS accounts
    Given The following issuance limits have been set:
      | year | issuance_cap |
      | 2021 | 10900        |
      | 2022 | 10000        |
      | 2023 | 10100        |
      | 2024 | 10200        |
      | 2025 | 10300        |
      | 2026 | 10400        |
      | 2027 | 10500        |
      | 2028 | 10600        |
      | 2029 | 10700        |
      | 2030 | 10800        |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    # ALLOWANCE unit types correspond to CP2 only.
    # NOTE: acquiring account query is type_label name case sensitive
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | OPEN       | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT   | Party Holding 1  | OPEN       | 4000 | ALLOWANCE | true       | true      | 4999     | 9000   | CP0      | CP0      |             | ALLOWANCE | 1010      | 0             | GOVERNMENT      | UK Total Quantity Account   |
    # as authority_1 user propose to issue uk allowances
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I enter value "100" in "Quantity" field
    When I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I get a new otp based on the existing secret for the "test_authority_1_user" user
    When I enter value "correct otp for user test_authority_1_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Proposal submitted" text
    # as authority_2 user claim the proposal task and then approve it
    When I sign in as "authority_2" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this proposal" text
    And I get a new otp based on the existing secret for the "test_authority_2_user" user
    When I enter value "correct otp for user test_authority_2_user" in "otp" field
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    And The page "contains" the "You have approved the proposed transaction" text
    # still as authority_2: a. access total quantity account and then
    # b. propose transaction: central transfer of an amount from total quantity account to UK Auction Account
    When I access "1010" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Central transfer" button
    And I click the "continue" button
    When I enter value "150" in "quantity" field
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "choice UK Auction Account 1011" button
    And I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowance 150                                                                               |
      | Acquiring account content                   | Account name or description UK Auction Account 1011                                                                        |
      | Transferring account content                | Account Holder Test Holder Name Account number UK-100-1010-0-47 Account name or description UK Total Quantity Account 1010 |
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    When I enter value "My comment" in "proposal comment" field
    When I get a new otp based on the existing secret for the "test_authority_2_user" user
    And I enter value "correct otp for user test_authority_2_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # as authority user, claim and approve the task
    And I sign in as "authority_3" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this proposal" text
    And I get a new otp based on the existing secret for the "test_authority_3_user" user
    When I enter value "correct otp for user test_authority_3_user" in "otp" field
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    And The page "contains" the "You have approved the proposed transaction" text
    # as authority user, access auction account to transfer (an amount of) allowances to auction delivery account
    When I access "1011" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Transfer of allowances to auction delivery account" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I enter value "110" in "quantity" field
    And I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                          |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowance 110                                                                                         |
      | Acquiring account content                   | Account not in the trusted account list Account number UK-100-1016-0-17 Account name or description UK Auction Delivery Account 1016 |
      | Transferring account content                | Account Holder Test Holder Name Account number UK-100-1011-0-42 Account name or description UK Auction Account 1011                  |
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    When I enter value "My comment" in "proposal comment" field
    And I get a new otp based on the existing secret for the "test_authority_3_user" user
    When I enter value "correct otp for user test_authority_3_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
    # as another authority user, approve the task
    And I sign in as "authority_4" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this proposal" text
    And I get a new otp based on the existing secret for the "test_authority_4_user" user
    When I enter value "correct otp for user test_authority_4_user" in "otp" field
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    # locate the Auction Delivery account and propose transaction will be available for senior admin:
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose a transaction type" text
    When I click the "Transfer allowances" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose the number of units" text
    When I enter value "100" in "quantity" field
    And I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter the following values to the fields:
      | field                             | fieldValue |
      | account user Defined Country Code | UK         |
      | account user Defined Account Type | 100        |
      | account user Defined Account Id   | 50001      |
      | account user Defined Period       | 2          |
      | account user Defined Check Digits | 11         |
    And I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Transferring account" text
    And The page "contains" the "UK-100-1016-0-17" text
    Then I see the following fields having the values:
      | fieldName                                   | field_value                                                                                                                         |
      | unit types and quantity to transfer details | Unit type Quantity to transfer Allowance 100                                                                                        |
      | Acquiring account content                   | Account not in the trusted account list Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 |
      | Transferring account content                | Account Holder Test Holder Name Account number UK-100-1016-0-17 Account name or description UK Auction Delivery Account 1016        |
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    When I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "You have submitted a transaction proposal" text
    # as another senior admin, Claim and Approve Transaction Proposal task
    When I sign in as "senior admin 2" user
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
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approve" text
    Then I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                         |
      | transferring account details | Account Holder Test Holder Name Account number UK-100-1016-0-17 Account name or description UK Auction Delivery Account 1016        |
      | transaction type details     | Transaction type Transfer allowances                                                                                                |
      | unit type details            | Unit type Quantity to transfer Allowance 100                                                                                        |
      | acquiring account details    | Account not in the trusted account list Account number UK-100-50001-2-11 Account name or description Operator holding account 50001 |
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this proposal" text
    And I get a new otp based on the existing secret for the "test_senior_admin 2_user" user
    When I enter value "correct otp for user test_senior_admin 2_user" in "otp" field
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    And The page "contains" the "You have approved the proposed transaction" text

  @exec-manual @test-case-id-57075518465
  Scenario: As an ar I can cancel a delayed ets transfer
    Given There is a "delayed" ets transfer
    And the correct units are reserved from the transferring account
    When I navigate to the transaction details
    And I click the "Cancel transaction" button
    And I confirm the cancellation process
    Then the transaction is "cancelled"
    And the correct units are released back to the transferring account
