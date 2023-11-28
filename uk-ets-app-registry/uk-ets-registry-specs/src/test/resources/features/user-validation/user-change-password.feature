@functional-area-user-validation
@deleteUnregisteredUser

Feature: User validation - User change password

  # Epic: User registration and Sign-in
  # Version: v2.6 (05/08/2020)
  # Story: Change Password
  # URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=23&modificationDate=1596635430000&api=v2

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
    When I click the "Change your password" link
    Then I am presented with the "Change password" screen

  @sampling-smoke @test-case-id-11745332995
  Scenario: As a registered user I can change my password
    When I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Current password" field
    And I enter value "th1s1sAp@ssw0rd_2" in "New password" field
    And I enter value "th1s1sAp@ssw0rd_2" in "Confirm new password" field
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Continue" button
    Then I am presented with the "Change password confirmation" screen
    When I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    # try to access with the old credentials
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    And I see an error summary with "Your email address or password is incorrect"
    # access with the new credentials
    And I clear the "Password" field
    When I enter value "th1s1sAp@ssw0rd_2" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Sign in 2fa authentication" screen

  @test-case-id-11745333018
  Scenario: As a registered user I cannot change my password when I apply invalid actions
    # empty otp
    When I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Current password" field
    And I enter value "th1s1sAp@ssw0rd_2" in "New password" field
    And I enter value "th1s1sAp@ssw0rd_2" in "Confirm new password" field
    And I click the "Continue" button
    Then I see an error summary with "Enter the 6-digit code shown in the authenticator app"
	# empty current password
    And I clear the "Current password" field
    When I enter value "123456" in "otp" field
    And I click the "Continue" button
    Then I see an error summary with "Enter the current password"
    # empty new password
    When I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Current password" field
    And I clear the "New password" field
    When I click the "Continue" button
    Then I see an error summary with "Password and confirmed password are required"
    # empty confirm password
    When I enter value "th1s1sAp@ssw0rd_2" in "New password" field
    And I clear the "Confirm new password" field
    When I click the "Continue" button
    Then I see an error summary with "Password and confirmed password are required"
    # different new password and confirm password
    When I enter value "th1s1sAp@ssw0rd_3" in "Confirm new password" field
    And I click the "Continue" button
    Then I see an error summary with "Password and confirmed password should match"
    # invalid old password
    And I clear the "Current password" field
    When I enter value "invalid old password" in "Current password" field
    And I clear the "Confirm new password" field
    When I enter value "th1s1sAp@ssw0rd_2" in "Confirm new password" field
    And I click the "Continue" button
    Then I see an error summary with "current password must not contain any spaces"
    # weak new password
    And I clear the "Current password" field
    When I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Current password" field
    And I clear the "New password" field
    When I enter value "123" in "New password" field
    And I clear the "Confirm new password" field
    And I enter value "123" in "Confirm new password" field
    And I click the "Continue" button
    Then I see an error summary with "password and confirm password should be 10 characters or more"
    # wrong otp
    And I clear the "New password" field
    And I clear the "Confirm new password" field
    When I enter value "th1s1sAp@ssw0rd_2" in "New password" field
    And I enter value "th1s1sAp@ssw0rd_2" in "Confirm new password" field
    When I click the "Continue" button
    Then I see an error summary with "Invalid code. Please try again"
