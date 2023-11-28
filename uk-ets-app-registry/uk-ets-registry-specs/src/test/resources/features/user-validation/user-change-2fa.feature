@functional-area-user-validation
@deleteUnregisteredUser

  # Epic: User registration and Sign-in
  # Version: v2.7 (12/08/2020)
  # Story: 4.2.7	Request to change two-factor authenticator
  # URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=24&modificationDate=1597234106000&api=v2

Feature: User validation - User change 2fa

  # Main steps:
  # 1.	The user requests to change the 2FA device (through My Profile)
  # 2.	An ‘Approve Request to change 2FA’ task is created for the Senior Admin (see story (Task) Approve Request to change 2FA)
  # 3.	The admin calls the user and approves the task (if verification is successful)
  # 4.	System sends email notification to change the 2FA device
  # 5.	The user clicks the email link to proceed with the QR code generation for the new device
  # 6.	System generates a new QR code
  # 7.	The user sets up the QR code in new 2FA device and confirms successful setup

  Background:
    # setup two factor authentication for an unregistered user
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    When I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                |
      | email   | user_unregistered@test.com |
    And I click the "Continue" button
    Then I am presented with the "Email Address" screen
    And I receive an "Confirm your work email address - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Email Confirmed" screen
    And I click the "Continue" button
    Then I am presented with the "Enter Personal Details" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Enter Work Contact Details" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    And I enter value "What a beautiful day" in "Memorable phrase" field
    When I click the "Continue" button
    Then I am presented with the "Choose Password" screen
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Check answers and submit" screen
    And I click the "Submit" button
    Then I am presented with the "Registered" screen
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Change two factor authentication" link
    And I am presented with the "Token change" screen

  @test-case-id-78735232645
  Scenario: As user I cannot set 2fA to a new device when I dont fill mandatory fields
	# error shown on Continue click with empty reason field
    When I click the "Continue" button
    Then I see an error summary with "Explain why you are requesting this change"
    And I see an error detail for field "event-name" with content "Error: Explain why you are requesting this change"
    And The page "contains" the "Enter the reason for requesting the change" text
    And The page "contains" the "Authenticator app installed on My Phone" text
    When I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And The page "contains" the "Change two factor authentication" text
    And The page "contains" the "Enter the code shown in the authenticator app" text
    # try to continue without setting otp or setting incorrect otp
    When I click the "Submit" button
    Then I see an error summary with "Enter a code"
    When I enter value "aaaaa" in "otp" field
    Then I see an error summary with "Enter a code"

  @test-case-id-78735232664
  Scenario: As user I can request for a 2fA change
    When I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Token change verification" screen
    And The page "contains" the "Two factor authentication update request successfully submitted." text
    And The page "contains" the "Request ID" text
    When I click the "Back to My Profile" link
    Then I am presented with the "My Profile" screen

  @sampling-smoke @test-case-id-78735232679
  Scenario: When I request for a 2fA change as user then correct task is created
    And I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Token change verification" screen
    And The page "contains" the "Two factor authentication update request successfully submitted." text
    And The page "contains" the "Request ID" text
    When I click the "Back to My Profile" link
    Then I am presented with the "My Profile" screen
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    # ensure task is create
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Approve two factor authentication change request" text
    And The page "contains" the "UNCLAIMED" text

  @test-case-id-78735232703
  Scenario: As user I cannot request for a 2fA change again when then is already a pending request
    And I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Token change verification" screen
    And The page "contains" the "Two factor authentication update request successfully submitted." text
    And The page "contains" the "Request ID" text
    When I click the "Back to My Profile" link
    Then I am presented with the "My Profile" screen
    # try to submit again
    When I click the "Change two factor authentication" link
    And I am presented with the "Token change" screen
    When I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Submit" button
    Then I see an error summary with "There is already another device change request pending approval"

  @sampling-smoke @test-case-id-78735232728
  Scenario: As user I can change the 2fA when the task is approved and verification email link is clicked
    When I enter value "Have new phone" in "reason" field
    And I click the "Continue" button
    Then I am presented with the "Token change otp" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Submit" button
    Then I am presented with the "Token change verification" screen
    And The page "contains" the "Two factor authentication update request successfully submitted." text
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
	# As admin claim and approve the Request to change 2FA
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Sign out" button
    And I receive an "Two factor authentication device update request - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    # change 2fa
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Set up 2 factor authentication" screen
    And I click the "Problems with scanning?" link
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen
