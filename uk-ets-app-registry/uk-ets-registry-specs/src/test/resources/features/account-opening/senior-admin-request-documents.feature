@functional-area-account-opening
@functional-area-account-management
@functional-area-user-validation

Feature: Account opening - Senior admin request documents from the Users or Accounts or Tasks menu

  Epic: Account Opening
  Version: v2.9 (16/07/2020)
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=17&modificationDate=1594984966000&api=v2

  #
  # This feature file correspond to 3 functional areas, as Senior admin request documents can be implemented via:
  # 1. Accounts menu Account holder link
  # 2. Account opening menu
  # 3. Users menu
  #

  #
  # NOTE: HISTORY AND COMMENTS AREA contains request documents only when req doc is submitted by account/task, not user.
  #

  @defect-UKETS-6064 @test-case-id-636171315606
  Scenario: As AR I can complete a senior admin AH documents request on an ad hoc basis via Account menu ah link
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Available quantity | Total available quantity: 0                  |
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                 |
      | Page main content | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update Request Account Transfer |
    And The page "contains" the "Request documents" text
    When I click the "Request documents" button
    Then I am presented with the "Request documents" screen
    # ensure remove and add works as expected
    And I see "3" elements of "Remove"
    When I click the "Add another document" link
    Then I see "4" elements of "Remove"
    When I click the "Remove Nr. 3" link
    Then I see "3" elements of "Remove"
    When I click the "Add another document" link
    Then I see "4" elements of "Remove"
    When I click the "Remove Nr. 1" link
    Then I see "3" elements of "Remove"
    # add documents
    When I enter value "document name 1 value" in "Document name 1" field
    And I enter value "document name 2 value" in "Document name 2" field
    And I enter value "document name 3 value" in "Document name 3" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    When I click the "AuthorisedRepresentative1" button
    And I enter value "comment 1 value" in "Comment" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    # ensure cancel functionality works as expected
    When I click the "Cancel" link
    Then I am presented with the "Request documents" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                 |
      | Page main content | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update Request Account Transfer |
    # request documents successfully
    And The page "contains" the "Request documents" text
    When I click the "Request documents" button
    Then I am presented with the "Request documents" screen
    # negative scenario for 0 documents request
    Then I see "3" elements of "Remove"
    When I click the "Remove Nr. 3" link
    Then I see "2" elements of "Remove"
    When I click the "Remove Nr. 2" link
    Then I see "1" elements of "Remove"
    When I click the "Remove Nr. 1" link
    Then I see "0" elements of "Remove"
    And I click the "continue" button
    Then I see an error summary with "Select at least one document type"
    # negative scenario for 11 documents request
    When I click the "Add another document" link
    Then I see "1" elements of "Remove"
    When I click the "Add another document" link
    Then I see "2" elements of "Remove"
    When I click the "Add another document" link
    Then I see "3" elements of "Remove"
    When I click the "Add another document" link
    Then I see "4" elements of "Remove"
    When I click the "Add another document" link
    Then I see "5" elements of "Remove"
    When I click the "Add another document" link
    Then I see "6" elements of "Remove"
    When I click the "Add another document" link
    Then I see "7" elements of "Remove"
    When I click the "Add another document" link
    Then I see "8" elements of "Remove"
    When I click the "Add another document" link
    Then I see "9" elements of "Remove"
    When I click the "Add another document" link
    Then I see "10" elements of "Remove"
    When I click the "Add another document" link
    Then I see "11" elements of "Remove"
    And I click the "continue" button
    Then I see an error summary with "You cannot select more than 10 document types"
    # positive scenario:
    When I click the "Remove Nr. 11" link
    Then I see "10" elements of "Remove"
    When I click the "Remove Nr. 10" link
    Then I see "9" elements of "Remove"
    When I click the "Remove Nr. 9" link
    Then I see "8" elements of "Remove"
    When I click the "Remove Nr. 8" link
    Then I see "7" elements of "Remove"
    When I click the "Remove Nr. 7" link
    Then I see "6" elements of "Remove"
    When I click the "Remove Nr. 6" link
    Then I see "5" elements of "Remove"
    When I click the "Remove Nr. 5" link
    Then I see "4" elements of "Remove"
    When I click the "Remove Nr. 4" link
    Then I see "3" elements of "Remove"
    When I enter value "document name 1 value" in "Document name 1" field
    And I enter value "document name 2 value" in "Document name 2" field
    And I enter value "document name 3 value" in "Document name 3" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    When I click the "AuthorisedRepresentative1" button
    And I enter value "comment 1 value" in "Comment" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | Page main content | Request account holder documents Check and submit your document request Account details Account holder Organisation 1 Account name Operator holding account 50001 Requested documents Change requested documents document name 1 value document name 2 value document name 3 value Person who will receive the request Change person who will receive this request Authorised representative AuthorisedRepresentative1 Why you are assigning this task comment 1 value Submit request |
    When I click the "Submit request" button
    Then I am presented with the "Request documents" screen
    And The page "contains" the "You have submitted a request for account holder documents." text
    When I click the "Go back to the account details" link
    Then I am presented with the "View account" screen
    # ensure that an event has not been created in Account History and comments area due to pending task
    When I click the "History and comments item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                          |
      | Page main content | History and comments No items added. |
    # ensure that as the task created is not completed, no items added in Account holder
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                   |
      | Page main content | Account holder Account holder Legal entity details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Address and contact details Legal entity address Test address 7 Second address line Third address line Town or city London Country United Kingdom Postcode 12345 Identification documentation No items added. Request documents |
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Submit documents" text
    # sign in as authorized representative 1, access task Choose files and Complete task:
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    When I enter value "authorised_representative1@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    And I get a new otp based on the existing secret for the "authorised_representative1" user
    Then I am presented with the "Sign in 2fa authentication" screen
    When I enter value "correct otp for user authorised_representative1" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Upload the requested documents" text
    And The page "contains" the "If you need help to upload the documents please contact the helpdesk" text
    # negative scenario file types: doc
    When I choose using "file-upload-0" from 'files-general' the 'sample.doc' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.doc' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.doc' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPG"
    # negative scenario file types: exe
    When I choose using "file-upload-0" from 'files-general' the 'sample.exe' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.exe' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.exe' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPG"
    # negative scenario file types: bat
    When I choose using "file-upload-0" from 'files-general' the 'sample.bat' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.bat' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.bat' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPG"
    # negative scenario for empty file
    When I choose using "file-upload-0" from 'files-general' the 'empty.doc' file
    And I choose using "file-upload-1" from 'files-general' the 'empty.doc' file
    When I choose using "file-upload-2" from 'files-general' the 'empty.doc' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file is empty"
    # negative scenario for file size limit
    When I choose using "file-upload-0" from 'files-general' the 'file_size_2_point_05_mb.doc' file
    And I choose using "file-upload-1" from 'files-general' the 'file_size_2_point_05_mb.doc' file
    When I choose using "file-upload-2" from 'files-general' the 'file_size_2_point_05_mb.doc' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be smaller than 2MB"
    # negative scenario for file with virus
    # When I choose using "file-upload-0" from 'files-general' the 'corona_virus_file.bat' file
    # When I choose using "file-upload-1" from 'files-general' the 'corona_virus_file.bat' file
    # When I choose using "file-upload-2" from 'files-general' the 'corona_virus_file.bat' file
    # And I click the "Complete request" button
    # Then I see an error summary with "The selected file contains a virus"
    # positive scenario file types: bmp, tif, png, pdf, jpg
    When I choose using "file-upload-0" from 'files-general' the 'sample.bmp' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.tif' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.PNG' file
    And I click the "Complete task" button
    Then I am presented with the "Task Details Complete" screen
    And I enter value "complete task comment 1" in "comment area" field
    And I click the "Complete" button
    Then I am presented with the "Task Details" screen
    # sign in as senior admin user and ensure that the account now depicts the changes
    # (authorized representative could continue this flow as well)
    And I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    # requested documents are shown:
    And The page "contains" the "document name 1 value" text
    And The page "contains" the "document name 1 value.pdf" text
    # request documents process can be triggered again
    And The page "contains" the "Request documents" text

  @test-case-id-636171315840
  Scenario: As AR I can complete a senior admin AH documents request as part of Account opening task
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    And I have created 1 "senior" administrators
    And I sign in as "senior admin" user
    # access ACCOUNT_OPENING_REQUEST task
    And I have created the following tasks
      | account_id | claimed_by | status | outcome | type                    | initiated_by | completed_by |
      | 100000001  | 100000008  |        |         | ACCOUNT_OPENING_REQUEST | 100000007    |              |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account" text
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    # submit request documents via access in authorised representatives < Authorised Representative 1
    And I click the "List of authorised representatives" link
    And I click the "Authorised Representative1" link
    And The page "contains" the "Initiate and approve" text
    And I click the "UK977538690871" link
    Then I am presented with the "Task Details User" screen
    When I click the "Request documents" button
    Then I am presented with the "Request documents" screen
    When I enter value "document name 1 value" in "Document name 1" field
    And I enter value "document name 2 value" in "Document name 2" field
    And I enter value "document name 3 value" in "Document name 3" field
    And I click the "continue" button
    And I enter value "comment 1 value" in "Comment" field
    And I click the "continue" button
    And I click the "Submit request" button
    Then I am presented with the "Request documents" screen
    And The page "contains" the "You have submitted a request" text
    # ensure that no item added yet in request documents due to pending submit document open task
    And I click the "Go back to the user details" link
    Then I am presented with the "Task Details User" screen
    # sign in as authorized representative of the submit documents task, upload documents and complete task
    # sign in as authorized representative 1, access task Choose files and Complete task:
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    When I enter value "authorised_representative1@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    And I get a new otp based on the existing secret for the "authorised_rep_authorised_representative1" user
    Then I am presented with the "Sign in 2fa authentication" screen
    When I enter value "correct otp for user authorised_rep_authorised_representative1" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    Then I click the "I need to download a template" link
    And I see the following fields having the values:
      | fieldName                            | field_value                    |
      | Upload the requested documents label | Upload the requested documents |
    And The page "contains" the "Only upload templates or documents that have been requested by the Registry Administrator or National Administrator." text
    And The page "contains" the "More information about how and when to authenticate documents can be found in the" text
    # negative scenario file types: doc
    When I choose using "file-upload-0" from 'files-general' the 'sample.doc' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.doc' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.doc' file
    Then I see an error summary with "the selected file must be pdf, png or jpgthe selected file must be pdf, png or jpgthe selected file must be pdf, png or jpg"
    # negative scenario file types: exe
    When I choose using "file-upload-0" from 'files-general' the 'sample.exe' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.exe' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.exe' file
    Then I see an error summary with "the selected file must be pdf, png or jpgthe selected file must be pdf, png or jpgthe selected file must be pdf, png or jpg"
    # negative scenario file types: bat
    When I choose using "file-upload-0" from 'files-general' the 'sample.bat' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.bat' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.bat' file
    Then I see an error summary with "the selected file must be pdf, png or jpgthe selected file must be pdf, png or jpgthe selected file must be pdf, png or jpg"
    # negative scenario for file size limit
    When I choose using "file-upload-0" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    And I choose using "file-upload-1" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    When I choose using "file-upload-2" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    Then I see an error summary with "the file must be smaller than 2mbthe file must be smaller than 2mbthe file must be smaller than 2mb"
    # negative scenario for file with virus
    #When I choose using "file-upload-0" from 'files-general' the 'corona_virus_file.bat' file
    #When I choose using "file-upload-1" from 'files-general' the 'corona_virus_file.bat' file
    #When I choose using "file-upload-2" from 'files-general' the 'corona_virus_file.bat' file
    #And I click the "Complete request" button
    #Then I see an error summary with "The selected file contains a virus"
    # positive scenario file types: bmp, tif, png, pdf, jpg
    When I choose using "file-upload-0" from 'files-general' the 'sample.pdf' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.JPG' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.PNG' file
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I click the "Sign out" button
    # sign in as senior admin user and ensure that the task now depicts the changes
    # (authorized representative could continue this flow as well)
    Then I am presented with the "Sign in" screen
    When I enter value "test_senior_admin_user@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    And I get a new otp based on the existing secret for the "test_senior_admin_user" user
    Then I am presented with the "Sign in 2fa authentication" screen
    When I enter value "correct otp for user test_senior_admin_user" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    And I select the "Task status: Completed" option
    And I select the "Task claimed by: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    # requested documents are shown:
    And The page "contains" the "sample.pdf" text
    And The page "contains" the "sample.JPG" text
    And The page "contains" the "sample.PNG" text

  @test-case-id-636171315978
  Scenario: As AR I can complete a senior admin AH documents request via User details
    Given I sign in as "senior admin" user
    And I have created 1 "enrolled" users
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    Then I am presented with the "Registry dashboard" screen
    When I access "UK88299344979" in "user-details"
    Then I am presented with the "User details" screen
    # request documents via Users screen
    When I click the "Request documents" button
    Then I am presented with the "Request documents" screen
    When I enter value "document name 1 value" in "Document name 1" field
    And I enter value "document name 2 value" in "Document name 2" field
    And I enter value "document name 3 value" in "Document name 3" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    # ensure cancel functionality works as expected
    When I click the "Cancel" link
    Then I am presented with the "Request documents" screen
    When I click the "Cancel request" button
    Then I am presented with the "User details" screen
    # request documents successfully
    And The page "contains" the "Request documents" text
    When I click the "Request documents" button
    Then I am presented with the "Request documents" screen
    When I enter value "document name 1 value" in "Document name 1" field
    And I enter value "document name 2 value" in "Document name 2" field
    And I enter value "document name 3 value" in "Document name 3" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    When I click the "AuthorisedRepresentative1" button
    And I enter value "comment 1 value" in "Comment" field
    And I click the "continue" button
    Then I am presented with the "Request documents" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                        |
      | Page main content | request user documents check and submit your document request recipient details not updated recipient details recipient not updated recipient enrolled user 0 updated recipient requested documents change requested documents document name 1 value document name 2 value document name 3 value why you are assigning this task not updated why you are assigning this task change why you are assigning this task comment 1 value submit request |
    When I click the "Submit request" button
    Then I am presented with the "Request documents" screen
    And The page "contains" the "You have submitted a request" text
    # navigate back to user details and ensure that no document is added due to pending open submit document task
    When I click the "Go back to the user details" link
    Then I am presented with the "User details" screen
    Then The page "contains" the "No items added." text
    # sign in as enrolled user, access task Choose files and Complete task:
    When I click the "Sign out" button
    Then I am presented with the "Sign in" screen
    When I enter value "enrolled_user_0@test.com" in "Email" field
    And I enter value "stkuy!gh34#$%dgf#$dfJHGjh" in "Password" field
    And I click the "Sign in" button
    And I get a new otp based on the existing secret for the "enrolled_user_0" user
    Then I am presented with the "Sign in 2fa authentication" screen
    When I enter value "correct otp for user enrolled_user_0" in "otp" field
    And I click the "Sign in" button
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Search" button
    And I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Upload the requested documents" text
    # negative scenario file types: doc
    When I choose using "file-upload-0" from 'files-general' the 'sample.doc' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.doc' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.doc' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPG"
    # negative scenario file types: exe
    When I choose using "file-upload-0" from 'files-general' the 'sample.exe' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.exe' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.exe' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPG"
    # negative scenario file types: bat
    When I choose using "file-upload-0" from 'files-general' the 'sample.bat' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.bat' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.bat' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPGThe selected file must be PDF, PNG or JPG"
    # negative scenario for empty file
    When I choose using "file-upload-0" from 'files-general' the 'empty.pdf' file
    And I choose using "file-upload-1" from 'files-general' the 'empty.pdf' file
    When I choose using "file-upload-2" from 'files-general' the 'empty.pdf' file
    And I click the "Complete request" button
    Then I see an error summary with "The selected file is emptyThe selected file is emptyThe selected file is empty"
    # negative scenario for file size limit
    When I choose using "file-upload-0" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    And I choose using "file-upload-1" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    When I choose using "file-upload-2" from 'files-general' the 'file_size_2_point_05_mb.pdf' file
    And I click the "Complete request" button
    Then I see an error summary with "The file must be smaller than 2MBThe file must be smaller than 2MBThe file must be smaller than 2MB"
    # negative scenario for file with virus
    #When I choose using "file-upload-0" from 'files-general' the 'corona_virus_file.bat' file
    #When I choose using "file-upload-1" from 'files-general' the 'corona_virus_file.bat' file
    #When I choose using "file-upload-2" from 'files-general' the 'corona_virus_file.bat' file
    #And I click the "Complete request" button
    #Then I see an error summary with "The selected file contains a virus"
    # positive scenario file types: bmp, tif, png, pdf, jpg
    When I choose using "file-upload-0" from 'files-general' the 'sample.pdf' file
    And I choose using "file-upload-1" from 'files-general' the 'sample.JPG' file
    When I choose using "file-upload-2" from 'files-general' the 'sample.PNG' file
    And I click the "Complete task" button
    And The page "contains" the "COMPLETED" text
    # sign in as senior admin user and ensure that the user now depicts the changes
    # (authorized representative could continue this flow as well)
    And I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "User Administration" link
    Then I am presented with the "User Administration" screen
    When I enter value "UK88299344979" in "Name or user ID textbox" field
    And I click the "Search" button
    And I click the "result row number 1" link
    Then I am presented with the "User details" screen
    # requested documents are shown:
    And The page "contains" the "sample.pdf" text
    And The page "contains" the "sample.JPG" text
    And The page "contains" the "sample.PNG" text
    # request documents process can be triggered again
    And The page "contains" the "Request documents" text
