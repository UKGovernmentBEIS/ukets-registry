@functional-area-ets-transactions

Feature: ETS transactions - ETS Transactions Issue UK allowances

  Epic: ETS Transactions
  Version: v0.4 (22/05/2020)
  Story: 5.3.3	Propose to issue UK allowances & 5.3.4 (Task) Issue UK allowances

  # For testing purposes, the following optional parameter set the "current year" to a given value: ie, for 2022:
  #	business.property.transaction.allocation-year = 2022
  #
  # ETS UK Allowances are always issued to the "UK Total Quantity Account" (National account, UK-100-xx, Acc.Holder= HMG)
  #
  # BRs for this transaction; skipped or Done(= implemented in this f.file)
  # skipped 2003 Transactions to acquiring accounts with status CLOSED are not allowed
  # skipped 2004 Transactions to acquiring accounts with status FULLY SUSPENDED are not allowed
  # skipped 2007 Transaction is not allowed for the type of acquiring account
  # skipped 2010 The acquiring account type is not allowed to hold the units being issued
  # skipped 2011 The acquiring account is fixed for this transaction type and cannot be modified
  # skipped 3006 Invalid unit types for this transaction
  # done    3007 At least one non-zero quantity must be specified
  # done    3008 The quantity must be a positive number without decimal places
  # skipped 3009 A transaction must not issue more than one unit type
  # done    ???? Transactions to accounts with other pending transactions of the same type are not allowed
  # done    3500 The quantity of allowances issued must not exceed the total cap for the current phase minus the allowances issued so far in this phase
  # skipped 3501 Issuance of allowances is only permitted for the current year

  # NOTE: acquiring account query is type_label name case sensitive

  Background:
    Given The following allocation phase have been set with initial "100000", consumed "0", pending "0"

  @test-case-id-29774717130
  Scenario: Senior admin user has access to ETS Administration Propose to issue UK allowances
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
    Then The page "contains" the "Request allocation of UK allowances" text
    And The page "contains" the "View issuance and allocation status" text
    And The page "does not contain" the "propose to issue" text

  @test-case-id-42138917011
  Scenario Outline: As Authority user I can propose to issue UK allowances the quantity must be a positive number
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    Then I see the following fields having the values:
      | fieldName          | field_value                                 |
      | table headers      | Year Cap Issued Remaining Quantity to issue |
      | 2030 row           | 2030 10,800 0 10,800                        |
      | 2029 row           | 2029 10,700 0 10,700                        |
      | 2028 row           | 2028 10,600 0 10,600                        |
      | 2027 row           | 2027 10,500 0 10,500                        |
      | 2026 row           | 2026 10,400 0 10,400                        |
      | 2025 row           | 2025 10,300 0 10,300                        |
      | 2024 row           | 2024 10,200 0 10,200                        |
      | 2023 row           | 2023 10,100 0 10,100                        |
      | 2022 row           | 2022 10,000 0 10,000                        |
      | 2021 row           | 2021 10,900 0 10,900 Select quantity        |
      | Total in phase row | Total in phase 104,500 0 104,500            |
      | Quantity           | [empty]                                     |
	# BR-3008
    And I enter value "<value>" in "Quantity" field
    When I click the "Continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Submit Request" button
    And I see an error summary with "The quantity must be a positive number without decimal places"

    Examples:
      | value   |
      | 1.5     |
      | 10.99   |
      | 100.001 |
      | aaa     |
      | *       |

  @test-case-id-29774717229
  Scenario: As Authority user I can cancel an issue UK allowances proposal
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    Then I see the following fields having the values:
      | fieldName          | field_value                                 |
      | table headers      | Year Cap Issued Remaining Quantity to issue |
      | 2030 row           | 2030 10,800 0 10,800                        |
      | 2029 row           | 2029 10,700 0 10,700                        |
      | 2028 row           | 2028 10,600 0 10,600                        |
      | 2027 row           | 2027 10,500 0 10,500                        |
      | 2026 row           | 2026 10,400 0 10,400                        |
      | 2025 row           | 2025 10,300 0 10,300                        |
      | 2024 row           | 2024 10,200 0 10,200                        |
      | 2023 row           | 2023 10,100 0 10,100                        |
      | 2022 row           | 2022 10,000 0 10,000                        |
      | 2021 row           | 2021 10,900 0 10,900 Select quantity        |
      | Total in phase row | Total in phase 104,500 0 104,500            |
      | Quantity           | [empty]                                     |
    And I enter value "10" in "Quantity" field
    When I click the "Continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Cancel" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Cancel" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen

  @exec-manual @test-case-id-29774717312
  Scenario: As Authority user I can navigate back during an issue UK allowances proposal
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
      | account_id       | kyoto_account_type    | registry_account_type     | account_name    | acc_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity | ub_un_ty | ub_acc_id | commit_period | acc_holder_type | type_label                |
      | UK-100-1010-0-47 | PARTY_HOLDING_ACCOUNT | UK_TOTAL_QUANTITY_ACCOUNT | Party Holding 1 | OPEN       | 1000 |       | true       | true      | 1        | 1000   | 0        | 0        |             |          | 1002      | 0             | GOVERNMENT      | UK Total Quantity Account |
    And I sign in as "authority_1" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000004 |
    Then I am presented with the "Registry dashboard" screen
    And I click the "ETS Administration" link
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    Then I see the following fields having the values:
      | fieldName          | field_value                                 |
      | table headers      | Year Cap Issued Remaining Quantity to issue |
      | 2030 row           | 2030 10,800 0 10,800                        |
      | 2029 row           | 2029 10,700 0 10,700                        |
      | 2028 row           | 2028 10,600 0 10,600                        |
      | 2027 row           | 2027 10,500 0 10,500                        |
      | 2026 row           | 2026 10,400 0 10,400                        |
      | 2025 row           | 2025 10,300 0 10,300                        |
      | 2024 row           | 2024 10,200 0 10,200                        |
      | 2023 row           | 2023 10,100 0 10,100                        |
      | 2022 row           | 2022 10,000 0 10,000                        |
      | 2021 row           | 2021 10,900 0 10,900 Select quantity        |
      | Total in phase row | Total in phase 104,500 0 104,500            |
      | Quantity           | [empty]                                     |
    And I enter value "10" in "Quantity" field
    When I click the "Continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Back" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Back" link
    Then I am presented with the "Registry dashboard" screen

  @test-case-id-29774717394
  Scenario: As Authority user during an issue UK allowances proposal I cannot continue without having set a quantity value
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    Then I see the following fields having the values:
      | fieldName          | field_value                                 |
      | table headers      | Year Cap Issued Remaining Quantity to issue |
      | 2030 row           | 2030 10,800 0 10,800                        |
      | 2029 row           | 2029 10,700 0 10,700                        |
      | 2028 row           | 2028 10,600 0 10,600                        |
      | 2027 row           | 2027 10,500 0 10,500                        |
      | 2026 row           | 2026 10,400 0 10,400                        |
      | 2025 row           | 2025 10,300 0 10,300                        |
      | 2024 row           | 2024 10,200 0 10,200                        |
      | 2023 row           | 2023 10,100 0 10,100                        |
      | 2022 row           | 2022 10,000 0 10,000                        |
      | 2021 row           | 2021 10,900 0 10,900 Select quantity        |
      | Total in phase row | Total in phase 104,500 0 104,500            |
      | Quantity           | [empty]                                     |
    # no quantity value set
    When I click the "Continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    # BR-3007
    Then I see an error summary with "One non-zero quantity must be specified"

  @sampling-smoke @test-case-id-29774717475 @sampling-mvp-smoke
  Scenario: As Authority user during an issue UK allowances proposal I cannot exceed max quantity limit of entire ets phase
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And I enter value "200000" in "Quantity" field
	  # BR-3500 : error if the phase cap is exceeded (Phase MAX limit for 2019 - 2028:  104.500)
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "The quantity to issue is more than the amount remaining for the year" text
    And The page "contains" the "The quantity to issue is more than the amount remaining for the phase." text
    When I get a new otp based on the existing secret for the "test_authority_1_user" user
    And I enter value "correct otp for user test_authority_1_user" in "otp" field
    And I click the "Submit" button
    Then I see an error summary with "The quantity of allowances issued (200000) must not exceed the total cap for the current phase minus the allowances issued so far in this phase (100000)"

  @sampling-smoke @test-case-id-29774717562
  Scenario: As Authority user during an issue UK allowances proposal I must be warned when I exceed max quantity limit of a year
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
     # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And I enter value "20000" in "Quantity" field
  	  # BR-3500 : error if the phase cap is exceeded (Phase MAX limit for 2019 - 2028:  104.500)
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I get a new otp based on the existing secret for the "test_authority_1_user" user
    When I enter value "correct otp for user test_authority_1_user" in "otp" field
    And The page "contains" the "The quantity to issue is more than the amount remaining for the year" text
    When I click the "Submit" button
    Then The page "contains" the "Proposal submitted" text
    # sign in as senior admin to ensure that An event was generated in the event history of the "UK Total Quantity Account" (National Account)
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1010" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "History and comments item" link
    And The page "contains" the "Transaction proposal task request submitted." text

  @sampling-smoke @test-case-id-29774717658
  Scenario: As I user I can change quantity amount during an issue uk allowances proposal
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I enter value "10" in "Quantity" field
    And I click the "Continue" button
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Change" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I clear the "Quantity" field
    And I enter value "20" in "Quantity" field
    And I click the "Continue" button
    And I click the "Continue" button
    Then I see the following fields having the values:
      | fieldName               | field_value                                 |
      | Table Headers           | Year Cap Issued Remaining Quantity to issue |
      | 2030 row                | 2030 10,800 0 10,800                        |
      | 2029 row                | 2029 10,700 0 10,700                        |
      | 2028 row                | 2028 10,600 0 10,600                        |
      | 2027 row                | 2027 10,500 0 10,500                        |
      | 2026 row                | 2026 10,400 0 10,400                        |
      | 2025 row                | 2025 10,300 0 10,300                        |
      | 2024 row                | 2024 10,200 0 10,200                        |
      | 2023 row                | 2023 10,100 0 10,100                        |
      | 2022 row                | 2022 10,000 0 10,000                        |
      | 2021 Row after proposal | 2021 10,900 0 10,900 20                     |
      | Total in phase row      | Total in phase 104,500 0 104,500            |
      | quantity to issue       | 20                                          |

  @sampling-smoke @test-case-id-29774717756
  Scenario: As I user I cannot submit an issue uk allowances proposal without setting otp
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And I enter value "10" in "Quantity" field
    When I click the "continue" button
    And I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And The page "contains" the "Quantity to issue for 2021" text
    When I click the "Submit" button
    And I see an error summary with "OTP code is required"

  @sampling-smoke @test-case-id-29774717856
  Scenario: As I user I can submit an issue uk allowances proposal
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
   # default ETS Administration screen: Propose to issue uk allowances
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And I enter value "10" in "Quantity" field
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Check and sign the proposal" text
    And I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Propose to issue UK allowances" text
    And The page "contains" the "Quantity to issue for 2021" text
    And The page "contains" the "UK-100-1010-0-47 - UK Total Quantity Account 1010" text
    And I get a new otp based on the existing secret for the "test_authority_1_user" user
    When I enter value "correct otp for user test_authority_1_user" in "otp" field
    When I click the "Submit" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And The page "contains" the "Proposal submitted" text
    And The page "contains" the "Transaction ID:" text
    # try to submit another proposal when there is already a pending one
    And I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I enter value "30" in "Quantity" field
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "continue" button
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    And I get a new otp based on the existing secret for the "test_authority_1_user" user
    When I enter value "correct otp for user test_authority_1_user" in "otp" field
    When I click the "Submit" button
    Then I see an error summary with "Transactions to accounts with other pending transactions of the same type are not allowed"
    # sign in as senior admin to ensure that An event was generated in the event history of the "UK Total Quantity Account" (National Account)
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "1010" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "History and comments item" link
    And The page "contains" the "Transaction proposal task request submitted." text
