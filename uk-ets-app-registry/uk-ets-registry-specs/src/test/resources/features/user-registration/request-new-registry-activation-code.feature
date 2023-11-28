@functional-area-user-registration
@sampling-smoke

Feature: User registration - Request new registry activation code

  Epic: User validation, enrolment and management
  Version: 2.2 (10/09/2020)
  Story: (4.2.2) As a Validated user, my Registry Activation Code has expired or email never received and I want to request a new one.
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @test-case-id-9497730062
  Scenario: As validated user I can request a new registry activation code because current code is not valid
    Given I sign in as "validated" user having current registry activation code issued before "32" days
    Then I am presented with the "Registry dashboard" screen
    When I click the "Enter registry activation code link" link
    Then I am presented with the "Registry Activation" screen
    # ensure that back link click works as expected
    When I click the "Back" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "Enter registry activation code link" link
    Then I am presented with the "Registry Activation" screen
    # try to add value not to all mandatory fields
    When I enter value "test" in "Textbox number 1" field
    And I click the "Continue" button
    Then I see an error summary with "Please enter your Registry Activation Code"
    When I enter value "test" in "Textbox number 2" field
    And I enter value "test" in "Textbox number 3" field
    And I enter value "test" in "Textbox number 4" field
    And I click the "Continue" button
    Then I see an error summary with "Please enter your Registry Activation Code"
    # try to Continue with invalid value
    And I enter value "test" in "Textbox number 5" field
    And I click the "Continue" button
    Then I see an error summary with "The registry activation code that you provided is invalid"
    And The page "contains" the "Enter your registry activation code" text
    And The page "contains" the "After you successfully enter your activation code you will be automatically signed out of the Registry." text
    When I click the "There's a problem with my code" link
    And I click the "Request a new registry activation code" link
    Then I am presented with the "Request a new registry activation code" screen
    And The page "contains" the "Request a new registry activation code" text
    # submit request
    When I click the "Submit request" button
    Then I am presented with the "Request new registry activation code submitted" screen
    And The page "contains" the "You have successfully requested a new activation code" text
    And The page "contains" the "Request ID" text
    When I click the "Back to home" link
    Then I am presented with the "Registry dashboard" screen
    When I click the "Sign out" button
    # sign in as senior admin and ensure that the Print letter with registry activation code task is created
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Search" button
    Then The page "contains" the "Print letter with registry activation code" text

  @test-case-id-9497730114
  Scenario: As validated user I can request a new registry activation code because I have not received the email with initial code
    Given I sign in as "validated" user having current registry activation code issued before "32" days
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Welcome VALIDATED USER" text
    And The page "contains" the "I have not received my Registry activation code" text
    When I click the "I have not received my Registry activation code" link
    Then The page "contains" the "Request a new registry activation code" text
    When I click the "Request a new registry activation code link" link
    Then I am presented with the "Request a new registry activation code" screen
    # submit request
    When I click the "Submit request" button
    Then I am presented with the "Request new registry activation code submitted" screen
    And The page "contains" the "You have successfully requested a new activation code" text
    And The page "contains" the "Request ID" text
    When I click the "Back to home" link
    Then I am presented with the "Registry dashboard" screen
    When I click the "Sign out" button
    # sign in as senior admin and ensure that the Print letter with registry activation code task is created
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Search" button
    Then The page "contains" the "Print letter with registry activation code" text

  @test-case-id-9497730146
  Scenario: As validated user I cannot request a new registry activation code because there is another pending request for a new code
    Given I sign in as "validated" user having current registry activation code issued before "32" days
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Welcome VALIDATED USER" text
    And The page "contains" the "I have not received my Registry activation code" text
    When I click the "I have not received my Registry activation code" link
    Then The page "contains" the "Request a new registry activation code" text
    When I click the "Request a new registry activation code link" link
    Then I am presented with the "Request a new registry activation code" screen
    And The page "contains" the "Request a new registry activation code" text
    # submit request
    When I click the "Submit request" button
    Then I am presented with the "Request new registry activation code submitted" screen
    And The page "contains" the "You have successfully requested a new activation code" text
    And The page "contains" the "Request ID" text
    When I click the "Back to home" link
    Then I am presented with the "Registry dashboard" screen
    When I click the "I have not received my Registry activation code" link
    Then The page "contains" the "Request a new registry activation code" text
    When I click the "Request a new registry activation code link" link
    Then I am presented with the "Request a new registry activation code" screen
    And The page "contains" the "Request a new registry activation code" text
    # try to submit request again
    When I click the "Submit request" button
    Then I see an error summary with "The registry administrator is already processing your Registry Activation Code. You 'll receive your Registry Activation Code in a separate email as soon as possible"

  @test-case-id-9497730183
  Scenario: As validated user I can request a new registry activation code even if current code has not expired yet
    Given I sign in as "validated" user having current registry activation code issued before "31" days
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Welcome VALIDATED USER" text
    And The page "contains" the "I have not received my Registry activation code" text
    When I click the "I have not received my Registry activation code" link
    Then The page "contains" the "Request a new registry activation code" text
    When I click the "Request a new registry activation code link" link
    Then I am presented with the "Request a new registry activation code" screen
    And The page "contains" the "Request a new registry activation code" text
    # submit request
    When I click the "Submit request" button
    Then The page "contains" the "You have successfully requested a new activation code" text