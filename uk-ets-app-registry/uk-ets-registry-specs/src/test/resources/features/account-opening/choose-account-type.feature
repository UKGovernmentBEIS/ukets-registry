@functional-area-account-opening

Feature: Account opening - Choose account type

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (& 6.1.1.1) Request to open a registry account
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  @exec-manual @test-case-id-21300312195
  Scenario: As user I can access Request a new registry account screen
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen

  @exec-manual @test-case-id-21300312200
  Scenario: As user I can access the Select the type of account screen
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Choose an account typeChoose |


  @test-case-id-604579612105 @exec-manual
  Scenario Outline: As user I can select type of registry account and proceed to next screen
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    When I select the "<account_type>" option
    And I click the "Continue" button
    Then The page "contains" the "<new_account_text>" text

    Examples:
      | account_type                      | new_account_text                            |
      | Operator Holding Account          | New ETS - Operator Holding Account          |
      | Aircraft Operator Holding Account | New ETS - Aircraft Operator Holding Account |
      | Trading Account                   | New ETS - Trading Account                   |
      | Person Holding Account            | New KP - Person Holding Account             |

  @exec-manual @test-case-id-604579612122
  Scenario Outline: As user I want a hint for each account type before I select one
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" link
    Given I navigate to the "Fill account type" screen
    When I mouse over "<account_type>"
    Then I am presented with "<hint>" text in a pop-up box

    Examples:
      | account_type                      | hint                              |
      | Operator Holding Account          | Operator Holding Account          |
      | Aircraft Operator Holding Account | Aircraft Operator Holding Account |
      | Trading Account                   | Trading Account                   |
      | Person Holding Account            | Person Holding Account            |

  @test-case-id-94204965 @exec-manual
  Scenario: As Read only admin I cannot request an account creation
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then The page "does not contain" the "Request account" text

  @test-case-id-94104366 @exec-manual
  Scenario Outline: As authorised representative I see the correct account types during account opening
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" link
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    And The page "does not contain" the "Operator Holding Account" text
    And The page "does not contain" the "Aircraft Operator Holding Account" text
    And The page "contains" the "Trading Account" text
    And The page "contains" the "Person Holding Account" text

    Examples:
      | user       |
      | validated  |
      | enrolled   |
      | registered |
