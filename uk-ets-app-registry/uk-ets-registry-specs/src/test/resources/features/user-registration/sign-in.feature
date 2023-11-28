@functional-area-user-registration

Feature: User registration - Sign in

  Epic: User registration and Sign-in
  Version: 2.0 (15/10/2019)
  Story: (& 5.2.1) as user I can sign in so and access the registry
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=14&modificationDate=1571143521449&api=v2

  @test-case-id-50881430211
  Scenario: As user I can sign in successfully
    Given I sign in as "registered" user
    Then I am presented with the "Registry dashboard" screen

  @test-case-id-21839230015
  Scenario Outline: As user I cant sign in if I dont submit my credentials
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen
    And I enter the mandatory fields leaving the mandatory field "<fieldName>" empty
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    And I see an error summary with "Your email address or password is incorrect"

    Examples:
      | fieldName |
      | username  |
      | password  |

  @test-case-id-50881430235
  Scenario: As user I enter incorrect credentials
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen
    And I enter the mandatory fields which are
      | fieldId  | field_value               |
      | username | x$x$x$@x$.gr              |
      | password | stkuy!gh34#$%dgf#$dfJHGjh |
    When I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"

  @exec-manual @test-case-id-50881430249
  Scenario Outline: As a suspended or deactivated or pending deactivation user I cannot sign in Registry
    Given I I sign in as "<status>" user
    And I navigate to the "Landing page" screen
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen
    And I enter the mandatory fields
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    And I see an error summary with "Your email address or password is incorrect"

    Examples:
      | status               |
      | SUSPENDED            |
      | PENDING_DEACTIVATION |
      | DEACTIVATED          |

  @exec-manual @test-case-id-50881430261
  Scenario: As user I can access Sign in screen
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen

  @exec-manual @test-case-id-50881430269
  Scenario: As user I can navigate to registration page
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen
    When I click the "Register one" link
    Then I am presented with the "Register" screen

  @exec-manual @test-case-id-50881430279
  Scenario: As user having my account locked I must wait until account is unlocked again
    Given I have my account locked
    When I navigate to the "Sign in" screen
    Then I see an error summary with "Your account has been temporarily locked for X minutes. Please try again later"

  @exec-manual @test-case-id-50881430286
  Scenario: As user having Y unsuccessful password attempts my account should be temporarily locked for X minutes
    Given I have Y unsuccessful password attempts using separate browser sessions
    Then My account should be locked

  @exec-manual @test-case-id-50881430292
  Scenario: As user having multiple open sessions in the system when I sign out from first one then I should be invalidated to others also
    Given I have 2 open sessions to the system
    When I click the "Sign out" link
    And Under the 2nd session I navigate to the "Registry dashboard" screen
    Then I see an error summary with "Your session was invalidated by a sign out request"

  @exec-manual @test-case-id-50881430300
  Scenario: As a System I should support a maximum of 5 sign in requests per second
    Given 5 sign in were executed during the last second
    When A new sign request is submitted
    Then The request is held for a second
