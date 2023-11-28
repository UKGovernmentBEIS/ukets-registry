@functional-area-ets-reconciliation

Feature: ETS transactions - ETS Reconciliation

  Epic: UK Transaction log
  Version: v1.0 (01/06/2020)

  # (A) Introduction

  # The ETS reconciliation is a technical process that takes place automatically -every night- between the Registry and
  # the Transaction Log. It involves the following business entities:
  # * ETS units (UK Allowances)
  # * ETS accounts (Aircraft/Operator Holding Account, UK Government accounts etc.)
  # * ETS transactions (Transfer of Allowances, Auction Delivery, Issuance of Allowances etc.)

  # Please note that ETS reconciliation is a separate process from the Kyoto Protocol (KP) reconciliation. The latter is
  # out of scope for this feature file.

  #  During an ETS reconciliation, the following steps take place:
  # | # | System          | Action                                                                                                |
  # | 1 | Registry        | Stops transaction processing.                                                                         |
  # | 2 | Registry        | Identifies the accounts which were involved in transactions after the last successful reconciliation. |
  # | 3 | Registry        | Calculates the totals per unit type per account.                                                      |
  # | 4 | Registry        | Sends these totals to the Transaction Log.                                                            |
  # | 5 | Transaction Log | Performs the same calculation.                                                                        |
  # | 6 | Transaction Log | Compares its own against the Registry's totals.                                                       |
  # | 7 | Transaction Log | Responds the result to the Registry.                                                                  |
  # | 8 | Registry        | Blocks the accounts whose totals do not match.                                                        |

  # (C) References

  # 1. UK ETS Reconciliation
  # https://admin.ukets.net/docs/architecture-description/0.1/process-view/uk_reconciliation.html

  # 2. Traceability matrix with the requirements, as reviewed and agreed with BEIS
  # https://admin.ukets.net/docs/supporting-documentation/0.1/other/transaction-log-thoughts.html#_requirement_t02

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"
    Given The following accounts have been successfully created
      | Account               | Account Type                      | Name           | Status | Available holdings for transactions | Purpose                     |
      | UK-100-900000025-2-60 | Operator Holding Account          | Factory 1      | OPEN   | 10000 UK Allowances                 | For transferring allowances |
      | UK-100-900000930-2-94 | Aircraft Operator Holding Account | Aviation 2     | OPEN   | 40000 UK Allowances                 | For transferring allowances |
      | UK-100-900000140-2-67 | UK Total Quantity Account         | Total quantity | OPEN   | 0                                   | For issuing allowances      |
    And The following users are available
      | User name    | Role                          | Purpose                                                                   |
      | Senior 1     | Senior Registry Administrator | For transferring allowances                                               |
      | Senior 2     | Senior Registry Administrator | For transferring allowances                                               |
      | Authority 1  | Authority                     | For issuing allowances                                                    |
      | Authority 2  | Authority                     | For issuing allowances                                                    |
      | System admin | System Admin                  | Internal technical role for manually launching the reconciliation process |

  @exec-manual @test-case-id-19270616979
  Scenario: As a senior admin i can see an alert regarding the ets reconciliation status
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I "see" an alert with the latest "ets reconciliation" status

  @exec-manual @backend @test-case-id-19270616986
  Scenario: Main success scenario Successful reconciliation
    When I propose a transaction "Issuance of allowances" as user "Authority 1"
    And I approve the transaction as user "Authority 2"
      | Transaction | Transferring Account | Acquiring Account | Units              |
      | UK1000001   | Total quantity       | Total quantity    | 5000 UK Allowances |
    And I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000002   | Factory 1            | Aviation 2        | 200 UK Allowances |
    And In "Transactions" search screen I see the following result
      | Transaction | Status    |
      | UK1000001   | Completed |
      | UK1000002   | Completed |
    Then I log in as user "System admin"
    And I select "ETS Administration"
    And I select "Reconciliation Administration"
    And I select "Start reconciliation"
    # wait for reconciliation
    And I wait until reconciliation
    And I select "Refresh"
    And I see the "Last Reconciliation"
      | Reconciliation | Status    |
      | 10000          | COMPLETED |

  @exec-manual @backend @test-case-id-19270617011
  Scenario: Extension 1 Reconciliation fails due to missing account in Registry
    When I manually modify the account number in the Registry database
      | SQL statement                                                           |
      | update account set identifier = 999999999 where identifier = 900000025; |
    And I manually execute a new reconciliation process
    Then I see the "Last Reconciliation"
      | Reconciliation | Status       |
      | 10001          | INCONSISTENT |
    And In "Account" search screen I see the following result
      | Account               | Status                      |
      | UK-100-900000025-2-60 | ALL_TRANSACTIONS_RESTRICTED |
    And In "Account" details screen I see the following event
      | "Account 900000025 has been restricted from all transactions due to failed reconciliation 10001." |

  @exec-manual @backend @test-case-id-19270617027
  Scenario: Extension 2 Reconciliation fails due to missing account in Transaction Log
    When I manually modify the account number in the Transaction Log database
    And I repeat the same steps with the previous scenario
    Then In "Account" details screen I see the following event
      | "Account 900000025 has been restricted from all transactions due to failed reconciliation 10002." |

  @exec-manual @backend @test-case-id-19270617035
  Scenario: Extension 3 Reconciliation fails due to missing units in Registry
    When I manually modify the account holdings in the Registry database
      | SQL statement                                                              |
      | update unit_block set account_id = 999999999 where account_id = 900000930; |
    And I manually execute a new reconciliation process
    Then I see the "Last Reconciliation"
      | Reconciliation | Status       |
      | 10003          | INCONSISTENT |
    And In "Account" search screen I see the following result
      | Account               | Status                      |
      | UK-100-900000930-2-94 | ALL_TRANSACTIONS_RESTRICTED |
    And In "Account" details screen I see the following event
      | "Account 900000930 has been restricted from all transactions due to failed reconciliation 10003." |

  @exec-manual @backend @test-case-id-19270617051
  Scenario: Extension 4 Reconciliation fails due to missing units in Transaction Log
    When I manually modify the account holdings in the Transaction Log database
    And I repeat the same steps with the previous scenario
    Then In "Account" details screen I see the following event
      | "Account 900000930 has been restricted from all transactions due to failed reconciliation 10004." |

  @exec-manual @backend @test-case-id-19270617059
  Scenario: Extension 5 Reconciliation fails due to different totals Registry
    When I manually modify the account holdings in the Registry database
      | SQL statement                                                                     |
      | update unit_block set start_block = start_block + 1 where account_id = 900000140; |
    And I manually execute a new reconciliation process
    Then I see the "Last Reconciliation"
      | Reconciliation | Status       |
      | 10005          | INCONSISTENT |
    And In "Account" search screen I see the following result
      | Account               | Status                      |
      | UK-100-900000140-2-67 | ALL_TRANSACTIONS_RESTRICTED |
    And In "Account" details screen I see the following event
      | "Account 900000140 has been restricted from all transactions due to failed reconciliation 10005." |

  @exec-manual @backend @test-case-id-19270617075
  Scenario: Extension 6 Reconciliation fails due to different totals Transaction Log
    When I manually modify the account holdings in the Transaction Log database
    And I repeat the same steps with the previous scenario
    And In "Account" details screen I see the following event
      | "Account 900000140 has been restricted from all transactions due to failed reconciliation 10006." |

  @exec-manual @backend @test-case-id-19270617083
  Scenario: Extension 7 Reconciliation fails due to different unit types
    When I manually modify the account holdings in the Registry database
      | SQL statement                                                                                       |
      | update unit_block set unit_type = 'AAU' where unit_type = 'ALLOWANCE' where account_id = 900000025; |
    And I manually execute a new reconciliation process
    Then I see the "Last Reconciliation"
      | Reconciliation | Status       |
      | 10007          | INCONSISTENT |
    And In "Account" search screen I see the following result
      | Account               | Status                      |
      | UK-100-900000025-2-60 | ALL_TRANSACTIONS_RESTRICTED |
    And In "Account" details screen I see the following event
      | "Account 900000025 has been restricted from all transactions due to failed reconciliation 10007." |
