@functional-area-user-validation

Feature: User validation - Search Users

  Epic: User validation and enrolment
  Version: v1.6 (12/03/2020)
  Story: Search Users
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20validation%20and%20enrolment.docx?version=10&modificationDate=1584023181000&api=v2

  @security-url-ad-hoc-get @test-case-id-936582531144
  Scenario Outline: Security front end test for user access via ad hoc url get
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    # menu item is not visible to user
    And The page "does not contain" the "User Administration" text
    # ensure that access to user details is denied via url ad hoc get as well
    When I get "user-details/UK977538690871" screen
    Then I am presented with the "Sign in" screen

    Examples:
      | user       |
      | enrolled   |
      | validated  |
      | registered |

  @sampling-smoke @test-case-id-30723531377
  Scenario: As a admin I can access user data for a filter search based on user id
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "UK977538690871" in "user-details"
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value             |
      | user basic information | SENIOR ADMIN USER       |
      | user basic status      | ENROLLED                |
      | user id information    | User ID: UK977538690871 |

  @exec-manual @test-case-id-30723531396
  Scenario: As a admin I can access user data for a filter search based on user status
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value        |
      | Hide filters       | Hide filters       |
      | Search             | Search             |
      | Clear              | Clear              |
      | Users label        | Users              |
      | Name or user ID    | Name or user ID    |
      | User status        | User status        |
      | User email address | User email address |
      | Date From          | From               |
      | Date To            | To                 |
      | User role          | User role          |
    # specify filter
    When I select the "User status: ENROLLED" option
    And I enter value "UK977538690871" in "Name or user ID textbox" field
    # apply search
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                                           |
      | user basic information                | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                              |
      | user id information                   | User ID: UK977538690871                                                                                                                                                                               |
      | Registration details                  | Registration details User ID UK977538690871 Status ENROLLED Email address test_senior_admin_user@test.com Registry Activation Code                                                                    |
      | Personal details                      | Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Home address Test Address 7 Second address line Third address line London UK 12345 Date of birth Country of birth UK |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                                 |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                                          |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                                            |
    And The page "contains" the "Memorable phrase" text
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName                             | field_value                                                                                                                                                                                           |
      | Registration details                  | Registration details User ID UK977538690871 Status ENROLLED Email address test_senior_admin_user@test.com Registry Activation Code                                                                    |
      | Personal details                      | Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Home address Test Address 7 Second address line Third address line London UK 12345 Date of birth Country of birth UK |
      | Authorised Representative in accounts | Authorised Representative in accounts No items added.                                                                                                                                                 |
      | Identification documentation          | Identification documentation No items added.                                                                                                                                                          |
      | Work contact details                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                |
      | Last signed in date time value        | [not empty nor null value]                                                                                                                                                                            |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    Then The page "contains" the "SENIOR ADMIN USER" text
    And I see the following fields having the values:
      | fieldName      | field_value     |
      | No items added | No items added. |
    # check that search data is retained both for filters and results:
    When I click the "Back to user list" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | UK977538690871                                                                       |
      | User status dropdown            | 3: ENROLLED                                                                          |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | [empty]                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # data check after clean:
    When I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | 0: null                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 0: null                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |

  @exec-manual @test-case-id-30723531479
  Scenario: As a admin I can hide search filters
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    When I click the "Hide filters" button
    Then I see the following fields having the values:
      | fieldName                   | field_value                                                                          |
      | Show filters                | Show filters                                                                         |
      | Users label                 | Users                                                                                |
      | Name or user ID             | [null]                                                                               |
      | User status                 | [null]                                                                               |
      | User email address          | [null]                                                                               |
      | Last sign in date Date From | [empty]                                                                              |
      | Last sign in date Date To   | [empty]                                                                              |
      | User role                   | [null]                                                                               |
      | Results headers             | User ID First and middle names Last name Role Registered on Last sign in date Status |

  @exec-manual @test-case-id-30723531512
  Scenario: As a admin I can access user data for a filter search based on user email
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # specify filter
    And I enter value "test_senior_admin_user@test.com" in "User email address textbox" field
    # apply search
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
      | Request Update         | Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                            |
      | page main content | History and comments No items added. , |
    # check that search data is retained both for filters and results:
    When I click the "Back to user list" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | [empty]                                                                              |
      | User email address textbox      | test_senior_admin_user@test.com                                                      |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | [empty]                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # data check after clean:
    When I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | 0: null                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 0: null                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |

  @exec-manual @test-case-id-30723531583
  Scenario: As a admin I can access user data for a filter search based on user role
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # specify filter
    And I select the "User role: Senior registry administrator" option
    And I enter value "UK977538690871" in "Name or user ID textbox" field
    # apply search
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
      | Request Update         | Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                            |
      | page main content | History and comments No items added. , |
    # check that search data is retained both for filters and results:
    When I click the "Back to user list" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | UK977538690871                                                                       |
      | User status dropdown            | [empty]                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 2: SENIOR_ADMIN                                                                      |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # data check after clean:
    When I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | 0: null                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 0: null                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |

  @exec-manual @test-case-id-936582531441
  Scenario Outline: As a admin I can access user data for a filter search based on user last sign in date From or To
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # specify filter
    And I enter value "<date_value>" in "<date>" field
    And I enter value "UK977538690871" in "Name or user ID textbox" field
    # apply search
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
      | Request Update         | Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                            |
      | page main content | History and comments No items added. , |
    # check that search data is retained both for filters and results:
    When I click the "Back to user list" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | UK977538690871                                                                       |
      | User status dropdown            | [empty]                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | <from_date_value>                                                                    |
      | Last sign in date To datetime   | <to_date_value>                                                                      |
      | User role dropdown              | [empty]                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # data check after clean:
    When I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | 0: null                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 0: null                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |

    Examples:
      | date                        | date_value | from_date_value | to_date_value |
      | Last sign in date Date From | 01012000   | 2000-01-01      | [empty]       |
      | Last sign in date Date To   | 01012100   | [empty]         | 2100-01-01    |

  @exec-manual @test-case-id-936582531518
  Scenario Outline: As a admin I can access user data for a filter search based on aka or first name only or first name with last name
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # specify filter user_id_or_name_value
    When I enter value "<user_id_or_name_value>" in "Name or user ID textbox" field
    # apply search
    And I click the "Search" button
    When I click the "result row number 1" link
    Then I am presented with the "User details" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content      | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
      | Request Update         | Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | user basic information | SENIOR ADMIN USER ENROLLED Change status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | user id information    | User ID: UK977538690871                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
    # check in user details tab
    When I click the "user details left side" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | page main content | User details Registration details Email address test_senior_admin_user@test.com User ID UK977538690871 Status ENROLLED Registry Activation Code Personal details First and middle names SENIOR ADMIN Last name USER Known as AKA Residential address Test Address 7 Second address line Third address line Town or city London Country UK Postcode 12345 Date of birth Country of birth UK Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line Identification documentation No items added. Authorised Representative in accounts No items added. |
    # check in user history and comments tab
    When I click the "user history and comments left side" link
    Then I see the following fields having the values:
      | fieldName | field_value                          |
      | Who       | SENIOR ADMIN USER                    |
      | What      | Transaction proposal task completed. |
    # check that search data is retained both for filters and results:
    When I click the "Back to user list" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | <user_id_or_name_value>                                                              |
      | User status dropdown            | [empty]                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | [empty]                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |
    # data check after clean:
    When I click the "Clear" button
    Then I see the following fields having the values:
      | fieldName                       | field_value                                                                          |
      | Name or user ID textbox         | [empty]                                                                              |
      | User status dropdown            | 0: null                                                                              |
      | User email address textbox      | [empty]                                                                              |
      | Last sign in date From datetime | [empty]                                                                              |
      | Last sign in date To datetime   | [empty]                                                                              |
      | User role dropdown              | 0: null                                                                              |
      | Results headers                 | User ID First and middle names Last name Role Registered on Last sign in date Status |

    Examples:
      | user_id_or_name_value |
      | AKA                   |
      | SENIOR ADMIN          |
      | SENIOR ADMIN USER     |

  @exec-manual @test-case-id-30723531812
  Scenario: As a admin I can see results by default in descending order
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                                          |
      | Hide filters       | Hide filters                                                                         |
      | Search             | Search                                                                               |
      | Clear              | Clear                                                                                |
      | Users label        | Users                                                                                |
      | Name or user ID    | Name or user ID                                                                      |
      | User status        | User status                                                                          |
      | User email address | User email address                                                                   |
      | Date From          | From                                                                                 |
      | Date To            | To                                                                                   |
      | User role          | User role                                                                            |
      | Results headers    | User ID First and middle names Last name Role Registered on Last sign in date Status |
    Then I see the elements "results" being "descending" sorted by "Registration date"
