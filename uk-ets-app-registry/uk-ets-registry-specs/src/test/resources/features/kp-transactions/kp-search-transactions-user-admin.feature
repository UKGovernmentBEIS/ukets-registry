@functional-area-kp-transactions

Feature: KP transactions - KP Search transactions user Admin

  Epic: Transactions
  Version: 1.2 (31/01/2020)
  Story: (& 5.2.1) as user, I can search and access to the transactions
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20registry%20-%20Transactions%20-%20BRs.xlsx?version=5&modificationDate=1580481329000&api=v2

  # Screens:
  # "Search Transactions" Is the 5.2.1 at pg 37 Screen 1: Search Transactions
  # Admin users do not have to be ARs of other accounts. They have such access by default.

  @exec-manual @test-case-id-52009422152
  Scenario Outline: As user according to my access rights I can see Transactions menu
    Given I sign in as "<user_name>" user
    Then I am presented with the "Registry dashboard" screen
    And The page "<transaction_in_page>" the "Transactions" text

    Examples:
      | user_name       | transaction_in_page |
      | junior admin    | contains            |
      | senior admin    | contains            |
      | read only admin | contains            |

  @exec-manual @test-case-id-52009422165
  Scenario Outline: As admin user I can access Transactions screen
    Given I sign in as "<user_name>" user
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

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @test-case-id-52009422212
  Scenario Outline: As admin user I can filter transaction list using Transaction ID filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                          |
      | transaction details label    | Transaction details                                                                                                                                  |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                          |
      | transaction status           | AWAITING APPROVAL                                                                                                                                    |
      | transaction details content  | Transaction account Party Holding Account 1001 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                          |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422255
  Scenario Outline: As admin user I can filter transaction list using Transaction Type filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @test-case-id-52009422301
  Scenario Outline: As admin user I can filter transaction list using Transaction Status filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422348
  Scenario Outline: As admin user I can filter transaction list using Transaction last update date From filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422390
  Scenario Outline: As admin user I can filter transaction list using Transaction last update date To filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422432
  Scenario Outline: As user I can filter transaction list using Transferring Account filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @test-case-id-52009422473
  Scenario: As admin user I can filter transaction list using Acquiring Account filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "read only admin" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

  @exec-manual @test-case-id-52009422513
  Scenario Outline: As admin user I can filter transaction list using Acquiring Account Type filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @test-case-id-52009422559
  Scenario: As admin user I can filter transaction list using Transferring Account Type filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "read only admin" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

  @exec-manual @test-case-id-52009422604
  Scenario Outline: As admin user I can filter transaction list using Unit Type filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422651
  Scenario Outline: As admin user I can filter transaction list using Transaction proposal date From filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422693
  Scenario Outline: As admin user I can filter transaction list using Transaction proposal date To filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-52009422735
  Scenario Outline: As admin user I can filter transaction list using Initiator or Approver user ID filter
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "<user_name>" user
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
      | Transferring account     | GB-100-1000-1-94  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                |
      | transaction details label    | Transaction details                                                                                                                        |
      | transaction ID               | Transaction ID: GB100000001                                                                                                                |
      | transaction status           | AWAITING APPROVAL                                                                                                                          |
      | transaction details content  | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account Party Holding Account 1000 |
      | quantity to transfer content | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation                |
    When I click the "Back to transaction list" link
    Then I am presented with the "Search Transactions" screen

    Examples:
      | user_name       |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-94208322924
  Scenario: As admin user I can initialize filters after a search
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And I sign in as "senior admin" user
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
    And I click the "Transactions" link
    Then I am presented with the "Search Transactions" screen
    And I click the "Advanced search" link
    # set filters values
    And I enter value "GB100000001" in "Transaction ID" field
    And I select the "Transaction Type: Issuance of KP units" option
    And I select the "Transaction Status: Awaiting approval" option
    And I enter value "01/01/2020" in "Transaction last update date From" field
    And I enter value "01/01/2050" in "Transaction last update date To" field
    And I enter value "01/01/2020" in "Transaction proposal date From" field
    And I enter value "01/01/2050" in "Transaction proposal date To" field
    And I enter value "GB-100-1000-1-94" in "Transferring account number" field
    And I enter value "GB-100-1000-1-94" in "Acquiring account number" field
    And I select the "Acquiring account type: All KP government accounts" option
    And I select the "Transferring account type: All KP government accounts" option
    And I select the "Unit type: Removal Unit" option
    # apply search
    When I click the "Search" button
    # check filters after search (before filters clean)
    Then I see the following fields having the values:
      | fieldName                          | field_value                   |
      | Transaction ID                     | GB100000001                   |
      | dropdown Transaction Type          | 1: IssueOfAAUsAndRMUs         |
      | dropdown Transaction Status        | 1: AWAITING_APPROVAL          |
      | dropdown Acquiring account type    | 7: ALL_KP_GOVERNMENT_ACCOUNTS |
      | dropdown Transferring account type | 7: ALL_KP_GOVERNMENT_ACCOUNTS |
      | dropdown Unit type                 | 2: RMU                        |
      | Transaction last update date From  | 2020-01-01                    |
      | Transaction last update date To    | 2050-01-01                    |
      | Transferring account number        | GB-100-1000-1-94              |
      | Acquiring account number           | GB-100-1000-1-94              |
      | Transaction proposal date From     | 2020-01-01                    |
      | Transaction proposal date To       | 2050-01-01                    |
    # check filters after filter clear
    And I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                          | field_value |
      | Transaction ID                     | [empty]     |
      | dropdown Transaction Type          | 0: null     |
      | dropdown Transaction Status        | 0: null     |
      | dropdown Acquiring account type    | 0: null     |
      | dropdown Transferring account type | 0: null     |
      | dropdown Unit type                 | 0: null     |
      | Transaction last update date From  | [empty]     |
      | Transaction last update date To    | [empty]     |
      | Transferring account number        | [empty]     |
      | Acquiring account number           | [empty]     |
      | Transaction proposal date From     | [empty]     |
      | Transaction proposal date To       | [empty]     |
    # results number reset
    Then I see "0" elements of "Transactions list returned result rows"

  @exec-manual @test-case-id-52009422854
  Scenario Outline: As admin user I can download as CSV file transaction list
    Given I sign in as "<user_name>" user
    Then I am presented with the "Registry dashboard" screen
    And the "Transaction" link is available  # menu option
    And I select the "Transaction" link
    Then I am presented with the "Search Transactions" screen
    And I click the "Search" button
    And I am presented with an transaction list
    When I click the "Download as cvs" button
    Then I receive a CVS file with the transaction list

    Examples:
      | user_name       |
      | senior admin    |
      | junior admin    |
      | read only admin |
