@functional-area-account-management

Feature: Account management - Account Search

  Epic: Account Management
  Version: 1.6 (31/01/2020)
  Story: (5.1.1)  as user, I can filter and access the registry’s account list.
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  # Users:
  # User_Registered     for a user having the REGISTERED status
  # User_Validated      for a user having the VALIDATED status
  # User_Enrolled       for a user having the ENROLLED status
  # User_RegAdmin		for a Senior/Junior Registry Administrator
  # User_RegAdminSenior for a Senior Registry Administrator
  # User_RegAdminJunior for a Junior Registry Administrator
  # User_RegAdminRO		for a Read-Only Registry Administrator

  @security-url-ad-hoc-get @test-case-id-2560196546
  Scenario: Security front end test for account access via ad hoc url get
    # create first open account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    # create second open account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "enrolled" user
    # try to access as user active AR the first account (account 50001)
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    Then I am presented with the "Registry dashboard" screen
    When I get "account-details/50001" screen
    Then I am presented with the "Landing page" screen
    # try to ad hoc get the url of the second account without being AR to it (account 50002)
    When I get "account-details/50002" screen
    Then I am presented with the "Landing page" screen
    # try to ad hoc get the respective url for a non existing account  (account 50003)
    When I get "account-details/50003" screen
    Then I am presented with the "Landing page" screen

  ## Checks for the Screen "Search accounts"  #######################################################
  @exec-manual @test-case-id-2560196590
  Scenario: As AR I can search accounts filtering by default criteria
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    Then I see the following fields having the values:
      | fieldName                    | field_value |
      | Account name or ID           | [empty]     |
      | Account holder name          | [empty]     |
      | Permit or monitoring plan ID | [empty]     |
      | Account status dropdown      | [empty]     |
      | Account type dropdown        | [empty]     |
    And The page "does not contain" the "Excluded For Year" text
    When I click the "Hide filters" button
    Then I see the following fields having the values:
      | fieldName                    | field_value |
      | Account name or ID           | [empty]     |
      | Account holder name          | [empty]     |
      | Permit or monitoring plan ID | [empty]     |
      | Account status dropdown      | [empty]     |
      | Account type dropdown        | [empty]     |
    When I click the "Account UK-100-50001-2-11" link
    Then I am presented with the "View account" screen

  @test-case-id-237921627
  Scenario Outline: As admin I can search accounts using id full name or User filter
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    # empty filters check:
    Then I see the following fields having the values:
      | fieldName                    | field_value |
      | Account name or ID           | [empty]     |
      | Account holder name          | [empty]     |
      | Permit or monitoring plan ID | [empty]     |
      | Account status dropdown      | [empty]     |
      | Account type dropdown        | [empty]     |
    And I enter value "<value>" in "Account name or ID" field
    When I click the "Search" button
    # hide default, then show filter check:
    And I click the "Hide filters" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         | value             |
      | senior admin | UK-100-50001-2-11 |
      | senior admin | 50001             |
      | junior admin | UK-100-50001-2-11 |
      | junior admin | 50001             |

  @test-case-id-237921672
  Scenario Outline: As user I can filter account list by Account name or ID filter
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I enter value "<value>" in "Account name or ID" field
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         | value             |
      | senior admin | UK-100-50001-2-11 |
      | senior admin | 50001             |
      | junior admin | UK-100-50001-2-11 |
      | junior admin | 50001             |

  @test-case-id-237921708
  Scenario Outline: As user I can filter account list by Account Status filter
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I select the "Accounts status: Open" option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @sampling-smoke @test-case-id-237921743
  Scenario Outline: As a AR I cannot see an open account when I am suspended for this account
    # create first account
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    # create second account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    # suspended authorized representative, even with correct access rights, should not be able to implement request update
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | SUSPENDED | <access_rights> | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    And I click the "Search" button
    Then The page "does not contain" the "UK-100-50001-2-11" text

    Examples:
      | access_rights        |
      | INITIATE_AND_APPROVE |
      | APPROVE              |

  @sampling-smoke @test-case-id-237921786
  Scenario Outline: As AR enrolled I dont see SUSPENDED or SUSPENDED PARTIALLY or TRANSFER PENDING or CLOSED accounts
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | <status>       | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
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
    And I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    And I enter value "GB-100-1000-1-94" in "Account name or ID" field
    And I click the "Search" button
    Then I see "0" elements of "Account list returned result rows"

    Examples:
      | status              |
      | SUSPENDED           |
      | SUSPENDED_PARTIALLY |
      | TRANSFER_PENDING    |
      | CLOSED              |

  @exec-manual @test-case-id-2560196835
  Scenario: As enrolled user I can filter account list by Account Type
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I select the "Account type: Operator holding account" option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

  @exec-manual @test-case-id-256019646591
  Scenario Outline: As a user I can filter account list by Allocation Withhold Status
    Given I have created an account with the following properties
      | property                   | value                        |
      | accountType                | OPERATOR_HOLDING_ACCOUNT     |
      | holderType                 | ORGANISATION                 |
      | holderName                 | Organisation 1               |
      | legalRepresentative        | Legal Rep1                   |
      | legalRepresentative        | Legal Rep2                   |
      | authorisedRepresentative   | Authorised Representative1   |
      | authorisedRepresentative   | Authorised Representative2   |
      | allocation_withhold_status | <allocation_withhold_status> |
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I select the "Allocation Withhold Status: " option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | allocation_withhold_status |
      | Withheld                   |
      | Not Withheld               |
      | No                         |

  @test-case-id-2560196861
  Scenario: As user I can filter account list by Account Type for person holding account
    Given the following accounts have been created
      | account_id        | kyoto_account_type     | registry_account_type | account_name     | stat | bal  | un_ty | transfers_outside_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | commitment_period_code | account_holder_id | type_label             |
      | GB-121-50032-2-58 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | OPEN | 1500 | RMU   | true                  | true      | 2500     | 3999   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50032     | 1                      | Null              | Person Holding Account |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I select the "Account type: Person holding account" option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                                                     |
      | table rows results | GB-121-50032-2-58 individual first name 1 individual last name 1 Person Holding Account Person Holding Account 50032 Open 1,500 |

  @test-case-id-237921868
  Scenario Outline: As senior admin user I can filter account list by Account Type
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I select the "Account type: Operator holding account" option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-237921901
  Scenario Outline: As user I can filter account list using AH filter
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I enter value "Organisation 1" in "Account holder name" field
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-237921998
  Scenario Outline: As user I can filter account list by Permit or Monitoring Id
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I enter value "1234567890" in "Permit or monitoring plan ID" field
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-2379211028
  Scenario Outline: As senior admin user I can filter account list by AR id
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Advanced search" link
    And I enter value "UK88299344979" in "Authorized Representative ID" field
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-2379211058
  Scenario Outline: As user I can filter account list by Regulator
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I click the "Advanced search" link
    And I select the "Regulator: SEPA" option
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-2379211089 @exec-manual
  Scenario Outline: As a admin I can filter account list by ExcludedForYear
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Advanced search" link
    And I enter value "2100" in "Excluded For Year" field
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                     |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Open 0 |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-5017213969
  Scenario: As admin I must not see gov accounts
    And I sign in as "senior admin" user
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I enter value "GB-100-1000-1-94" in "Account name or ID" field
    And I click the "Search" button
    Then I see "0" elements of "Account list returned result rows"