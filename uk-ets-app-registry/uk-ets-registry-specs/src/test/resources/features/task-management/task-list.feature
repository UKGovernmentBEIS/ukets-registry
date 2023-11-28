@functional-area-task-management

Feature: Task management - Task list

  Epic: Task Management
  Version: 1.4 (17/01/2020 Doc version v1.4)
  Story: (& 4.3.1) As a registry-administrator, I can access the tasks.
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Task%20management.docx?version=4&modificationDate=1573220147000&api=v2

  # Screens:
  # "Task Main"			 is the 4.1.1 at pg 14 screen 1: Task Overview
  # "Task Comments"		 is the 4.1.1 at pg 16 screen 2: Task history and comments
  # "Task Confirmation"	 is the 4.1.1 at pg 18 screen 3: Task confirmation
  # "Task Completed"	 is the 4.1.1 at pg 20 screen 4: Task completed task
  # "Task Claim"		 is the 4.1.2 at pg 21 screen  : Bulk Claim
  # "Task Assign"		 is the 4.1.3 at pg 22 screen  : Bulk Assign

  @security-url-ad-hoc-get @test-case-id-930588827307
  Scenario: Security front end test for task access via ad hoc url get
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
    #   And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I sign in as "enrolled" user
    And I have created 1 "senior" administrators
    Given I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000002  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000002    | 100000002    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    # ensure that the application does not return the result
    And I see "0" elements of "Task list returned result rows"
    # ensure that task cannot be accessed via browser url ad hoc get
    When I get "task-details/100000001" screen
    Then I am presented with the "Sign in" screen

  @exec-manual @test-case-id-930588827343
  Scenario: As a admin I can see task list headers
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I see the following fields having the values:
      | fieldName                        | field_value               |
      | Request ID Header                | Request ID                |
      | Task type Header                 | Task type                 |
      | Name of initiator Header         | Name of initiator         |
      | Name of claimant Header          | Name of claimant          |
      | Account number Header            | Account number            |
      | Account type Header              | Account type              |
      | Account holder Header            | Account holder            |
      | Authorised representative Header | Authorised representative |
      | Transaction ID Header            | Transaction ID            |
      | Created on Header                | Created on                |
      | Task status Header               | Task status               |

  @exec-manual @test-case-id-930588827364
  Scenario: As a admin when I refresh page then data is retained
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I see the following fields having the values:
      | fieldName                        | field_value               |
      | Request ID Header                | Request ID                |
      | Task type Header                 | Task type                 |
      | Name of initiator Header         | Name of initiator         |
      | Name of claimant Header          | Name of claimant          |
      | Account number Header            | Account number            |
      | Account type Header              | Account type              |
      | Account holder Header            | Account holder            |
      | Authorised representative Header | Authorised representative |
      | Transaction ID Header            | Transaction ID            |
      | Created on Header                | Created on                |
      | Task status Header               | Task status               |
    Then I refresh the current page
    And I am presented with the "Task List" screen
    Then I see the following fields having the values:
      | fieldName                        | field_value               |
      | Request ID Header                | Request ID                |
      | Task type Header                 | Task type                 |
      | Name of initiator Header         | Name of initiator         |
      | Name of claimant Header          | Name of claimant          |
      | Account number Header            | Account number            |
      | Account type Header              | Account type              |
      | Account holder Header            | Account holder            |
      | Authorised representative Header | Authorised representative |
      | Transaction ID Header            | Transaction ID            |
      | Created on Header                | Created on                |
      | Task status Header               | Task status               |

  @exec-manual @test-case-id-930588827400
  Scenario: As a admin under the task list screen I can hide filters
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    Then I see the following fields having the values:
      # headers
      | fieldName                        | field_value               |
      | Request ID Header                | Request ID                |
      | Name of initiator Header         | Name of initiator         |
      | Name of claimant Header          | Name of claimant          |
      | Account number Header            | Account number            |
      | Account type Header              | Account type              |
      | Account holder Header            | Account holder            |
      | Authorised representative Header | Authorised representative |
      | Transaction ID Header            | Transaction ID            |
      | Created on Header                | Created on                |
      | Task status Header               | Task status               |
      # textbox titles
      | Task status label                | Task status               |
      | Name of claimant label           | Name of claimant          |
      | Task type label                  | Task type                 |
      | Request Id label                 | Request Id                |
      # textboxes
      | dropdown Task status             | 1: OPEN                   |
      | dropdown Task type               | 0: null                   |
      | Account number                   | [empty]                   |
      | Account holder                   | [empty]                   |
      | Name of claimant                 | [empty]                   |
      | Request Id                       | [empty]                   |
    When I click the "Hide filters" button
    Then I see the following fields having the values:
      # headers
      | fieldName                        | field_value               |
      | Request ID Header                | Request ID                |
      | Name of initiator Header         | Name of initiator         |
      | Name of claimant Header          | Name of claimant          |
      | Account number Header            | Account number            |
      | Account type Header              | Account type              |
      | Account holder Header            | Account holder            |
      | Authorised representative Header | Authorised representative |
      | Transaction ID Header            | Transaction ID            |
      | Created on Header                | Created on                |
      | Task status Header               | Task status               |
      # textbox titles
      | Task status label                | [null]                    |
      | Name of claimant label           | [null]                    |
      | Task type label                  | [null]                    |
      | Request Id label                 | [null]                    |
      # textboxes
      | dropdown Task status             | 1: OPEN                   |
      | dropdown Task type               | 0: null                   |
      | Account number                   | [empty]                   |
      | Account holder                   | [empty]                   |
      | Name of claimant                 | [empty]                   |
      | Request Id                       | [empty]                   |

  @test-case-id-800400227296
  Scenario Outline: As admin user I can filter task list for a particular account using account number
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
    Given I sign in as "<user>" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000002    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click should not be shown
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | 50001   |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @test-case-id-800400227348
  Scenario Outline: As a non admin user I dont see task list for a particular account using account number filter
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    Given I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "50001" in "Account number" field
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-800400227383
  Scenario Outline: As user I can filter task list for a particular claimant
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    When I enter value "<field_value> USER" in "Name of claimant" field
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click should not be shown
      | dropdown Task status | 0: null            |
      | dropdown Task type   | 0: null            |
      | Account number       | [empty]            |
      | Account holder       | [empty]            |
      | Name of claimant     | <field_value> USER |
      | Request Id           | [empty]            |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            | field_value     |
      | senior admin    | SENIOR ADMIN    |
      | junior admin    | JUNIOR ADMIN    |
      | read only admin | READ ONLY ADMIN |

  @exec-manual @test-case-id-800400227438
  Scenario Outline: As a non admin user I dont see task list after filtering using a particular claimant
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    When I enter value "<field_value> USER" in "Name of claimant" field
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       | field_value |
      | registered | REGISTERED  |
      | validated  | VALIDATED   |
      | enrolled   | ENROLLED    |

  @test-case-id-800400227472
  Scenario: As admin user I can filter task list for a particular ah
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
    And I sign in as "read only admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    When I enter value "Organisation 1" in "Account holder" field
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click should not be shown
      | dropdown Task status | 0: null        |
      | dropdown Task type   | 0: null        |
      | Account number       | [empty]        |
      | Account holder       | Organisation 1 |
      | Name of claimant     | [empty]        |
      | Request Id           | [empty]        |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

  @exec-manual @test-case-id-800400227522
  Scenario Outline: As a non admin user I cannot see task list after filtering for a particular ah
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" link
    And I enter value "Organisation 1" in "Account holder" field
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @test-case-id-800400227557
  Scenario: As admin user I can filter task list for a particular request ID
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
    And I sign in as "read only admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    When I enter value "100000001" in "Request Id" field
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click should not be shown
      | dropdown Task status | 0: null   |
      | dropdown Task type   | 0: null   |
      | Account number       | [empty]   |
      | Account holder       | [empty]   |
      | Name of claimant     | [empty]   |
      | Request Id           | 100000001 |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

  @exec-manual @test-case-id-800400227607
  Scenario Outline: As a non admin user I cannot see task list filtering using a particular request ID
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I enter value "100000001" in "Request Id" field
    When I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @test-case-id-800400227641
  Scenario: As admin user I can filter task list for a particular task status
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
    And I sign in as "read only admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Completed" option
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click should not be shown
      | dropdown Task status | 4: COMPLETED |
      | dropdown Task type   | 0: null      |
      | Account number       | [empty]      |
      | Account holder       | [empty]      |
      | Name of claimant     | [empty]      |
      | Request Id           | [empty]      |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

  @exec-manual @test-case-id-800400227690
  Scenario Outline: As a non admin user I cannot see task list for a particular task status filter
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Completed" option
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @test-case-id-930588827900
  Scenario: As user I cannot fill comments in completed tasks
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000007  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000007    | 100000007    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Completed" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "The task with request id 100000001 has been completed"

  @test-case-id-930588827931
  Scenario: As admin user I can filter task list for a particular task type
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
    And I sign in as "read only admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I select the "Task type: Open Account" option
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null                     |
      | dropdown Task type   | 12: ACCOUNT_OPENING_REQUEST |
      | Account number       | [empty]                     |
      | Account holder       | [empty]                     |
      | Name of claimant     | [empty]                     |
      | Request Id           | [empty]                     |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

  @test-case-id-800400227799
  Scenario Outline: As a non admin user I cannot see task list using a particular task type filter
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I select the "Task type: Open Account" option
    When I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-800400227834
  Scenario Outline: As admin user I can filter task list for a particular initiator
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" link
    And I select the "Task status: All" option
    When I enter value "<field_value> ADMIN USER" in "Name of initiator" field
    Then I am presented with the "Task List" screen
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | [empty] |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            | field_value |
      | senior admin    | SENIOR      |
      | junior admin    | JUNIOR      |
      | read only admin | READ ONLY   |

  @exec-manual @test-case-id-800400227888
  Scenario Outline: As a non admin user I cannot see task list filtering for a particular Name of initiator
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" link
    And I select the "Task status: All" option
    And I enter value "<field_value> ADMIN USER" in "Name of initiator" field
    Then I am presented with the "Task List" screen
    When I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       | field_value |
      | registered | REGISTERED  |
      | validated  | VALIDATED   |
      | enrolled   | ENROLLED    |

  @exec-manual @test-case-id-800400227925
  Scenario Outline: As admin user I can filter task list for a particular Account type
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" link
    And I select the "Task status: All" option
    And I select the "Account type: Operator holding account" option
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | [empty] |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-800400227978
  Scenario Outline: As a non admin user I cannot see task list filtering for a particular Account type
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" link
    When I select the "Task status: All" option
    And I select the "Account type: Trading account" option
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-800400228014
  Scenario Outline: As admin user I can filter task list for a particular Task outcome
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Task status: All" option
    And I select the "Task outcome: Approved" option
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | [empty] |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-800400228067
  Scenario Outline: As a non admin user I cannot see task list filtering for a particular Task outcome
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Task status: All" option
    And I select the "Task outcome: Approved" option
    When I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-800400228103
  Scenario Outline: As admin user I can filter task list for a Initiated or claimed by role
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Task status: All" option
    And I select the "Initiated or claimed by role: Authorised Representative" option
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | [empty] |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

    Examples:
      | user            |
      | senior admin    |
      | junior admin    |
      | read only admin |

  @exec-manual @test-case-id-800400228156
  Scenario Outline: As a non admin user I cannot filter for a Initiated or claimed by role
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    Then The page "does not contain" the "Initiated or claimed by role" text

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @test-case-id-800400228189
  Scenario: As admin user I can filter task list for a particular transaction id
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    And I enter value "GB100000001" in "Transaction Id" field
    And I click the "Search" button
    And I click the "Show filters" button
    Then I see the following fields having the values:
      # filters criteria check after search click
      | dropdown Task status | 0: null |
      | dropdown Task type   | 0: null |
      | Account number       | [empty] |
      | Account holder       | [empty] |
      | Name of claimant     | [empty] |
      | Request Id           | [empty] |
    And I click the "Clear" button
    # filters criteria check after clear click
    Then I see the following fields having the values:
      | fieldName            | field_value |
      | dropdown Task status | 0: null     |
      | dropdown Task type   | 0: null     |
      | Account number       | [empty]     |
      | Account holder       | [empty]     |
      | Name of claimant     | [empty]     |
      | Request Id           | [empty]     |

  @exec-manual @test-case-id-800400228261
  Scenario Outline: As a non admin user I can filter task list for a particular transaction id
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    # create transaction
    And the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 0   | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 2 | OPEN           | 0   | RMU   | true       | true      | 2000     | 3000   | CP2      | CP2      |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               | 100000001         |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    And I enter value "GB100000001" in "Transaction Id" field
    And I click the "Search" button
    Then I see "1" elements of "Task list returned result rows"
    And The page "contains" the "GB100000001" text
    And The page "contains" the "Transaction Proposal" text
    And The page "contains" the "UNCLAIMED" text

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-930588828509
  Scenario: As senior admin user I can filter task list for a particular date
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    And I click the "Claimed on From" button
    When I enter value "01012000" in "Claimed on From" field
    And I click the "Claimed on To" button
    And I enter value "01012050" in "Claimed on To" field
    And I click the "Created on From" button
    When I enter value "01012000" in "Created on From" field
    And I click the "Created on To" button
    And I enter value "01012050" in "Created on To" field
    And I click the "Completed on From" button
    When I enter value "01012000" in "Completed on From" field
    And I click the "Completed on To" button
    When I enter value "01012050" in "Completed on To" field
    And I click the "Search" button
    Then The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "100000001" text
    And The page "contains" the "COMPLETED" text

  @exec-manual @test-case-id-800400228363
  Scenario Outline: As senior admin user I get an error when I search using a filled partially date
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    And I click the "Claimed on From" button
    When I enter value "<claimed_on_from>" in "Claimed on From" field
    And I click the "Claimed on To" button
    And I enter value "<claimed_on_to>" in "Claimed on To" field
    And I click the "Created on From" button
    When I enter value "<created_on_from>" in "Created on From" field
    And I click the "Created on To" button
    And I enter value "<created_on_to>" in "Created on To" field
    And I click the "Completed on From" button
    When I enter value "<completed_on_from>" in "Completed on From" field
    And I click the "Completed on To" button
    When I enter value "<completed_on_to>" in "Completed on To" field
    And I click the "Search" button
    Then I see an error summary with "Partially date filtering cannot be accepted"

    Examples:
      | claimed_on_from | claimed_on_to | created_on_from | created_on_to | completed_on_from | completed_on_to |
      | 0101            | 01012050      | 01012000        | 01012050      | 01012000          | 01012050        |
      | 01012000        | 0101          | 01012000        | 01012050      | 01012000          | 01012050        |
      | 01012000        | 01012050      | 0101            | 01012050      | 01012000          | 01012050        |
      | 01012000        | 01012050      | 01012000        | 0101          | 01012000          | 01012050        |
      | 01012000        | 01012050      | 01012000        | 01012050      | 0101              | 01012050        |
      | 01012000        | 01012050      | 01012000        | 01012050      | 01012000          | 0101            |

  @exec-manual @test-case-id-800400228412
  Scenario Outline: As user I can expand my search to include end user tasks as Admin user
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    And I select the "Expand search to user tasks: <expand_search>" option
    And I click the "Search" button
    Then The page "contains" the "100000001" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "COMPLETED" text

    Examples:
      | expand_search |
      | Yes           |
      | No            |

  @exec-manual @test-case-id-800400228448
  Scenario Outline: As user I cannot expand my search to include end user tasks as Non Admin user
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
    And I sign in as "<user>" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    When I select the "Task status: All" option
    Then The page "does not contain" the "Expand search to user tasks" text

    Examples:
      | user       |
      | registered |
      | validated  |
      | enrolled   |

  @exec-manual @test-case-id-930588828673
  Scenario: As user I see results shown in descending order after task search after returning back from a task
    Given I sign in
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I search in Task List
    And the results returned are shown in "descending" order
    And I open the Task
    When I click "Back"
    Then I am presented with the "Task List" screen
    And the results returned are shown in "descending" order

  ## Checking the CLAIM functionality ###############################################################
  @sampling-smoke @test-case-id-930588828687
  Scenario: As user I can CLAIM one task which is not already claimed and The assignee becomes the claimant of task
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
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # ensure for the unclaimed criterion that the task is not shown
    And I click the "Show filters" button
    When I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"
    # the assignee becomes the claimant of the task:
    And I click the "Show filters" button
    When I select the "Task status: Claimed" option
    And I click the "Search" button
    Then I see "1" elements of "Task list returned result rows"
    And The page "contains" the "100000001" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "CLAIMED" text

  @sampling-smoke @test-case-id-930588828732
  Scenario: As user I can CLAIM multiple tasks of same type
    # create first open account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    # create second open account
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by |
      | 100000001  |            |        |         | DELETE_TRUSTED_ACCOUNT_REQUEST | 100000001    |              |
      | 100000002  |            |        |         | DELETE_TRUSTED_ACCOUNT_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # ensure for the unclaimed criterion that the task is not shown
    And I click the "Show filters" button
    When I select the "Task status: unclaimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "0" elements of "Task list returned result rows"

  @sampling-smoke @test-case-id-930588828772
  Scenario: As user I can CLAIM one task which is already claimed by another user
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000002  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    Then I see "1" elements of "Task list returned result rows"
    When I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # ensure for the claimed criterion that the task is shown
    And I click the "Show filters" button
    When I select the "Task status: Claimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"

  @sampling-smoke @test-case-id-930588828806
  Scenario: As user I can CLAIM one task which is already claimed by me only if i leave a comment
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created 1 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000002    |              |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    # try without comment - error summary
    When I click the "Claim task" button
    Then I see an error summary with "Comment is mandatory when claiming claimed tasks"
    # try with comment
    When I enter value "claim comment 1" in "Enter some comments" field
    And I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And I see "1" elements of "Task list returned result rows"

  @exec-manual @test-case-id-930588828839
  Scenario: As user I can CLAIM multiple tasks of same type which are already claimed case of 2 tasks
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    When I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # ensure for the claimed criterion that the task is not shown
    And I see "2" elements of "Task list returned result rows"

  @test-case-id-930588828868
  Scenario: As user I cannot CLAIM multiple tasks of DIFFERENT type which are already claimed case of 2 tasks
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 1                        |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 2           |
      | legalRepresentative | Legal Rep3               |
      | legalRepresentative | Legal Rep4               |
    And I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by | diff                    |
      |            | 100000001  |        |         | ACCOUNT_OPENING_REQUEST        | 100000001    |              | ACCOUNT_OPENING_REQUEST |
      | 100000001  | 100000001  |        |         | DELETE_TRUSTED_ACCOUNT_REQUEST | 100000001    |              |                         |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "You can only claim or assign multiple tasks if the tasks are the same task type, please try another selection"

  @exec-manual @test-case-id-930588828897
  Scenario: As user I can claim already claimed task when NO claim comment is added
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created 1 "junior admin" users
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000002  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    And I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    # ensure for the claimed criterion that the task is not shown
    And I see "1" elements of "Task list returned result rows"

  @exec-manual @test-case-id-930588828926
  Scenario: As user I can claim UNCLAIMED task when NO claim comment is added
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task successfully
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When  task
    Then I am presented with the "Task List" screen
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "100000001" text
    And The page "contains" the "SENIOR ADMIN USER" text
    And The page "contains" the "CLAIMED" text

  @exec-manual @test-case-id-930588828956
  Scenario: As user I cannot claim CLAIMED task when NO claim comment is added
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I have created 3 "senior admin" users
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000002  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    When I click the "Show filters" button
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # try to claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    When I click the "Claim task" button
    Then I see an error summary with "You cannot claim an already claimed task without comment"

  @test-case-id-930588828982
  Scenario: As user I cannot CLAIM tasks of different types for 2 tasks which are NOT claimed
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountIndex                | 1                              |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by | diff                    |
      |            |            |        |         | ACCOUNT_OPENING_REQUEST        | 100000001    |              | ACCOUNT_OPENING_REQUEST |
      | 100000001  |            |        |         | DELETE_TRUSTED_ACCOUNT_REQUEST | 100000001    |              |                         |
    And I click the "Tasks" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # claim task
    And I click the "Lower Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I see an error summary with "You can only claim or assign multiple tasks if the tasks are the same task type, please try another selection"

  @exec-manual @test-case-id-930588829010
  Scenario: As user I cannot apply CLAIM functionality when no task is selected
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
      | 100000001  | 100000001  |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" button
    And I click the "Show filters" button
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    # claim task
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    And  task
    Then I see an error summary with "No task has been selected from task list to be claimed"

  ## Checking the ASSIGN functionality ##############################################################
  @test-case-id-930588829037
  Scenario: As a admin I can ASSIGN one task which is not already claimed and The assignee becomes the claimant of task
    Given I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created 2 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              | ACCOUNT_OPENING_REQUEST |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # assign task
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SENIOR" in "User" field
    And I select the "SENIOR ADMIN 0" option
    And I enter value "this is a comment text 1" in "Enter some comments" field
    When I click the "Assign" button
    Then I am presented with the "Task List" screen
    # the assignee becomes the claimant of the task:
    And I select the "Task status: Claimed" option
    And I click the "Search" button
    Then I see "1" elements of "Task list returned result rows"
    And The page "contains" the "100000001" text
    And The page "contains" the "SENIOR ADMIN 0" text
    And The page "contains" the "CLAIMED" text

  @test-case-id-930588829075
  Scenario: As an admin I can ASSIGN two tasks of same type
    Given I have created an account with the following properties
      | property     | value                    |
      | accountType  | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex | 1                        |
      | holderType   | ORGANISATION             |
      | holderName   | Organisation 2           |
    And I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created 2 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by | diff                    |
      |            |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              | ACCOUNT_OPENING_REQUEST |
      |            |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              | ACCOUNT_OPENING_REQUEST |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # assign task
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SENIOR" in "User" field
    And I select the "SENIOR ADMIN 0" option
    And I enter value "this is a comment text 1" in "Enter some comments" field
    When I click the "Assign" button
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I click the "Search" button
    And I select the "Exclude user tasks: No" option
    And I see "2" elements of "Task list returned result rows"

  @test-case-id-800400228916
  Scenario Outline: As a admin I cannot ASSIGN task when user or comment is missing
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I have created 5 "senior" administrators
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # assign task
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "<fieldValue>" in "<field>" field
    And I select the "SENIOR ADMIN 0" option
    When I click the "Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I see an error summary with "<error>"

    Examples:
      | field   | fieldValue          | error                |
      | User    | SENIOR              | Comment is required  |
      | comment | Enter some comments | Please select a user |

  @test-case-id-930588829143
  Scenario: As a admin I cannt ASSIGN one or more tasks of different types
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountIndex                | 1                              |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I sign in as "senior admin" user
    And I have created 6 "enrolled" users
    And I have created 2 "senior" administrators
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                           | initiated_by | completed_by | diff                    |
      |            |            |        |         | ACCOUNT_OPENING_REQUEST        | 100000001    |              | ACCOUNT_OPENING_REQUEST |
      | 100000001  |            |        |         | DELETE_TRUSTED_ACCOUNT_REQUEST | 100000001    |              |                         |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I click the "Search" button
    And I see "2" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # assign task
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SENIOR" in "User" field
    And I select the "SENIOR ADMIN 0" option
    And I enter value "this is a comment text 1" in "Enter some comments" field
    When I click the "Assign" button
    Then I see an error summary with "You can only claim or assign multiple tasks if the tasks are the same task type, please try another selection"

  @test-case-id-930588829174
  Scenario: As a junior admin I cant ASSIGN a task to a senior admin
    #Junior admin will not even get the list of senior admins in order to select one
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
    And I have created 5 "senior" administrators
    And I sign in as "junior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  |            |        |         | ACCOUNT_OPENING_REQUEST | 100000001    |              |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: Unclaimed" option
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And I "check" the "Select All Result Rows" checkbox
    # assign task
    And I click the "Upper Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I enter value "SENIOR" in "User" field
    And I select the "SENIOR ADMIN 0" option
    And I enter value "comment text 1" in "Enter some comments" field
    When I click the "Assign" button
    Then I am presented with the "Task List Bulk Assign" screen
    And I see an error summary with "Please select a user"

  @exec-manual @test-case-id-930588829205
  Scenario: As senior admin user When searching by Request ID system also displays any spinoff tasks
    Given I sign in as "senior admin" user
    And a "open account" task named "task_1" is available
    And I have the following spin-off tasks:
      | task_number | task_details                     |
      | task_2      | task with Parent ID = Request ID |
      | task_3      | task with request documents      |
      | task_4      | print letter task                |
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I enter a valid "Request ID" in "Request" field
    When I click the "Search" button
    Then I see the following tasks is results list:
      | tasks_available |
      | task_1          |
      | task_2          |
      | task_3          |
      | task_4          |
