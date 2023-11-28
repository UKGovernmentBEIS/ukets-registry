@functional-area-account-management

Feature: Account management - Account transfer

  # BRs: https://pmo.trasys.be/confluence/pages/viewpage.action?spaceKey=UKETS&title=BRs+for+Installation+and+account+transfer

  # Basic positive business flows:
  # from organization to organization: @test-case-id-9230842542
  # from organization to individual:   @test-case-id-9230186043
  # from individual to organization:   @test-case-id-9230186043
  # from individual to individual:     @test-case-id-9230186043

  @test-case-id-9230842542
  Scenario Outline: As SRA or JRA I can transfer an OHA account of allowed account status to another account holder by adding a valid existing account holder id organization
    # THE ACCOUNT CREATED BELOW HAS EXCEPTIONALLY THE "accountOwnership" PARAMETER
    # IN ORDER TO CREATE OHA WITH ACCOUNT TRANSFER
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 0                          |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
      | accountOwnership         | ACTIVE                     |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Request Account Transfer" text
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I click the "Enter account holder ID" button    
    And I enter value "100002" in "account holder ID" field
    And I click the "Available typeahead Organisation 2" link
    When I click the "Continue" button
    # check and submit
    And The page "contains" the "Check your answers" text
    When I click the "Submit" button
    Then I am presented with the "Account transfer" screen
    And The page "contains" the "Your account transfer request has been submitted" text
    # Sign as another senior admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that account holder name is changed in account screen > overview submenu
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                  |
      | Account holder content | Account Holder: Organisation 2 Close account |
    # ensure that account holder name is changed in account screen > account holder submenu
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                                 | field_value         |
      | row content of Organization details: Name | Name Organisation 2 |

    @sampling-smoke
    Examples:
      | user         | account_status |
      | senior admin | OPEN           |

    Examples:
      | user         | account_status |
      # check for other users
      | junior admin | OPEN           |

    Examples:
      | user         | account_status      |
      # check for other account status
      | senior admin | SUSPENDED           |
      | senior admin | SUSPENDED_PARTIALLY |

  @test-case-id-629546364 @sampling-smoke
  Scenario: As SRA I can see that upon account transfer of an account with authorised representative the authorised representatives are removed from the account
    # THE ACCOUNT CREATED BELOW HAS EXCEPTIONALLY THE "accountOwnership" PARAMETER
    # IN ORDER TO CREATE OHA WITH ACCOUNT TRANSFER
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 0                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
      | accountOwnership         | ACTIVE                     |
    And I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative5 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Request Account Transfer" text
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I click the "Enter account holder ID" button
    And I enter value "100002" in "account holder ID" field
    And I click the "Available typeahead Organisation 2" link
    When I click the "Continue" button
    # check and submit
    And The page "contains" the "Check your answers" text
    When I click the "Submit" button
    Then I am presented with the "Account transfer" screen
    And The page "contains" the "Your account transfer request has been submitted" text
    # Sign as another senior admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Authorised representatives item" link
    # ensure that authorised representatives have been removed (only title "Authorised representatives" exists)
    Then I see the following fields having the values:
      | fieldName         | field_value                                               |
      | Page main content | Authorised representatives No items added. Request update |

  @test-case-id-624058364
  Scenario: As SRA I can complete an account transfer process for an account without authorised representatives
    # THE ACCOUNT CREATED BELOW HAS EXCEPTIONALLY THE "accountOwnership" PARAMETER
    # IN ORDER TO CREATE OHA WITH ACCOUNT TRANSFER
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 0                        |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
      | accountOwnership    | ACTIVE                   |
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 1                        |
      | accountStatus       | OPEN                     |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 2           |
      | legalRepresentative | Legal Rep3               |
      | legalRepresentative | Legal Rep4               |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Request Account Transfer" text
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I click the "Enter account holder ID" button
    And I enter value "100002" in "account holder ID" field
    And I click the "Available typeahead Organisation 2" link
    When I click the "Continue" button
    # check and submit
    And The page "contains" the "Check your answers" text
    When I click the "Submit" button
    Then I am presented with the "Account transfer" screen
    And The page "contains" the "Your account transfer request has been submitted" text
    # Sign as another senior admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I click the "Request id result 1" link
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that account holder name is changed in account screen > overview submenu
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName              | field_value                                  |
      | Account holder content | Account Holder: Organisation 2 Close account |
    # ensure that account holder name is changed in account screen > account holder submenu
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName                                 | field_value         |
      | row content of Organization details: Name | Name Organisation 2 |

  @test-case-id-9958306043 @exec-manual
  Scenario Outline: As SRA or JRA I can transfer an OHA account of allowed account status to another account holder by adding a new organisation as account holder
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Request Account Transfer" text
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I click the "Add a new organisation as account holder" button
    When I click the "Continue" button
    Then I am presented with the "Account transfer" screen
    And I enter value "new Account holder name" in "Name" field
    When I click the "Registration number" button
    # company flow
    When I click the "Company registration number: I do not have a registration number" button
    When I click the "VAT registration number: I do not have a registration number" button
    And I enter value "vat reason 1" in "VAT registration number: Reason" field
    And I enter value "company reason 1" in "Company registration number: Reason" field
    When I click the "Continue" button
    # organization flow
    Then I am presented with the "Account transfer" screen
    And I enter value "address value 1" in "Address line 1" field
    And I enter value "town 1" in "Town or city" field
    And I enter value "12345" in "Postcode" field
    When I click the "Continue" button
    # fill mandatory information
    And I enter value "given name 1" in "Given names" field
    And I enter value "last name 1" in "Last name" field
    When I click the "I confirm that the primary contact is aged 18 or over" button
    When I click the "Continue" button
    # fill primary contact information
    And I enter value "position 1" in "What is their position in the company?" field
    When I click the "Primary contact work address is the same as the Account Holder address" button
    When I select the "country code GR for first phone" option
    And I enter value "6911111111" in "phone number 1" field
    And I enter value "abc@abc.com" in "Email address" field
    And I enter value "abc@abc.com" in "Confirm their email address" field
    When I click the "Continue" button
    # check and submit
    And The page "contains" the "Check your answers" text
    When I click the "Submit" button
    Then I am presented with the "Account transfer" screen
    And The page "contains" the "An update request has been submitted" text
    # Sign as another senior admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    # ensure that account holder id is changed in account list screen
    Then I see the following fields having the values:
      | fieldName               | field_value                                                                                                             |
      | Account result number 1 | UK-100-50001-2-11 Operator holding account 50001 Operator holding account new Account holder name Open NOT APPLICABLE 0 |
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure account holder name is changed in account screen > overview submenu
    And The page "contains" the "new Account holder name" text
    # ensure account holder name is changed in account screen > account holder submenu
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "new Account holder name" text

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-9230186043 @exec-manual
  Scenario Outline: As SRA I can transfer an OHA account of allowed account status to another account holder by entering account holder id
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | <account_holder_1_type>    |
      | holderName               | account holder 1 name      |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    # create a second account so as to have an existing account holder id
    And I have created another account holder named "account holder 2 name" of "<account_holder_2_type>" type
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Request Account Transfer" text
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    # choose business flow
    When I follow the "<business_flow_path>" path
    And I click the "Continue" button
    # check and submit
    And The page "contains" the "Check your answers" text
    Then I click the "Submit" button
    Then I am presented with the "Account transfer" screen
    And The page "contains" the "An update request has been submitted" text
    # Sign as another senior admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Account transfer" text
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "The proposed request has been approved" text
    # ensure that upon task approval, for the account_holder_1_type the account holder name is changed in account > account holder submenu
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I access the account with "<account_holder_1_type>" account holder type
    Then I see that the account holder name is "account holder 2 name"
    # ensure that upon task approval, for the account_holder_2_type the account holder name is changed in account > account holder submenu
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I access the account with "<account_holder_2_type>" account holder type
    Then I see that the account holder name is "account holder 1 name"

    Examples:
      | business_flow_path                     | account_holder_1_type | account_holder_2_type |
      | Enter account holder ID > Organization | ORGANIZATION          | INDIVIDUAL            |
      | Enter account holder ID > Individual   | INDIVIDUAL            | ORGANIZATION          |
      | Enter account holder ID > Individual   | INDIVIDUAL            | INDIVIDUAL            |

  @test-case-id-126352142 @exec-manual
  Scenario Outline: As not authorised user I cannot transfer an OHA account to another account holder
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then The page "does not contain" the "Request Account Transfer" text

    Examples:
      | user                      |
      | authorised representative |
      | authority                 |

  @test-case-id-103958142
  Scenario Outline: As SRA I cannot transfer an OHA account of invalid status to another account holder
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | <account_status>           |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    And The page "does not contain" the "Request Account Transfer" text

    Examples:
      | account_status   |
      | CLOSED           |
      | TRANSFER_PENDING |

  @test-case-id-1094372142 @exec-manual
  Scenario Outline: As SRA I cannot transfer an account of invalid account type to another account holder
    Given I have created an account with the following properties
      | property    | value          |
      | accountType | <account_type> |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I am presented with the "View account" screen
    When I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    And I click the "Submit" button
    Then I see an error summary with "You can only transfer Operator Holding Accounts"
    And I see an error detail for field "event-type" with content "You can only transfer Operator Holding Accounts"

    Examples:
      | account_type                      |
      | TRADING                           |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | PERSON_HOLDING_ACCOUNT            |

  @test-case-id-1090492142 @exec-manual
  Scenario: As SRA I cannot transfer an account to the same account holder
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I fill the mandatory fields using the same account holder id value
    And I click the "Submit" button
    Then I see an error summary with "You cannot transfer the account to the same Account Holder"
    And I see an error detail for field "event-type" with content "You cannot transfer the account to the same Account Holder"

  @test-case-id-9230839042 @exec-manual
  Scenario Outline:  As SRA I cannot transfer an account when i enter invalid values
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative6 |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Account Transfer" button
    Then I am presented with the "Account transfer" screen
    When I enter value "<value>" in "<business_flow_path>" field
    And I click the "Submit" button
    Then I see an error summary with "The account holder does not exist. Enter a valid account holder ID."
    And I see an error detail for field "event-type" with content "The account holder does not exist. Enter a valid account holder ID."

    Examples:
      | business_flow_path | value                          |
      | organization       | [empty]                        |
      | individual         | [empty]                        |
      | organization       | not existing account holder id |
      | individual         | not existing account holder id |
