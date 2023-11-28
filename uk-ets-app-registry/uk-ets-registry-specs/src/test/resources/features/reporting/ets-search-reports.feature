@functional-area-reporting

Feature: Reporting - ETS search reports

  Epic: Reports
  Version: v0.4 (22/03/2021)
  Story: 4.1 - 4.5 Search Users / Accounts / Transactions / Tasks

  # Available ONLY for the admins: SRA, JRA, RoA

  @test-case-id-8752893803
  Scenario Outline: As AR or Authority I do not have access to the Reports menu
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
	# Tasks
    When I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And The page "does not contain" the "Generate current report" text
	# Accounts
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    And The page "does not contain" the "Generate current report" text
	# Transactions
    When I click the "Transactions" button
    Then I am presented with the "Search Transactions" screen
    And The page "does not contain" the "Generate current report" text

    @sampling-smoke
    Examples:
      | user                      |
      | authorized representative |

    @exec-manual
    Examples:
      | user      |
      | authority |

  @test-case-id-87038569801 @exec-manual
  Scenario Outline: As admin user I have access to the Reports menu
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type    | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                     | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT  | OPERATOR_HOLDING_ACCOUNT | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "<user>" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Transaction status       | Completed         |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
	# Tasks
    When I click the "Tasks" button
    Then I am presented with the "Task List" screen
    Then The page "contains" the "Generate current report" text
	# Accounts
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    Then The page "contains" the "Generate current report" text
	# Transactions
    When I click the "Transactions" button
    Then I am presented with the "Search Transactions" screen
    Then The page "contains" the "Generate current report" text

    Examples:
      | user            |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @test-case-id-29538569039 @exec-manual
  Scenario: As SRA I can execute the Search report
    Given the following accounts have been created
      | account_id       | kyoto_account_type     | registry_account_type    | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PERSON_HOLDING_ACCOUNT | Null                     | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT  | OPERATOR_HOLDING_ACCOUNT | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Transaction status       | Completed         |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
	# Tasks
    When I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Generate current report" text
    When I click the "Generate current report" button
    Then The page "contains" the "You have successfully submitted your request to generate a report." text
	# Accounts
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And The page "contains" the "Generate current report" text
    When I click the "Generate current report" button
    Then The page "contains" the "You have successfully submitted your request to generate a report." text
	# Transactions
    When I click the "Transactions" button
    Then I am presented with the "Search Transactions" screen
    And The page "contains" the "Generate current report" text
    When I click the "Generate current report" button
    Then The page "contains" the "You have successfully submitted your request to generate a report." text
    When I click the "Reports" button
    Then I am presented with the "Reports Downloads" screen
    # ensure menu items are correct
    And The page "contains" the "Standard reports" text
    And The page "contains" the "Download files" text
    # Check on Reports > Downloads > ensure xlsx files are ready for download
    And The page "contains" the "uk_ets_Search_Tasks_" text
    And The page "contains" the "uk_ets_Search_Transactions_" text
    And The page "contains" the "uk_ets_Search_Accounts_" text
    When I click the "latest uk ets Search Tasks xlsx report" link
    Then I see that the "tasks" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
    When I click the "latest uk ets Search Transactions xlsx report" link
    Then I see that the "accounts" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
    When I click the "latest uk ets Search Accounts xlsx report" link
    Then I see that the "transactions" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
     # Check on Reports > Standard
    When I click the "Standard reports" button
    Then I am presented with the "Reports standard" screen
    When I download the "All users" file
    Then I see that the "All users" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
    When I download the "Authorised representatives per account" file
    Then I see that the "Authorised representatives per account" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
    When I download the "Accounts with no ARs or AR nomination" file
    Then I see that the "Accounts with no ARs or AR nomination" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
    When I download the "Orphan Users" file
    Then I see that the "Orphan Users" downloaded "xlsx" file has the content below:
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |
      | placeholderValue1 | placeholderValue2 | placeholderValue3 | placeholderValue4 |