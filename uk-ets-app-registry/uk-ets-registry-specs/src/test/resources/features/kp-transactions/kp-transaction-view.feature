@functional-area-kp-transactions

Feature: KP transactions - KP transaction View

  Epic: Transactions
  Version: 1.3 (12/02/2020)
  Story: (& 5.2.2) as user, I can view any transaction or transaction proposal in the Registry

  # Screens:
  # "View Transaction" Is the 5.2.2  at pg 42 Screen 1: View transaction details

  @test-case-id-758623796
  Scenario Outline: As admin user I have the correct transaction details visibility for defined transaction status and AR access rights
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type    | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                     | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT  | OPERATOR_HOLDING_ACCOUNT | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "<sign_in_user>" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value          |
      | Originating Country Code | GB                   |
      | Account ID               | 100000001            |
      | Claimed by               | 100000001            |
      | Initiated by             | 100000002            |
      | Transaction type         | Internal Transfer    |
      | Transaction status       | <transaction_status> |
      | Commitment period        | 2                    |
      | Acquiring account        | GB-100-1000-1-94     |
      | Transferring account     | GB-100-1001-1-89     |
      | Unit type                | RMU                  |
      | Quantity to issue        | 40                   |
      | Environmental activity   | Afforestation        |
    Then I am presented with the "Registry dashboard" screen
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                      |
      | transaction details label          | Transaction details                                                                                                              |
      | transaction ID                     | Transaction ID: GB100000001                                                                                                      |
      | transaction status                 | <expected_status_label>                                                                                                          |
      | transaction details content        | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 RMU Acquiring account GB-100-1000-1-94 |
      | quantity to transfer content       | Unit type Original CP Applicable CP Quantity to transfer Project or activity RMU CP2 CP2 40 Afforestation and reforestation      |
      | History & comments area            | No items added.                                                                                                                  |
      | Warning and error messages content | No items added.                                                                                                                  |
    And The page "<exp_cancel_button>" the "Cancel transaction" text

    Examples:
      | sign_in_user    | transaction_status         | expected_status_label      | exp_cancel_button |
      | senior admin    | Awaiting approval          | AWAITING APPROVAL          | does not contain  |
      | senior admin    | Delayed                    | DELAYED                    | contains          |
      | senior admin    | Proposed                   | PROPOSED                   | does not contain  |
      | senior admin    | Rejected                   | REJECTED                   | does not contain  |
      | senior admin    | Accepted                   | ACCEPTED                   | does not contain  |
      | junior admin    | Awaiting approval          | AWAITING APPROVAL          | does not contain  |
      
    @exec-manual
    Examples:
      | sign_in_user    | transaction_status         | expected_status_label      | exp_cancel_button |
      | junior admin    | Delayed                    | DELAYED                    | contains          |
      | junior admin    | Proposed                   | PROPOSED                   | does not contain  |
      | junior admin    | Checked no discrepancy     | CHECKED NO DISCREPANCY     | does not contain  |
      | junior admin    | Checked discrepancy        | CHECKED DISCREPANCY        | does not contain  |
      | junior admin    | Completed                  | COMPLETED                  | does not contain  |
      | junior admin    | Terminated                 | TERMINATED                 | does not contain  |
      | junior admin    | Rejected                   | REJECTED                   | does not contain  |
      | junior admin    | Cancelled                  | CANCELLED                  | does not contain  |
      | junior admin    | Accepted                   | ACCEPTED                   | does not contain  |
      | junior admin    | STL checked no discrepancy | STL CHECKED NO DISCREPANCY | does not contain  |
      | senior admin    | Checked no discrepancy     | CHECKED NO DISCREPANCY     | does not contain  |
      | senior admin    | Checked discrepancy        | CHECKED DISCREPANCY        | does not contain  |
      | senior admin    | Completed                  | COMPLETED                  | does not contain  |
      | senior admin    | Terminated                 | TERMINATED                 | does not contain  |
      | senior admin    | STL checked no discrepancy | STL CHECKED NO DISCREPANCY | does not contain  |
      | senior admin    | Cancelled                  | CANCELLED                  | does not contain  |
      | senior admin    | STL checked discrepancy    | STL CHECKED DISCREPANCY    | does not contain  |
      | senior admin    | Revered                    | REVERSED                   | does not contain  |
      | junior admin    | STL checked discrepancy    | STL CHECKED DISCREPANCY    | does not contain  |
      | read only admin | Awaiting approval          | AWAITING APPROVAL          | does not contain  |
      | junior admin    | Revered                    | REVERSED                   | does not contain  |
      | read only admin | Delayed                    | DELAYED                    | contains          |
      | read only admin | Proposed                   | PROPOSED                   | does not contain  |
      | read only admin | Checked no discrepancy     | CHECKED NO DISCREPANCY     | does not contain  |
      | read only admin | Checked discrepancy        | CHECKED DISCREPANCY        | does not contain  |
      | read only admin | Completed                  | COMPLETED                  | does not contain  |
      | read only admin | Terminated                 | TERMINATED                 | does not contain  |
      | read only admin | Rejected                   | REJECTED                   | does not contain  |
      | read only admin | Cancelled                  | CANCELLED                  | does not contain  |
      | read only admin | Accepted                   | ACCEPTED                   | does not contain  |
      | read only admin | STL checked no discrepancy | STL CHECKED NO DISCREPANCY | does not contain  |
      | read only admin | STL checked discrepancy    | STL CHECKED DISCREPANCY    | does not contain  |
      | read only admin | Revered                    | REVERSED                   | does not contain  |

  @test-case-id-758623881
  Scenario Outline: As a non admin user I have the correct transaction details visibility for defined transaction status and AR access rights
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type    | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                     | Party Holding 1 | OPEN           | 1500 | AAU   | false      | true      | 2500     | 3999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT  | OPERATOR_HOLDING_ACCOUNT | Party Holding 2 | OPEN           | 1000 | AAU   | false      | true      | 4000     | 4999   | CP1      | CP1      |             | AAU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <acq_account_ac_right> | 100000001 |
      | ACTIVE | <tr_account_ac_right>  | 100000002 |
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value          |
      | Originating Country Code | GB                   |
      | Account ID               | 100000001            |
      | Claimed by               | 100000001            |
      | Initiated by             | 100000002            |
      | Transaction type         | Internal Transfer    |
      | Transaction status       | <transaction_status> |
      | Commitment period        | 1                    |
      | Acquiring account        | GB-100-1000-1-94     |
      | Transferring account     | GB-100-1001-1-89     |
      | Unit type                | AAU                  |
      | Quantity to issue        | 40                   |
      | Environmental activity   |                      |
    Then I am presented with the "Registry dashboard" screen
    When I access "GB100000001" in "transaction-details"
    Then I am presented with the "Transaction details" screen
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                      |
      | transaction details label          | Transaction details                                                                                                              |
      | transaction ID                     | Transaction ID: GB100000001                                                                                                      |
      | transaction status                 | <expected_status_label>                                                                                                          |
      | transaction details content        | Transaction account GB-100-1001-1-89 Transaction type Internal transfer Total quantity 40 AAU Acquiring account GB-100-1000-1-94 |
      | quantity to transfer content       | Unit type Original CP Applicable CP Quantity to transfer Project or activity AAU CP1 CP1 40                                      |
      | History & comments area            | No items added.                                                                                                                  |
      | Warning and error messages content | No items added.                                                                                                                  |
    And The page "<exp_cancel_button>" the "Cancel transaction" text

    @sampling-smoke
    Examples:
      | transaction_status | acq_account_ac_right | tr_account_ac_right  | expected_status_label | exp_cancel_button |
      | Delayed            | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | DELAYED               | contains          |
      | Awaiting approval  | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | AWAITING APPROVAL     | does not contain  |
      | Completed          | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | COMPLETED             | does not contain  |
      | Terminated         | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | TERMINATED            | does not contain  |
      | Rejected           | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | REJECTED              | does not contain  |
      | Cancelled          | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | CANCELLED             | does not contain  |
      
    Examples:
      | transaction_status | acq_account_ac_right | tr_account_ac_right  | expected_status_label | exp_cancel_button |
      | Awaiting approval  | INITIATE             | INITIATE             | AWAITING APPROVAL     | does not contain  |
      | Delayed            | INITIATE             | INITIATE             | DELAYED               | contains          |
      | Awaiting approval  | READ_ONLY            | READ_ONLY            | AWAITING APPROVAL     | does not contain  |
      | Delayed            | READ_ONLY            | READ_ONLY            | DELAYED               | contains          |
      | Delayed            | APPROVE              | INITIATE             | DELAYED               | contains          |
      | Awaiting approval  | APPROVE              | APPROVE              | AWAITING APPROVAL     | does not contain  |

    @exec-manual
    Examples:
      | transaction_status         | acq_account_ac_right | tr_account_ac_right  | expected_status_label      | exp_cancel_button |
      | Awaiting approval          | READ_ONLY            | INITIATE_AND_APPROVE | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | READ_ONLY            | INITIATE_AND_APPROVE | DELAYED                    | contains          |
      | Awaiting approval          | READ_ONLY            | INITIATE             | AWAITING APPROVAL          | does not contain  |
      | Awaiting approval          | READ_ONLY            | APPROVE              | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | READ_ONLY            | INITIATE             | DELAYED                    | contains          |
      | Delayed                    | READ_ONLY            | APPROVE              | DELAYED                    | contains          |
      | Delayed                    | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | DELAYED                    | contains          |
      | Delayed                    | INITIATE_AND_APPROVE | INITIATE             | DELAYED                    | contains          |
      | Proposed                   | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | PROPOSED                   | does not contain  |
      | Checked no discrepancy     | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | CHECKED NO DISCREPANCY     | does not contain  |
      | Checked discrepancy        | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | CHECKED DISCREPANCY        | does not contain  |
      | Accepted                   | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | ACCEPTED                   | does not contain  |
      | STL checked no discrepancy | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | STL CHECKED NO DISCREPANCY | does not contain  |
      | STL checked discrepancy    | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | STL CHECKED DISCREPANCY    | does not contain  |
      | Revered                    | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | REVERSED                   | does not contain  |
      | Awaiting approval          | INITIATE_AND_APPROVE | INITIATE_AND_APPROVE | AWAITING APPROVAL          | does not contain  |
      | Awaiting approval          | INITIATE_AND_APPROVE | INITIATE             | AWAITING APPROVAL          | does not contain  |
      | Awaiting approval          | INITIATE_AND_APPROVE | APPROVE              | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | INITIATE_AND_APPROVE | APPROVE              | DELAYED                    | contains          |
      | Awaiting approval          | INITIATE_AND_APPROVE | READ_ONLY            | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | INITIATE_AND_APPROVE | READ_ONLY            | DELAYED                    | contains          |
      | Awaiting approval          | INITIATE             | INITIATE_AND_APPROVE | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | INITIATE             | INITIATE_AND_APPROVE | DELAYED                    | contains          |
      | Awaiting approval          | INITIATE             | APPROVE              | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | INITIATE             | APPROVE              | DELAYED                    | contains          |
      | Awaiting approval          | INITIATE             | READ_ONLY            | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | INITIATE             | READ_ONLY            | DELAYED                    | contains          |
      | Awaiting approval          | APPROVE              | INITIATE_AND_APPROVE | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | APPROVE              | INITIATE_AND_APPROVE | DELAYED                    | contains          |
      | Awaiting approval          | APPROVE              | INITIATE             | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | APPROVE              | APPROVE              | DELAYED                    | contains          |
      | Awaiting approval          | APPROVE              | READ_ONLY            | AWAITING APPROVAL          | does not contain  |
      | Delayed                    | APPROVE              | READ_ONLY            | DELAYED                    | contains          |
