@functional-area-account-management

Feature: Account management - OHA installation account details and AOHA aircraft account details update

  Epic: Account Management
  Version: 3.15 (10/05/2021)
  Story: (5.1.3)  View Account - Request to update the installation details
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @test-case-id-09312395753
  Scenario Outline: As SRA or JRA user I can update the account installation details of an OHA or the aircraft details of an AOHA account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "<submenu_tab>" link
    Then I am presented with the "View account" screen
    # check old value
    And The page "contains" the "<old_value>" text
    # update value
    When I click the "Request Update" button
    Then I am presented with the "View account operator update" screen
    When I select the "<new_value>" option
    And I click the "Continue" button
    Then I am presented with the "View account operator update" screen
    When I click the "Submit" button
    Then I am presented with the "View account operator update" screen
    And The page "contains" the "An update request has been submitted" text
    # approve task
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
    And The page "contains" the "Update account aircraft operator details" text
    When I click the "Back to task list" link
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # check updated value after task approval
    When I click the "Back to task list" link
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "<submenu_tab>" link
    Then The page "contains" the "<new_value>" text

    @sampling-smoke
    Examples:
      | account_get | user         | account_type                      | account_status | submenu_tab                    | old_value | new_value  |
      # checks for account types
      | 50011       | senior admin | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN           | Aircraft operator details item | SEPA      | BEIS-OPRED |

    Examples:
      | account_get | user         | account_type             | account_status              | submenu_tab               | old_value                                       | new_value                                 |
      # checks for senior and junior admins
      | 50001       | senior admin | OPERATOR_HOLDING_ACCOUNT | OPEN                        | Installation details item | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |
      | 50001       | junior admin | OPERATOR_HOLDING_ACCOUNT | OPEN                        | Installation details item | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |
      # checks for account status
      | 50001       | senior admin | OPERATOR_HOLDING_ACCOUNT | SUSPENDED                   | Installation details item | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |
      | 50001       | senior admin | OPERATOR_HOLDING_ACCOUNT | SUSPENDED_PARTIALLY         | Installation details item | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |
      | 50001       | senior admin | OPERATOR_HOLDING_ACCOUNT | ALL_TRANSACTIONS_RESTRICTED | Installation details item | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |

  @test-case-id-845028781 @exec-manual
  Scenario Outline: As sra I cannot change first or last year of verified emission when allocations already exist
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure that there are allocated amounts: 50 for 2021 and 100 for 2022
    When I click the "Allocation status for UK allowances submenu" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                 | field_value |
      | Allocated amount for 2021 | 50          |
      | Allocated amount for 2022 | 100         |
    # ensure that installation details contain the correct first and last year
    When I click the "Installation details item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName  | field_value |
      | First year | 2021        |
      | Last year  | null        |
    # try to request update
    When I click the "Request update" button
    And I enter value "<year_value>" in "<year>" field
    And I click the "Continue" button
    Then The page "contains" the "Check and sign your proposal" text
    When I click the "Submit" button
    Then I see an error summary with "<error_summary>"

    Examples:
      | year                            | year_value | error_summary                                                                                                           |
      | First year of verified emission | 2022       | First Year of Verified Emission Submission cannot be set after the first year for which allocations have been allocated |
      | Last year of verified emission  | 2021       | Last Year of Verified Emission Submission cannot be set before the last year for which allocations have been allocated  |

  @test-case-id-74917012
  Scenario Outline: As SRA or JRA user I can update the reporting period to exclude years for which no emission has been reported
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      # needed for current scenario:
      | emissions  | <emissions>  |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account>" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "<link>" link
    Then I see the following fields having the values:
      | fieldName                                       | field_value |
      | First year of verified emission submission text | 2021        |
    And I click the "Request Update" link
    # update last year future year
    And I clear the "last year of verified emissions" field
    When I enter value "2030" in "last year of verified emissions" field
    And I click the "Continue" button
    And I click the "Submit" button
    And The page "contains" the "An update request has been submitted." text
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
    And The page "contains" the "Update account installation details" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that account has updated emissions last year
    When I access "<account>" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "<link>" link
    Then I see the following fields having the values:
      | fieldName                                      | field_value |
      | Last year of verified emission submission text | 2030        |

    @sampling-smoke
    Examples:
      | user         | account_type             | link                      | emissions | account |
      | senior admin | OPERATOR_HOLDING_ACCOUNT | Installation details item | 2022:100                     | 50001   |

    @exec-manual
    Examples:
      | user         | account_type                      | link                           | emissions | account |
      | senior admin | OPERATOR_HOLDING_ACCOUNT          | Installation details item      | 2022:0                       | 50001   |
      | junior admin | OPERATOR_HOLDING_ACCOUNT          | Installation details item      | 2022:100                     | 50001   |
      | senior admin | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | Aircraft operator details item | 2022:100                     | 50011   |

  @test-case-id-74917011 @exec-manual
  Scenario Outline: As SRA or JRA user I cannot update the reporting period to exclude years for which zero or positive or emissions have already been reported
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      # needed for current scenario:
      | emissions  | <emissions>  |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account>" in "account-details"
    And I click the "<link>" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value |
      | First year of verified emission submission | 2021        |
    When I click the "Request Update" link
    # update first year future year
    And I clear the "first year of verified emissions" field
    And I enter value "2030" in "first year of verified emissions" field
    And I click the "Continue" button
    Then I see an error summary with "You must choose a different year. The first year cannot be set to a later date as emissions have been reported for the year that will be excluded after the update."

    Examples:
      | user         | account_type                      | link                      | emissions | account |
      | senior admin | OPERATOR_HOLDING_ACCOUNT          | Installation details      | 2022:100                     | 50001   |
      | senior admin | OPERATOR_HOLDING_ACCOUNT          | Installation details      | 2022:0                       | 50001   |
      | junior admin | OPERATOR_HOLDING_ACCOUNT          | Installation details      | 2022:100                     | 50001   |
      | senior admin | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | Aircraft operator details | 2022:100                     | 50011   |

  @test-case-id-09749625753 @exec-manual
  Scenario Outline: I cannot update oha account installation details or aoha aircraft details when I do not sign in as SRA or JRA or when account status is closed or proposed
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "<submenu_tab>" link
    Then I am presented with the "View account" screen
    # check old value
    And The page "contains" the "<old_value>" text
    And The page "does not contain" the "Request Update" text

    Examples:
      | user                               | account_type             | account_status |
      # invalid users
      | read only admin                    | OPERATOR_HOLDING_ACCOUNT | OPEN           |
      | auth rep with INITIATE AND APPROVE | OPERATOR_HOLDING_ACCOUNT | OPEN           |
      | authority                          | OPERATOR_HOLDING_ACCOUNT | OPEN           |
      | enrolled                           | OPERATOR_HOLDING_ACCOUNT | OPEN           |
      | validated                          | OPERATOR_HOLDING_ACCOUNT | OPEN           |
      # invalid accounts
      | senior admin                       | TRADING                  | OPEN           |
      | senior admin                       | UK_SURRENDER_ACCOUNT     | OPEN           |
      # invalid account status
      | senior admin                       | OPERATOR_HOLDING_ACCOUNT | CLOSED         |
      | senior admin                       | OPERATOR_HOLDING_ACCOUNT | PROPOSED       |

  @test-case-id-09204921758 @exec-manual
  Scenario Outline: During oha account installation details or aoha aircraft details update i can cancel the operation
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account_get>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "<submenu_tab>" link
    Then I am presented with the "View account" screen
    # check old value
    And The page "contains" the "<old_value>" text
    # set new value but cancel update instead of submit
    When I click the "Request Update" button
    Then I am presented with the "View account operator update" screen
    When I select the "<new_value>" option
    And I click the "Continue" button
    Then I am presented with the "View account operator update" screen
    When I click the "Cancel" button
    Then I am presented with the "Account overview tal transaction rules" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen
    When I click the "<submenu_tab>" link
    Then I am presented with the "View account" screen
    And The page "contains" the "<old_value>" text

    Examples:
      | account_get | user         | account_type                      | account_status | submenu_tab                    | old_value                                       | new_value                                 |
      | 50001       | senior admin | OPERATOR_HOLDING_ACCOUNT          | OPEN           | Installation details item      | Manufacture of mineral wool insulation material | Manufacture of ceramic products by firing |
      | 50011       | senior admin | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | OPEN           | Aircraft operator details item | SEPA                                            | BEIS-OPRED                                |
