@functional-area-user-registration

Feature: User registration - Email registration

  Epic: User registration and sign-in
  Version: v3.1 (09/10/2020)
  Story: Enter and verify email address
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=12&amp;modificationDate=1568901007000&amp;api=v2

  @sampling-smoke @test-case-id-026410129463
  Scenario: As a not registered user I cannot enter an email address longer than 256 characters
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                                                                                                                                                                                                                                                              |
      | email   | yDD3tjbex7ORs3y9K8nnxQMtR2TBzIjsfrXW48eX41lbnaUXnh1vvytyJxuUk4JY3Yv7eEP2OTsrds9E46cXzi0UJsbRIlNYrApCvxGV1exE8akKIARQhe16fgHOQKed4r6AjQ8LHbbcEEi5k1Q2XCF4pWdN6xTKQG3G7RZdPuTWZBWrwP75eojYq2pUCigHrXJHryPe1d2DNnNcUwK7UrJ6Ml8ne4LO05vPGPy4og3ICnGfIRtUbNbUuy1Y5rwp@test.gr |
    Then I see the following values in the fields
      | fieldId | field_value                                                                                                                                                                                                                                                      |
      | email   | yDD3tjbex7ORs3y9K8nnxQMtR2TBzIjsfrXW48eX41lbnaUXnh1vvytyJxuUk4JY3Yv7eEP2OTsrds9E46cXzi0UJsbRIlNYrApCvxGV1exE8akKIARQhe16fgHOQKed4r6AjQ8LHbbcEEi5k1Q2XCF4pWdN6xTKQG3G7RZdPuTWZBWrwP75eojYq2pUCigHrXJHryPe1d2DNnNcUwK7UrJ6Ml8ne4LO05vPGPy4og3ICnGfIRtUbNbUuy1Y5rwp |

  @deleteUnregisteredUser @sampling-smoke @test-case-id-026410129476
  Scenario: As a not registered user I can click email verification link
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                |
      | email   | user_unregistered@test.com |
    When I click the "Continue" button
    Then I am presented with the "Email Address" screen
    And I receive an "Confirm your work email address - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Email Confirmed" screen

  @deleteUnregisteredUser @sampling-smoke @test-case-id-026410129491
  Scenario: As a not registered user once I register to system then an event is generated
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                |
      | email   | user_unregistered@test.com |
    When I click the "Continue" button
    Then I am presented with the "Email Address" screen
    And I receive an "Confirm your work email address - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address
    When I click the correct email link
    Then I am presented with the "Email Confirmed" screen
    # sign in and navigate to my profile  history and comments shows the event of registration process
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
    And The page "contains" the "Set up two factor authentication" text
    # setup 2fa
    And I set for "user_unregistered@test.com" user "correct" verification code after "1" seconds using "QR barcode scan" method with "sha256" algorithm and "totp" otp standard for "6" digits
    And I click the "Continue" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # event of user registration has been successfully created
    Then The page "contains" the "Registered" text

  @deleteUnregisteredUser @test-case-id-026410129540
  Scenario: As a not registered user when I submit my email address I successfully receive an email message
    Given I navigate to the "Email Address" screen
    Then I am presented with the "Email Address" screen
    And I accept all cookies
    And I enter the mandatory fields which are
      | fieldId | field_value                |
      | email   | user_unregistered@test.com |
    And I click the "Continue" button
    Then I am presented with the "Email Address" screen
    And I receive an "Confirm your work email address - UK Emissions Trading Registry" email message regarding the "user_unregistered@test.com" email address

  @test-case-id-026410129553 @sampling-smoke
  Scenario: As a not registered user I can navigate from landing page to start registration page
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    And I click the "Sign in" button
    And I click the "create a registry sign in" link
    Then I am presented with the "Email info" screen

  @exec-manual @test-case-id-47680529363
  Scenario Outline: As a not registered user I can see the correct screen when registry application is down
    Given I navigate to the "Landing page" screen
    And I accept all cookies
    And registry application is down
    And I click the "<page>" link
    Then I am presented with the "Error <page>" screen

    Examples:
      | page                           |
      | Start the registration process |
      | Sign in                        |

  @sampling-smoke @test-case-id-026410129576 @sampling-mvp-smoke
  Scenario: As a not registered user I can access Verify your email screen
    When I navigate to the "Email info" screen
    Then I am presented with the "Email info" screen
    And I click the "create a registry sign in" link
    Then I am presented with the "Email info" screen
    And I accept all cookies
    When I click the "Continue" button
    Then The page "contains" the "What is your email address?" text

  @security @owasp-asvs-2.7 @nist-800-63B-5.1.3.2 @deleteUnregisteredUser @exec-manual @test-case-id-47680529388
  Scenario Outline: As a user I must be prevented or allowed to click email verification link before it expires
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
    And I <implement_complete> complete the user registration process
    When I click the correct email link
    Then I am presented with the "Email Confirmed" screen
    And I <implement_complete> see an error summary with 'The email address has already been confirmed.'

    Examples:
      | implement_complete |
      | do                 |
      | do not             |

  @security @owasp-asvs-2.7 @nist-800-63B-5.1.3.2 @deleteUnregisteredUser @exec-manual @test-case-id-026410129612
  Scenario: As a not registered user I cannot click email verification link after it expires
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
    And I wait until the link expires
    When I click again on the link in the email message received in the "user_unregistered@test.com" email address
    Then I am presented with the "Email Confirmed" screen
    And I see an error summary with 'The email verification link has expired.'
