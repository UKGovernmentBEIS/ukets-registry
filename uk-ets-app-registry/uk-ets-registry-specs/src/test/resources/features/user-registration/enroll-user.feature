@functional-area-user-registration
@exec-manual

Feature: User registration - Enroll User

  Epic: User Validation and Enrolment
  Version: 6 (06/12/2019, Doc version 1.2)
  Story: (&4.3.1) As a Validated user, I received the Register Activation Code and I can entered-it in the system so that I become Enrolled.
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20validation%20and%20enrolment.docx?version=6&modificationDate=1575643746000&api=v2

  # Screens:
  # "Enter Registry Activation Code"	is the 4.3.1   at pg 18 screen  1: Prompt to enter Registry Activation Code
  # "Successful enrolled"			    is the 4.3.1   at pg 18 screen  2: Successfully entry message
  # "dashboard"		          	  	    the user's home page in the application (after login)
  #
  # Users:
  # User_Registered    		  for a user having the REGISTERED status,
  # User_Validated            for a user having the VALIDATED status,
  # User_Enrolled             for a user having the ENROLLED status,
  # User_Unenrol_Pending      for a user having the UNENROLMENT_PENDING status,
  # User_Unenrolled           for a user having the UNENROLLED status,
  # User_RegAdmin			  for a Registry Administrator (senior or junior)
  # User_RegAdminSenior		  for a Senior Registry Administrator
  # User_RegAdminJunior		  for a Junior Registry Administrator
  # User_RegAdminRO		      for a Read-Only Registry Administrator

  Background:
    Given I sign in as "validated" user

  @test-case-id-31541729658
  Scenario: As validated user I can access Enter Registry Activation Code screen
    Given I navigate to the "dashboard" screen
    When I click the "Enter Registry Activation Code" link
    Then I am presented with the "Enter Registry Activation Code" screen
    When I click the "Back" link
    Then I am presented with the "dashboard" screen

  @sampling-smoke @test-case-id-31541729667
  Scenario: As validated user I can register my self
    Given I navigate to the "Enter Registry Activation Code" screen
    When I enter the mandatory fields which are
      | field_id        | field_type | field_value       |
      | activation-code | text       | "activation-code" |
  # Need here a valid activation code
    And I click the "Apply" button
    Then I am presented with the "Successful enrolled" screen
    And The page "contains" the "Your access to the registry has been activated" text
    And I click the "Go to home" link
# 	Checking that my status is now ENROLLED
    Then I am presented with the "dashboard" screen

  @test-case-id-31541729681
  Scenario: As validated user I provide an invalid activation code
    Given I navigate to the "Enter Registry Activation Code" screen
    And I enter the mandatory fields which are
      | field_id        | field_type | field_value |
      | activation-code | text       | xxx$$$xxx   |
  # Invalid activation code
    And I click the "Apply" button
    Then I see an error summary with "The registry activation code that you provided is invalid"

  @test-case-id-31541729691
  Scenario: As validated user I provide an expired activation code
    Given I navigate to the "Enter Registry Activation Code" screen
    And I enter the mandatory fields which are
      | field_id        | field_type | field_value               |
      | activation-code | text       | "expired-activation-code" |
  # Need here an expired activation code.
    And I click the "Apply" button
    Then I see an error summary with "The registry activation code has expired"
