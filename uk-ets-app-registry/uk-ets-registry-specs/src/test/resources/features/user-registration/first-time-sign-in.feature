@functional-area-user-registration
@deleteUnregisteredUser

 #
 # In test automation note scope, FreeOTP mobile application is technically simulated by speakeasy for node.js library.
 #

Feature: User registration - First time sign in

  Epic: User registration and Sign-in
  Story: (& 5.2.1) Setup two factor authentication
  Version: v3.1 (09/10/2020)
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=14&modificationDate=1571143521449&api=v2

  Background:
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    And I accept all cookies
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
    And I get a User Id
    And The page "contains" the "Your user ID" text
    And The page "contains" the "You must record your user ID and keep it in a safe place." text
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Set up 2 factor authentication" screen
    And The page "contains" the "Set up two factor authentication" text

  @sampling-smoke @test-case-id-57122729755
  Scenario: As unregistered user implement registration and sign in using 2FA by SCANNING QR CODE setup for first sign in using correct algorithm within time allowed
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen

  @sampling-smoke @test-case-id-57122729764
  Scenario: As admin user I can see a New OTP has been activated event generated after a new registered user first time sign in
    # first time successful sign in
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen
    # sign in as admin to ensure that the event has been created
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    When I enter value "correct userId" in "Name or user ID textbox" field
    And I click the "Search" button
    And I click the "result row number 1" link
    Then I am presented with the "User details" screen
    When I click the "user history and comments left side" link
    Then The page "contains" the "New OTP has been activated" text

  @sampling-smoke @test-case-id-57122729787
  Scenario: As unregistered user implement registration and sign in using MANUAL 2FA setup for first sign in using correct algorithm within time allowed
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen

  @sampling-smoke @test-case-id-57122729798
  Scenario: As unregistered user implement registration I cannot sign in when verification code is incorrect
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "incorrect" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    And I see an error summary with "enter the 6-digit code again"

  @test-case-id-32473829606
  Scenario Outline: As unregistered user implement registration I cannot sign in when algorithm hash is not sha256
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "<algorithm>" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    And I see an error summary with "enter the 6-digit code again"

    Examples:
      | algorithm |
      | sha1      |
      | sha512    |

  @test-case-id-57122729820
  Scenario: As unregistered user implement registration I cannot sign in when otp type is hotp instead of totp
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "sha1" algorithm and "hotp" otp standard for "6" digits
    And I click the "Continue" button
    And I see an error summary with "enter the 6-digit code again"

  @test-case-id-57122729828
  Scenario: As unregistered user implement registration I cannot sign in when digits generated are 8 instead of 6
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "8" digits
    And I click the "Continue" button
    And I see an error summary with "enter the 6-digit code again"

  @exec-manual @test-case-id-57122729837
  Scenario: As unregistered user implement registration I can sign in when verification code is set 29 seconds later
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "29" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen

  @exec-manual @test-case-id-57122729848
  Scenario: As unregistered user implement registration I cannot sign in when verification code is set 31 seconds later
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "31" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    And I see an error summary with "Invalid authenticator code"
