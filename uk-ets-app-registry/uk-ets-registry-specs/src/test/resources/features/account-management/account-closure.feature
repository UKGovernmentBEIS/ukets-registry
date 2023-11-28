@functional-area-account-management

Feature: Account management - Account closure

  @test-case-id-7490547101
  Scenario Outline: As sra or jra admin I can close oha or aoha or kp or trading or person holding account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
      | compliantEntityEndYear   | 2021                       |
      | emissions                | 2021:100                   |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "Close request has been submitted" text
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
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Close request has been approved" text
    # ensure account status is closed after account closure task complete
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                                                       |
      | table rows results | UK-100-50001-2-11 Organisation 1 Operator holding account Operator holding account 50001 Closed 0 |

    @sampling-smoke
    Examples:
      | user         | account_type             |
      | senior admin | OPERATOR_HOLDING_ACCOUNT |

    @exec-manual
    Examples:
      | user         | account_type                      |
      | senior admin | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | senior admin | TRADING                           |
      | senior admin | PERSON_HOLDING_ACCOUNT            |
      | senior admin | KP NON GOV ACCOUNT                |
      | junior admin | OPERATOR_HOLDING_ACCOUNT          |

  @test-case-id-129834711 @exec-manual
  Scenario Outline: I can see a warning in case of account closure of overallocated or underallocated or not allocated amount
    Given I have created an account with the following properties
      | property                 | value                                       |
      | accountType              | <account_type> with <allowances> allowances |
      | accountStatus            | OPEN                                        |
      | holderType               | ORGANISATION                                |
      | holderName               | Organisation 1                              |
      | legalRepresentative      | Legal Rep1                                  |
      | legalRepresentative      | Legal Rep2                                  |
      | authorisedRepresentative | Authorised Representative6                  |
      | compliantEntityEndYear   | 2021                                        |
      | emissions                | 2021:100                                    |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "The <account_type> has <allowances> allowances" text

    Examples:
      | user         | account_type      | allowances      |
      #
      | senior admin | Aircraft Operator | under-allocated |
      | senior admin | Aircraft Operator | unallocated     |
      | senior admin | Aircraft Operator | over-allocated  |
      #
      | senior admin | installation      | under-allocated |
      | senior admin | installation      | unallocated     |
      | senior admin | installation      | over-allocated  |
      #
      | junior admin | Aircraft Operator | under-allocated |
      | junior admin | Aircraft Operator | unallocated     |
      | junior admin | Aircraft Operator | over-allocated  |
      #
      | junior admin | installation      | under-allocated |
      | junior admin | installation      | unallocated     |
      | junior admin | installation      | over-allocated  |
      
  @sampling-smoke @test-case-id-7490547102
  Scenario: I cannot close an account if the account has non zero balance
    Given I have created an account with the following properties
      | property                 | value                                                                     |
      | accountType              | OPERATOR_HOLDING_ACCOUNT                                                  |
      | accountIndex             | 5                                                                         |
      | accountStatus            | OPEN                                                                      |
      | holderType               | ORGANISATION                                                              |
      | holderName               | Organisation 1                                                            |
      | legalRepresentative      | Legal Rep1a                                                               |
      | legalRepresentative      | Legal Rep2b                                                               |
      | authorisedRepresentative | Authorised Representative1                                                |
      | unitsInformation         | 1100,ALLOWANCE,10000002000,10000003099,10000019,CP0,CP0,empty,empty,false |
      | commitmentPeriod         | 0                                                                         |
      | transfersOutsideTal      | true                                                                      |
      | approveSecondAr          | true                                                                      |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "10000019" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And I see an error summary with "The account cannot be closed as there is non-zero account balance"

  @test-case-id-7490547103 @exec-manual
  Scenario Outline: As ar I can close an kp or trading account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "<access_rights>" in the account with ID "100000001"
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "Close request has been submitted" text
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
    And The page "contains" the "Approve account closure" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Close request has been approved" text
    # ensure account status is closed after account closure task complete
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                    |
      | table rows results | UK-100-50001-2-11 50001 Organisation 1 Closed NOT APPLICABLE 0 |

    Examples:
      | account_type       | access_rights        |
      | KP NON GOV ACCOUNT | INITIATE_AND_APPROVE |
      | TRADING            | INITIATE_AND_APPROVE |
      | KP NON GOV ACCOUNT | APPROVE              |
      | TRADING            | APPROVE              |
      | KP NON GOV ACCOUNT | INITIATE             |
      | TRADING            | INITIATE             |

  @test-case-id-7490547104 @exec-manual
  Scenario: As sra I can close kp gov account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | KP GOV ACCOUNT             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "Close request has been submitted" text
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
    And The page "contains" the "Approve account closure" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Close request has been approved" text
    # ensure account status is closed after account closure task complete
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    Then I see the following fields having the values:
      | fieldName          | field_value                                                    |
      | table rows results | UK-100-50001-2-11 50001 Organisation 1 Closed NOT APPLICABLE 0 |

  @test-case-id-7490547105 @exec-manual
  Scenario Outline: I cannot close account of specific account types if I login with user of no such capability
    Given I have created an account with the following properties
      | property    | value          |
      | accountType | <account_type> |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And The page "does not contain" the "Close account" text

    Examples:
      | user                      | account_type                      |
      | senior admin              | ETS GOV ACCOUNT                   |
      | junior admin              | ETS GOV ACCOUNT                   |
      | read only admin           | ETS GOV ACCOUNT                   |
      | junior admin              | KP GOV ACCOUNT                    |
      | read only admin           | KP GOV ACCOUNT                    |
      | read only admin           | OPERATOR_HOLDING_ACCOUNT          |
      | read only admin           | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | read only admin           | KP NON GOV ACCOUNT                |
      | read only admin           | KP GOV ACCOUNT                    |
      | read only admin           | TRADING                           |
      | read only admin           | PERSON_HOLDING_ACCOUNT            |
      | authorised representative | ETS GOV ACCOUNT                   |
      | authorised representative | OPERATOR_HOLDING_ACCOUNT          |
      | authorised representative | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |

  @test-case-id-7490547106 @exec-manual
  Scenario: During account closure I can cancel the account closure operation
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | KP GOV ACCOUNT             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    # ensure cancel functionality works as expected
    When I click the "Cancel" link
    Then I am presented with the "Request documents" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen

  @test-case-id-7490547107 @exec-manual
  Scenario: I cannot submit account closure request if comment area is empty
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    Then I see an error summary with "Enter a reason for closing this account"
    And I see an error detail for field "event-name" with content "Error: Enter a reason for closing this account"

  @test-case-id-7490547108 @exec-manual
  Scenario: Ensure that ars are removed from account after account closure
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "Close request has been submitted" text
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
    And The page "contains" the "Approve account closure" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Close request has been approved" text
    # ensure authorised representative is removed
    Then The authorised representative "Authorised Representative6" is removed from the account

  @test-case-id-7490547109 @exec-manual
  Scenario Outline: Account closure request submit cases regarding pending tasks and invalid parameters
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When There is the <condition> condition
    Then I <result>

    Examples:
      | condition                                                                        | result                                |
      # dynamic_surrender_status
      | dynamic_surrender_status C                                                       | Cannot submit account closure request |
      | dynamic_surrender_status C                                                       | Cannot approve account closure task   |
      # last_year_of_verified_emissions_null
      | last_year_of_verified_emissions_null                                             | Cannot submit account closure request |
      | last_year_of_verified_emissions_null                                             | Cannot approve account closure task   |
      # last_year_of_verified_emissions_greater_than_current_year
      | last_year_of_verified_emissions_greater_than_current_year                        | Cannot submit account closure request |
      | last_year_of_verified_emissions_greater_than_current_year                        | Cannot approve account closure task   |
      # tal_update_after_account_closure_request
      | tal_update_after_account_closure_request                                         | Cannot approve account closure task   |
      # allocation table / allocation request
      | exists pending task other than "upload allocation table" or "allocation request" | Cannot submit account closure request |
      | exists pending task "upload allocation table" or "allocation request"            | Can submit account closure request    |
      | exists "upload allocation table" or "allocation request" pending task            | Cannot approve account closure task   |
