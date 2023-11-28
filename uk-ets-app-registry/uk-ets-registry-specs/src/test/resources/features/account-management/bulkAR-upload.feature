@functional-area-account-management
@exec-manual

Feature: Account Management - Bulk AR upload

  Epic: Account Management
  Version: v3.6 (20/01/2021)
  Story: 5.6.2	Bulk AR upload
  
 # Input XLS file, having columns:
 # | Account Name | Acount Number | User ID | First Name | Last Name | Email    | Permissions |
 # | not used     |               |         | not used   | not used  | not used |             |
 #
 # Conditions
 #    Accounts should have the OPEN status,
 #    Users    should have the REGISTERED, VALIDATED or ENROLLED status
 #    Permission can be "Initiate and approve" or "Initiate" or "Approve" or "Read only".
 #    No Add-AR task should be open for this Accountid - User Id  pair.

  @test-case-id-55122153
  Scenario Outline: As a junior or read only admin or AR or authority I must not see the Bulk AR upload tab.
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And The page "does not contain" the "Bulk AR upload" text

    Examples:
      | user                      |
      | junior admin              |
      | read only admin           |
      | authorized representative |
      | authority                 |

  @test-case-id-3023082198
  Scenario: As senior-admin user I cannot submit a Bulk-Ar xls when the upload process does not meet appropriate criteria
    Given the following accounts exist
      | Account Number       | Account type | account status |
      | UK-100-10000050-0-5  | OHA          | OPEN           |
      | UK-100-10000057-0-67 | OHA          | OPEN           |
      | UK-100-10000051-0-97 | AOHA         | OPEN           |
      | UK-100-10000063-0-37 | OHA          | PROPOSED       |
    And I sign in as "senior admin" user
    And the following users exist
      | User ID        | User status | User name       |
      | UK813935774586 | ENROLLED    | enrolled user   |
      | UK405681794859 | REGISTERED  | registered user |
      | UK40282084687  | VALIDATED   | validated user  |
      | UK602104496149 | SUSPENDED   | suspended user  |
    And a "BulkAR.xlsx" file exist having the contents
      | Account Name | Acount Number        | User ID        | First Name      | Last Name | Email | Permissions          |
      | OHA          | UK-100-10000050-0-5  | UK111111111111 | enrolled user   |           |       | Approve              |
      | OHA          | UK-100-10000055-0-77 | UK813935774586 | enrolled user   |           |       | Initiate and approve |
      | OHA          | UK-100-10000057-0-67 | UK405681794859 | registered user |           |       | Read only            |
      | AOHA         | UK-100-10000051-0-97 | UK40282084687  | validated user  |           |       | Initiate             |
      | OHA          | UK-100-10000063-0-37 | UK602104496149 | validated user  |           |       | Initiate             |
      | xxxx         |                      |                |                 |           |       |                      |
    And a "WrongBulkAR.xlsx" file exist having the contents
      | xAccount Name | xAcount Number      | xUser ID | xFirst Name | xLast Name | xEmail   | xPermissions |
      | not used      | UK-100-10000050-0-5 |          | not used    | not used   | not used |              |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And The page "contain" the "Bulk AR upload" text
    When I click the "Bulk AR upload" link
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text
    And The page "contains" the "The table must be an XLSX file" text
	# ensure that the flow does not continue if no file has been chosen
    When I click the "Continue" button
    Then I see an error summary with "Please select a file"
	# ensure empty .doc file cannot be uploaded
    When I choose using "ARupload" from 'files-general' the 'empty.doc' file
    And I click the "Continue" button
    Then I see an error summary with "The selected file must be an XLSX"
	# ensure empty .xls file cannot be uploaded
    When I choose using "ARupload" from 'files-general' the 'empty.xls' file
    And I click the "Continue" button
    Then I see an error summary with "The selected file must be an XLSX"
    #
    When I choose using "ARupload" from 'upload-allocation-table' the 'WrongBulkAR.xlsx' file
    And I click the "Continue" button
    Then I see an error summary with "The chosen file contains the following errors:"
    And  I see an error summary with "Account number column missing"
    And  I see an error summary with "Permissions column missing"
    And  I see an error summary with "UserID column missing"
	#
    When I choose using "ARupload" from 'upload-allocation-table' the 'BulkAR.xlsx' file
    And I click the "Continue" button
    Then I see an error summary with "The chosen file contains the following errors:"
    And  I see an error summary with "The Account number belongs to an account with Proposed status"
    And  I see an error summary with "The User ID does not exist in the Registry"
    And  I see an error summary with "Permissions must not be empty"
    And  I see an error summary with "The Account number must not be empty"
    And  I see an error summary with "The User ID must not be empty"

  @test-case-id-3023082261
  Scenario: As senior-admin user I can prepare to submit a Bulk-Ar xls and navigate back or cancel-it
    Given the following accounts exist
      | Account Number       | Account type | account status |
      | UK-100-10000050-0-5  | OHA          | OPEN           |
      | UK-100-10000057-0-67 | OHA          | OPEN           |
      | UK-100-10000051-0-97 | AOHA         | OPEN           |
    And I sign in as "senior admin" user
    And the following users exist
      | User ID        | User status | User name       |
      | UK813935774586 | ENROLLED    | enrolled user   |
      | UK405681794859 | REGISTERED  | registered user |
      | UK40282084687  | VALIDATED   | validated user  |
    And a "BulkAR.xlsx" file exist having the contents
      | Account Name | Acount Number        | User ID        | First Name      | Last Name | Email | Permissions          |
      | OHA          | UK-100-10000050-0-5  | UK813935774586 | enrolled user   |           |       | Approve              |
      | OHA          | UK-100-10000055-0-77 | UK813935774586 | enrolled user   |           |       | Initiate and approve |
      | OHA          | UK-100-10000057-0-67 | UK405681794859 | registered user |           |       | Read only            |
      | AOHA         | UK-100-10000051-0-97 | UK40282084687  | validated user  |           |       | Initiate             |
    #
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And The page "contain" the "Bulk AR upload" text
    When I click the "Bulk AR upload" link
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text
    When I choose using "ARupload" from 'upload-allocation-table' the 'BulkAR.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "Bulk AR upload check" screen
    And The page "contain" the "Check the file and submit" text
    And The page "contain" the "BulkAR.xlsx" text
	# upload a valid file and ensure that the back navigation functionality is feasible
    When I click the "Back" link
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text
    When I choose using "ARupload" from 'upload-allocation-table' the 'BulkAR.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "Bulk AR upload check" screen
    And The page "contain" the "Check the file and submit" text
    # ensure that the process can be cancelled
    # For some reason first try below does nothing - keep it like this for now
    When I click the "Cancel" link
    And The page "contain" the "Cancel request" text
    And The page "contain" the "Are you sure you want to cancel" text
    When I click the "Cancel request" button
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text

  @test-case-id-3023082310
  Scenario: As senior-admin user I can submit a Bulk-Ar xls and add-AR tasks will be created
    Given the following accounts exist
      | Account Number       | Account type | account status |
      | UK-100-10000050-0-5  | OHA          | OPEN           |
      | UK-100-10000057-0-67 | OHA          | OPEN           |
      | UK-100-10000051-0-97 | AOHA         | OPEN           |
    And I sign in as "senior admin" user
    And the following users exist
      | User ID        | User status | User name       |
      | UK813935774586 | ENROLLED    | enrolled user   |
      | UK405681794859 | REGISTERED  | registered user |
      | UK40282084687  | VALIDATED   | validated user  |
    And a "BulkAR.xlsx" file exist having the contents
      | Account Name | Acount Number        | User ID        | First Name      | Last Name | Email | Permissions          |
      | OHA          | UK-100-10000050-0-5  | UK813935774586 | enrolled user   |           |       | Approve              |
      | OHA          | UK-100-10000055-0-77 | UK813935774586 | enrolled user   |           |       | Initiate and approve |
      | OHA          | UK-100-10000057-0-67 | UK405681794859 | registered user |           |       | Read only            |
      | AOHA         | UK-100-10000051-0-97 | UK40282084687  | validated user  |           |       | Initiate             |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And The page "contain" the "Bulk AR upload" text
    When I click the "Bulk AR upload" link
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text
    When I choose using "ARupload" from 'upload-allocation-table' the 'BulkAR.xlsx' file
    And I click the "Continue" button
    Then I am presented with the "Bulk AR upload check" screen
    And The page "contain" the "Check the file and submit" text
    And The page "contain" the "BulkAR.xlsx" text
	#
    When I click the "Submit" button
    And The page "contain" the "AR table uploaded" text
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I see the following tasks
      | task type                     | name of initiator | account number       | authorised representative | task status |
      | Add authorised representative | senior admin      | UK-100-10000050-0-5  | enrolled user             | UNCLAIMED   |
      | Add authorised representative | senior admin      | UK-100-10000055-0-77 | enrolled user             | UNCLAIMED   |
      | Add authorised representative | senior admin      | UK-100-10000057-0-67 | registered user           | UNCLAIMED   |
      | Add authorised representative | senior admin      | UK-100-10000051-0-97 | validated user            | UNCLAIMED   |
	# As a 2nd admin, validate a task
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I enter value "UK-100-10000050-0-5" in "Account number" field
    And I click the "Search" button
    Then I am presented with the "Task List" screen
    And I see the following tasks
      | task type                     | name of initiator | account number      | authorised representative | task status |
      | Add authorised representative | senior admin      | UK-100-10000050-0-5 | enrolled user             | UNCLAIMED   |
    When I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And The page "contain" the "Add authorised representative" text
    And The page "contain" the "UK-100-10000050-0-5" text
    And The page "contain" the "Approve" text
    When I click the "Approve" button
    Then I am presented with the "Task Details approve request" screen
    When I click the "Complete task" button
	# the task is now APPROVED
    When I access "10000050" in "account-details"
    Then I am presented with the "View account overview" screen
    When I click the "Authorised representatives" link
    And The page "contain" the "enrolled user" text
	# Check the BR: No Add-AR task should be open for this Accountid - User Id  pair
	#   by re-loading the same xls file.
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And The page "contain" the "Bulk AR upload" text
    When I click the "Bulk AR upload" link
    Then I am presented with the "Bulk AR upload" screen
    And The page "contain" the "Upload AR access table" text
    When I choose using "ARupload" from 'upload-allocation-table' the 'BulkAR.xlsx' file
    And I click the "Continue" button
    Then I see an error summary with "The chosen file contains the following errors:"
    And  I see an error summary with "The AR is already added"
