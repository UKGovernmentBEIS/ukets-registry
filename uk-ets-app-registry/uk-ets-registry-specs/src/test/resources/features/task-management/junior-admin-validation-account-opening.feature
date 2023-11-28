@functional-area-task-management

Feature: Task management - Junior admin validation account opening

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (Task) review account opening task
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @sampling-smoke @test-case-id-82437826555
  Scenario: As a Junior admin I cannot claim account opening task
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000003  |        |         | ACCOUNT_OPENING_REQUEST | 100000003    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "You do not have permission to perform this action"

  @exec-manual @test-case-id-82437826584
  Scenario: As a Junior admin I cannot comment on a task that I have not claimed and assigned
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "You do not have permission to perform this action"

  @sampling-smoke @test-case-id-82437826612
  Scenario: As a Junior admin I cannot Approve task initiated by me
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000003  |        |         | ACCOUNT_OPENING_REQUEST | 100000003    |              |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

  @test-case-id-82437826636
  Scenario: As a Junior admin I can Approve task initiated by another user
    Given I sign in as "junior admin" user
    And I have created 6 "enrolled" users
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000008    |              | ACCOUNT_OPENING_REQUEST |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    Then I see the following fields having the values:
      | fieldName                   | field_value  |
      | Open Account status content | Open Account |
      | Task details status         | APPROVED     |

  @test-case-id-82437826635
  Scenario: As a Junior admin I can Reject task
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000003  |        |         | ACCOUNT_OPENING_REQUEST | 100000003    |              |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "Reject" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    Then I see the following fields having the values:
      | fieldName                   | field_value  |
      | Open Account status content | Open Account |
      | Task details status         | REJECTED     |

  @exec-manual @test-case-id-82437826659
  Scenario: As a Junior admin I can view more info about task
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "more info" link
    Then I see the following fields having the values:
      | fieldName                   | field_value  |
      | Open Account status content | Open Account |
    And The page "contains" the "Initiated by JUNIOR ADMIN USER" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @test-case-id-82437826683
  Scenario: As a Junior admin I can view task history and comments
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000003  |        |         | ACCOUNT_OPENING_REQUEST | 100000003    |              |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    Given I click the "History & comments" link
    Then I am presented with the "Task Details" screen
    And I click the "Add comment" link
    And I enter value "comment 1" in "comment area" field
    And I clear the "comment area" field
    And I enter value "comment number 2" in "comment area" field
    When I click the "Add Comment" button
    Then I see the following fields having the values:
      | fieldName             | field_value      |
      | comment text result 1 | comment number 2 |

  @test-case-id-82437826711
  Scenario: As a Junior admin I can view the details of AR who are attached to opening request
    Given I sign in as "junior admin" user
    And I have created 6 "enrolled" users
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000008    |              | ACCOUNT_OPENING_REQUEST |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    Given I click the "List of authorised representatives" link
    Then I see the following fields having the values:
      | fieldName       | field_value     |
      | ENROLLED USER 0 | ENROLLED USER 0 |
      | ENROLLED USER 1 | ENROLLED USER 1 |
      | ENROLLED USER 2 | ENROLLED USER 2 |
      | ENROLLED USER 3 | ENROLLED USER 3 |
      | ENROLLED USER 4 | ENROLLED USER 4 |
      | ENROLLED USER 5 | ENROLLED USER 5 |
    When I click the "ENROLLED USER 0" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                       |
      | Authorised Representative1 content | Name ENROLLED USER 0 Request documents User ID UK88299344979 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "ENROLLED USER 1" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                        |
      | Authorised Representative2 content | Name ENROLLED USER 1 Request documents User ID UK689820232063 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "ENROLLED USER 2" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                        |
      | Authorised Representative3 content | Name ENROLLED USER 2 Request documents User ID UK844883074633 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "ENROLLED USER 3" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                        |
      | Authorised Representative4 content | Name ENROLLED USER 3 Request documents User ID UK309464690132 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "ENROLLED USER 4" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                        |
      | Authorised Representative5 content | Name ENROLLED USER 4 Request documents User ID UK908452725792 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "ENROLLED USER 5" link
    Then I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                        |
      | Authorised Representative6 content | Name ENROLLED USER 5 Request documents User ID UK353782343224 Permissions Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "Back to task list" link
    Then I am presented with the "Task List" screen

  @sampling-smoke @test-case-id-82437826770
  Scenario: As a Junior admin I can change ah but I cannot complete the task
    Given I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative6        |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | INDIVIDUAL                 |
      | holderName               | Individual name 1          |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                              |
      |            | 100000004  |        |         | ACCOUNT_OPENING_REQUEST | 100000004    |              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Account holder & primary contact(s)" link
    And I click the "Change account holder" link
    And I enter value "100002" in "Enter account holder ID" field
    And I click the "account holder ID change Apply" button
    Then The page "contains" the "Account Holder has changed" text
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

  @exec-manual @test-case-id-82437826794
  Scenario: As a Junior admin I can change regulator
    Given I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | accountStatus            | OPEN                              |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative6        |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | INDIVIDUAL                 |
      | holderName               | Individual name 1          |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "Aircraft operator" link
    And I click the "Change regulator" link
    And I select the "Regulator: DAERA" option
    And I click the "regulator change Apply" button
    Then The page "contains" the "Regulator has changed to DAERA" text

  @sampling-smoke @test-case-id-82437826813
  Scenario: As a Junior Registry Admin I can assign a task to another Junior admin
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
    And I sign in as "junior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000003  |        |         | ACCOUNT_OPENING_REQUEST | 100000003    |              |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "JUNIOR" in "User" field
    And I click the "Available typeahead JUNIOR ADMIN 0" link
    And I enter value "comment 1" in "Enter some comments" field
    And I click the "Assign" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    Then The page "contains" the "Initiated by JUNIOR ADMIN USER" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @sampling-smoke @test-case-id-82437826849
  Scenario: As a Junior Registry Admin I cannot assign a task to a senior admin
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | authorisedRepresentative    | Authorised Representative1     |
      | authorisedRepresentative    | Authorised Representative2     |
      | authorisedRepresentative    | Authorised Representative3     |
      | authorisedRepresentative    | Authorised Representative4     |
      | authorisedRepresentative    | Authorised Representative5     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "junior admin" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    # ensure that the list returns the Junior Admin result - that the dropdown type ahead works properly
    When I enter value "Jun" in "User" field
    And I click the "Assign task" button
    Then I see an error summary with "Please select a user"
    When I clear the "User" field
    # ensure that the SENIOR ADMIN 0 does not exist in dropdown list return results
    And I enter value "SEN" in "User" field
    Then The page "does not contain" the "SENIOR ADMIN 0" text
    When I clear the "User" field
    And I enter value "SENIOR" in "User" field
    Then The page "does not contain" the "SENIOR ADMIN 0" text
    When I clear the "User" field
    And I enter value "SENIOR ADMIN" in "User" field
    Then The page "does not contain" the "SENIOR ADMIN 0" text
    When I clear the "User" field
    And I enter value "SENIOR ADMIN " in "User" field
    Then The page "does not contain" the "SENIOR ADMIN 0" text

  @exec-manual @test-case-id-82437826897
  Scenario: As a junior admin I can see events generated by spinoff requests such as print letter
    Given I sign in as "junior admin" user
    And I have already approve an account opening request
    And a print letter task is generated via the account opening request
    When an event is generated regarding the print letter task
    Then I see this event in "comments history" section in the account opening request
