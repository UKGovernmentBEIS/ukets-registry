@functional-area-user-validation

Feature: User validation - Handle cookies

  @sampling-smoke @test-case-id-722012431263
  Scenario: As a browser user I cannot continue if I dont handle cookies when I start the registration process
    Given I navigate to the "Landing page" screen
    When I click the "Sign in" link
    And I click the "create a registry sign in" link
    Then I am presented with the "Email info" screen
    # ensure that if I do not accept cookies I cannot continue
    And I click the "Continue" button
    Then I am presented with the "Email info" screen
    # ensure that if I then accept cookies I do access system
    When I accept all cookies
    Then I am presented with the "Email info" screen
    And The page "contains" the "You’ve accepted all cookies." text
    And The page "does not contain" the "What is your email address?" text
    And I click the "Continue" button
    Then I am presented with the "Email info" screen
    And The page "contains" the "What is your email address?" text

  @sampling-smoke @test-case-id-722012431281
  Scenario: As a browser user I cannot continue if I dont handle cookies when I start the sign process
    Given I have created 1 "senior" administrators
    And I navigate to the "Landing page" screen
    When I click the "Sign in" link
    Then I am presented with the "Sign in" screen
    # try to access system without handling cookies
    When I enter value "senior_user_0@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    Then The "Sign in" "button" is "disabled"
    # finally accept cookies and navigate to the next screen
    When I accept all cookies
    Then The "Sign in" "button" is "enabled"
    When I click the "Sign in" button
    Then The page "contains" the "Enter the 6-digit code shown in the app." text

  @exec-manual @test-case-id-722012431298
  Scenario: As a non registered user I must accept cookies to use the service
    Given I navigate to the "Landing page" screen
    When I accept all cookies
    And I click the "Sign in" button
    Then I am presented to "Sign in" screen
    And all cookies are enabled on my browser

  @exec-manual @test-case-id-40201131096
  Scenario Outline: As a non registered user I can set cookie preferences
    Given I navigate to the "<page>" screen
    When I set cookie preferences:
      | google_analytics   | strictly_needed   |
      | <google_analytics> | <strictly_needed> |
    And I click the "Save changes" button and I go back to "<page>"
    And cookies are configured as below:
      | google_analytics   | strictly_needed   |
      | <google_analytics> | <strictly_needed> |
    And I can sign in with valid user credentials

    Examples:
      | page         | google_analytics | strictly_needed |
      | Landing page | use              | use             |
      | Sign in      | use              | use             |
      | Landing page | use              | do not use      |
      | Sign in      | use              | do not use      |
      | Landing page | do not use       | use             |
      | Sign in      | do not use       | use             |

  @exec-manual @test-case-id-40201131118
  Scenario Outline: As a non registered user I can set cookie preferences
    Given I navigate to the "<page>" screen
    When I set cookie preferences:
      | google_analytics | strictly_needed |
      | do not use       | do not use      |
    And I click the "Save changes" button and I go back to "<page>"
    And cookies are configured as below:
      | google_analytics | strictly_needed |
      | do not use       | do not use      |
    And I cannot sign in with valid user credentials

    Examples:
      | page         |
      | Landing page |
      | Sign in      |
