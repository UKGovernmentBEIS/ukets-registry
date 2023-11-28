@functional-area-ets-transactions

# deletion transaction: delete units from an "ets" account. Units are transferred to the "uk deletion account".

Feature: ETS transactions - Deletion transaction

  @test-case-id-02349875111
  Scenario Outline: As SRA or AR with correct access rights and correct account status I can delete units from an ets non gov oha or aoha or trading account
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | account_status   | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT   | Deletion account name | OPEN             | 401 | ALLOWANCE | true       | true      | 10000001100 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | <account_type>        | Oha account name      | <account_status> | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Deletion of allowances" button
    And I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter value "20" in "quantity" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "You have submitted a transaction proposal" text
    # sign in as another admin, claim and approve the task
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    # ensure that correct units are subtracted (100-20)
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                             |
      | Available quantity | Total available quantity: 80 Allowances |
    # ensure that the correct units (401) are not added to the deletion account because task is not approved yet
    When I access "10000007" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                              |
      | Available quantity | Total available quantity: 401 Allowances |

    @sampling-smoke
    Examples:
      | user         | account_status | account_type             |
      | senior admin | OPEN           | OPERATOR_HOLDING_ACCOUNT |

    @exec-manual
    Examples:
      | user                                       | account_status      | account_type                      |
      | senior admin                               | SUSPENDED           | OPERATOR_HOLDING_ACCOUNT          |
      | senior admin                               | SUSPENDED           | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | senior admin                               | SUSPENDED           | TRADING                           |
      | senior admin                               | SUSPENDED_PARTIALLY | OPERATOR_HOLDING_ACCOUNT          |
      | ar with INITIATE_AND_APPROVE access rights | OPEN                | OPERATOR_HOLDING_ACCOUNT          |
      | ar with INITIATE access rights             | OPEN                | OPERATOR_HOLDING_ACCOUNT          |

  @test-case-id-02349875109
  Scenario Outline: As SRA or AR with correct access rights and correct account status I can delete units from an ets non gov oha or aoha or trading account
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type | account_name          | account_status   | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT   | Deletion account name | OPEN             | 401 | ALLOWANCE | true       | true      | 10000001100 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | <account_type>        | Oha account name      | <account_status> | 20  | ALLOWANCE | true       | true      | 10000003000 | 10000003019 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     | UK100000002            |
    Given I sign in as "<user>" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value            |
      | Originating Country Code | GB                     |
      | Account ID               | 100000002              |
      | Claimed by               | 100000001              |
      | Initiated by             | 100000002              |
      | Transaction type         | Deletion of Allowances |
      | Commitment period        | 2                      |
      | Acquiring account        | UK-100-10000007-0-26   |
      | Transferring account     | UK-100-10000056-0-72   |
      | Unit type                | ALLOWANCE              |
      | Quantity to issue        | 20                     |
    Then I am presented with the "Registry dashboard" screen
    # ensure that correct units are subtracted (100-20)
    When I access "100000002" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    When I access "10000056" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                 |
      | Available quantity | Total available quantity: 0 |
    # ensure that the correct units (401) are added to the deletion account because task is now approved
    When I access "10000007" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                              |
      | Available quantity | Total available quantity: 421 Allowances |

    @sampling-smoke
    Examples:
      | user         | account_status | account_type             |
      | senior admin | OPEN           | OPERATOR_HOLDING_ACCOUNT |

    @exec-manual
    Examples:
      | user                                       | account_status      | account_type                      |
      | senior admin                               | SUSPENDED           | OPERATOR_HOLDING_ACCOUNT          |
      | senior admin                               | SUSPENDED           | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | senior admin                               | SUSPENDED           | TRADING                           |
      | senior admin                               | SUSPENDED_PARTIALLY | OPERATOR_HOLDING_ACCOUNT          |
      | ar with INITIATE_AND_APPROVE access rights | OPEN                | OPERATOR_HOLDING_ACCOUNT          |
      | ar with INITIATE access rights             | OPEN                | OPERATOR_HOLDING_ACCOUNT          |

  @test-case-id-0234987514 @exec-manual
  Scenario: I can submit another units deletion request upon pending same task
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN           | 401 | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | OPEN           | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    # create already existing same task
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                 | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | DELETION TRANSACTION | 100000001    |              |
    When I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "account_id" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Deletion of allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter value "50" in "quantity" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "An update request has been submitted" text

  @test-case-id-02349875112 @exec-manual
  Scenario Outline: I cannot delete units from an kp or auction delivery or ets gov account
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type   | account_name          | account_status | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT     | Deletion account name | OPEN           | 401 | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | <registry_account_type> | Oha account name      | OPEN           | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "account_id" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "does not contain" the "Deletion of allowances" text

    Examples:
      | registry_account_type |
      | KP ACCOUNT            |
      | AUCTION DELIVERY      |
      | ETS GOV               |

  @test-case-id-02349875113 @exec-manual
  Scenario Outline: I cannot delete units when specified quantity value is not correct
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status | bal              | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN           | 401              | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | OPEN           | <quantity_value> | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "account_id" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And I click the "Deletion of allowances" button
    Then I am presented with the "Account Overview Transactions" screen
    And I enter value "<quantity_value>" in "quantity" field
    When I click the "Continue" button
    Then I am presented with the "Account Overview Transactions" screen
    And I see an error summary with "<error_summary>"

    Examples:
      | quantity_value                    | error_summary                                                                          |
      # more than existing quantity (edge case with decimal precision)
      | oha exact quantity plus .01 units | The requested quantity exceeds the account balance for the unit type being transferred |
      # invalid values
      | !                                 | The quantity must be a positive number without decimal places                          |
      | a                                 | The quantity must be a positive number without decimal places                          |
      | -1                                | The quantity must be a positive number without decimal places                          |
      | 0                                 | The quantity must be a positive number without decimal places                          |
      | 10.1                              | The quantity must be a positive number without decimal places                          |
      | 10.01                             | The quantity must be a positive number without decimal places                          |

  @test-case-id-02349875115  @exec-manual
  Scenario Outline: As sra or jra or read only or authority or ar with incorrect account status I cannot delete account units
    Given the following accounts have been created
      | account_id           | kyoto_account_type    | registry_account_type    | account_name          | account_status   | bal | un_ty     | tr_out_tal | ap_sec_ar | ub_start    | ub_end      | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty  | ub_acc_id | acc__commitm_period | accountHolderType | typeLabel                | sop | reservedForTransaction |
      | UK-100-10000007-0-26 | PARTY_HOLDING_ACCOUNT | UK_DELETION_ACCOUNT      | Deletion account name | OPEN             | 401 | ALLOWANCE | true       | true      | 10000001000 | 10000001500 | CP0      | CP0      |             | ALLOWANCE | 10000007  | 0                   | ORGANISATION      | UK Deletion Account      |     |                        |
      | UK-100-10000056-0-72 | PARTY_HOLDING_ACCOUNT | OPERATOR_HOLDING_ACCOUNT | Oha account name      | <account_status> | 100 | ALLOWANCE | true       | true      | 10000003000 | 10000003099 | CP0      | CP0      |             | ALLOWANCE | 10000056  | 0                   | ORGANISATION      | Operator holding account |     |                        |
    When I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "account_id" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Propose transaction" button
    Then I am presented with the "Account Overview Transactions" screen
    And The page "does not contain" the "Deletion of allowances" text

    Examples:
      | user                                       | account_status               |
      # sign in user
      | junior admin                               | OPEN                         |
      | read only admin                            | OPEN                         |
      | ar with APPROVE access rights              | OPEN                         |
      | ar with READ_ONLY access rights            | OPEN                         |
      # account status for sra
      | senior admin                               | TRANSFER PENDING             |
      | senior admin                               | SOME TRANSACTIONS RESTRICTED |
      | senior admin                               | ALL TRANSACTIONS RESTRICTED  |
      | senior admin                               | CLOSURE PENDING              |
      | senior admin                               | CLOSED                       |
      # account status for ar with correct access rights
      | ar with INITIATE_AND_APPROVE access rights | SUSPENDED                    |
      | ar with INITIATE_AND_APPROVE access rights | TRANSFER PENDING             |
      | ar with INITIATE_AND_APPROVE access rights | SOME TRANSACTIONS RESTRICTED |
      | ar with INITIATE_AND_APPROVE access rights | ALL TRANSACTIONS RESTRICTED  |
      | ar with INITIATE_AND_APPROVE access rights | CLOSURE PENDING              |
      | ar with INITIATE_AND_APPROVE access rights | CLOSED                       |

