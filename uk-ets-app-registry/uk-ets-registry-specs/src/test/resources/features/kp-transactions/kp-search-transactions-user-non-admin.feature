@functional-area-kp-transactions

Feature: KP transactions - KP Search transactions user Non Admin

  Epic: Transactions
  Version: 1.2 (31/01/2020)
  Story: (& 5.2.1) as user, I can search and access to the transactions
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20registry%20-%20Transactions%20-%20BRs.xlsx?version=5&modificationDate=1580481329000&api=v2

  # Screens:
  # "Search Transactions" Is the 5.2.1 at pg 37 Screen 1: Search Transactions

  @security-url-ad-hoc-get @test-case-id-45865123032
  Scenario: Security front end test for transaction access via ad hoc url get
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 0    | RMU   | true       | true      | 7000     | 6999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "enrolled" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    # transactions test data (set Acquiring and Transferring accounts as defined at aforementioned account creation test data step)
    # As acquiring account AR I can only see the completed transactions, As transferring account AR I can see all transactions
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
    # navigate to transactions search, set filters values and search the AWAITING_APPROVAL transaction
    Then I am presented with the "Registry dashboard" screen
    # as non authorised representative to Acquiring and Transferring accounts, i cannot see the transaction via url ad hoc get
    And I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    When I get "transaction-details/GB100000001" screen
    Then I am presented with the "Sign in" screen

  @exec-manual @test-case-id-79978622922
  Scenario Outline: As a non admin user according to my access rights I have appropriate visibility of Transactions menu
    Given I sign in as "<user_name>" user
    Then I am presented with the "Registry dashboard" screen
    And The page "<transaction_in_page>" the "Transactions" text

    Examples:
      | user_name  | transaction_in_page |
      | registered | does not contain    |
      | validated  | does not contain    |
      | enrolled   | contains            |

  @exec-manual @test-case-id-45865123084
  Scenario: As enrolled user I can access Transactions screen
    Given I sign in as "enrolled" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    And I click the "Hide filters" button
    And I click the "Show filters" button
    And I click the "Advanced search" link
    # filters labels check
    Then I see the following fields having the values:
      | fieldName                          | field_value                  |
      | Transaction ID label               | Transaction ID               |
      | Transaction Type label             | Transaction Type             |
      | Transaction Status label           | Transaction Status           |
      | Transaction last update date label | Transaction last update date |
      | Acquiring account type label       | Acquiring account type       |
      | Transferring account type label    | Transferring account type    |
      | Unit type label                    | Unit type                    |
      | Initiator user ID label            | Initiator user ID            |
      | Approver user ID label             | Approver user ID             |
      | Transaction proposal date label    | Transaction proposal date    |
    # filter values check
    Then I see the following fields having the values:
      | fieldName                          | field_value |
      | Transaction ID                     | [empty]     |
      | dropdown Transaction Type          | [empty]     |
      | dropdown Transaction Status        | [empty]     |
      | dropdown Acquiring account type    | [empty]     |
      | dropdown Transferring account type | [empty]     |
      | dropdown Unit type                 | [empty]     |
      | Transaction last update date From  | [empty]     |
      | Transaction last update date To    | [empty]     |
      | Transferring account number        | [empty]     |
      | Acquiring account number           | [empty]     |
      | Transaction proposal date From     | [empty]     |
      | Transaction proposal date To       | [empty]     |
      | Initiator user ID                  | [empty]     |
      | Approver user ID                   | [empty]     |

  @test-case-id-79978622976
  Scenario Outline: As authorised representative I have appropriate visibility of transactions results
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status      | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | <acc_status_acq>    | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | <acc_status_transf> | 0    | RMU   | true       | true      | 7000     | 6999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "enrolled" user
    And I have created 1 "senior" administrators
    And I am "<acq_account_user_status>" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I am "<transf_account_user_status>" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000002"
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    # transactions test data (set Acquiring and Transferring accounts as defined at aforementioned account creation test data step)
    # As acquiring account AR I can only see the completed transactions, As transferring account AR I can see all transactions
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
    # navigate to transactions search, set filters values and search the AWAITING_APPROVAL transaction
    Then I am presented with the "Registry dashboard" screen
    And I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    And I click the "Advanced search" link
    And I enter value "GB100000001" in "Transaction ID" field
    When I click the "Search" button
    # results number check - 1 or 0 transactions should be visible according to current test data
    Then I see "<results>" elements of "Transactions list returned result rows"

    Examples:
      | acq_account_user_status | transf_account_user_status | acc_status_acq | acc_status_transf | results |
      # acquiring account ACTIVE - transferring account ACTIVE
      | ACTIVE                  | ACTIVE                     | OPEN           | OPEN              | 1       |

    @exec-manual
    Examples:
      | acq_account_user_status | transf_account_user_status | acc_status_acq               | acc_status_transf           | results |
      # acquiring account ACTIVE - transferring account ACTIVE
      | ACTIVE                  | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | ACTIVE                  | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | ACTIVE                  | ACTIVE                     | TRANSFER_PENDING             | OPEN                        | 1       |
      | ACTIVE                  | ACTIVE                     | CLOSED                       | OPEN                        | 1       |
      | ACTIVE                  | ACTIVE                     | SUSPENDED                    | OPEN                        | 1       |
      | ACTIVE                  | ACTIVE                     | SUSPENDED_PARTIALLY          | OPEN                        | 1       |
      | ACTIVE                  | ACTIVE                     | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | ACTIVE                  | ACTIVE                     | TRANSFER_PENDING             | SUSPENDED                   | 1       |
      | ACTIVE                  | ACTIVE                     | CLOSED                       | CLOSED                      | 0       |
      | ACTIVE                  | ACTIVE                     | SUSPENDED                    | CLOSED                      | 0       |
      | ACTIVE                  | ACTIVE                     | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account SUSPENDED - transferring account ACTIVE
      | SUSPENDED               | ACTIVE                     | OPEN                         | OPEN                        | 1       |
      | SUSPENDED               | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | SUSPENDED               | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | SUSPENDED               | ACTIVE                     | TRANSFER_PENDING             | OPEN                        | 1       |
      | SUSPENDED               | ACTIVE                     | CLOSED                       | OPEN                        | 1       |
      | SUSPENDED               | ACTIVE                     | SUSPENDED                    | OPEN                        | 1       |
      | SUSPENDED               | ACTIVE                     | SUSPENDED_PARTIALLY          | OPEN                        | 1       |
      | SUSPENDED               | ACTIVE                     | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | SUSPENDED               | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | SUSPENDED               | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | SUSPENDED               | ACTIVE                     | TRANSFER_PENDING             | SUSPENDED                   | 1       |
      | SUSPENDED               | ACTIVE                     | CLOSED                       | CLOSED                      | 0       |
      | SUSPENDED               | ACTIVE                     | SUSPENDED                    | CLOSED                      | 0       |
      | SUSPENDED               | ACTIVE                     | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account ACTIVE - transferring account SUSPENDED
      | ACTIVE                  | SUSPENDED                  | OPEN                         | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | TRANSFER_PENDING             | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | CLOSED                       | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | SUSPENDED                    | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | SUSPENDED_PARTIALLY          | OPEN                        | 0       |
      | ACTIVE                  | SUSPENDED                  | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | SUSPENDED                  | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | SUSPENDED                  | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | ACTIVE                  | SUSPENDED                  | TRANSFER_PENDING             | SUSPENDED                   | 0       |
      | ACTIVE                  | SUSPENDED                  | CLOSED                       | CLOSED                      | 0       |
      | ACTIVE                  | SUSPENDED                  | SUSPENDED                    | CLOSED                      | 0       |
      | ACTIVE                  | SUSPENDED                  | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account SUSPENDED - transferring account SUSPENDED
      | SUSPENDED               | SUSPENDED                  | OPEN                         | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | TRANSFER_PENDING             | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | CLOSED                       | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | SUSPENDED                    | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | SUSPENDED_PARTIALLY          | OPEN                        | 0       |
      | SUSPENDED               | SUSPENDED                  | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | SUSPENDED               | SUSPENDED                  | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | SUSPENDED               | SUSPENDED                  | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | SUSPENDED               | SUSPENDED                  | TRANSFER_PENDING             | SUSPENDED                   | 0       |
      | SUSPENDED               | SUSPENDED                  | CLOSED                       | CLOSED                      | 0       |
      | SUSPENDED               | SUSPENDED                  | SUSPENDED                    | CLOSED                      | 0       |
      | SUSPENDED               | SUSPENDED                  | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account ACTIVE - transferring account REQUESTED
      | ACTIVE                  | REQUESTED                  | OPEN                         | OPEN                        | 1       |
      | ACTIVE                  | REQUESTED                  | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | ACTIVE                  | REQUESTED                  | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | ACTIVE                  | REQUESTED                  | TRANSFER_PENDING             | OPEN                        | 1       |
      | ACTIVE                  | REQUESTED                  | CLOSED                       | OPEN                        | 1       |
      | ACTIVE                  | REQUESTED                  | SUSPENDED                    | OPEN                        | 1       |
      | ACTIVE                  | REQUESTED                  | SUSPENDED_PARTIALLY          | OPEN                        | 1       |
      | ACTIVE                  | REQUESTED                  | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | REQUESTED                  | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | REQUESTED                  | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | ACTIVE                  | REQUESTED                  | TRANSFER_PENDING             | SUSPENDED                   | 1       |
      | ACTIVE                  | REQUESTED                  | CLOSED                       | CLOSED                      | 0       |
      | ACTIVE                  | REQUESTED                  | SUSPENDED                    | CLOSED                      | 0       |
      | ACTIVE                  | REQUESTED                  | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account ACTIVE - transferring account REJECTED
      | ACTIVE                  | REJECTED                   | OPEN                         | OPEN                        | 1       |
      | ACTIVE                  | REJECTED                   | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | ACTIVE                  | REJECTED                   | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | ACTIVE                  | REJECTED                   | TRANSFER_PENDING             | OPEN                        | 1       |
      | ACTIVE                  | REJECTED                   | CLOSED                       | OPEN                        | 1       |
      | ACTIVE                  | REJECTED                   | SUSPENDED                    | OPEN                        | 1       |
      | ACTIVE                  | REJECTED                   | SUSPENDED_PARTIALLY          | OPEN                        | 1       |
      | ACTIVE                  | REJECTED                   | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | REJECTED                   | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | ACTIVE                  | REJECTED                   | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | ACTIVE                  | REJECTED                   | TRANSFER_PENDING             | SUSPENDED                   | 1       |
      | ACTIVE                  | REJECTED                   | CLOSED                       | CLOSED                      | 0       |
      | ACTIVE                  | REJECTED                   | SUSPENDED                    | CLOSED                      | 0       |
      | ACTIVE                  | REJECTED                   | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |
      # acquiring account REQUESTED - transferring account ACTIVE
      | REQUESTED               | ACTIVE                     | OPEN                         | OPEN                        | 1       |
      | REQUESTED               | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | OPEN                        | 0       |
      | REQUESTED               | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | OPEN                        | 0       |
      | REQUESTED               | ACTIVE                     | TRANSFER_PENDING             | OPEN                        | 1       |
      | REQUESTED               | ACTIVE                     | CLOSED                       | OPEN                        | 1       |
      | REQUESTED               | ACTIVE                     | SUSPENDED                    | OPEN                        | 1       |
      | REQUESTED               | ACTIVE                     | SUSPENDED_PARTIALLY          | OPEN                        | 1       |
      | REQUESTED               | ACTIVE                     | OPEN                         | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | REQUESTED               | ACTIVE                     | ALL_TRANSACTIONS_RESTRICTED  | ALL_TRANSACTIONS_RESTRICTED | 0       |
      | REQUESTED               | ACTIVE                     | SOME_TRANSACTIONS_RESTRICTED | SUSPENDED                   | 0       |
      | REQUESTED               | ACTIVE                     | TRANSFER_PENDING             | SUSPENDED                   | 1       |
      | REQUESTED               | ACTIVE                     | CLOSED                       | CLOSED                      | 0       |
      | REQUESTED               | ACTIVE                     | SUSPENDED                    | CLOSED                      | 0       |
      | REQUESTED               | ACTIVE                     | SUSPENDED_PARTIALLY          | TRANSFER_PENDING            | 0       |

  @exec-manual @test-case-id-45865123278
  Scenario: As user when I search for a transaction I see results in correct sorting
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 0    | RMU   | true       | true      | 7000     | 6999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "enrolled" user
    And I have created 1 "senior" administrators
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000002"
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
    And I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    And I click the "Advanced search" link
    And I enter value "GB100000001" in "Transaction ID" field
    When I click the "Search" button
    Then I see "1" elements of "Transactions list returned result rows"
    And The results are sorted correctly
