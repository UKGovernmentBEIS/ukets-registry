@functional-area-user-validation
@deleteUnregisteredUser

Feature: User validation - User forgot password

  # Epic: User registration and Sign-in
  # Version: v3.1 (09/10/2020)
  # Story: Forgot password
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
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    And The page "contains" the "Sign in to the UK Emissions Trading Registry" text
    And The page "contains" the "Email address" text
    And The page "contains" the "Password" text
    When I click the "I have forgotten or I have to reset my password" link
    Then I am presented with the "Forgot password" screen
    And The page "contains" the "Reset my password" text

  @sampling-smoke @test-case-id-44916833129 @sampling-mvp-smoke
  Scenario: As an enrolled user I cannot confirm the forgot my password process filling invalid email address
	# try to confirm with empty email or continue with invalid value
    And I click the "Confirm" button
    Then I see an error summary with "Email address is required"
    And I see an error detail with id "event-name-error" and content "Email address is required."
    When I enter value "abc" in "Email address" field
    And I click the "Confirm" button
    Then I see an error summary with "Enter an email address in the correct format, like name@example.com"
    And I see an error detail with id "event-name-error" and content "Enter an email address in the correct format, like name@example.com"

  @sampling-smoke @test-case-id-44916833141
  Scenario: As an enrolled user I can request another password reset during forgot password process
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Confirm" button
    Then I am presented with the "Forgot password" screen
    And The page "contains" the "Password reset email sent" text
    And The page "contains" the "user_unregistered@test.com" text
    # case of I didn't get an email < request another password reset email
    And I click the "I didnt get an email" link
    Then I am presented with the "Forgot password" screen
    And I click the "request another password reset email" link
    Then I am presented with the "Forgot password" screen
    And The page "contains" the "Reset my password" text

  @test-case-id-44916833155
  Scenario: As an enrolled user I cannot complete the forgot my password process when I enter invalid values in reset screen
    And I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Confirm" button
    Then I am presented with the "Forgot password" screen
    And The page "contains" the "Password reset email sent" text
    And The page "contains" the "user_unregistered@test.com" text
    And I receive an "Password reset request - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Forgot password reset" screen
    And The page "contains" the "Reset your password" text
    # try to continue without password set
    And I enter value "abcdefg1234!@#$%^&*(" in "confirm password" field
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Continue" button
    Then I see an error summary with "Password and confirmed password are required"
    And I see an error detail with id "event-name-error" and content "Password and confirmed password are required"
    # try to continue without confirm password
    And I clear the "confirm password" field
    And I enter value "abcdefg1234!@#$%^&*(" in "password" field
    And I click the "Continue" button
    Then I see an error summary with "Password and confirmed password are required"
    And I see an error detail with id "event-name-error" and content "Password and confirmed password are required"
    # try to continue without otp
    And I clear the "password" field
    And I clear the "otp" field
    And I enter value "abcdefg1234!@#$%^&*(" in "password" field
    And I enter value "abcdefg1234!@#$%^&*(" in "confirm password" field
    And I click the "Continue" button
    Then I see an error summary with "OTP is required"
    And I see an error detail with id "event-name-error" and content "OTP is required."
    # try to continue with invalid otp
    And I enter value "123456" in "otp" field
    And I click the "Continue" button
    Then I see an error summary with "Invalid OTP code"
    # try to continue with different password and confirm password
    And I clear the "password" field
    And I clear the "confirm password" field
    And I enter value "password___________1" in "password" field
    And I enter value "password___________2" in "confirm password" field
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Continue" button
    Then I see an error summary with "Password and confirmed password should match"
    And I see an error detail with id "event-name-error" and content "Password and confirmed password should match"

  @sampling-smoke @test-case-id-44916833203
  Scenario: As an enrolled user I can successfully complete the forgot my password process
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Confirm" button
    Then I am presented with the "Forgot password" screen
    And The page "contains" the "Password reset email sent" text
    And The page "contains" the "user_unregistered@test.com" text
    And I receive an "Password reset request - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Forgot password reset" screen
    And The page "contains" the "Reset your password" text
    And I enter value "abcdefg1234!@#$%^&*(passwordNew" in "password" field
    And I enter value "abcdefg1234!@#$%^&*(passwordNew" in "confirm password" field
    # get otp
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Continue" button
    Then I am presented with the "Forgot password reset" screen
    And The page "contains" the "You have successfully reset your password" text
    # sign in with the new password
    When I click the "sign in to the UK Emissions Registry" link
    Then I am presented with the "Sign in" screen
    And The page "contains" the "Sign in to the UK Emissions Trading Registry" text
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "abcdefg1234!@#$%^&*(passwordNew" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Sign in 2fa authentication" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Pink Floyd" text

