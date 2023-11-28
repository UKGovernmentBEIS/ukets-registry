@functional-area-reporting
@exec-manual

Feature: Reporting - Public report page

  Epic: Reports
  Version: v0.4 (22/03/2021)
  Story: 4.1 - 4.5 Search Users / Accounts / Transactions / Tasks

  @test-case-id-884937111
  Scenario Outline: As unregistered user I can access public report page of kp or ets via landing page link
    Given I navigate to the "Landing page" screen
    And I click the "<reports_type>" link
    Then I am presented with the "<reports_type>" screen
    When I click the "<file_name>" link
    # manual executed step:
    # The <file_name> file is successfully downloaded with correct data

    Examples:
      | reports_type                  | file_name                      |
      | Kyoto Protocol Public Reports | Account information report.xls |
      | ETS Public Reports            | UK ETS Trading Accounts.xls    |

  @test-case-id-884937112
  Scenario Outline: As unregistered user I can navigate back to landing page from public kp or ets reports page
    Given I navigate to the "Landing page" screen
    And I click the "<reports_type>" link
    Then I am presented with the "<reports_type>" screen
    When I click the "UK ETS Homepage" link
    Then I navigate to the "Landing page" screen

    Examples:
      | reports_type                  |
      | Kyoto Protocol Public Reports |
      | ETS Public Reports            |

  @test-case-id-89723579019341
  Scenario Outline: As a user I can configure the publication details of the report section in the corresponding public reports page properly
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Reports" link
    Then I am presented with the "Reports" screen
    And I click the "<reports_type>" link
    Then I am presented with the "<reports_type>" screen
    When I click "<section>" button
    Then I am presented with the "<section>" screen
    And I see the following fields having the values:
      | fieldName | field_value                                        |
      | title     | uk ets registry participants and allocations (oha) |
    When I click "update_details" button
    Then I am presented with the "public reports update section details" screen
    When I clear the "title" field
    # change value from: "uk ets registry participants and allocations (oha)"
    # to: "uk ets registry participants and allocations (operator holding account)"
    And I enter value "uk ets registry participants and allocations (operator holding account)" in "title" field
    And I click "Continue" button
    And I click "Continue" button
    When I click "Submit" button
    Then The page "contains" the "An update has been submitted" text
    # ensure change is applied
    And I am presented with the "<section>" screen
    And I see the following fields having the values:
      | fieldName | field_value                                                             |
      | title     | uk ets registry participants and allocations (operator holding account) |

    Examples:
      | user         | reports_type                  | section                                            |
      | senior admin | Kyoto Protocol Public Reports | account information (paragraph 45)                 |
      | authority    | ETS Public Reports            | uk ets registry participants and allocations (oha) |

  @test-case-id-89723579019342
  Scenario Outline: As a user I can manually upload a report to be published in the section of the corresponding public reports page properly
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Reports" link
    Then I am presented with the "Reports" screen
    And I click the "<reports_type>" link
    Then I am presented with the "<reports_type>" screen
    When I click "<section>" button
    Then I am presented with the "<section>" screen
    And I see the following fields having the values:
      | fieldName | field_value                                        |
      | title     | uk ets registry participants and allocations (oha) |
    When I click "upload_new_file" button
    And I apply the <action> action
    And I click "Continue" button
    And I click "Submit" button
    Then The page "contains" the "A new file has been submitted" text
    # ensure file is uploaded
    And I am presented with the "<section>" screen
    And The page "contains" the "public_report.xlsx" text

    Examples:
      | user         | reports_type                  | section                                            | action                                                 |
      | senior admin | Kyoto Protocol Public Reports | account information (paragraph 45)                 | upload public_report.xlsx file                         |
      | authority    | ETS Public Reports            | uk ets registry participants and allocations (oha) | upload public_report.xlsx file and choose ongoing year |

  @test-case-id-89723579019343
  Scenario Outline: As a user I can unpublish a report in the public reports page
    # Given there is already a public report published
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Reports" link
    Then I am presented with the "Reports" screen
    And I click the "<reports_type>" link
    Then I am presented with the "<reports_type>" screen
    When I click "<section>" button
    Then I am presented with the "<section>" screen
    And The page "contains" the "public_report.xlsx" text
    # unpublish the file
    And I click "Unpublish" button
    # ensure file is uploaded
    Then I am presented with the "<section>" screen
    And The page "contains" the "public_report.xlsx" text

    Examples:
      | user         | reports_type                  | section                                            |
      | senior admin | Kyoto Protocol Public Reports | account information (paragraph 45)                 |
      | authority    | ETS Public Reports            | uk ets registry participants and allocations (oha) |