@functional-area-dashboard
@exec-manual

  #
  # This feature file stands for manual execution.
  #

Feature: Dashboard - Landing page

  # Epic: User registration and sign-in
  # Version: v3.1 (09/10/2020)
  # Story: 4.1.1   Visit Landing page
  # URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=12&amp;modificationDate=1568901007000&amp;api=v2

  @test-case-id-06688416639
  Scenario: As a not signed in user I access the Landing page and I can Sign in
    When I navigate to the "Landing" screen
    Then The page "contains" the "GOV.UK" text
    And  The page "contains" the "UK Emissions Trading Registry" text
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen

  @test-case-id-06688416647
  Scenario: As a not signed in user I access the Landing page and I can Start the Registration process
    When I navigate to the "Landing" screen
    Then The page "contains" the "GOV.UK" text
    And  The page "contains" the "UK Emissions Trading Registry" text
    When I click the "Start the Registration Process" link
    Then I am presented with the "Register" screen

  @test-case-id-06688416655
  Scenario: As a not signed in user I access the Landing page and I can go to Gov uk
    When I navigate to the "Landing" screen
    Then The page "contains" the "GOV.UK" text
    And  The page "contains" the "UK Emissions Trading Registry" text
    When I click the "Gov.uk" link
    Then I am presented with the "Gov.uk" screen

  @test-case-id-06688416663
  Scenario: As a not signed-in user, I access the Landing page to Sign in but the service is down
    When I navigate to the "Landing" screen
    Then The page "contains" the "GOV.UK" text
    And  The page "contains" the "UK Emissions Trading Registry" text
    When I click the "Sign out" link
    Then I am presented with the "service is unavailable" screen
