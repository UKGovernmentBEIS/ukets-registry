@functional-area-compliance

 # first / last year of verified emissions corresponds to db first / last year columns in compliant_entity table

Feature: Compliance - Update exclusion status

  @sampling-smoke @test-case-id-284500011
  Scenario Outline: As SRA I can update the exclusion account status of an oha or aoha account selecting current year value
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account>" in "account-details"
    Then I am presented with the "View account" screen
    And I click the "Emissions and Surrenders" link
    And I click the "Update exclusion status" button
    Then I am presented with the "Update exclusion status" screen
    # select current year
    And I select the "2021" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "Yes" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    And I click the "Submit" button
    Then I am presented with the "Update exclusion status" screen
    And The page "contains" the "The exclusion status has been updated" text
    And I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    And I click the "Emissions and Surrenders" link
    Then I see the following fields having the values:
      | fieldName | field_value |
      | <field>   | Excluded    |

    Examples:
      | account_type                      | account | field                |
      | OPERATOR_HOLDING_ACCOUNT          | 50001   | Reportable emissions |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | 50011   | Aviation emissions   |

  @test-case-id-284500012 @exec-manual
  Scenario Outline: As JRA or ROA or AR or Authority user I cannot update the exclusion account status of an oha or aoha account
    Given I have created an account with the following properties
      | property    | value          |
      | accountType | <account_type> |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    And I click the "Emissions and Surrenders" button
    Then The page "does not contain" the "Update exclusion status" text

    Examples:
      | user                      | account_type                      |
      | junior admin              | OPERATOR_HOLDING_ACCOUNT          |
      | read only admin           | OPERATOR_HOLDING_ACCOUNT          |
      | authorised representative | OPERATOR_HOLDING_ACCOUNT          |
      | authority                 | OPERATOR_HOLDING_ACCOUNT          |
      | junior admin              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |

  @test-case-id-284500013 @exec-manual
  Scenario Outline: As SRA user I cannot update the exclusion account status of an account which is not oha or aoha
    Given I have created an account with the following properties
      | property    | value          |
      | accountType | <account_type> |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then The page "does not contain" the "Emissions and Surrenders" text

    Examples:
      | account_type           |
      | TRADING                |
      | PERSON_HOLDING_ACCOUNT |
      | KP NON GOV ACCOUNT     |

  @test-case-id-284500014 @exec-manual
  Scenario: As SRA I have to select a year during update exclusion status in order to proceed with submit
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    And I click the "Emissions and Surrenders" button
    And I click the "Update exclusion status" button
    Then I am presented with the "Update exclusion status" screen
    # continue without selecting year
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    Then I see an error summary with "You must select a year"
    And I see an error detail with id "event-name-error" and content "You must select a year."

  @test-case-id-284500015 @exec-manual
  Scenario: As SRA I can cancel the update exclusion status operation
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<account>" in "account-details"
    And I click the "Emissions and Surrenders" button
    And I click the "Update exclusion status" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "current_year" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "Yes" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    When I click the "Cancel" link
    Then I am presented with the "Update exclusion status" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen

  @test-case-id-284500016 @exec-manual
  Scenario: As SRA I cannot set the exclusion status for a year for which emissions have been reported
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      # needed for current scenario:
      | emissions  | 2022:100                        |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    And I click the "Emissions and Surrenders" button
    And I click the "Update exclusion status" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "current_year" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    Then I see an error summary with "Emissions have already been reported for the selected year"
    And I see an error detail with id "event-name-error" and content "Emissions have already been reported for the selected year"

  @test-case-id-284500017 @exec-manual
  Scenario: As SRA I have to select an update exclusion status option of Yes or No that is not the same with the current one
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    And I click the "Emissions and Surrenders" button
    And I click the "Update exclusion status" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "current_year" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    And I select the "No" option
    And I click the "Continue" button
    Then I am presented with the "Update exclusion status" screen
    Then I see an error summary with "You must select the other option"
    And I see an error detail with id "event-name-error" and content "You must select the other option"

