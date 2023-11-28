@functional-area-user-validation
@deleteUnregisteredUser

Feature: User validation - User change 2fa emergency

  Epic: User registration and Sign-in
  Version: v2.7 (12/08/2020)
  Story: 4.2.8	Request to change two-factor authenticator due to emergency
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=24&modificationDate=1597234106000&api=v2

  # Main steps:
  # 1. The user selects the relevant link on sign-in page  (see Sign in story)
  # 2. The user enters the email address
  # 3. System sends an email link with an emergency button
  # 4. The user clicks on the panic button on the email
  # 5. System suspends the user account and creates ‘Approve 2FA emergency change request’ task for admin (see story (Task) Approve 2FA emergency change request). For security purposes the user is not notified that his account is suspended
  # 6. The admin calls the user and approves the task (if verification is successful)
  # 7. System sends email notification to setup a new 2FA device
  # 8. The user clicks the email link to proceed with the QR code generation for the new device
  # 9. System generates a new QR code
  # 0. The user sets up the QR code in new 2FA device and confirms successful setup

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
    # sign out
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    And The page "contains" the "Sign in to the UK Emissions Trading Registry" text
    When I click the "I have lost access to my authenticator app" link
    And I am presented with the "Emergency otp change" screen
    And The page "contains" the "Request emergency access to my account" text

  @test-case-id-31313732421
  Scenario: As not logged in user I cannot complete lost 2fA device process if I dont enter mandatory fields or I enter invalid values
    When I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # try to continue without filling the email
    When I click the "Submit" button
    Then I see an error summary with "Enter your email address"
    And I see an error detail with id "event-name-error" and content "Enter your email address"
    # try to enter invalid email
    When I enter value "abc" in "Email address" field
    And I click the "Submit" button
    Then I see an error summary with "Enter an email address in the correct format, like name@example.com"
    And I see an error detail with id "event-name-error" and content "Enter an email address in the correct format, like name@example.com"

  @test-case-id-31313732436
  Scenario: As not logged in user I can navigate backwards during 2fA device process
    When I click the "Back" button
    Then I am presented with the "Sign in" screen
    When I click the "I have lost access to my authenticator app" link
    And I am presented with the "Emergency otp change" screen
    And The page "contains" the "Request emergency access to my account" text
    When I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    When I click the "Back" link
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Request emergency access to my account" text

  @test-case-id-31313732450
  Scenario: As not logged in user I can request emergency access again during 2fA device process
    When I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # enter email and continue
    When I enter value "random@random.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "We have sent an emergency access link to" text
    And The page "contains" the "random@random.com" text
    # request emergency access again
    When I click the "I didnt get an email" button
    And I click the "request emergency access again" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "If you have lost your device and cannot access your authenticator app you must request an emergency access link email." text
    When I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text

  @exec-manual @test-case-id-31313732471
  Scenario: As not logged in user I cannot complete lost 2fA device process when there is another pending same task
    And I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # enter email and continue
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "We have sent an emergency access link to" text
    And The page "contains" the "user_unregistered@test.com" text
	# Click the email link
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    And The page "contains" the "Emergency access request submitted" text
    And The page "contains" the "Request ID" text
    When I try to complete again a lost 2fA device process
    Then I see an error summary with "There is already an emergency access request pending approval"

  @sampling-smoke @test-case-id-31313732491
  Scenario: As not logged in user I cannot complete lost 2fA device process when task is pending because my account is currently disabled
    And I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # enter email and continue
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "We have sent an emergency access link to" text
    And The page "contains" the "user_unregistered@test.com" text
	# Click the email link
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency otp change submitted" screen
    And The page "contains" the "Emergency access request submitted" text
    And The page "contains" the "Request ID" text
    # ensure Account is disabled
    When I click the "Back to Sign in" link
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"

  @sampling-smoke @test-case-id-31313732517
  Scenario: As not logged in user I can submit a request regarding lost 2fA device
    When I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # enter email and continue
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "We have sent an emergency access link to" text
    And The page "contains" the "user_unregistered@test.com" text
	# Click the email link
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency otp change submitted" screen
    And The page "contains" the "Emergency access request submitted" text
    And The page "contains" the "Request ID" text

  @sampling-smoke @test-case-id-31313732536
  Scenario: As not logged in user I can complete lost 2fA device process when task is approved
    And I click the "Continue" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "Enter the email address associated with your UK Registry account" text
    # enter email and continue
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency otp change" screen
    And The page "contains" the "We have sent an emergency access link to" text
    And The page "contains" the "user_unregistered@test.com" text
	# Click the email link
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency otp change submitted" screen
    And The page "contains" the "Emergency access request submitted" text
    And The page "contains" the "Request ID" text
	# As admin claim and approve proposal task
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request two factor authentication emergency access" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
	# Click the email link in the email sent by the system
    And I receive an "Two factor authentication device update request - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Set up 2 factor authentication" screen
    And The page "contains" the "Set up two factor authentication" text
