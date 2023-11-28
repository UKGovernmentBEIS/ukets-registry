@functional-area-ets-transactions
@exec-manual
@backend

Feature: ETS transactions - Transaction Log - Transaction Business Checks

  Epic: UK Transaction log
  Version: v1.0 (01/06/2020)
  Story: 4.2.2 Check business rules for transaction

  # (A) Introduction

  # During every ETS transaction, the following business checks are being executed in the Transaction Log:
  # | # | Description                                                                                                                                       | Error Code |
  # | 1 | The transferring account number does not exist in the Transaction Log.                                                                            |    10001   |
  # | 2 | The acquiring account number does not exist in the Transaction Log.                                                                               |    10002   |
  # | 3 | The requested quantity exceeds the account balance of the transferring account in the Transaction Log for the unit type being transferred.|    10003   |
  # | 4 | The quantity of allowances issued must not exceed the issuance limit set in the Transaction Log.                                                  |    10004   |
  # | 5 | The transferring account in the Transaction Log does not hold the serial blocks being transferred.                                                |    10005   |

  # If any of these checks fails, then:
  # 1. The system stops the check processing, i.e. no further checks are being executed.
  # 2. The system marks the transaction as terminated.
  # 3. The system responds to the Registry with status "STL checked discrepancy" including the business error code.
  # 4. The transfer of units does not take place, i.e. the units remain held by the transferring account.

  # This feature file contains a "Background" section with steps common to all scenarios. The first scenario depicts the
  # "happy path", i.e. a successfully completed transaction. Then, the rest scenarios cause the transaction business
  # checks to fail in the Transaction Log, resulting to a terminated transaction.

  # (B) Unit Testing

  # The business checks are fully covered by unit testing as presented in the following table.
  # | # | Implementation                       | Unit Test                                | Methods | Branches | Coverage |
  # | 1 | CheckTransferringAccountExists       | CheckTransferringAccountExistsTest       |    2    |     2    |   100%   |
  # | 2 | CheckAcquiringAccountExists          | CheckAcquiringAccountExistsTest          |    2    |     2    |   100%   |
  # | 3 | CheckRequestedQuantityExceedsBalance | CheckRequestedQuantityExceedsBalanceTest |    2    |     2    |   100%   |
  # | 4 | CheckIssuanceLimits                  | CheckIssuanceLimitsTest                  |    2    |     6    |   100%   |
  # | 5 | CheckSerialNumbers                   | CheckSerialNumbersTest                   |    3    |     4    |   100%   |

  # (C) Technical References

  # 1. UK ETS Transaction Log Overview
  # https://admin.ukets.net/docs/architecture-description/0.1/logical-view/transaction-log.html

  # 2. Traceability matrix with the requirements, as reviewed and agreed with BEIS
  # https://admin.ukets.net/docs/supporting-documentation/0.1/other/transaction-log-thoughts.html#_introducing_a_uktl

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"
    Given The following accounts have been successfully created
      | Account               | Account Type                      | Name           | Status | Available holdings for transactions | Purpose                     |
      | UK-100-900000025-2-60 | Operator Holding Account          | Factory 1      | OPEN   | 10000 UK Allowances                 | For transferring allowances |
      | UK-100-900000930-2-94 | Aircraft Operator Holding Account | Aviation 2     | OPEN   | 40000 UK Allowances                 | For transferring allowances |
      | UK-100-900000140-2-67 | UK Total Quantity Account         | Total quantity | OPEN   | 0                                   | For issuing allowances      |
    And The following users are available
      | User name   | Role                          | Purpose                     |
      | Senior 1    | Senior Registry Administrator | For transferring allowances |
      | Senior 2    | Senior Registry Administrator | For transferring allowances |
      | Authority 1 | Authority                     | For issuing allowances      |
      | Authority 2 | Authority                     | For issuing allowances      |
    And The following issuance limit has been set in both the Registry and the Transaction Log
      | Phase                 | Issuance Limit |
      | Phase 1 (2021 - 2030) | 1000000        |

  @test-case-id-27286518541
  Scenario: Main success scenario Transfer of Allowances
    When I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000000   | Factory 1            | Aviation 2        | 200 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status    |
      | UK1000000   | Completed |
    And In the account holdings I see the following units
      | Account Name | Available holdings for transactions |
      | Factory 1    | 9800 UK Allowances                  |
      | Aviation 2   | 40200 UK Allowances                 |

  # Remarks on main success scenario:
  #   This scenario is being executed automatically by the E2E testing infrastructure. Its full details and steps
  #   are available in the file "features/ets-transactions/request_transfers.feature".
  #
  #   The status "Completed" shown in Registry, indicates that the transaction has reached the Transaction Log,
  #   it has been validated by the Transaction Log and a successful response has been returned to the Registry.

  @test-case-id-27286518565
  Scenario: Extension 1 The transferring account number does not exist in Transaction Log
    When I manually modify the transferring account number in the Transaction Log database
      | SQL statement                                                           |
      | update account set identifier = 999999999 where identifier = 900000025; |
    And I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000001   | Factory 1            | Aviation 2        | 200 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status     |
      | UK1000001   | Terminated |
    And In "Transaction" details screen I see the following event
      | TL10001: "The transferring account number does not exist in the Transaction Log" |

  @test-case-id-27286518584
  Scenario: Extension 2 The acquiring account number does not exist in Transaction Log
    When I manually modify the acquiring account number in the Transaction Log database
      | SQL statement                                                           |
      | update account set identifier = 999999999 where identifier = 900000930; |
    And I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000002   | Factory 1            | Aviation 2        | 200 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status     |
      | UK1000002   | Terminated |
    And In "Transaction" details screen I see the following event
      | TL10002: "The acquiring account number does not exist in the Transaction Log" |

  @test-case-id-27286518603
  Scenario: Extension 3 The requested quantity exceeds the account balance of the transferring account in the Transaction Log for unit type being transferred
    When I manually modify the transferring account holdings in the Transaction Log database
      | SQL statement                                                              |
      | update unit_block set account_id = 999999999 where account_id = 900000025; |
    And I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000003   | Factory 1            | Aviation 2        | 200 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status     |
      | UK1000003   | Terminated |
    And In "Transaction" details screen I see the following event
      | TL10003: "The requested quantity exceeds the account balance of the transferring account in the Transaction Log for the unit type being transferred" |

  @test-case-id-27286518622
  Scenario: Extension 4 The quantity of allowances issued must not exceed the issuance limit set in Transaction Log
    When I manually modify the issuance limit in the Transaction Log database
      | SQL statement                                                                      |
      | update allocation_phase set consumed_phase_cap = initial_phase_cap where code = 1; |
    And I propose a transaction "Issuance of allowances" as user "Authority 1"
    And I approve the transaction as user "Authority 2"
      | Transaction | Transferring Account | Acquiring Account | Units              |
      | UK1000004   | Total quantity       | Total quantity    | 5000 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status     |
      | UK1000004   | Terminated |
    And In "Transaction" details screen I see the following event
      | TL10004: "The quantity of allowances issued must not exceed the issuance limit set in the Transaction Log" |

  @test-case-id-27286518641
  Scenario: Extension 5 The transferring account in the Transaction Log does not hold serial blocks being transferred
    When I manually modify the serial numbers of the transferring account holdings in the Transaction Log database
      | SQL statement                                                                                                                |
      | update unit_block set start_block = start_block + 199999999, end_block = end_block + 199999999 where account_id = 900000025; |
    And I propose a transaction "Transfer allowances" as user "Senior 1"
    And I approve the transaction as user "Senior 2"
      | Transaction | Transferring Account | Acquiring Account | Units             |
      | UK1000005   | Factory 1            | Aviation 2        | 200 UK Allowances |
    Then In "Transactions" search screen I see the following result
      | Transaction | Status     |
      | UK1000005   | Terminated |
    And In "Transaction" details screen I see the following event
      | TL10005: "The transferring account in the Transaction Log does not hold the serial blocks being transferred" |
