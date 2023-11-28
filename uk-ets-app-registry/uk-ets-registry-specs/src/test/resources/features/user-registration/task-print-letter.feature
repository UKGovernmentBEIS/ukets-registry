@functional-area-user-registration
@exec-manual

Feature: User registration - Task Print Letter

  Epic: User Validation and Enrolment
  Version: 6 (06/12/2019, Doc version 1.2)
  Story: (&4.4.3) As a Registry administrator (senior or junior) I can print the letter with the registry activation code and send it to the user by post.
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20validation%20and%20enrolment.docx?version=6&modificationDate=1575643746000&api=v2

  # Screens:
  # "Task Main"				  is the Task Management 4.1.1 at pg 14 screen 1: Task Overview
  # "Task Comments"		  is the Task Management 4.1.1 at pg 16 screen 2: Task history and comments
  # "Task Confirmation" is the Task Management 4.1.1 at pg 18 screen 3: Task confirmation
  # "Task Completed"		is the Task Management 4.1.1 at pg 20 screen 4: Task completed task

  # Users:
  # User_Registered    	 for a user having the REGISTERED status,
  # User_Validated       for a user having the VALIDATED status,
  # User_Enrolled        for a user having the ENROLLED status,
  # User_Unenrol_Pending for a user having the UNENROLMENT_PENDING status,
  # User_Unenrolled      for a user having the UNENROLLED status,
  # User_RegAdmin				 for a Registry Administrator (senior or junior)

  # Checking the Task's generic functionalities  ###################################################

  @test-case-id-57407730372
  Scenario: As a admin I can access Print letter with registry activation code task
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
    And I sign in as "senior admin" user
    # create an unclaimed "Print letter with registry activation code" task already exists
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | PRINT_ENROLMENT_LETTER_REQUEST | 100000001    |              |
    # select the task
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    # Check task title
    Then I see the following fields having the values:
      | fieldName                                        | field_value                                |
      | Print letter with registry activation code label | Print letter with registry activation code |
    # Check more info link
    When I click the "more info" link
    Then I see the following fields having the values:
      | fieldName            | field_value                                                 |
      | Initiated by content | Initiated by SENIOR ADMIN USER on ongoing date short format |
    When I click the "less info" link
    # Check the Comment & History link
    When I click the "History & comments" link
    Then I am presented with the "Task Comments" screen
    When I click the "Add comment" link
    And I enter value "comment 1" in "comment area" field
    And I clear the "comment area" field
    When I click the "Back" link
    Then I am presented with the "Task Details" screen
    When I click the "Back to task list" link
    Then I am presented with the "Task List" screen

  @test-case-id-57407730414
  Scenario: As a admin in Checking Tasks Comments Add or Clear Comment section I can enter comments to task
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
    And I sign in as "senior admin" user
    # create an unclaimed "Print letter with registry activation code" task already exists
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | PRINT_ENROLMENT_LETTER_REQUEST | 100000001    |              |
    # select the task
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I select the "Task type: Print letter with registry activation code" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # add comment to task
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "History & comments" link
    Then I am presented with the "Task Comments" screen
    When I click the "Add comment" link
    And I enter value "comment number 1" in "comment area" field
    And I clear the "comment area" field
    And I enter value "comment number 2" in "comment area" field
    And I click the "Add Comment" button
    Then The page "contains" the "comment number 2" text
    And The page "does not contain" the "comment number 1" text

  @test-case-id-891780630249
  Scenario Outline: As a admin I can see clickable Download button in Print letter with registry activation code task
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
    And I sign in as "<user>" user
    # create an unclaimed "Print letter with registry activation code" task already exists
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | PRINT_ENROLMENT_LETTER_REQUEST | 100000001    |              |
    # select the task
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I select the "Task type: Print letter with registry activation code" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # click download pdf button
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And I click the "Download letter" link

    Examples:
      | user         |
      | junior admin |
      | senior admin |

  @test-case-id-891780630285 @exec-manual
  Scenario Outline: As a admin I can access Print letter with registry activation code task
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
    And I sign in as "<user>" user
    # create an unclaimed "Print letter with registry activation code" task already exists
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | PRINT_ENROLMENT_LETTER_REQUEST | 100000001    |              |
    # select the task
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I select the "Task type: Print letter with registry activation code" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # download pdf
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Download letter" link
    Then A pdf file is "downloaded" successfully

    Examples:
      | user         |
      | junior admin |
      | senior admin |

  @test-case-id-891780630322
  Scenario Outline: As a admin in Checking Tasks Complete task sector I can complete Print letter with registry activation code task
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
    And I sign in as "<user>" user
    # create an unclaimed "Print letter with registry activation code" task already exists
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | PRINT_ENROLMENT_LETTER_REQUEST | 100000001    |              |
    # select the task
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I select the "Task type: Print letter with registry activation code" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # complete task
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Complete request" button
    Then I am presented with the "Task Details Complete" screen
    And I enter value "complete comment 1" in "comment area" field
    When I click the "Complete" button
    Then I am presented with the "Task Details" screen
    And I see the following fields having the values:
      | fieldName           | field_value                                |
      | Task details type   | Print letter with registry activation code |
      | Task details status | COMPLETED                                  |

    Examples:
      | user         |
      | junior admin |
      | senior admin |
      | senior admin |
