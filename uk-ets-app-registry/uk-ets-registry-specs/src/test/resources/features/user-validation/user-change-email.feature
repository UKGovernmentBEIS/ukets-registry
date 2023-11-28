@functional-area-user-validation
@deleteUnregisteredUser
@deleteNewTestUser

Feature: User validation - User change email

  Epic: User registration and Sign-in
  Version: v2.6 (05/08/2020)
  Story: Change email
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=23&modificationDate=1596635430000&api=v2

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
    When I click the "Change your email address" link
    Then I am presented with the "Email change" screen

  @test-case-id-08899332831
  Scenario: As an user I get an error summary when I apply invalid actions during email change process
    # no value in any field
    When I click the "Continue" button
    Then I see an error summary with "enter your new email addressconfirm your new email addressenter the 6-digit code shown in the authenticator app"
	# no code entered
    When I enter value "test2email@test2email.com" in "New email address" field
    And I enter value "test2email@test2email.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "Enter the 6-digit code shown in the authenticator app"
    # different email and confirm email
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "1_test2email@test2email.com" in "New email address" field
    And I enter value "2_test2email@test2email.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "invalid re-typed email address. the new email address and the re-typed new email address should matchenter the 6-digit code shown in the authenticator app"
	# wrong email formats
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "test1" in "New email address" field
    And  I enter value "test1" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "enter an email address in the correct format, like name@example.comenter an email address in the correct format, like name@example.comenter the 6-digit code shown in the authenticator app"
    # try to use the existing email for the current user
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "test_enrolled_user@test.com" in "New email address" field
    And  I enter value "test_enrolled_user@test.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "enter the 6-digit code shown in the authenticator app"
    # try to use an existing email of another user
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "senior_user_0@test.com" in "New email address" field
    And  I enter value "senior_user_0@test.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "enter the 6-digit code shown in the authenticator app"
	# invalid email length
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "characters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emairacters257emcharacters257lcharacters257emailchaters257echaracters257email@characters257email.com" in "New email address" field
    And  I enter value "characters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emairacters257emcharacters257lcharacters257emailchaters257echaracters257email@characters257email.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "enter an email address in the correct format, like name@example.comenter an email address in the correct format, like name@example.comenter the 6-digit code shown in the authenticator app"
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                                                                                                                      |
      | New email address         | characters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emairacters257emcharacters257lcharacters257emailchaters257echaracters257email@characters257email.co |
      | Confirm new email address | characters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emailcharacters257emairacters257emcharacters257lcharacters257emailchaters257echaracters257email@characters257email.co |
    # invalid one time password
    And I clear the "New email address" field
    And I clear the "Confirm new email address" field
    When I enter value "new_test@test.com" in "New email address" field
    And  I enter value "new_test@test.com" in "Confirm new email address" field
    And I click the "Continue" button
    Then I see an error summary with "Enter the 6-digit code shown in the authenticator app"

  @sampling-smoke @test-case-id-08899332889
  Scenario: As user I can change my email
    When I enter value "new_test@test.com" in "New email address" field
    And  I enter value "new_test@test.com" in "Confirm new email address" field
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Continue" button
    Then I am presented with the "Email change verification" screen
    # Click the email link in the email sent to the new address
    And I receive an "Confirm your new email address - UK Emissions Trading Registry" email message regarding the "new_test@test.com" email address
    When I click the correct email link
    Then I am presented with the "Email change confirmation" screen
    And The page "contains" the "Email address update request successfully submitted" text
    # As admin claim and approve the task
    When I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin" user
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
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    And The page "contains" the "The request ID is" text
    # As the end-user, authenticate with otp and login using the new email
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    When I enter value "new_test@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    And User "user_unregistered@test.com" two factor authentication corresponds to "new_test@test.com" user due to email change
    And I get a new otp based on the existing secret for the "new_test@test.com" user
    Then I am presented with the "Sign in 2fa authentication" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user new_test@test.com" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Pink Floyd" text
