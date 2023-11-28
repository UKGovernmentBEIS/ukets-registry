@functional-area-task-management

Feature: Task management - Senior admin validation account opening

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (Task) review account opening task
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @exec-manual @test-case-id-628570526914
  Scenario: As senior admin I can return to task list and assign task to another admin
    # create claimed task
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
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    Then I am presented with the "Registry dashboard" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    Given I click the "Back to task list" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    # assign task from a senior admin to a junior admin
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
    Then The page "contains" the "Initiated by SENIOR ADMIN USER" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @exec-manual @test-case-id-628570526957
  Scenario: As senior admin I can view more info about account opening task
    # create claimed task
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
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    Then The page "contains" the "Initiated by SENIOR ADMIN USER" text
    And The page "contains" the "Claimed by Registry Administrator" text

  @test-case-id-628570526984
  Scenario: As senior admin I can view task history and comments
    # create claimed task
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
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000007  |        |         | ACCOUNT_OPENING_REQUEST | 100000007    |              |
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

  @test-case-id-628570527018
  Scenario: As senior admin I can view the details of AR who are attached to opening request
    # create claimed task
    Given I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              | ACCOUNT_OPENING_REQUEST |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "List of authorised representatives" link
    Then I see the following fields having the values:
      | fieldName       | field_value     |
      | ENROLLED USER 0 | ENROLLED USER 0 |
      | ENROLLED USER 1 | ENROLLED USER 1 |
      | ENROLLED USER 2 | ENROLLED USER 2 |
      | ENROLLED USER 3 | ENROLLED USER 3 |
      | ENROLLED USER 4 | ENROLLED USER 4 |
      | ENROLLED USER 5 | ENROLLED USER 5 |
    And I click the "ENROLLED USER 0" link
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

  @sampling-smoke @test-case-id-628570527078 @sampling-mvp-smoke
  Scenario: As senior admin I can APPROVE the opening request when I am not initiator
    # create claimed task
    Given I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created 1 "junior" administrators
    # task where the initiator is not the sign in user 4 eyes principle
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000008    |              | ACCOUNT_OPENING_REQUEST |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    Given I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Back" link
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName                   | field_value  |
      | Task details status         | Approved     |

  @sampling-smoke @test-case-id-628570527117
  Scenario: As senior admin I cannot approve the opening request when I am initiator
    # create claimed task
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
    # i initiate the task
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000007  |        |         | ACCOUNT_OPENING_REQUEST | 100000007    |              |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I see an error summary with "You cannot approve a task initiated by you. The 4-eyes security principle applies to this task"

  @sampling-smoke @test-case-id-628570527149
  Scenario: As senior admin I can REJECT opening request
    # create claimed task
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000007  |        |         | ACCOUNT_OPENING_REQUEST | 100000007    |              |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    # back button check
    When I click the "Back" link
    Then I am presented with the "Task Details" screen
    When I click the "Reject" button
    Then I am presented with the "Task Details" screen
    # empty comment check
    When I click the "Complete task" button
    Then I see an error summary with "Enter a reason for rejecting the request"
    # invalid comment white spaces check
    And I enter value "[empty]" in "comment area" field
    When I click the "Complete task" button
    Then I see an error summary with "Enter a reason for rejecting the request"
    # valid comment check
    And I clear the "comment area" field
    And I enter value "comment 1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "more info" link
    Then I see the following fields having the values:
      | fieldName                   | field_value  |
      | Task details status         | REJECTED     |

  @test-case-id-628570527197
  Scenario: As senior admin I can change ah
    # create claimed task
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
    # create a task
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                              |
      |            | 100000004  |        |         | ACCOUNT_OPENING_REQUEST | 100000005    |              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # change account holder
    And I click the "Account holder & primary contact(s)" link
    And I click the "Change account holder" link
    And I enter value "100002" in "Enter account holder ID" field
    When I click the "account holder ID change Apply" button
    # check change
    Then The page "contains" the "Account Holder has changed" text
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Account holder & primary contact(s)" link
    Then The page "contains" the "Account Holder has changed" text
    Given I click the "History & comments" link
    Then The page "contains" the "Open account task completed" text
    Then The page "contains" the "AH change from org1 to 100002" text

  @test-case-id-628570527230 @exec-manual
  Scenario: As senior admin I can change regulator
    # create claimed task
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
    # create a task
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                              |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000002    |              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    # change regulator
    And I click the "Aircraft operator" link
    And I click the "Change regulator" link
    And I select the "Regulator: DAERA" option
    And I click the "regulator change Apply" button
    And The page "contains" the "Regulator has changed" text
    And I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Account details" link
    And I click the "Aircraft operator" link
    Then The page "contains" the "DAERA" text
    And The page "contains" the "Regulator has changed" text

  @exec-manual @test-case-id-628570527262
  Scenario: As senior admin I can see events generated by spinoff requests such as print letter
    # create claimed task
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
    And I sign in as "senior admin" user
    And I have created 1 "junior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I have already approve an account opening request
    And a print letter task is generated via the account opening request
    When an event is generated regarding the print letter task
    Then I see this event in "comments history" section in the account opening request
