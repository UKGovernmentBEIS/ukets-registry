@functional-area-kp-transactions

Feature: KP transactions - KP Units issue

  Epic: KP Transactions
  Version: 1.0 (31/01/2020)
  Story: (& 5.1.1) As a Senior-Admin, I can submit a proposal for issuing KP units for a specific Commitment period under a specific national account
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20KP%20Transactions.docx?version=4&modificationDate=1580481329000&api=v2

  # Screens:
  # "Propose KP units, period" Is the 5.1.1 at pg 12 Screen 1: Specify the commitment period and the acquiring account
  # "Propose KP units, units"  Is the 5.1.1 at pg 13 Screen 2: Specify the units to issue
  # "Propose KP units, review" Is the 5.1.1 at pg 14 Screen 3: Review and sign
  # "Propose KP units, result" Is the 5.1.1 at pg 15 Screen 4: Result

  # info: balance calculated: unit_block_end - unit black start + 1.
  # info: unit blocks values ranges should not overlap each other.

  Background:
    # create gov account(s)
    * the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | CLOSED         | 101  | RMU   | true       | true      | 2100     | 2200   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 3 | OPEN           | 1501 | RMU   | true       | true      | 3000     | 4500   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    * I sign in as "senior admin" user
    * I have created 1 "senior" administrators
    * the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 1      | AAU       | 80       |               | 10       |
      | 1      | RMU       | 50       | Afforestation | 20       |

  @exec-manual @test-case-id-83611324560
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units period screen I must fill mandatory fields
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    When I click the "Continue" button
    Then I see an error summary with "Please select an acquiring account"
    And I see an error detail for field "event-name" with content "Error: Please select an acquiring account."

  @sampling-smoke @test-case-id-83611324574
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units period screen I cannot see a CLOSED account
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    When I select the "Commitment period option 1" option
    Then The page "contains" the "GB-100-1000-1-94" text
    But The page "does not contain" the "GB-100-1002-1-84" text

  @exec-manual @test-case-id-83611324587
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units period screen I can navigate to next screen
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                           |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue |

  @exec-manual @test-case-id-83611324603
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units period screen I can navigate to previous screen
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                           |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue |
    When I click the "Back" button
    Then I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |

  @exec-manual @test-case-id-83611324623
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units period screen I can navigate to next screen with correct data
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |

  @exec-manual @test-case-id-83611324641
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units units screen I must fill mandatory fields
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    When I click the "Continue" button
    Then I see an error summary with "Please select the unit type"

  @sampling-smoke @test-case-id-83611324661
  Scenario: When I access Propose KP units units screen I must specify at least one non zero quantity entering 1 value only
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    When I click the "Continue" button
    And I click the "unit type 100000001" button
    And I enter value "0" in "issue quantity 100000001" field
    When I click the "Continue" button
    Then I see an error summary with "At least one non-zero quantity must be specified"
    
  @exec-manual @test-case-id-83611324684
  Scenario: As senior admin when I access Propose KP units units screen I can navigate to next screen
    # KP Administration screen / Internal Transfer
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    And I click the "unit type 100000001" button
    And I enter value "20" in "issue quantity 100000001" field
    And I click the "Continue" button
    # ensure correct current page data
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                |
      | Check and sign your proposal label | Check and sign your proposal                               |
      | Units to issue headers             | Unit type Limit Issued Pending Remaining Quantity to issue |
      | Units to issue content             | AAU 80 10 0 20                                             |
    And The page "contains" the "GB-100-1000-1-94 - Party Holding Account 1000" text
    When I click the "Back" link
    Then I see the following fields having the values:
      | fieldName                | field_value                                                                 |
      | Unit type table headers  | Select Unit Type Limit Issued Pending Remaining                             |
      | Unit type table results  | AAU Quantity to issue 80 10 0 RMU - Afforestation and reforestation 50 20 0 |
      | issue quantity 100000001 | 20                                                                          |

  @sampling-smoke @test-case-id-83611324718
  Scenario: The quantity of AAUs issued must not exceed allowed quantity for Commitment Period
      # KP Administration screen / Internal Transfer
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    # AAU value set:
    And I click the "unit type 100000001" button
    And I enter value "71" in "issue quantity 100000001" field
    And I click the "Continue" button
    Then I see an error summary with "The quantity of AAUs issued must not exceed allowed quantity for the Commitment Period"

  @sampling-smoke @test-case-id-83611324741
  Scenario: The quantity of RMUs issued must not exceed allowed quantity for that LULUCF activity type and Commitment Period
    # KP Administration screen / Internal Transfer
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    # RMU value set:
    And I click the "unit type 100000002" button
    And I enter value "31" in "issue quantity 100000002" field
    And I click the "Continue" button
    Then I see an error summary with "The quantity of RMUs issued for each LULUCF activity type must not exceed the allowed quantity for that LULUCF activity type and Commitment Period"

  @test-case-id-71687624606
  Scenario Outline: quantity must be a positive number without decimal places
    # KP Administration screen / Internal Transfer
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    And I click the "unit type 100000001" button
    And I enter value "<value>" in "issue quantity 100000001" field
    And I click the "Continue" button
    Then I see an error summary with "The quantity must be a positive number without decimal places"

    Examples:
      | value   |
      | 1.5     |
      | 10.99   |
      | 100.001 |
      | aaa     |
      | *       |

  @sampling-smoke @test-case-id-83611324794
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units review screen I can finalise operation
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    When I click the "unit type 100000001" button
    And I enter value "10" in "issue quantity 100000001" field
    And I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    When I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName     | field_value                                                |
      | Units headers | Unit type Limit Issued Pending Remaining Quantity to issue |
      | Units content | AAU 80 10 0 70 10                                          |
    And The page "contains" the "GB-100-1000-1-94" text
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "KP Administration" screen
    # transaction id is now generated:
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                            |
      | 4 eyes principle notification content | The 4 eyes principle applies - the transaction must first be approved by another user. |
      | proposal status message               | KP units issuance proposal submitted                                                   |
    And The page "contains" the "Transaction ID:" text

  @test-case-id-83611324832
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units review screen when I finalise operation a transaction task is generated
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                                     |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue           |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining                 |
      | Unit type table results                                     | AAU 80 10 0 70 RMU - Afforestation and reforestation 50 20 0 30 |
    And I click the "unit type 100000001" button
    And I enter value "10" in "issue quantity 100000001" field
    And I click the "Continue" button
    And I click the "Continue" button
    # ensure correct current page data
    Then I see the following fields having the values:
      | fieldName              | field_value                                                |
      | Units to issue headers | Unit type Limit Issued Pending Remaining Quantity to issue |
      | Units to issue content | AAU 80 10 0 70 10                                          |
    And The page "contains" the "GB-100-1000-1-94 - Party Holding Account 1000" text
    # back links check
    When I click the "Back" button
    And I click the "Back" button
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "Select the unit type to specify the quantity to issue" text
    Then I see the following fields having the values:
      | fieldName                | field_value |
      | issue quantity 100000001 | 10          |
    When I click the "Back" button
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "Specify the commitment period and the acquiring account" text
    And The page "contains" the "GB-100-1000-1-94 - Party Holding Account 1000" text
    # change links check
    When I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "Select the unit type to specify the quantity to issue" text
    When I click the "Continue" button
    And I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    When I click the "Change Commitment period and acquiring account" link
    Then I am presented with the "KP Administration" screen
    And I select the "Acquiring account option GB-100-1001-1-89 - Party Holding Account 1001" option
    When I click the "Continue" button
    And The page "contains" the "Select the unit type to specify the quantity to issue" text
    When I click the "Continue" button
    And I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "GB-100-1001-1-89 - Party Holding Account 1001" text
    When I click the "Change Units to issue" link
    When I click the "unit type 100000002" button
    And I enter value "15" in "issue quantity 100000002" field
    When I click the "Continue" button
    And I click the "Continue" button
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "GB-100-1001-1-89 - Party Holding Account 1001" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                                |
      | Units to issue headers | Unit type Limit Issued Pending Remaining Quantity to issue |
      | Units to issue content | RMU - Afforestation and reforestation 50 20 0 30 15        |
    # submit
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                            |
      | 4 eyes principle notification content | The 4 eyes principle applies - the transaction must first be approved by another user. |
      | proposal status message               | KP units issuance proposal submitted                                                   |
    And The page "contains" the "Transaction ID:" text
    # ensure transaction task is generated
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"

  @exec-manual @test-case-id-83611324913
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units review screen I can finalise operation Error for blank password
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                           |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining       |
      | Unit type table results                                     | AAU 80 10 0 RMU - Afforestation 50 20 0               |
    And I click the "unit type 100000001" button
    And I enter value "10" in "issue quantity 100000001" field
    Then I see the following fields having the values:
      | fieldName     | field_value    |
      | fields values | correct values |
    # Error for blank password
    When I click the "Continue" button
    Then I see an error summary with "Enter password"
    And I see an error detail for field "Enter password" with content "Enter password"

  @exec-manual @test-case-id-83611324940
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units review screen I can finalise operation Error for invalid password
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    And The page "contains" the "Issue KP Units" text
    Then I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                           |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue |
      | Unit type table headers                                     | Select Unit Type Limit Issued Pending Remaining       |
      | Unit type table results                                     | AAU 80 10 0 RMU - Afforestation 50 20 0               |
    And I click the "unit type 100000001" button
    And I enter value "10" in "issue quantity 100000001" field
    Then I see the following fields having the values:
      | fieldName     | field_value    |
      | fields values | correct values |
    # Error for invalid password
    When I enter my password
    And  I click the "Continue" button
    But  my password is invalid
    Then I see an error summary with "Invalid password or one time code"
    And I see an error detail for field "Enter password" with content "Invalid password"

  @exec-manual @test-case-id-83611324970
  Scenario: As a REG ADMIN SENIOR when I access Propose KP units review screen I can finalise the operation when Transactions to accounts with other pending transactions of same type are not allowed
    # create transaction and transaction task for current test case needs (pending transaction)
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
    # KP Administration screen / Internal Transfer:
    Given I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    Then I see the following fields having the values:
      | fieldName                           | field_value                                             |
      | Specify the commitment period label | Specify the commitment period and the acquiring account |
    And I select the "Commitment period option 1" option
    And I select the "Acquiring account option GB-100-1000-1-94 - Party Holding Account 1000" option
    When I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName                                                   | field_value                                           |
      | Select the unit type to specify the quantity to issue label | Select the unit type to specify the quantity to issue |
    Then I see the following fields having the values:
      | fieldName               | field_value                                     |
      | Unit type table headers | Select Unit Type Limit Issued Pending Remaining |
      | Unit type table results | AAU 80 10 0 RMU - Afforestation 50 20 0         |
    And I click the "unit type 100000001" button
    And I enter value "10" in "issue quantity 100000001" field
    # Error Transactions to accounts with other pending transactions of the same type are not allowed
    When I click the "Continue" button
    Then I see an error summary with "Transactions to accounts with other pending transactions of the same type are not allowed"
