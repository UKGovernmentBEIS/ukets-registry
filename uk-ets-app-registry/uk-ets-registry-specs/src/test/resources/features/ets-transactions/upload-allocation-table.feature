@functional-area-ets-transactions

# Users:
# authority user: this user has the senior admin access rights, plus UK ETS administration functionality:
# 1. they can upload allocation table
# 2. issue uk allowance

#
# xlsx NAME PATTERN:
# the id in the files should be the same as the correct one in compliant entity.identifier column value.
#
# PART 1: value: UK: Only this value is allowed for positive scenarios upload.
# PART 2: value: NER: For New Entrance Reserve (only for OHA), value: NAVAT: For National Aviation (only for AOHA), NAT: only for OHA.
# PART 3: value: 2021_2030: Years defined into the file.
# PART 4: value: A valid MD5 CHECKSUM id. This is a unique identifier of the file. Example: 6046C725D62899AE712EFD87B5306DEB.
# This MD5 CHECKSUM id had been generated
# using the tool link: https://emn178.github.io/online-tools/md5_checksum.html
# or using the tool link: http://onlinemd5.com/

# For testing purposes, the following optional parameter set the "current year" to a given value: ie, for 2022:
#	business.property.transaction.allocation-year = 2022
#
# And the current ETS phase begins in 2021

# NOTE: acquiring account query is type_label name case sensitive

# NAP: National Allocation Plan

Feature: ETS transactions - ETS Transactions Upload Allocation table

  Epic: ETS Transactions
  Version: v0.4 (22/05/2020)
  Story: 5.3.1	Upload allocation table & 5.3.2	(Task) Approve allocation table & 5.5.1	(Extension) View account (allocation status)

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"

  @sampling-smoke @test-case-id-50134318697
  Scenario: Ensure that as senior admin user have correct access to ETS Administration menu items
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "ETS Administration" text
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration request allocation" screen
    And The page "does not contain" the "Upload allocation table" text
    And The page "contains" the "Request allocation of UK allowances" text

  @test-case-id-2398572912 @exec-manual
  Scenario: Ensure that a allocation of uk allowances request can be submitted and then approved
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "ETS Administration" text
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration request allocation" screen
    And The page "contains" the "Request allocation of UK allowances" text
    And The page "contains" the "Choose the allocation year" text
    When I click the "2021" button
    And I click the "Continue" button
    And I click the "Submit request" button
    And The page "contains" the "Request submitted" text
     # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Close account" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    # ensure that before approval data is correct in area: allocation overview
    Then I see the following fields having the values:
      | fieldName                                                                      | field_value        |
      | Allocation overview: Year                                                      | 2021               |
      | Allocation overview: Total allowances to allocate                              | 0 allowances       |
      | Allocation overview: Transferring account                                      | Allocation Account |
      | Allocation overview: Current allowances holdings                               | 1,994 allowances   |
      | Allocation overview: Excluded operators/installations                          | 7                  |
      | Allocation overview: Withheld operators/installations                          | 3                  |
      | Allocation overview: Transfer pending accounts(installations)                  | 5                  |
      | Allocation overview: Closed/fully suspended accounts (installations/operators) | 1                  |
    # ensure that before approval data is correct in area: allocation details
    Then I see the following fields having the values:
      | fieldName                                | field_value                   |
      | Allocation details: Allocation type      | 0 Accounts - 0 Total quantity |
      | Allocation details: Installations        | 0 Accounts - 0 Total quantity |
      | Allocation details: Aircraft operators   | 0 Accounts - 0 Total quantity |
      | Allocation details: New Entrants Reserve | 0 Accounts - 0 Total quantity |
      | Allocation details: Total                | 0 Accounts - 0 Total quantity |
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Allocate allowances proposal has been approved" text

  @sampling-smoke @test-case-id-50134318721
  Scenario: As Authority user I cannot submit a NAP when the upload process does not meet appropriate criteria
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
    # create second ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep2a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative2 |
      | compliantEntityEndYear   | 2030                       |
    # create first ETS AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountIndex             | 1                                 |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 3                    |
      | legalRepresentative      | Legal Rep3a                       |
      | legalRepresentative      | Legal Rep3b                       |
      | authorisedRepresentative | Authorised Representative3        |
      | compliantEntityEndYear   | 2030                              |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    # ensure that the flow does not continue if no file has been chosen
    When I click the "Continue" button
    Then I see an error summary with "Please select a file"
    # ensure empty .doc file cannot be uploaded
    When I choose using "allocationTable" from 'files-general' the 'empty.doc' file
    And I click the "Continue" button
    Then I see an error summary with "The selected file must be an XLSX"
    # ensure empty .xls file cannot be uploaded
    When I choose using "allocationTable" from 'files-general' the 'empty.xls' file
    And I click the "Continue" button
    Then I see an error summary with "The selected file must be an XLSX"
    # ensure that a file with wrong checksum cannot be uploaded
    When I choose using "allocationTable" from 'upload-allocation-table' the 'UK_NER_2021_2030_wrongCheckSum.xlsx' file
    And I click the "Continue" button
    Then I see an error summary with "MD5 checksum is invalid"
    # upload a valid file and ensure that the back navigation functionality is feasible
    When I choose using "allocationTable" from 'upload-allocation-table' the 'UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Allocation table" screen
    When I click the "Back" link
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "The table must be an XLSX file" text

  @test-case-id-23498672151 @exec-manual
  Scenario Outline: As Authority user I cannot submit a NAP when the uploaded file contains error
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
    # create second ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep2a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative2 |
      | compliantEntityEndYear   | 2030                       |
    # create first ETS AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountIndex             | 1                                 |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 3                    |
      | legalRepresentative      | Legal Rep3a                       |
      | legalRepresentative      | Legal Rep3b                       |
      | authorisedRepresentative | Authorised Representative3        |
      | compliantEntityEndYear   | 2030                              |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    # upload file with invalid content
    When I choose using "allocationTable" from 'files-general' the '<data_error>.xlsx' file
    And I click the "Continue" button
    Then I see an error summary with "The selected file contains errors"
    And The page "contains" the "Download the following CSV file to view the errors" text
    And I can download an xlsx file with "<message>" error having the columns below:
      | Row number                                                           |
      | Operator ID                                                          |
      | BR code                                                              |
      | Error message                                                        |

   Examples:
   | data_error                             | message                                                                                                                                       |
   #
   | quantity with negative value           | Invalid quantity - only zero or positive values are permitted – Code: 4004                                                                    |
   #
   | empty installation id                  | Invalid Installation ID - the ID must not be empty – Code: 4005                                                                               |
   | empty operator id                      | Invalid Operator ID - the ID must not be empty – Code: 4005                                                                                   |
   #
   | not existing installation id           | Invalid Installation ID – the ID does not exist in the Registry – Code: 4006                                                                  |
   | not existing operator id               | Invalid Operator ID – the ID does not exist in the Registry – Code: 4006                                                                      |
   #
   | duplicate installation ID in the table | Invalid Installation ID - duplicate entries – Code: 4007                                                                                      |
   | duplicate opearator ID in the table    | Invalid Operator ID - duplicate entries – Code: 4007                                                                                          |
   #
   | Installation id missing from the table | Missing Installation ID – allocation records have been deleted from the allocation table even though deletions are not permitted – Code: 4008 |
   | Operator id missing from the table     | Missing Operator ID – allocation records have been deleted from the allocation table even though deletions are not permitted – Code: 4008     |
   #
   | Invalid period reported                | Invalid year – all years must be within the period specified in the file name – Code: 4009                                                    |
   #
   | Invalid phase reported                 | Invalid year – all years must be within the current phase (2021 – 2030) – Code: 4010                                                          |
   #
   | oha or aoha with invalid quantity      | Invalid quantity - Installation or Operator should operate the corresponding year of the specified non zero quantity – Code: 4011             |

  @exec-manual @test-case-id-58560474921
  Scenario: As Authority user I can cancel the upload process of NAP submit
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
    # create second ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep2a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative2 |
    # create first ETS AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountIndex             | 1                                 |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 3                    |
      | legalRepresentative      | Legal Rep3a                       |
      | legalRepresentative      | Legal Rep3b                       |
      | authorisedRepresentative | Authorised Representative3        |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    # ensure that the process can be cancelled
    When I choose using "allocationTable" from 'upload-allocation-table' the 'UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Allocation table" screen
    When I click the "Cancel" link
    Then I am presented with the "ETS Administration Allocation table check and submit" screen
    When I click the "Cancel request" button
    Then I am presented with the "ETS Administration Allocation table check and submit" screen
    And The page "contains" the "The table must be an XLSX file" text

  @sampling-smoke @test-case-id-96768318686
  Scenario Outline: As Authority user I can submit a NAP when the upload process meets appropriate criteria
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
      | compliantEntityEndYear   | not specified              |
    # create second ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep2a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative2 |
      | compliantEntityEndYear   | not specified              |
    # create first ETS AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountIndex             | 1                                 |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 3                    |
      | legalRepresentative      | Legal Rep3a                       |
      | legalRepresentative      | Legal Rep3b                       |
      | authorisedRepresentative | Authorised Representative3        |
      | compliantEntityEndYear   | not specified                     |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "Upload allocation table" text
    # FILE UPLOAD AND SUBMIT
    When I choose using "allocationTable" from 'upload-allocation-table' the '<file_name>' file
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "<file_name>" text
    And I click the "Submit request" button
    # Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "Request submitted" text
    And The page "contains" the "Request ID:" text
    # ensure that I cannot upload a new NAT while another NAT is pending approval
    When I click the "Upload allocation table" link
    And I choose using "allocationTable" from 'upload-allocation-table' the '<file_name>' file
    And I click the "Continue" button
    Then I see an error summary with "You cannot upload a new allocation table, while another allocation table of the same type is pending approval"
    # ensure that an event was generated in the event history of the "UK Total Quantity Account" (National Account)
    And I click the "Sign out" link
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1010" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account details item" link
    Then I see the following fields having the values:
      | fieldName    | field_value                     |
      | Account name | UK Total Quantity Account 1010  |
      | Account type | ETS - UK Total Quantity Account |
    When I click the "History and comments item" link
    And The page "contains" the "Allocation table upload task request submitted." text
    # ensure that a task "Approve allocation table" was created for the AUTHORITY user
    And I click the "Sign out" link
    And I sign in as "authority_2" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
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
    And The page "contains" the "<file_name>" text
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Explain why you are approving this request" text
    And The page "contains" the "Approve request" text
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    And The page "contains" the "Proposal approved" text
    And The page "contains" the "Request ID" text
    And I click the "Sign out" link
    # sign in as senior admin to ensure that data is correct regarding Allocation status
    And I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Search" button
    And I click the "<account>" link
    Then I am presented with the "View account" screen
    # assertion in Overview area:
    Then I see the following fields having the values:
      | fieldName          | field_value                 |
      | Available quantity | Total available quantity: 0 |
    # assertion in Allocation status for UK allowances area:
    And I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    And The page "contains" the "<allocation_status>" text

    Examples:
      | file_name                                              | account                   | allocation_status                            |
      # NER
      | UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx | Account UK-100-50001-2-11 | Allocation status under New Entrants Reserve |
      # NAT
      | UK_NAT_2021_2030_73F5FC547AB70BECB8B1A5F9AC30C4B9.xlsx | Account UK-100-50002-2-6  | Allocation status                            |

    @exec-manual
    Examples:
      | file_name                                                | account                   | allocation_status |
      # NAVAT
      | UK_NAVAT_2021_2030_ee688710be15ce4b508bed3333b63e8a.xlsx | Account UK-100-50012-2-53 | Allocation status |

  @test-case-id-9124387411 @exec-manual
  Scenario Outline: As authority I cannot upload a new allocation table when another allocation job in pending
    Given I have already uploaded an allocation table
    And Another user has already "approved" the allocation table upload task
    And There is an "<type>" at "<status>" status
    When I try to upload a new allocation table
    Then I see an error summary with "You cannot upload a new allocation table, while an allocation allowances task is pending, or while an allocation job is scheduled or running."

    Examples:
      | type            | status    |
      | allocation task | pending   |
      | allocation job  | scheduled |
      | allocation job  | running   |

  @test-case-id-96768318687 @exec-manual
  Scenario Outline: As Authority user I cannot submit a NAP when allocation year is not within limits
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
    #| compliantEntityStartYear | <start_year_value>          |
    #| compliantEntityEndYear   | <end_year_value>            |
    # create second ETS OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep2a                |
      | legalRepresentative      | Legal Rep2b                |
      | authorisedRepresentative | Authorised Representative2 |
    #| compliantEntityStartYear | <start_year_value>          |
    #| compliantEntityEndYear   | <end_year_value>            |
    # create first ETS AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ACCOUNT
    And I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountIndex             | 1                                 |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 3                    |
      | legalRepresentative      | Legal Rep3a                       |
      | legalRepresentative      | Legal Rep3b                       |
      | authorisedRepresentative | Authorised Representative3        |
    #| compliantEntityStartYear | <start_year_value>                 |
    #| compliantEntityEndYear   | <end_year_value>                   |
    # create first ETS UK_TOTAL_QUANTITY_ACCOUNT ACCOUNT
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty     | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 | ALLOWANCE | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Upload allocation table" link
    Then I am presented with the "ETS Administration Allocation table" screen
    And The page "contains" the "Upload allocation table" text
    # FILE UPLOAD AND SUBMIT
    When I choose using "allocationTable" from 'upload-allocation-table' the '<file_name>' file
    And I click the "Continue" button
    Then I see an error summary with "Invalid quantity - Installation or Operator should operate the corresponding year of the specified non zero quantity"

    Examples:
      | file_name                                                |
      # NER with invalid start year
      | UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx   |
      # NAT with invalid start year
      | UK_NAT_2021_2030_73F5FC547AB70BECB8B1A5F9AC30C4B9.xlsx   |
      # NAVAT with invalid start year
      | UK_NAVAT_2021_2030_ee688710be15ce4b508bed3333b63e8a.xlsx |
      # NER with invalid end year
      | UK_NER_2021_2030_6046C725D62899AE712EFD87B5306DEB.xlsx   |
      # NAT with invalid end year
      | UK_NAT_2021_2030_73F5FC547AB70BECB8B1A5F9AC30C4B9.xlsx   |
      # NAVAT with invalid end year
      | UK_NAVAT_2021_2030_ee688710be15ce4b508bed3333b63e8a.xlsx |
