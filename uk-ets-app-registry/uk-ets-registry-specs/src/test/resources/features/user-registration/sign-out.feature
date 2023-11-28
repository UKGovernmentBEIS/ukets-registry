@functional-area-user-registration

Feature: User registration - Sign out

  Epic: User registration and Sign-in
  Version: 2.0 (15/10/2019)
  Story: (& 5.2.2) as user I can sign out from the registry
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=14&modificationDate=1571143521449&api=v2

  @test-case-id-94559830314
  Scenario: As user I can sign out from registry
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen

  @exec-manual @test-case-id-816530119
  Scenario Outline: As user I am automatically signed out when I am inactive for 30 minutes
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    When I wait "<predefined_inactive_time>"
    Then I am presented with the "<page>" screen
    When I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen

    Examples:
      | predefined_inactive_time            | page               |
      | exact time threshold minus 1 second | Registry dashboard |
      | exact time threshold                | Sign in            |

  @exec-manual @test-case-id-94559830340
  Scenario: As user I can sign in after I am automatically signed out when I am inactive for 10 minutes
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen
    And I am presented with the "Sign in" screen
    Then I sign in as "registered" user
    And I am presented with the "Registry dashboard" screen
