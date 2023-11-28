@functional-area-ets-transactions

Feature: ETS transactions - Return excess auction & Tasks

  Epic: ETS Transactions
  Version: v1.7 (04/03/2021)
  Story: 5.6.2 (Extension) Transaction - Return excess auction & Tasks

  # Information for Return excess auction:
  # From UK Auction delivery account To UK Auction account
  # acquiring_account_status / transferring_account_status can be open or SUSPENDED or SUSPENDED_PARTIALLY or SOME_TRANSACTIONS_RESTRICTED for Return excess auction transaction

  @test-case-id-86522941
  Scenario Outline: As SRA or AR user I can trigger a Return excess auction transaction task and units are correctly transferred upon task approval
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status                    | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | <transferring_account_status> | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | <acquiring_account_status>    | 1000 | ALLOWANCE | true       | true      | 3001     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "<sign_in_role>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    When I select the "Return of excess auction" option
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Choose the number of units" text
    And I enter value "100" in "quantity" field
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Check and sign your proposal" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "You have submitted a transaction proposal" text
    And The page "contains" the "The request ID is" text
    And The page "contains" the "The transaction ID is" text
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
    When I "check" the "Select All Result Rows" checkbox
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
    # the task is now APPROVED, check that the units were removed from the UK_AUCTION_DELIVERY_ACCOUNT account:
    And The page "contains" the "You have approved the proposed transaction" text
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                        | field_value                                |
      | Total available quantity content | Total available quantity: 1,900 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0                 |
    # check that the units were added to the UK_AUCTION_ACCOUNT:
    When I access "1011" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                        | field_value                                |
      | Total available quantity content | Total available quantity: 1,100 Allowances |
      | Total reserved quantity content  | Total reserved quantity: 0                 |

    @sampling-smoke
    Examples:
      | sign_in_role | acquiring_account_status | transferring_account_status |
      | senior admin | OPEN                     | OPEN                        |

    Examples:
      | sign_in_role | acquiring_account_status     | transferring_account_status  |
      | senior admin | SOME_TRANSACTIONS_RESTRICTED | OPEN                         |
      | senior admin | SOME_TRANSACTIONS_RESTRICTED | SOME_TRANSACTIONS_RESTRICTED |
      | senior admin | OPEN                         | SUSPENDED                    |
      | senior admin | OPEN                         | SUSPENDED_PARTIALLY          |
      | senior admin | OPEN                         | SOME_TRANSACTIONS_RESTRICTED |

    @exec-manual
    Examples:
      | sign_in_role                                                 | acquiring_account_status | transferring_account_status |
      | auth rep with INITIATE or INITIATE AND APPROVE access rights | OPEN                     | OPEN                        |

  @test-case-id-86524050 @exec-manual
  Scenario Outline: As SRA user I cannot trigger a Return excess auction transaction task when acquiring or transferring account is ALL TRANSACTIONS RESTRICTED
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status             | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | <acquiring_account>    | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | <transferring_account> | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName           | field_value         |
      | Propose transaction | [null] |

    Examples:
      | acquiring_account           | transferring_account        |
      | ALL_TRANSACTIONS_RESTRICTED | OPEN                        |
      | OPEN                        | ALL_TRANSACTIONS_RESTRICTED |

  @test-case-id-86524944 @exec-manual
  Scenario: As SRA user I cannot trigger a Return excess auction transaction task when transferring account has closed status
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | CLOSED     | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | OPEN       | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button

  @test-case-id-86514945 @exec-manual
  Scenario: As JRA user I cannot trigger a Return excess auction transaction task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | OPEN       | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button

  @test-case-id-86385941 @exec-manual
  Scenario Outline: As SRA user I cannot trigger a Return excess auction transaction task when I set invalid values
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | OPEN       | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | OPEN       | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    And I select the "Return of excess auction" option
    And I enter value "<quantity>" in "quantity" field
    And I click the "Continue" button
    Then I see an error summary with "<error_summary>"

    Examples:
      | quantity | error_summary                                                 |
      | 0        | At least one non-zero quantity must be specified              |
      | 100.1    | The quantity must be a positive number without decimal places |
      | -1       | The quantity must be a positive number without decimal places |
      | [empty]  | The quantity must be a positive number without decimal places |
      | 2001     | The proposed quantity is more than the available quantity     |

  @test-case-id-86845948 @exec-manual
  Scenario: As SRA user while implementing a Return excess auction transaction I see data retained while navigating backwards
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status                    | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | <transferring_account_status> | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | <acquiring_account_status>    | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "<sign_in_role>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    And I select the "Return of excess auction" option
    And I enter value "100" in "quantity" field
    And I click the "Continue" button
    When I click the "Back" button
    And I see the following fields having the values:
      | fieldName | field_value |
      | quantity  | 100         |

  @test-case-id-86306946 @exec-manual
  Scenario: As SRA user I can cancel a Return excess auction transaction proposal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type       | account_name     | acc_status                    | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | commit_period | acc_holder_type | type_label                  |
      | UK-100-1016-0-17 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_DELIVERY_ACCOUNT | Auction Delivery | <transferring_account_status> | 2000 | ALLOWANCE | true       | true      | 1        | 2000   | CP0      | CP0      |             | ALLOWANCE | 1016      | 0             | ORGANISATION    | UK Auction Delivery Account |
      | UK-100-1011-0-42 | PARTY_HOLDING_ACCOUNT | UK_AUCTION_ACCOUNT          | Auction          | <acquiring_account_status>    | 1000 | ALLOWANCE | true       | true      | 2999     | 4000   | CP0      | CP0      |             | ALLOWANCE | 1011      | 0             | GOVERNMENT      | UK Auction Account          |
    When I sign in as "<sign_in_role>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1016" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    And I select the "Return of excess auction" option
    And I enter value "100" in "quantity" field
    And I click the "Continue" button
    And I click the "Cancel" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "contains" the "Are you sure you want to cancel this proposal and return to the account page?" text
    When I click the "Cancel proposal" button
    Then I am presented with the "View account overview" screen
