@functional-area-user-validation

Feature: User validation - Update user details

  Epic: UK Registry - User validation enrolment and management.docx
  Version: v2.6
  Story: Search Update user details

  @test-case-id-97064369231
  Scenario Outline: As a user I can update my own user details
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I click the "Submit" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "An update request has been submitted" text
    # ensure task is created, sign in as (another) sra, claim and approve task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure new data is depicted
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And The page "contains" the "new test work address" text

    @sampling-smoke
    Examples:
      | user         |
      | senior admin |

    @exec-manual
    Examples:
      | user                      |
      | enrolled                  |
      | validated                 |
      | registered                |
      | junior admin              |
      | read only admin           |
      | authorised representative |
      | authority                 |

  @test-case-id-97064369233
  Scenario: As an admin user I cannot submit a second user update request for my user details when there is already a pending request for my user details
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I click the "Submit" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "An update request has been submitted" text
    When I click the "Back to the user details" link
    Then I am presented with the "My Profile" screen
    # try to request update again
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I see an error summary with "Another request to update user details is pending approval"

  @test-case-id-97064369232 @exec-manual
  Scenario Outline: As admin user I can update user details of another user of non deactivated or pending deactivation user status
    Given I sign in as "<user>" user
    And I have created 1 "registered" users
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "User details" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    # update user personal details
    Then I am presented with the "User details" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "User details" screen
    # update work contact details
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "User details" screen
    # update memorable phrase
    And I enter value "mem phrase 1" in "memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "User details" screen
    # check and confirm screen
    And The page "contains" the "Check the update and confirm" text
    When I click the "Submit" button
    Then I am presented with the "User details" screen
    And The page "contains" the "An update request has been submitted" text
    # ensure task is created, sign in as (another) sra, claim and approve task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure new data is depicted
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And The page "contains" the "new test work address" text

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-97064369234 @exec-manual
  Scenario: As an admin user I cannot approve my own user details update task
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I click the "Submit" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "An update request has been submitted" text
    # ensure task is created, sign in as (another) sra, claim and approve task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

  @test-case-id-97064369237 @exec-manual
  Scenario: As an admin user I can navigate back to the user details page after user details task submit
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I click the "Submit" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "An update request has been submitted" text
    When I click the "Back to the user details" link
    Then I am presented with the "My Profile" screen

  @test-case-id-97064369235 @exec-manual
  Scenario: As an admin user I can navigate backwards during user details update ensuring that data is kept
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    # back click functionality
    When I click the "Back" link
    Then I am presented with the "My Profile" screen
    And I see the following fields having the values:
      | fieldName      | field_value           |
      | Address line 1 | new test work address |
    When I click the "Back" link
    Then I am presented with the "My Profile" screen
    And I see the following fields having the values:
      | fieldName            | field_value |
      | Date of birth: Day   | 01          |
      | Date of birth: Month | 01          |
      | Date of birth: Year  | 1980        |
    When I click the "Back" link
    Then I am presented with the "My Profile" screen
    And I see data retained

  @test-case-id-97064369236 @exec-manual
  Scenario: As an admin user during user details update I can cancel the operation
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure old details are correct
    And The page "contains" the "Test Address 7" text
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Update user details" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I enter value "01" in "Date of birth: Day" field
    And I enter value "01" in "Date of birth: Month" field
    And I enter value "1980" in "Date of birth: Year" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I clear the "Address line 1" field
    And I enter value "new test work address" in "Address line 1" field
    When I click the "Continue" button
    Then I am presented with the "My Profile" screen
    # cancel operation and ensure request update is feasible again
    When I click the "Cancel" link
    Then I am presented with the "My Profile" screen
    When I click the "Cancel request" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "Request update" text