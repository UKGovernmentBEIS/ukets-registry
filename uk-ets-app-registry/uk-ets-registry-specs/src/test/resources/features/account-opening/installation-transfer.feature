@functional-area-account-opening

Feature: Account opening - Installation transfer

  Epic: Account Management
  Version: 3.14 (06/05/2021)
  Story: (5.6.5) Account Opening with installation transfer
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  # BRs: https://pmo.trasys.be/confluence/pages/viewpage.action?spaceKey=UKETS&title=BRs+for+Installation+and+account+transfer

  @test-case-id-652947494 @sampling-smoke
  Scenario: As SRA I can open an OHA account with installation transfer and allowances
    # oha account with allowances and installationOwnership
    Given I have created an account with the following properties
      | property                 | value                                                                     |
      | accountType              | OPERATOR_HOLDING_ACCOUNT                                                  |
      | accountIndex             | 5                                                                         |
      | accountStatus            | OPEN                                                                      |
      | holderType               | ORGANISATION                                                              |
      | holderName               | Organisation 1                                                            |
      | legalRepresentative      | Legal Rep1a                                                               |
      | legalRepresentative      | Legal Rep2b                                                               |
      | authorisedRepresentative | Authorised Representative1                                                |
      | unitsInformation         | 1100,ALLOWANCE,10000002000,10000003099,10000019,CP0,CP0,empty,empty,false |
      | commitmentPeriod         | 0                                                                         |
      | transfersOutsideTal      | true                                                                      |
      | approveSecondAr          | true                                                                      |
      | installationOwnership    | 100000001,1234567890                                                      |
    # start of prerequisite 1: login
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" button
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    When I select the "Operator Holding Account" option
    And I click the "Continue" button
    When I click the "Account holder details" link
    When I select the "Organisation" option
    And I click the "Continue" button
    When I select the "Add a new organisation" option
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
    # start of prerequisite 3: create a primary contact
    When I click the "Add the primary contact" link
    When I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    When I click the "Account details" link
    When I enter value "account name value 1" in "Account name" field
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 5
    # fill Installation information
    When I click the "Installation information" link
    When I click the "Is this an installation transfer: Yes" button
    And The page "contains" the "Add the installation information" text
    And I click the "Continue" button
    # installation id: installation id of the previously created oha account
    When I enter value "100001" in "Installation Id" field
    # installation name: a new name
    And I enter value "new installation name" in "Installation name" field
    # installation name: a unique new permit id
    And I enter value "4321" in "Permit ID" field
    And I click the "Continue" button
    And The page "contains" the "new installation name" text
    And The page "contains" the "4321" text
    When I click the "Continue" button
    # add authorised representative
    When I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK977538690871" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    When I click the "Continue" button
    # finalize account opening
    When I click the "Continue" button
    When I click the "Confirm and submit" button
    And I get a Request Id
    And I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
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
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 2" link
    Then I am presented with the "View account" screen
    # ensure balance is zero
    Then I see the following fields having the values:
      | fieldName          | field_value                 |
      | Available quantity | Total available quantity: 0 |
    # ensure status is closed
    And The page "contains" the "Closed" text
    # ensure correct content in installation details
    When I click the "Installation details item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                                     |
      | Page main content | installation details no installation is linked to this account. |
    # ensure authorised representative(s) removal
    When I click the "Authorised representatives item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName         | field_value                                |
      | Page main content | Authorised representatives No items added. |
    # manual executed step:
    # ensure that compliance status is the same as before:
    # Then I see the following fields having the values:
    # | fieldName         | field_value |
    # | Compliance status | A           |
    # manual executed step:
    # ensure that compliance history data is removed from old account:
    # And I click the "Emissions and Surrenders" link
    # Then I see the following fields having the values:
    #  | fieldName                      | field_value                                                                       |
    #  | Reportable emissions           | Excluded (or emissions value if not empty)                                        |
    #  | Surrender status history       | Excluded (or emissions value if not empty)                                        |
    #  | Surrender history and comments | current_datetime System Exclusion status updated action_id Excluded: current_year |
    # ensure new account status is Open and transferred installation id EXISTS in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 1" link
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName          | field_value                                |
      | Available quantity | Total available quantity: 1,100 Allowances |
    When I click the "Installation details item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Open" text
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                         |
      | Page main content | Installation details Installation name new installation name Installation ID 100001 Installation activity type Manufacture of mineral wool insulation material Permit ID 4321 Permit entry into force 14 November 1986 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021 Request Update |
  # manual executed step:
  # ensure that compliance history data is added to the new account:
  # And I click the "Emissions and Surrenders" link
  # Then I see the following fields having the values:
  #  | fieldName                      | field_value                                                                       |
  #  | Reportable emissions           | Excluded (or emissions value if not empty)                                        |
  #  | Surrender status history       | Excluded (or emissions value if not empty)                                        |
  #  | Surrender history and comments | current_datetime System Exclusion status updated action_id Excluded: current_year |

  @test-case-id-94645265
  Scenario Outline: As SRA or JRA only I can open an OHA account with installation transfer
    # create an OHA so as to use the installation id at further step
    # THE ACCOUNT CREATED BELOW HAS EXCEPTIONALLY THE "installationOwnership" PARAMETER
    # IN ORDER TO CREATE OHA WITH INSTALLATION TRANSFER
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
      | installationOwnership    | 100000001,1234567890       |
    # start of prerequisite 1: login
    When I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" button
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    When I select the "Operator Holding Account" option
    And I click the "Continue" button
    When I click the "Account holder details" link
    When I select the "Organisation" option
    And I click the "Continue" button
    When I select the "Add a new organisation" option
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
    # start of prerequisite 3: create a primary contact
    When I click the "Add the primary contact" link
    When I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    When I click the "Account details" link
    When I enter value "account name value 1" in "Account name" field
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 5
    # fill Installation information
    When I click the "Installation information" link
    When I click the "Is this an installation transfer: Yes" button
    And The page "contains" the "Add the installation information" text
    And I click the "Continue" button
    # installation id: installation id of the previously created oha account
    When I enter value "100001" in "Installation Id" field
    # installation name: a new name
    And I enter value "new installation name" in "Installation name" field
    # installation name: a unique new permit id
    And I enter value "4321" in "Permit ID" field
    And I click the "Continue" button
    And The page "contains" the "new installation name" text
    And The page "contains" the "4321" text
    When I click the "Continue" button
    # add authorised representative
    When I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    When I click the "Continue" button
    # finalize account opening
    When I click the "Continue" button
    When I click the "Confirm and submit" button
    And I get a Request Id
    And I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |
    # sign in as another admin, claim and approve the task
    When I click the "Sign out" link
    Then I am presented with the "Sign in" screen
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    # ensure old account status is Transfer pending and installation id DOES NOT EXIST in account > installation submenu
    When I access "50002" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Installation details item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Transfer pending" text
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                           |
      | Page main content | Installation details Installation name Installation Name Installation ID 100001 Installation activity type Manufacture of mineral wool insulation material Permit ID 1234567890 Permit entry into force 14 November 1986 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021 Request Update |
    # Approve the task
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Open Account with installation transfer" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Approved" text
    # ensure new account status is Open and transferred installation id EXISTS in account > installation submenu
    When I click the "Accounts" button
    Then I am presented with the "Account list" screen
    When I click the "Search" button
    And I click the "Account number result 1" link
    Then I am presented with the "View account" screen
    When I click the "Installation details item" link
    Then I am presented with the "View account" screen
    And The page "contains" the "Open" text
    And I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                         |
      | Page main content | Installation details Installation name new installation name Installation ID 100001 Installation activity type Manufacture of mineral wool insulation material Permit ID 4321 Permit entry into force 14 November 1986 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021 Request Update |

    Examples:
      | user         |
      | senior admin |
      | junior admin |

  @test-case-id-94642061 @exec-manual
  Scenario Outline: As SRA I cannot successfully fill in the installation transfer details when I enter invalid values
    # start of prerequisite 1: login
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" button
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    When I select the "Operator Holding Account" option
    And I click the "Continue" button
    When I click the "Account holder details" link
    When I select the "Organisation" option
    And I click the "Continue" button
    When I select the "Add a new organisation" option
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
    # start of prerequisite 3: create a primary contact
    When I click the "Add the primary contact" link
    When I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    When I click the "Account details" link
    When I enter value "account name value 1" in "Account name" field
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 5
    # fill Installation information
    When I click the "Installation information" link
    When I click the "Is this an installation transfer: Yes" button
    And I click the "Continue" button
    # installation id: installation id of the previously created oha account
    When I enter value "<installation_id>" in "Installation Id" field
    # installation name: a new name
    And I enter value "<installation_name>" in "Installation name" field
    # installation name: a unique new permit id
    And I enter value "<permit_id>" in "Permit ID" field
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail with id "event-name-error" and content "<error>"

    Examples:
      | installation_id | installation_name     | permit_id                      | error                                                                                                    |
      # negative scenario for installation id that does not belong to oha account
      | 12345           | new installation name | 4321                           | You can only transfer the installation of Operator Holding Accounts                                      |
      # negative scenarios for Installation ID
      | [empty]         | new installation name | 4321                           | Enter the installation ID. The installation ID must be a positive number without decimal places.         |
      | !               | new installation name | 4321                           | The installation ID must be a positive number without decimal places.                                    |
      | 123             | new installation name | 4321                           | The account holder does not exist. Enter a valid account holder ID.                                      |
      | abc             | new installation name | 4321                           | The installation ID must be a positive number without decimal places.                                    |
      # negative scenarios for Installation name
      | 100001          | [empty]               | 4321                           | Enter the installation name.                                                                             |
      # negative scenarios for Permit ID
      | 100001          | new installation name | [empty]                        | Enter the permit ID.                                                                                     |
      | 100001          | new installation name | [permit id of another account] | An account with this permit ID already exists. You cannot create another account with the same permit ID |
      # negative scenario for installation id of an account os CLOSED status
      | 123456          | new installation name | 4321                           | The account is closed. The transfer cannot be requested.                                                 |
      # negative scenario for installation id of account transfer to the same account holder
      | 123456          | new installation name | 4321                           | You cannot transfer the account to the same Account Holder                                               |

  @test-case-id-9764501 @exec-manual
  Scenario: As SRA I can successfully fill in the installation transfer details when I enter permit id value of the old account
    # start of prerequisite 1: login
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" button
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    When I select the "Operator Holding Account" option
    And I click the "Continue" button
    When I click the "Account holder details" link
    When I select the "Organisation" option
    And I click the "Continue" button
    When I select the "Add a new organisation" option
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
    # start of prerequisite 3: create a primary contact
    When I click the "Add the primary contact" link
    When I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    When I click the "Account details" link
    When I enter value "account name value 1" in "Account name" field
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 5
    # fill Installation information
    When I click the "Installation information" link
    When I click the "Is this an installation transfer: Yes" button
    And I click the "Continue" button
    # installation id: installation id of the previously created oha account
    When I enter value "<installation_id>" in "Installation Id" field
    # installation name: a new name
    And I enter value "<installation_name>" in "Installation name" field
    # installation name: a unique new permit id
    And I enter value "[permit id of the old account]" in "Permit ID" field
    And I click the "Continue" button
    # finalize account opening
    When I click the "Continue" button
    When I click the "Confirm and submit" button
    And I get a Request Id
    And I see the following fields having the values:
      | fieldName | field_value                   |
      | Title     | We have received your request |

  @test-case-id-88405985 @exec-manual
  Scenario Outline: As SRA I cannot request an account opening with installation transfer if the old transferring account has pending task
    # create an OHA so as to use the installation id at further step
    Given I have created an account with the following properties
      | property                 | value                      |
      # already pending task:
      | alreadyPendingTaskType   | <task_type>                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountIndex             | 1                          |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 2             |
      | legalRepresentative      | Legal Rep3                 |
      | legalRepresentative      | Legal Rep4                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | trustedAccount           | UK-100-50001-2-11 ACTIVE   |
    # start of prerequisite 1: login
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    And I click the "Request account" button
    Then I am presented with the "Request to open registry account" screen
    When I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Operator Holding Account"
    When I select the "Operator Holding Account" option
    And I click the "Continue" button
    When I click the "Account holder details" link
    When I select the "Organisation" option
    And I click the "Continue" button
    When I select the "Add a new organisation" option
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue |
      | Organisation name | Name 1     |
    And I select the "Fill Registration number" option
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    And I select the "Fill VAT Registration number" option
    And I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 2: open account type: "Operator Holding Account"
    # start of prerequisite 3: create a primary contact
    When I click the "Add the primary contact" link
    When I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    And I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    And I click the "Continue" button
    When I enter the following values to the fields:
      | field                      | fieldValue               |
      | Position                   | Consultant               |
      | Line 1                     | line 1 value             |
      | Fill Town or city          | Smyrna                   |
      | dropdown                   | Country United Kingdom   |
      | Fill Postcode              | 12345                    |
      | dropdown                   | Country code 1: GR (30)  |
      | Fill Phone number 1        | 6911111111               |
      | Fill Email address         | estoudiantina@smyrna.com |
      | Fill Re-type email address | estoudiantina@smyrna.com |
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    When I click the "Account details" link
    When I enter value "account name value 1" in "Account name" field
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: add transaction rules
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Continue" button
    # end of prerequisite 5
    # fill Installation information
    When I click the "Installation information" link
    When I click the "Is this an installation transfer: Yes" button
    And I click the "Continue" button
    # installation id: installation id of the previously created oha account
    When I enter value "100001" in "Installation Id" field
    # installation name: a new name
    And I enter value "new installation name" in "Installation name" field
    # installation name: a unique new permit id
    And I enter value "4321" in "Permit ID" field
    And I click the "Continue" button
    Then I see an error summary with "The account has outstanding tasks. The transfer cannot be requested."
    And I see an error detail with id "event-name-error" and content "The account has outstanding tasks. The transfer cannot be requested."

    Examples:
      | task_type                                              |
      | AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST             |
      | AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST          |
      | AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST              |
      | AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST              |
      | AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST              |
      | AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST |
      | ADD_TRUSTED_ACCOUNT_REQUEST                            |
      | DELETE_TRUSTED_ACCOUNT_REQUEST                         |
      | ALLOCATION_TABLE_UPLOAD_REQUEST                        |
      | ALLOCATION_REQUEST                                     |
      | TRANSACTION_RULES_UPDATE_REQUEST                       |
      | TRANSACTION_REQUEST                                    |
      | ACCOUNT_OPENING_REQUEST                                |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS                 |
      | ACCOUNT_HOLDER_UPDATE_DETAILS                          |
      | AH_REQUESTED_DOCUMENT_UPLOAD                           |
      | INSTALLATION_TRANSFER                                  |
      | ACCOUNT_TRANSFER                                       |
      | INSTALLATION_DETAILS_UPDATE                            |

