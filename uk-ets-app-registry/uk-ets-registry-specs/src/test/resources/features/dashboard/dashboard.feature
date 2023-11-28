@functional-area-dashboard

Feature: Dashboard - Dashboard

  Epic: Dashboard
  Version: 1.0 (06/12/2019)
  Story: (& 4.1) as user, I access my dashboard
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Dashboard.docx?version=4&modificationDate=1575643746000&api=v2

  # User roles:
  # REGISTERED    	for a user having the REGISTERED status,
  # VALIDATED       for a user having the VALIDATED status,
  # ENROLLED        for a user having the ENROLLED status,
  # UNENROL_PENDING for a user having the UNENROL_PENDING status,
  # UNENROLLED      for a user having the UNENROLLED status,
  # REGADMIN		for a Registry Administrator (senior or junior)
  # REGADMINSENIOR	for a Senior Registry Administrator
  # REGADMINJUNIOR	for a Junior Registry Administrator
  # REGADMINRO		for a Read-Only Registry Administrator

  @test-case-id-16034916375
  Scenario Outline: As a non admin User I see correct menu items in my dashboard
    Given I sign in as "<user_role>" user
    Then I am presented with the "Registry dashboard" screen
    # ensure "Home" button menu is visible
    And The page "contains" the "Home" text
    # ensure "Tasks" button menu is visible
    And The page "contains" the "Tasks" text
    # ensure "Accounts" button menu is visible
    And The page "contains" the "Accounts" text
    # ensure links are clickable and redirect successfully to the correct respective page
    When I click the "<user_link>" link
    Then I am presented with the "<screen>" screen

    Examples:
      | user_role | user_link    | screen                           |
      # ENROLLED
      | enrolled  | Home         | Registry dashboard               |
      | enrolled  | Accounts     | Account list                     |
      | enrolled  | Transactions | Search Transactions              |
      # VALIDATED
      | validated | Home         | Registry dashboard               |
      | validated | Accounts     | Request to open registry account |

  @test-case-id-16034916399
  Scenario Outline: As an admin User I see correct menu items in my dashboard
    Given I sign in as "<user_role>" user
    Then I am presented with the "Registry dashboard" screen
    # ensure "Home" button menu is visible
    Then The page "contains" the "Home" text
    # ensure "Tasks" button menu is visible
    And The page "contains" the "Tasks" text
    # ensure "Accounts" button menu is visible
    And The page "<contains_accounts>" the "Accounts" text
    # ensure "Transactions" button menu is visible
    And The page "<contains_transactions>" the "Transactions" text
    # ensure "User administration" button menu is visible
    And The page "<contains_user_admin>" the "User Administration" text
    # ensure "KP Administration" button menu is visible
    And The page "<contains_kp_admin>" the "KP Administration" text
    # ensure "ETS Administration" button menu is visible
    And The page "<contains_ets_admin>" the "ETS Administration" text
    # ensure "Registry Administration" button menu is visible
    And The page "<contains_registry_admin>" the "Registry Administration" text
    # ensure "Reports" button menu is visible
    And The page "<contains_reports>" the "Reports" text

    Examples:
      | user_role       | contains_user_admin | contains_kp_admin | contains_registry_admin | contains_ets_admin | contains_reports | contains_accounts | contains_transactions |
      | senior admin    | contains            | contains          | does not contain        | contains           | contains         | contains          | contains              |
      | junior admin    | contains            | does not contain  | does not contain        | does not contain   | contains         | contains          | contains              |
      | read only admin | contains            | contains          | does not contain        | contains           | contains         | contains          | contains              |
      | authority_1     | does not contain    | does not contain  | does not contain        | contains           | does not contain | contains          | contains              |
      | registered      | does not contain    | does not contain  | does not contain        | does not contain   | does not contain | contains          | does not contain      |


  @exec-manual @test-case-id-16034916449
  Scenario Outline: As user I access my dashboard and I can Sign out
    Given I sign in as "<user_role>" user
    Then I am presented with the "Registry dashboard" screen
    And I see the following fields having the values:
      | fieldName     | field_value                      |
      | Welcome label | Welcome <first_name> <last_name> |
    And I click the "Sign out" link
    Then I am presented with the "Sign in" screen

    Examples:
      | user_role       | first_name      | last_name |
      | registered      | REGISTERED      | USER      |
      | validated       | VALIDATED       | USER      |
      | enrolled        | ENROLLED        | USER      |
      | senior admin    | SENIOR ADMIN    | USER      |
      | junior admin    | JUNIOR ADMIN    | USER      |
      | read only admin | READ ONLY ADMIN | USER      |

  @exec-manual @test-case-id-87253616596
  Scenario: As a Registered User I see the warning message and request account
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I see the following fields having the values:
      | fieldName            | field_value                                                                                                                                            |
      | LIMITED access label | Warning You only have LIMITED access to the Registry as your personal and work contact details have not yet been verified by a Registry Administrator. |
    When I click the "Accounts" link
    Then I see the following fields having the values:
      | fieldName           | field_value     |
      | Request account tab | Request account |

  @exec-manual @test-case-id-87253616609
  Scenario: As validated user I see the warning message and request account
    Given I sign in as "validated" user
    Then I am presented with the "Registry dashboard" screen
    And I see the following fields having the values:
      | fieldName                           | field_value                                                                                                                                                                                                                  |
      | Registry activation code label      | Warning A Registry activation code has been sent by post to your permanent residential address. Once you receive the mail, please use the link below to activate your access to the registry. Enter registry activation code |
      | Enter registry activation code link | Enter registry activation code                                                                                                                                                                                               |
    When I click the "I have not received my Registry activation code" link
    Then I see the following fields having the values:
      | fieldName                                   | field_value                            |
      | Request a new registry activation code link | Request a new registry activation code |
    When I click the "Accounts" link
    Then I see the following fields having the values:
      | fieldName           | field_value     |
      | Request account tab | Request account |
