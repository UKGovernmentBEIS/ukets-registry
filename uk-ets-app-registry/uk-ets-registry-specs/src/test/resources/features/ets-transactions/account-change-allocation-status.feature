@functional-area-account-management

# For testing purposes, the following optional parameter set the "current year" to a given value: ie, for 2022:
#	business.property.transaction.allocation-year = 2022
#
# And the current ETS phase begins in 2021

# NOTE: acquiring account query is type_label name case sensitive

Feature: ETS transactions - ETS Transactions Account change allocation status

  Epic: ETS transactions
  Version: 1.0 (03/06/2020)
  Story: (5.5.1)  (Extension) View account (allocation status)
  (5.9.1)	Change allocation status (withhold)
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20ETS%20transactions.docx?version=5&modificationDate=1591185313000&api=v2

  # Users:
  # User_Registered     for a user having the REGISTERED status,
  # User_Validated      for a user having the VALIDATED status,
  # User_Enrolled       for a user having the ENROLLED status,
  # User_RegAdmin		for a Registry Administrator (senior or junior)
  # User_RegAdminSenior for a Senior Registry Administrator
  # User_RegAdminJunior for a Junior Registry Administrator
  # User_RegAdminRO		for a Read-Only Registry Administrator

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"
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
    # create first ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 0                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative1 |
      | compliantEntityEndYear   | 2030                       |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    # sign in as authority user and upload excel file and then issue uk allowances
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    When I choose using "allocationTable" from 'upload-allocation-table' the 'UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "Check the request and submit" text
    And I click the "Submit request" button
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "Request submitted" text
    # issue uk allowances
    And I click the "Propose to issue UK allowances" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I enter value "10" in "Quantity" field
    When I click the "continue" button
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I get a new otp based on the existing secret for the "test_authority_1_user" user
    And I enter value "correct otp for user test_authority_1_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Proposal submitted" text
    # approve as authority 2 user the task regarding the upload and the task regarding the issuance
    And I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    And I sign in as "authority_2" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Checkbox result 1" button
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I get a new otp based on the existing secret for the "test_authority_2_user" user
    When I enter value "correct otp for user test_authority_2_user" in "otp" field
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "You have approved the proposed transaction" text
    And The page "contains" the "The transaction ID is" text
    # claim and approve the upload allocation task
    And I click the "Back to task list" button
    Then I am presented with the "Task List" screen
    And I click the "Checkbox result 1" button
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 2" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    And The page "contains" the "Proposal approved" text
    And The page "contains" the "Request ID" text
    And I click the "Sign out" link
    Then I am presented with the "Sign in" screen

  @test-case-id-66571916800
  Scenario: As senior admin I can change allocation status
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN Change status                           |
      | Available quantity | Total available quantity: 0                  |
    When I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Page main content | allocation status for uk allowances change withhold status allocation status under new entrants reserve year entitlement allocated remaining to be allocated to be returned withhold status exclusion status 2021 1090 0 1090 0 2022 1000 0 1000 0 2023 1010 0 1010 0 2024 1020 0 1020 0 2025 1030 0 1030 0 2026 1040 0 1040 0 2027 1050 0 1050 0 2028 1060 0 1060 0 2029 1070 0 1070 0 2030 1080 0 1080 0 total in phase 10450 0 10450 0 |
    And The page "contains" the "Change withhold status" text
    When I click the "Change withhold status" button
    Then I am presented with the "View Account allocation status" screen
    # previous allocation status
    Then I see the following fields having the values:
      | fieldName         | field_value                                              |
      | page main content | select year change withhold status 2021 allowed withheld |
    # change allocation status
    When I click the "Withheld" button
    And I click the "Continue" button
    Then I see an error summary with "Explain why you are changing the status"
    When I enter value "reason 1" in "Justification" field
    And I click the "Continue" button
    Then I am presented with the "Account allocation status check update request" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                       |
      | page main content | check the update, sign and apply updates change type of update allocation status year withhold status 2021 withheld justification for changing the withhold status reason 1 submit request cancel |
    And I click the "Submit request" button
    # new allocation status
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | Page main content | allocation status for uk allowances change withhold status allocation status under new entrants reserve ! warning there are years with a withheld status. you can see the justification in the history and comments year entitlement allocated remaining to be allocated to be returned withhold status exclusion status 2021 1090 0 1090 0 withheld 2022 1000 0 1000 0 2023 1010 0 1010 0 2024 1020 0 1020 0 2025 1030 0 1030 0 2026 1040 0 1040 0 2027 1050 0 1050 0 2028 1060 0 1060 0 2029 1070 0 1070 0 2030 1080 0 1080 0 total in phase 10450 0 10450 0 |

  @test-case-id-01346616721
  Scenario Outline: According to admin type I have the correct visibility of Change allocation status button
    Given I sign in as "<user_role>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | <account_name_label>        |
      | Available quantity | Total available quantity: 0 |
    When I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    And The page "<contains>" the "Change withhold status" text

    Examples:
      | user_role       | contains         | account_name_label                           |
      | senior admin    | contains         | Account name: Operator holding account 50001 |
      | junior admin    | does not contain | Account name: Operator holding account 50001 |
      | read only admin | does not contain | Account name: Operator holding account 50001 |

  @test-case-id-66571916876
  Scenario: As an AR I can read only the allocation status
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN                                         |
      | Available quantity | Total available quantity: 0                  |
    When I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                        |
      | Page main content | allocation status for uk allowances allocation status under new entrants reserve year entitlement allocated remaining to be allocated to be returned withhold status exclusion status 2021 1090 0 1090 0 2022 1000 0 1000 0 2023 1010 0 1010 0 2024 1020 0 1020 0 2025 1030 0 1030 0 2026 1040 0 1040 0 2027 1050 0 1050 0 2028 1060 0 1060 0 2029 1070 0 1070 0 2030 1080 0 1080 0 total in phase 10450 0 10450 0 |
    And The page "does not contain" the "Change withhold status" text

  @exec-manual @test-case-id-66571916898
  Scenario: As an AR or admin user I can view allocation status of an account for each allocation type separately such as NAT NER NAVAT
    Given I sign in as "<user>" user
    And I have created an Operator Holding Account
    And Allowances have been allocated from both NAT and NER tables to this account
    When I navigate to "Allocation status for UK allowances" screen
    Then I see the Allocation status for this account
    And I see the Allocation status under New Entrants Reserve for this account
      | user            |
      | enrolled        |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-66571916913
  Scenario: As an AR or admin user I must not see the allocation status of an account if there are no allocation entries in current phase
    Given I sign in as "<user>" user
    And I have created an Operator Holding Account
    And No Allowances have been allocated to this account in the current phase
    When I navigate to "Allocation status for UK allowances" screen
    Then I don't see the Allocation status for this account
    And I don't see the Allocation status under New Entrants Reserve for this account
      | user            |
      | enrolled        |
      | senior admin    |
      | junior admin    |
      | read only admin |
