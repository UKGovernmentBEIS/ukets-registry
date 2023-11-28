@functional-area-user-validation
@deleteUnregisteredUser

Feature: User validation - Change 2FA and Password

  Epic: User registration and Sign-in
  Version: v3.1 (09/10/2020)
  Story: 4.2.10  Lost password and two-factor-authentication request
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=28&modificationDate=1602238265000&api=v2

  # Main steps:
  # 1.	The user selects the relevant link on sign-in page
  # 2.	The user enters the email address
  # 3.	System sends an email link with an emergency button
  # 4.	The user clicks on the emergency button on the email
  # 5.	System suspends the user account and creates ‘Request password and two factor authentication emergency access’ task for admin (see story (Task) Request password and two factor authentication emergency access). For security purposes the user is not notified that his account is suspended
  # 6.	The admin calls the user and approves the task (if verification is successful)
  # 7.	System unsuspends the user
  # 8.	System sends email notification to the user that the request has been approved, together with a link to reset the password
  # 9.	User resets the password
  # 10.	User signs in
  # 11.	System generates a new QR code
  # 12.	User sets up the new QR code in new 2FA device and confirms successful setup

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
    And The page "contains" the "Your user ID" text
    And The page "contains" the "You must record your user ID and keep it in a safe place." text
    And I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Set up 2 factor authentication" screen
    And The page "contains" the "Set up two factor authentication" text
    # setup 2fa
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen
    And I click the "Sign out" button
    Then I am presented with the "Sign in" screen

  @sampling-smoke @test-case-id-795541331904 @sampling-mvp-smoke
  Scenario: As a registered user I can activate the Lost Password and 2FA process
    # access "I have forgotten my password and lost access to my authenticator app"
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Emergency password and otp change" screen
    When I click the "Continue" button
    Then I am presented with the "Emergency password and otp change" screen
    When I enter value "user_unregistered@test.com" in "Email address" field
    # submit and get email
    When I click the "Submit" button
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access request submitted" screen
    And The page "contains" the "Request ID" text
    And I see the following fields having the values:
      | fieldName        | field_value                        |
      | submit title     | Emergency access request submitted |
      | request Id value | [not empty nor null value]         |
	# Ensure systems has suspended the user until an admin approves the corresponding task and an email is received regarding login failure
    When I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"
    # approve task as admin
    When I refresh the current page
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request password and two factor authentication emergency access" text
    And The page "contains" the "UNCLAIMED" text
	# The admin calls the user and approves the task (if verification is successful)
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The request ID is" text
    And I see the following fields having the values:
      | fieldName          | field_value                            |
      | task handled title | The proposed request has been approved |
      | request Id value   | [not empty nor null value]             |
    And I click the "Sign out" link
	# System unsuspends the user and sends them an approved request email notification with password reset link
    And I receive an "Reset your password and two factor authenticator app - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    # User resets password
    Then I am presented with the "Reset your password and authenticator app" screen
    When I click the "Continue" button
    And The page "contains" the "You must create a strong and memorable password. Your password should be 10 or more characters. You can use a mix of letters, numbers or symbols, or a combination of three random words." text
    When I click the "Continue" button
    Then I am presented with the "Enter your new password" screen
    And The page "contains" the "Enter your new password" text
    When I enter value "new_password!@#5233!" in "New Password" field
    And I enter value "new_password!@#5233!" in "Confirm password" field
    And I click the "Continue" button
	# User sets up the new QR code in new 2FA device and signs in
    Then I am presented with the "Set up 2 factor authentication" screen
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    And I click the "Continue" button
    Then I am presented with the "Credentials successfully changed" screen
    And I click the "sign in to Registry" link
    Then I am presented with the "Sign in" screen
    # ensure that I can login again with the new credentials
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "new_password!@#5233!" in "Password" field
    And I click the "Sign in" button
    Then I am presented with the "Sign in 2fa authentication" screen
    And I get a new otp based on the existing secret for the "user_unregistered@test.com" user
    When I enter value "correct otp for user user_unregistered@test.com" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    And The page "contains" the "Pink Floyd" text

  @test-case-id-795541331991
  Scenario: As a registered user after otp and password change I cannot login with the old credentials
    # access "I have forgotten my password and lost access to my authenticator app"
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Emergency password and otp change" screen
    When I click the "Continue" button
    Then I am presented with the "Emergency password and otp change" screen
    When I enter value "user_unregistered@test.com" in "Email address" field
    # submit and get email
    When I click the "Submit" button
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access request submitted" screen
    And The page "contains" the "Request ID" text
    And I see the following fields having the values:
      | fieldName        | field_value                        |
      | submit title     | Emergency access request submitted |
      | request Id value | [not empty nor null value]         |
	# Ensure systems has suspended the user until an admin approves the corresponding task and an email is received regarding login failure
    When I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"
    # approve task as admin
    When I refresh the current page
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request password and two factor authentication emergency access" text
    And The page "contains" the "UNCLAIMED" text
	# The admin calls the user and approves the task (if verification is successful)
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The request ID is" text
    And I see the following fields having the values:
      | fieldName          | field_value                            |
      | task handled title | The proposed request has been approved |
      | request Id value   | [not empty nor null value]             |
    And I click the "Sign out" link
	# System unsuspends the user and sends them an approved request email notification with password reset link
    And I receive an "Reset your password and two factor authenticator app - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    # User resets password
    Then I am presented with the "Reset your password and authenticator app" screen
    When I click the "Continue" button
    Then I am presented with the "Enter your new password" screen
    And The page "contains" the "Enter your new password" text
    When I enter value "new_password!@#5233!" in "New Password" field
    And I enter value "new_password!@#5233!" in "Confirm password" field
    And I click the "Continue" button
	# User sets up the new QR code in new 2FA device and signs in
    Then I am presented with the "Set up 2 factor authentication" screen
    And I click the "Problems with scanning?" link
    Then I am presented with the "Set up 2 factor authentication" screen
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Credentials successfully changed" screen
    And I click the "sign in to Registry" link
    Then I am presented with the "Sign in" screen
     # try to login with the old credentials
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"

  @test-case-id-65389531855
  Scenario Outline: As a registered user during otp and password change I cannot set invalid or weak password
    # access "I have forgotten my password and lost access to my authenticator app"
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Emergency password and otp change" screen
    When I click the "Continue" button
    Then I am presented with the "Emergency password and otp change" screen
    When I enter value "user_unregistered@test.com" in "Email address" field
    # submit and get email
    When I click the "Submit" button
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access request submitted" screen
    And The page "contains" the "Request ID" text
    And I see the following fields having the values:
      | fieldName        | field_value                        |
      | submit title     | Emergency access request submitted |
      | request Id value | [not empty nor null value]         |
	# Ensure systems has suspended the user until an admin approves the corresponding task and an email is received regarding login failure
    When I click the "Sign in" button
    Then I am presented with the "Sign in" screen
    When I enter value "user_unregistered@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    Then I see an error summary with "Your email address or password is incorrect"
    # approve task as admin
    When I refresh the current page
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request password and two factor authentication emergency access" text
    And The page "contains" the "UNCLAIMED" text
	# The admin calls the user and approves the task (if verification is successful)
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The request ID is" text
    And I see the following fields having the values:
      | fieldName          | field_value                            |
      | task handled title | The proposed request has been approved |
      | request Id value   | [not empty nor null value]             |
    And I click the "Sign out" link
	# System unsuspends the user and sends them an approved request email notification with password reset link
    And I receive an "Reset your password and two factor authenticator app - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    # User resets password
    Then I am presented with the "Reset your password and authenticator app" screen
    When I click the "Continue" button
    Then I am presented with the "Enter your new password" screen
    And The page "contains" the "Enter your new password" text
    When I enter value "<new_password>" in "New Password" field
    And I enter value "<confirm_password>" in "Confirm password" field
    And I click the "Continue" button
    Then I see an error summary with "<error_summary>"

    @sampling-smoke
    Examples:
      | new_password           | confirm_password       | error_summary           |
      | new_password!@#5233!_1 | new_password!@#5233!_2 | Passwords don't match   |
      | 1                      | 1                      | Enter a strong password |
      
    @exec-manual
    Examples:
      | new_password           | confirm_password       | error_summary           |
      | 7_chars                | 7_chars                | Enter a strong password |

  @exec-manual @test-case-id-65389531927
  Scenario Outline: As a validated or enrolled or Authority or admin user I activate the Lost Password and 2FA process
    Given user "user_registered@test.com" is a <user> user having 2FA and having accepted the Terms-&-Conditions
	# The user selects the relevant link on sign-in page
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
	# The user enters the email address
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    When I enter value "user_registered@test.com" in "Email address" field
    And I click the "Submit" button
	# System sends an email link with an emergency button/link
    Then I am presented with the "Emergency access email sent" screen
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access request submitted" screen
    And The page "contains" the "What happens next" text
	# System suspends the user account and creates task for admin
    When I sign in as "user_registered@test.com" user
    Then I see an error summary with "Account is disabled, contact admin"
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request password and two factor authentication emergency access" text
    And The page "contains" the "UNCLAIMED" text
	# The admin calls the user and approves the task (if verification is successful)
    When I "check" the "1" elements of "Task list returned result rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I select the "Task type: Request password and two factor authentication emergency access" option
    And I click the "Search" button
    Then The page "contains" the "Request password and two factor authentication emergency access" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "user_registered@test.com" text
    And The page "contains" the "User ID" text
    When I click the "Approve" button
    Then I am presented with the "Task confirmation approve" screen
    And I click the "Complete task" button
    Then I am presented with the "Task confirmation approved" screen
	# System unsuspends the user and sends email notification to the user that the request has been approved, together with a link to reset the password
    And I receive an "Reset your password and two factor authenticator app - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Reset your password and authenticator app" screen
	# User resets the password
    When I click the "Continue" button
    Then I am presented with the "Enter your new password" screen
    When I enter value "abc$12345# #" in "New password" field
    And I enter value "abc$12345# #" in "Confirm new password" field
    And I click the "Continue" button
    Then I am presented with the "Sign in" screen
	# User sets up the new QR code in new 2FA device and signs in
    When I sign in as "user_registered@test.com" user
    Then I am presented with the "Set 2fA" screen
    And The page "contains" the "Set up two factor authentication" text
    And I click the "Problems with scanning?" link
    And I set for "<user>" user "correct" verification code after "1" seconds using "Manual Key" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen

    Examples:
      | user         |
      | validated    |
      | enrolled     |
      | Authority    |
      | senior admin |
      | junior admin |

  @exec-manual @test-case-id-795541332222
  Scenario: As a user I activate the Lost Password and 2FA process and I give a wrong email
    Given user "user_registered@test.com" is a REGISTERED user having 2FA and having accepted the Terms-&-Conditions
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    # try to continue without filling the email
    When I click the "Submit" button
    Then I see an error summary with "Enter your email address"
    And I see an error detail with id "event-name-error" and content "Enter your email address"
    # try to enter invalid email
    When I enter value "abc" in "Email address" field
    And I click the "Submit" button
    Then I see an error summary with "Enter an email address in the correct format, like name@example.com"
    And I see an error detail with id "event-name-error" and content "Enter an email address in the correct format, like name@example.com"

  @exec-manual @test-case-id-795541332242
  Scenario: As a user I activate the Lost Password and 2FA process giving a email that does not correspond to a user
    Given user "user_registered@test.com" is an ENROLLED user having 2FA and having accepted the Terms-&-Conditions
    And there is no "user_unregistered@test.com" user
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    When I enter value "user_unregistered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency access email sent" screen
    And I do not receive an "Emergency access" email message in the "user_unregistered@test.com" email address
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "does not contains" the "Request password and two factor authentication emergency access" text recently created

  @exec-manual @test-case-id-795541332263
  Scenario: As a user I activate the Lost Password and 2FA process and for any reason I can reactivate it again
    # i.e. in case that I haven't received an email, or a link has expired.
    Given user "user_registered@test.com" is an ENROLLED user having 2FA and having accepted the Terms-&-Conditions
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    When I enter value "user_registered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency access email sent" screen
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen

  @exec-manual @test-case-id-795541332284
  Scenario: As a user I activate the Lost Password and 2FA process and the email links are expired
    Given user "user_registered@test.com" is a VALIDATED user having 2FA and having accepted the Terms-&-Conditions
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    When I enter value "user_registered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency access email sent" screen
    When the link in the email message received in the "user_registered@test.com" email address has expired
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_registered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access link has expired" screen
    And The page "contains" the "The emergency access link has expired" text
    When I click the "Restart your request for emergency access to the UK Registry" link
    Then I am presented with the "Request emergency access to my account" screen

  @exec-manual @test-case-id-795541332305
  Scenario: As a user I activate the Lost Password and 2FA process but the admin rejects the demand
    Given user "user_registered@test.com" is a AUTHORITY user having 2FA and having accepted the Terms-&-Conditions
    When I navigate to the "Sign-in" screen
    Then The page "contains" the "I have forgotten my password and lost access to my authenticator app" text
    When I click the "I have forgotten my password and lost access to my authenticator app" link
    And I am presented with the "Request emergency access to my account" screen
    When I click the "Continue" button
    Then I am presented with the "Enter the email address associated with your UK Registry account" screen
    When I enter value "user_registered@test.com" in "Email address" field
    And I click the "Submit" button
    Then I am presented with the "Emergency access email sent" screen
    And I receive an "Emergency access - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Emergency access request submitted" screen
    And The page "contains" the "What happens next" text
    When I sign in as "user_registered@test.com" user
    Then I see an error summary with "Account is disabled, contact admin"
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Request password and two factor authentication emergency access" text
    And The page "contains" the "UNCLAIMED" text
    When I "check" the "1" elements of "Task list returned result rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I select the "Task type: Request password and two factor authentication emergency access" option
    And I click the "Search" button
    Then The page "contains" the "Request password and two factor authentication emergency access" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "user_registered@test.com" text
    And The page "contains" the "User ID" text
    When I click the "Reject" button
    Then I am presented with the "Task confirmation reject" screen
    When I enter value "reject comment 1" in "Why are you rejecting this request?" field
    And I click the "Complete task" button
    Then I am presented with the "Task confirmation rejected" screen
    When I sign in as "user_registered@test.com" user
    Then I am presented with the "Registry dashboard" screen

