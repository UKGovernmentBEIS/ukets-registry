@functional-area-user-registration

Feature: User registration - View user Details and Change user status

  Epic: User Validation and Enrolment
  Version: v1.6 (12/03/2020)
  Story: (&4.1.1) as user I can view my own user details or the details of another user if permitted
  (&4.4.1) as administrator I can change the status of a user
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20validation%20and%20enrolment.docx?version=10&modificationDate=1584023181000&api=v2

  # as user I can view my own user details.
  # As a registry administrator I can view the user details of any user registered in the Registry
  # As a registry administrator I can change the status of a user, for example validate a user who has just registered.

  @test-case-id-70761430586 @sampling-smoke
  Scenario: As admin I can change status of a user from REGISTERED to VALIDATED and SUSPENDED and rollback to previous state
    Given I sign in as "senior admin" user
    And I have created 1 "registered" users
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                                                                         |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                                                                   |
      | user status                           | REGISTERED Change status                                                                                                                                                                                                            |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                                                              |
      | Registration details                  | Registration details User ID UK88299344979 Status REGISTERED Email address registered_user_0@test.com Registry activation code                                                                                                      |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Home address Test Address 7 Second address line Third address line Town or city London Postcode 12345 Country UK Date of birth Country of birth UK |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                                                               |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                                                                      |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                                              |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                                                                          |
    # validate user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Suspend user label  | Suspend user  |
      | Validate user label | Validate user |
    And I select the "Validate user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Current user status | Registered    |
      | Action              | Validate user |
      | New user status     | Validated     |
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | VALIDATED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status VALIDATED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # validate user
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | VALIDATED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status VALIDATED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Suspend user label | Suspend user |
    And I select the "Suspend user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Validated    |
      | Action              | Suspend user |
      | New user status     | Suspended    |
    And I enter value "comment 1" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | SUSPENDED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status SUSPENDED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                           |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | SUSPENDED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status SUSPENDED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                           |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Restore user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Suspended    |
      | Action              | Restore user |
      | New user status     | Validated    |
    And I enter value "comment 2" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | VALIDATED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status VALIDATED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | REGISTERED USER 0                                                                                                                                                                      |
      | user status                           | VALIDATED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status VALIDATED Email address registered_user_0@test.com Registry activation code                                                          |
      | Personal details                      | Personal details First and middle names REGISTERED Last name USER 0 Known as Date of birth Country of birth UK                                                                     |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |

  @test-case-id-90415630523
  Scenario Outline: As admin I can change status of a user from REGISTERED and ENROLLED to SUSPENDED and rollback to previous state
    Given I sign in as "senior admin" user
    And I have created 1 "<curr_status>" users
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                                                                       |
      | user basic information                | <status> USER 0                                                                                                                                                                                                                   |
      | user status                           | <status> Change status                                                                                                                                                                                                            |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                                                            |
      | Registration details                  | Registration details User ID UK88299344979 Status <status> Email address <email> Registry activation code                                                                                                                         |
      | Personal details                      | Personal details First and middle names <status> Last name USER 0 Known as Home address Test Address 7 Second address line Third address line Town or city London Postcode 12345 Country UK Date of birth Country of birth UK |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                                                             |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                                                                    |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                                            |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                                                                        |
    # suspend user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Suspend user label | Suspend user |
    And I select the "Suspend user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | <old_status> |
      | Action              | Suspend user |
      | New user status     | Suspended    |
    And I enter value "comment 1" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | <status> USER 0                                                                                                                                                                        |
      | user status                           | SUSPENDED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status SUSPENDED Email address <email> Registry activation code                                                                             |
      | Personal details                      | Personal details First and middle names <status> Last name USER 0 Known as <personal_details>                                                                                      |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                           |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | <status> USER 0                                                                                                                                                                        |
      | user status                           | SUSPENDED Change status                                                                                                                                                                |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status SUSPENDED Email address <email> Registry activation code                                                                             |
      | Personal details                      | Personal details First and middle names <status> Last name USER 0 Known as <personal_details>                                                                                      |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                           |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # Restore user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Restore user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Suspended    |
      | Action              | Restore user |
      | New user status     | <old_status> |
    And I enter value "comment 2" in "comment area" field
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | <status> USER 0                                                                                                                                                                        |
      | user status                           | <status> Change status                                                                                                                                                                 |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status <status> Email address <email> Registry activation code                                                                              |
      | Personal details                      | Personal details First and middle names <status> Last name USER 0 Known as <personal_details>                                                                                      |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                            |
      | user basic information                | <status> USER 0                                                                                                                                                                        |
      | user status                           | <status> Change status                                                                                                                                                                 |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                 |
      | Registration details                  | Registration details User ID UK88299344979 Status <status> Email address <email> Registry activation code                                                                              |
      | Personal details                      | Personal details First and middle names <status> Last name USER 0 Known as <personal_details>                                                                                      |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                  |
      | Identification documentation          | Identification documentation No items added. Request documents                                                                                                                         |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                             |

    Examples:
      | curr_status | status     | old_status | email                      | personal_details                                                                                                                                   |
      | registered  | REGISTERED | Registered | registered_user_0@test.com | Home address Test Address 7 Second address line Third address line Town or city London Postcode 12345 Country UK Date of birth Country of birth UK |
      | enrolled    | ENROLLED   | Enrolled   | enrolled_user_0@test.com   | Date of birth Country of birth UK                                                                                                                  |

  @test-case-id-70761430840
  Scenario: As a Admin I can see change button for another senior admin
    Given I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                                                                      |
      | user basic information                | SENIOR ADMIN 0                                                                                                                                                                                                                   |
      | user status                           | ENROLLED Change status                                                                                                                                                                                                           |
      | user id information                   | User ID: UK88299344979                                                                                                                                                                                                           |
      | Registration details                  | Registration details User ID UK88299344979 Status ENROLLED Email address senior_user_0@test.com Registry activation code                                                                                                         |
      | Personal details                      | Personal details First and middle names SENIOR Last name ADMIN 0 Known as Home address Test Address 7 Second address line Third address line Town or city London Postcode 12345 Country UK Date of birth Country of birth UK |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                                                            |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                                                                     |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                                           |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                                                                       |

  @exec-manual @test-case-id-70761430865
  Scenario: As a admin user I can view my own user details
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    And The page "contains" the "Print enrolment letter task request submitted. 1000000" text

  @exec-manual @test-case-id-70761430886
  Scenario: As a Admin I can ensure data is retained when I cancel change of status
    Given I sign in as "senior admin" user
    And I have created 1 "registered" users
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | page main content      | User details Registration details Email address registered_user_0@test.com User ID UK88299344979 User Status REGISTERED Registry activation code Personal details First and middle names REGISTERED Last name USER 0 Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | REGISTERED USER 0 REGISTERED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | user id information    | User ID: UK88299344979                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
    # validate user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Suspend user label  | Suspend user  |
      | Validate user label | Validate user |
    And I click the "Back" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | page main content      | User details Registration details Email address registered_user_0@test.com User ID UK88299344979 User Status REGISTERED Registry activation code Personal details First and middle names REGISTERED Last name USER 0 Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | REGISTERED USER 0 REGISTERED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | user id information    | User ID: UK88299344979                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Suspend user label  | Suspend user  |
      | Validate user label | Validate user |
    And I click the "Cancel" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | page main content      | User details Registration details Email address registered_user_0@test.com User ID UK88299344979 User Status REGISTERED Registry activation code Personal details First and middle names REGISTERED Last name USER 0 Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | REGISTERED USER 0 REGISTERED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | user id information    | User ID: UK88299344979                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Suspend user label  | Suspend user  |
      | Validate user label | Validate user |
    And I select the "Validate user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I click the "Back" button
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Suspend user label  | Suspend user  |
      | Validate user label | Validate user |
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value   |
      | Current user status | Registered    |
      | Action              | Validate user |
      | New user status     | Validated     |
    And I click the "Cancel" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | page main content      | User details Registration details Email address registered_user_0@test.com User ID UK88299344979 User Status REGISTERED Registry activation code Personal details First and middle names REGISTERED Last name USER 0 Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | REGISTERED USER 0 REGISTERED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
      | user id information    | User ID: UK88299344979                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

  @exec-manual @test-case-id-70761430969
  Scenario: As a Admin I can verify status change of a user is displayed as event in history and comments section
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    When I select the "User status: ENROLLED" option
    And I enter value "UK977538690871" in "Name or user ID textbox" field
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
    # suspend user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Suspend user" option
    And I click the "Continue" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status SUSPENDED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER SUSPENDED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status SUSPENDED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName            | field_value                                            |
      | page main content    | History and comments                                   |
      | history and comments | History and comments When Who What Request ID Comments |
    # Restore user
    When I click the "Change status" link
    Then I am presented with the "Change user status" screen
    And I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Restore user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Suspended    |
      | Action              | Restore user |
      | New user status     | Enrolled     |
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | user status            | ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
    Then I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Restore user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Suspended    |
      | Action              | Restore user |
      | New user status     | Enrolled     |
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | user status            | ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName            | field_value                                            |
      | page main content    | History and comments                                   |
      | history and comments | History and comments When Who What Request ID Comments |
    Then I see the following fields having the values:
      | fieldName          | field_value  |
      | Restore user label | Restore user |
    And I select the "Restore user" option
    And I click the "Continue" button
    Then I am presented with the "Change User status confirmation" screen
    And I see the following fields having the values:
      | fieldName           | field_value  |
      | Current user status | Suspended    |
      | Action              | Restore user |
      | New user status     | Enrolled     |
    And I click the "Apply" button
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | user status            | ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 User Status ENROLLED Registry activation code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact 01 ( Default ) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Request documents Authorised Representative in accounts No items added. |
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName            | field_value                                            |
      | page main content    | History and comments                                   |
      | history and comments | History and comments When Who What Request ID Comments |
