@functional-area-compliance

Feature: Compliance - Upload emissions table request and task

  # Valid upload emissions table pattern: UK_Emissions_<DDMMYYYY>_<Regulator>_<MD5 checksum><_suffix>.xlsx
  # "View account" page: "Emissions and Surrenders" content: Page includes label named "Reportable" for oha / "Aviation" for aoha

  # first / last year of verified emissions corresponds to db first / last year columns in compliant_entity table

  @test-case-id-8303765121
  Scenario Outline: As SRA I can successfully upload emissions table and after task approval the emissions are updated
    # needed for the emissions table excel file that will be uploaded
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | <account_type>             |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration request allocation" screen
    When I click the "Upload emissions table" link
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I click the "Choose file" button
    # correlated to created account created: "compliant_entity" table: "identifier" column
    And I choose using "emissionsTable" from 'upload-emissions-table' the 'UK_Emissions_31122021_EA_561a9ea7a25a38a490f60c7783dbef81.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I get a new otp based on the existing secret for the "test_senior_admin_user" user
    And I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "ETS Administration Upload emissions table" screen
    And The page "contains" the "Request Submitted" text
    # as seconds sra claim and approve the task
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    When I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And The page "contains" the "Claimed" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    And I get a new otp based on the existing secret for the "test_senior_admin 2_user" user
    And I enter value "correct otp for user test_senior_admin 2_user" in "otp" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure emissions are now updated
    When I access "<account>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Emissions and Surrenders" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                       |
      | Cumulative verified emissions content | Cumulative verified emissions 100 |
      | Cumulative surrenders content         | Cumulative surrenders -           |
      # Reportable (for oha) / Aviation (for aoha) emissions:
      | <emissions_name>                      | 100                               |

    @sampling-smoke
    Examples:
      | account_type                      | emissions_name     | account |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT | Aviation emissions | 50011   |

    @exec-manual
    Examples:
      | account_type             | emissions_name       | account |
      | OPERATOR_HOLDING_ACCOUNT | Reportable emissions | 50001   |

  @test-case-id-8303765122 @exec-manual
  Scenario Outline: As SRA I cannot upload emissions table due to applied business rules
    # needed for the emissions table excel file that will be uploaded
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
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration request allocation" screen
    When I click the "Upload emissions table" link
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I click the "Choose file" button
    And I choose using "emissionsTable" from 'upload-emissions-table' the '<file_name>.<file_type>' file
    When I click the "Continue" button
    Then I see an error summary with "<error_summary>"

    Examples:
      | file_name                                                   | file_type | error_summary                                      |
      # not xlsx file type
      | UK_Emissions_01012021_BEIS_a3bae9fc752bec215b504f8eb6b08cac | pdf       | Invalid file format provided for the given content |
      | UK_Emissions_01012021_BEIS_a3bae9fc752bec215b504f8eb6b08cac | jpeg      | Invalid file format provided for the given content |
      # not valid file name template
      | test_01012021_BEIS_a3bae9fc752bec215b504f8eb6b08cac         | xlsx      | Invalid file name provided for the given content   |
      | UK_Emissions_test_BEIS_a3bae9fc752bec215b504f8eb6b08cac     | xlsx      | Invalid file name provided for the given content   |
      | UK_Emissions_01012021_test_a3bae9fc752bec215b504f8eb6b08cac | xlsx      | Invalid file name provided for the given content   |
      | UK_Emissions_01012021_BEIS_test                             | xlsx      | Invalid file name provided for the given content   |

  @test-case-id-8303765123 @exec-manual
  Scenario: As SRA I can cancel the upload emissions table request
    # needed for the emissions table excel file that will be uploaded
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
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration request allocation" screen
    When I click the "Upload emissions table" link
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I click the "Choose file" button
    And I choose using "emissionsTable" from 'upload-emissions-table' the 'UK_Emissions_31122021_EA_561a9ea7a25a38a490f60c7783dbef81.xlsx' file
    When I click the "Continue" button
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I click the "Cancel" button
    Then I am presented with the "ETS Administration Upload emissions table" screen
    When I click the "Cancel request" button
    Then I am presented with the "ETS Administration Upload emissions table" screen
    # ensure that Choose file button is available again
    And The page "contains" the "Choose file" text