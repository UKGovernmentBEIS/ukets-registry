@functional-area-user-validation

Feature: User validation - User Deactivation

  @test-case-id-73891871911
  Scenario Outline: As a user I can submit a user deactivation request for myself and an admin can successfully approve it
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "An update request has been submitted" text
    # sign in as another admin, claim and approve the task
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
    And The page "contains" the "Deactivate user" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    # ensure status before approval is Deactivation pending
    And The page "contains" the "Deactivation pending" text
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that user status is now DEACTIVATED
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value       |
      | user basic information | SENIOR ADMIN USER |
      | user status            | DEACTIVATED       |

    @sampling-smoke
    Examples:
      | user         |
      | senior admin |

    @exec-manual
    Examples:
      | user         |
      | junior admin |
      | read only    |
      | authority    |
      | validated    |
      | enrolled     |
      | registered   |

  @test-case-id-245087181 @exec-manual
  Scenario Outline: As sra only I can see the user link ion task details page
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I click the "Submit" button
    # assert that request is submitted
    Then The page "contains" the "An update request has been submitted" text
    # ensure than sra only can see the user link
    When I navigate to the task details page
    Then The "user ID link" is <status> in "User Id" row

    Examples:
      | user            | status        |
      | senior admin    | available     |
      | junior admin    | not available |
      | read only admin | not available |

  @test-case-id-73891871196 @sampling-smoke
  Scenario: As an authorized representative I can submit a user deactivation request for myself and an admin can successfully approve it
    Given I sign in as "authorized representative" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "An update request has been submitted" text
    # sign in as another admin, claim and approve the task
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
    And The page "contains" the "Deactivate user" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    # ensure status before approval is Deactivation pending
    And The page "contains" the "Deactivation pending" text
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that user status is now DEACTIVATED
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                    |
      | user basic information | AUTHORIZED REPRESENTATIVE USER |
      | user status            | DEACTIVATED                    |

  @test-case-id-73891871888 @exec-manual
  Scenario: As a user I cannot submit a second deactivation request for users in pending deactivation status
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    When I click the "Request Update" button
    Then I am presented with the "User details" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "User details" screen
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    Then I am presented with the "User details" screen
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "An update request has been submitted" text
    # sign in as another admin, claim and approve the task
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
    And The page "contains" the "Deactivate user" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    # ensure status before approval is DEACTIVATION_PENDING
    And The page "contains" the "DEACTIVATION_PENDING" text
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    And The page "contains" the "The proposed request has been approved" text
    # ensure that I cannot submit another deactivation request for the same user
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    When I click the "Request Update" button
    Then I am presented with the "User details" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I see an error summary with "You cannot submit deactivation request for a user in status deactivated or pending deactivation."

  @test-case-id-7389187199
  Scenario Outline: As an SRA or JRA user I can request a user deactivation for another user and deactivation is successful after task approval
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    When I click the "Request Update" button
    When I click the "Deactivate user" button
    And I click the "Continue" button
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    And I click the "Submit" button
    # assert that request is submitted
    And The page "contains" the "An update request has been submitted" text
    # sign in as another admin, claim and approve the task
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
    And The page "contains" the "Deactivate user" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    # ensure status before approval is DEACTIVATION_PENDING
    And The page "contains" the "Deactivation pending" text
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that user status is now DEACTIVATED
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value       |
      | user basic information | SENIOR ADMIN USER |
      | user basic status      | DEACTIVATED       |

    Examples:
      | user         |
      | senior admin |

    @exec-manual
    Examples:
      | user         |
      | junior admin |

  @test-case-id-73891871031 @exec-manual
  Scenario: As read only admin I cannot proceed with user deactivation request for another user
    Given I sign in as "read only admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    Then The page "does not contain" the "Deactivate user" text

  @test-case-id-73891871912 @exec-manual
  Scenario: As a user I cannot submit a user deactivation request for myself if I do not fill the justification textbox
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I see an error summary with "Enter a reason for deactivating this user"

  @test-case-id-73891871913 @exec-manual
  Scenario Outline: As a user I can cancel a user deactivation request request for myself
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Request update" button
    Then I am presented with the "My Profile" screen
    When I click the "Deactivate user" button
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    When I enter value "About to leave registry" in "Reason" field
    And I click the "Continue" button
    Then I am presented with the "My Profile" screen
    And I click the "Cancel" button
    Then I am presented with the "My Profile" screen
    And I click the "Cancel request" button
    Then I am presented with the "My Profile" screen
    And The page "contains" the "Request Update" text
    Examples:
      | user                      |
      | senior admin              |
      | authorized representative |