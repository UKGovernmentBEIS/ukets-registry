@functional-area-account-management

Feature: Account management - Account View or Edit Details

  Epic: Account Management
  Version: 2.2 (19/03/2020)
  Story: (5.1.3)  View Account - Request to update the account details
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  # Users:
  # User_Registered     for a user having the REGISTERED status,
  # User_Validated      for a user having the VALIDATED status,
  # User_Enrolled       for a user having the ENROLLED status,
  # User_RegAdmin		for a Registry Administrator
  # User_RegAdminSenior for a Senior Registry Administrator
  # User_RegAdminJunior for a Junior Registry Administrator
  # User_RegAdminRO		for a Read-Only Registry Administrator

  @test-case-id-0112911153
  Scenario: As enrolled NON AR user I cannot access View account screen because I dont see the account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "enrolled" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    When I enter value "UK-100-50001-2-11" in "Account name or ID" field
    And I click the "Search" button
    Then The page "does not contain" the "UK-100-50001-2-11" text

  @sampling-smoke @test-case-id-0112911177
  Scenario: As enrolled AR user I CANNOT access View account screen of a PROPOSED status account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | PROPOSED                   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "enrolled" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    When I enter value "UK-100-50001-2-11" in "Account name or ID" field
    And I click the "Search" button
    Then The page "does not contain" the "UK-100-50001-2-11" text

  @test-case-id-0112911202
  Scenario: As enrolled AR user I can access View account screen of an OPEN status account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "enrolled" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure that data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN                                         |
      | Available quantity | Total available quantity: 0                  |

  @test-case-id-745471214
  Scenario Outline: As admin I can access View account screen of an OPEN or PROPOSED status account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | OPEN                       |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | legalRepresentative      | Legal Rep1                 |
      | legalRepresentative      | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # Ensure that data shown is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | <status>                                     |
      | Available quantity | Total available quantity: 0                  |

    @sampling-smoke
    Examples:
      | user            | status |
      | read only admin | OPEN   |

    Examples:
      | user         | status             |
      | junior admin | OPEN               |
      | senior admin | OPEN Change status |

  @test-case-id-0112911270
  Scenario: As user when I go back from View account screen to accounts list data is retained
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Back to account list" link
    Then I am presented with the "Account list" screen
    Then I see "1" elements of "Account list returned result rows"

  @exec-manual @test-case-id-0112911303
  Scenario: As AR enrolled I can go back from account details to accounts list
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Back to account list" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    Then I see the following fields having the values:
      | fieldName          | field_value       |
      | Account name or ID | UK-100-50001-2-11 |

  @test-case-id-745471316
  Scenario Outline: As admin I can navigate to all oha account details sections
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # basic information
    Then I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN Change status                           |
      | Available quantity | Total available quantity: 0                  |
    # item information
    When I click the "<link_name>" link
    Then I see the following fields having the values:
      | fieldName         | field_value               |
      | Page main content | <page_main_content_value> |
    # cross check History and comments item
    When I click the "History and comments item" link
    Then The page "contains" the "History and comments" text

    Examples:
      | link_name                       | page_main_content_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | Overview item                   | Overview Total available quantity: 0 Total reserved quantity: 0 Current surrender status:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | Account details item            | Account details Account type ETS - Operator Holding Account Account name Operator holding account 50001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | Account holder item             | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update Request Account Transfer                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Installation details item       | Installation details Installation name Installation Name Installation ID 100001 Installation activity type Manufacture of mineral wool insulation material Permit ID 1234567890 Permit entry into force 14 November 1986 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021 Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | Authorised representatives item | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative1 Authorised Representative2 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Authorised Representative3 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative3 Authorised Representative4 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative4 Authorised Representative5 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative5 Authorised Representative6 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 Request update |
      | Holdings item                   | Holdings Total available quantity: 0 Total reserved quantity: 0 Click on the unit to view more details. Unit type Available quantity Reserved quantity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | Transactions item               | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Trusted accounts item           | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |

  @test-case-id-745471369
  Scenario Outline: As admin I can navigate to all aoha account details sections
    Given I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50011" in "account-details"
    Then I am presented with the "View account" screen
    # basic information
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Aircraft operator holding account 50011 |
      | Account status     | OPEN Change status                                    |
      | Available quantity | Total available quantity: 0                           |
    # item information
    When I click the "<link_name>" link
    Then I see the following fields having the values:
      | fieldName         | field_value               |
      | Page main content | <page_main_content_value> |

    Examples:
      | link_name                       | page_main_content_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
      | Overview item                   | Overview Total available quantity: 0 Total reserved quantity: 0 Current surrender status:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | Account details item            | Account details Account type ETS - Aircraft Operator Holding Account Account name Aircraft operator holding account 50011                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | Account holder item             | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | Aircraft operator details item  | Aircraft operator details Aircraft Operator ID 100001 Monitoring plan ID 1234567890 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021 Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | Authorised representatives item | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative1 Authorised Representative2 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Authorised Representative3 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative3 Authorised Representative4 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative4 Authorised Representative5 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative5 Authorised Representative6 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 Request update |
      | History and comments item       | History and comments No items added.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | Holdings item                   | Holdings Total available quantity: 0 Total reserved quantity: 0 Click on the unit to view more details. Unit type Available quantity Reserved quantity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | Transactions item               | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Trusted accounts item           | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |

  @test-case-id-745471420
  Scenario Outline: As enrolled AR I can navigate to all oha account details sections
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN                                         |
      | Available quantity | Total available quantity: 0                  |
    # item information
    When I click the "<link_name>" link
    Then I see the following fields having the values:
      | fieldName         | field_value               |
      | Page main content | <page_main_content_value> |

    Examples:
      | link_name                       | page_main_content_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Overview item                   | overview total available quantity: 0 total reserved quantity: 0 current surrender status:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | Account details item            | Account details Account type ETS - Operator Holding Account Account name Operator holding account 50001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Account holder item             | Account Holder Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Installation details item       | Installation details Installation name Installation Name Installation ID 100001 Installation activity type Manufacture of mineral wool insulation material Permit ID 1234567890 Permit entry into force 14 November 1986 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
      | Authorised representatives item | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative1 Authorised Representative2 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Authorised Representative3 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative3 Authorised Representative4 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative4 Authorised Representative5 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative5 Authorised Representative6 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 ENROLLED USER initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about ENROLLED USER Request update |
      | Holdings item                   | Holdings Total available quantity: 0 Total reserved quantity: 0 Unit type Available quantity Reserved quantity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | Transactions item               | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
      | Trusted accounts item           | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
      | Emissions and Surrenders        | emissions and surrenders help with surrenders cumulative verified emissions - cumulative surrenders - current surrender status help with current surrender status reportable emissions during reporting period help with reportable emissions year reportable emissions last update 2021 surrender status history captured every year on 30 april, 11:59 pm year status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

  @test-case-id-745471467
  Scenario Outline: As enrolled AR I can navigate to all aoha account details sections
    Given I have created an account with the following properties
      | property                 | value                             |
      | accountType              | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |
      | holderType               | ORGANISATION                      |
      | holderName               | Organisation 1                    |
      | legalRepresentative      | Legal Rep1                        |
      | legalRepresentative      | Legal Rep2                        |
      | authorisedRepresentative | Authorised Representative1        |
      | authorisedRepresentative | Authorised Representative2        |
      | authorisedRepresentative | Authorised Representative3        |
      | authorisedRepresentative | Authorised Representative4        |
      | authorisedRepresentative | Authorised Representative5        |
      | authorisedRepresentative | Authorised Representative6        |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50011" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                           |
      | Account name label | Account name: Aircraft operator holding account 50011 |
      | Account status     | OPEN                                                  |
      | Available quantity | Total available quantity: 0                           |
    # item information
    When I click the "<link_name>" link
    Then I see the following fields having the values:
      | fieldName         | field_value               |
      | Page main content | <page_main_content_value> |

    Examples:
      | link_name                       | page_main_content_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Overview item                   | Overview Total available quantity: 0 Total reserved quantity: 0 Current surrender status:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Account details item            | Account details Account type ETS - Aircraft Operator Holding Account Account name Aircraft operator holding account 50011                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
      | Account holder item             | Account Holder Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request Update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Aircraft operator details item  | Aircraft operator details Aircraft Operator ID 100001 Monitoring plan ID 1234567890 Regulator SEPA First year of verified emission submission 2021 Last year of verified emission submission 2021                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
      | Authorised representatives item | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative1 Authorised Representative2 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Authorised Representative3 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative3 Authorised Representative4 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative4 Authorised Representative5 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative5 Authorised Representative6 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 AUTHORIZED REPRESENTATIVE USER initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about AUTHORIZED REPRESENTATIVE USER Request update |
      | History and comments item       | History and comments No items added.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | Holdings item                   | Holdings Total available quantity: 0 Total reserved quantity: 0 Unit type Available quantity Reserved quantity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
      | Transactions item               | transactions rules for transactions is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list? no are transfers to accounts not on the trusted account list allowed? yes is the approval of a second ar necessary to execute a surrender transaction or a return of excess allocation? yes request update                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
      | Trusted accounts item           | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | Emissions and Surrenders        | emissions and surrenders help with surrenders cumulative verified emissions - cumulative surrenders - current surrender status help with current surrender status aviation emissions during reporting period help with aviation emissions year aviation emissions last update 2021 surrender status history captured every year on 30 april, 11:59 pm year status                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |

  @exec-manual @test-case-id-0112911537
  Scenario: As AR I can see AH details of an account
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                       |
      | Account name label | Account name: Operator holding account 50001 OPEN |
      | Available quantity | Total available quantity: 0                       |
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                        |
      | Page main content | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update |

  @exec-manual @test-case-id-745471552
  Scenario Outline: As admin I can see AH details of an account
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                     |
      | Account name label | <account_name_area_information> |
      | Available quantity | Total available quantity: 0     |
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                        |
      | Page main content | Account Holder Account Holder ID 100001 Organisation details Name Organisation 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Request Update |

    Examples:
      | user            | account_name_area_information                                   |
      | junior admin    | Account name: Operator holding account 50001 OPEN               |
      | senior admin    | Account name: Operator holding account 50001 OPEN Change status |
      | read only admin | Account name: Operator holding account 50001 OPEN               |

  @exec-manual @test-case-id-0112911619
  Scenario: As AR I can view active account ARs
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                       |
      | Account name label | Account name: Operator holding account 50001 OPEN |
      | Available quantity | Total available quantity: 0                       |
    When I click the "Authorised representatives item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | Page main content | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative4 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative5 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative6 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info ENROLLED USER Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info |

  @exec-manual @test-case-id-745471632
  Scenario Outline: As admin I can view active account ARs
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | <account_name_information>  |
      | Available quantity | Total available quantity: 0 |
    When I click the "Authorised representatives item" link
    And The page "<request_update_existence>" the "Request update" text
    Then I see the following fields having the values:
      | Authorised Representatives content | Name Access rights Work contact details Authorised Representative1 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative4 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative5 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative6 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info |

    Examples:
      | user            | account_name_information                                        | request_update_existence |
      | senior admin    | Account name: Operator holding account 50001 OPEN Change status | contains                 |
      | junior admin    | Account name: Operator holding account 50001 OPEN               | contains                 |
      | read only admin | Account name: Operator holding account 50001 OPEN               | does not contain         |

  @exec-manual @test-case-id-745471677
  Scenario Outline: As admin I can go back from account details to accounts list
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Back to account list" link
    Then I am presented with the "Account list" screen

    Examples:
      | user            |
      | junior admin    |
      | senior admin    |
      | read only admin |

  @exec-manual @test-case-id-745471713
  Scenario Outline: As admin I can see accounts holdings
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "<user>" user
    And I have created 1 "senior" administrators
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               |                   |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Account name label | <account_name_information>                    |
      | Available quantity | Total available quantity: 1,199 Removal Units |
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                      |
      | Page main content | Holdings Total available quantity: 0 Total reserved quantity: 0 Click on the unit to view more details. Unit type Original CP Applicable CP Available quantity Reserved quantity |

    Examples:
      | user            | account_name_information                                    |
      | senior admin    | Account name: Party Holding Account 1000 OPEN Change status |
      | junior admin    | Account name: Party Holding Account 1000 OPEN               |
      | read only admin | Account name: Party Holding Account 1000 OPEN               |

  @exec-manual @test-case-id-0112911787
  Scenario: As AR I can see accounts holdings
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | account_status | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1000-1-94 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN           | 1199 | RMU   | true       | true      | 1        | 1199   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
    And the following issuance limits and units have been set for the current commitment period
      | period | unit_type | quantity | activity      | consumed |
      | 2      | AAU       | 80       |               | 10       |
      | 2      | RMU       | 50       | Afforestation | 20       |
    And I sign in as "enrolled" user
    And I have created 1 "senior" administrators
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I create the following transactions, transaction tasks, levels and units
      | fieldName                | field_value       |
      | Originating Country Code | GB                |
      | Account ID               | 100000001         |
      | Claimed by               |                   |
      | Initiated by             | 100000002         |
      | Transaction type         | Internal Transfer |
      | Commitment period        | 2                 |
      | Acquiring account        | GB-100-1000-1-94  |
      | Transferring account     | GB-100-1001-1-89  |
      | Unit type                | RMU               |
      | Quantity to issue        | 40                |
      | Environmental activity   | Afforestation     |
    Then I am presented with the "Registry dashboard" screen
    When I access "1000" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                   |
      | Account name label | Account name: Party Holding Account 1000 OPEN |
      | Available quantity | Total available quantity: 1,199 Removal Units |
    When I click the "Holdings item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                              |
      | Page main content | Holdings Total available quantity: 0 Total reserved quantity: 0 Unit type Original CP Applicable CP Available quantity Reserved quantity |

  @exec-manual @test-case-id-745471802
  Scenario Outline: As admin I can see tal of an account
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
    And I sign in as "<user>" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | <account_name_information>  |
      | Available quantity | Total available quantity: 0 |
    When I click the "Trusted accounts item" link
    # ensure that the "Request update" button is shown:
    Then The page "contains" the "Trusted accounts" text
    And The page "contains" the "No items added." text

    Examples:
      | user            | account_name_information                                        |
      | senior admin    | Account name: Operator holding account 50001 OPEN Change status |
      | junior admin    | Account name: Operator holding account 50001 OPEN               |
      | read only admin | Account name: Operator holding account 50001 OPEN               |

  @exec-manual @test-case-id-028402061
  Scenario Outline: As admin I can search an account in tal based on tal specific fields
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
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | <account_name_information>  |
      | Available quantity | Total available quantity: 0 |
    When I click the "Trusted accounts item" link
    And I click the "Show filters" button
    And I enter value "<search_enter_text>" in "<field>" field
    And I click the "Search" button
    Then I see the following fields having the values:
      | fieldName | field_value         |
      | tal list  | correct tal account |

    Examples:
      | search_enter_text                     | field                       |
      | trusted_account_1_account_number      | Account number              |
      | trusted_account_1_account_name        | Account name or description |
      | trusted_account_1_account_description | Account name or description |
      | trusted_account_1_type                | Trusted_account_type        |

  @exec-manual @test-case-id-0112911873
  Scenario: As AR enrolled I can see tal of an account
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    # ensure data is correct and OPEN status is visible:
    Then I see the following fields having the values:
      | fieldName          | field_value                                       |
      | Account name label | Account name: Operator holding account 50001 OPEN |
      | Available quantity | Total available quantity: 0                       |
    When I click the "Trusted accounts item" link
    # ensure "Request update" button is visible:
    And The page "contains" the "Request update" text
    Then I see the following fields having the values:
      | Page main content | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending |

  @test-case-id-745471883
  Scenario Outline: As authorised representative i can edit the account name or billing address
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | OPEN                           |
      | holderType                  | ORGANISATION                   |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account details item" link
    Then I am presented with the "View account" screen
    When I click the "<update_type>" button
    Then I am presented with the "View account" screen
    When I clear the "<clear_element>" field
    And I enter value "<new_value>" in "account name text box" field
    When I click the "Continue" button
    Then I am presented with the "View account" screen
    When I click the "Submit" button
    Then I am presented with the "View account" screen
    And The page "contains" the "The account details have been updated." text
    When I click the "Back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Account details item" link
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName    | field_value    |
      | Account name | <new_value_ui> |

    @sampling-smoke
    Examples:
      | update_type    | clear_element         | new_value | new_value_ui |
      | Update account | account name text box | new name  | new name     |

    @exec-manual
    Examples:
      | update_type                    | clear_element                    | new_value           | new_value_ui                                |
      | Update account billing address | account billing address text box | new billing address | Account billing address new billing address |

  @sampling-smoke @test-case-id-745471934
  Scenario Outline: As AR I see the account status and the ARs for OPEN or ALL TRANSACTIONS RESTRICTED or SOME TRANSACTIONS RESTRICTED accounts
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | <account_status>           |
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | <account_status_ui>                          |
      | Available quantity | Total available quantity: 0                  |
    When I click the "Authorised representatives item" link
    And The page "contains" the "Authorised Representative1" text
    And The page "contains" the "Authorised Representative2" text
    And The page "contains" the "Authorised Representative3" text
    And The page "contains" the "Authorised Representative4" text
    And The page "contains" the "Authorised Representative5" text
    And The page "contains" the "Authorised Representative6" text

    Examples:
      | account_status               | account_status_ui            |
      | OPEN                         | OPEN                         |
      | ALL_TRANSACTIONS_RESTRICTED  | ALL TRANSACTIONS RESTRICTED  |
      | SOME_TRANSACTIONS_RESTRICTED | SOME TRANSACTIONS RESTRICTED |

  @test-case-id-745471979
  Scenario Outline: As AR I cannot see the account status of CLOSED or SUSPENDED or SUSPENDED PARTIALLY or TRANSFER PENDING account
    Given I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | accountStatus            | <account_status>           |
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
    And I sign in as "enrolled" user
    And I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    And I click the "Accounts" link
    Then I am presented with the "Account list" screen
    And I click the "Show filters" button
    When I enter value "UK-100-50001-2-11" in "Account name or ID" field
    And I click the "Search" button
    #And I clear the "Account name or ID" field
    Then The page "does not contain" the "UK-100-50001-2-11" text

    @sampling-smoke
    Examples:
      | account_status |
      | CLOSED         |

    Examples:
      | account_status      |
      | SUSPENDED           |
      | SUSPENDED_PARTIALLY |
      | TRANSFER_PENDING    |

  @test-case-id-0112912045
  Scenario: As admin I can see the correct account status and the ARs
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
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                  |
      | Account name label | Account name: Operator holding account 50001 |
      | Account status     | OPEN Change status                           |
      | Available quantity | Total available quantity: 0                  |
    When I click the "Authorised representatives item" link
    Then  I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
      | Page main content | Authorised representatives Active representatives Name Access rights Work contact details Authorised Representative1 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative1 Authorised Representative2 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative2 Authorised Representative3 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative3 Authorised Representative4 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative4 Authorised Representative5 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative5 Authorised Representative6 initiate and approve transactions and trusted account list (tal) updates email dont@care.com Phone number +UK (44)1434634996 more info about Authorised Representative6 Request update |

  @exec-manual @test-case-id-0112912083
  Scenario: As admin user I cannot see the suspended ARs of account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative1           |
      | authorisedRepresentative | Authorised Representative2           |
      | authorisedRepresentative | Authorised Representative3           |
      | authorisedRepresentative | Authorised Representative4           |
      | authorisedRepresentative | Authorised Representative5 SUSPENDED |
      | authorisedRepresentative | Authorised Representative6           |
    And I sign in as "senior admin" user
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                     |
      | Account name label | Account name: Operator holding account 50001 OPEN Change status |
      | Available quantity | Total available quantity: 0                                     |
    When I click the "Authorised representatives item" link
    Then The page "contains" the "Authorised representatives" text
    And The page "contains" the "Active representatives" text
    Then I see the following fields having the values:
      | Authorised Representatives content | Name Access rights Work contact details Authorised Representative1 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative4 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative6 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info |
    And The page "contains" the "Request update" text

  @exec-manual @test-case-id-0112912123
  Scenario: As AR I cannot see the suspended ARs of an account
    Given I have created an account with the following properties
      | property                 | value                                |
      | accountType              | OPERATOR_HOLDING_ACCOUNT             |
      | holderType               | ORGANISATION                         |
      | holderName               | Organisation 1                       |
      | legalRepresentative      | Legal Rep1                           |
      | legalRepresentative      | Legal Rep2                           |
      | authorisedRepresentative | Authorised Representative1           |
      | authorisedRepresentative | Authorised Representative2           |
      | authorisedRepresentative | Authorised Representative3           |
      | authorisedRepresentative | Authorised Representative4           |
      | authorisedRepresentative | Authorised Representative5 SUSPENDED |
      | authorisedRepresentative | Authorised Representative6           |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I have created the following tasks
      | account_id | claimed_by | status   | outcome  | type                    | initiated_by | completed_by |
      | 100000001  | 100000001  | APPROVED | APPROVED | ACCOUNT_OPENING_REQUEST | 100000001    | 100000001    |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName          | field_value                                                     |
      | Account name label | Account name: Operator holding account 50001 OPEN Change status |
      | Available quantity | Total available quantity: 0                                     |
    When I click the "Authorised representatives item" link
    Then The page "contains" the "Authorised representatives" text
    And The page "contains" the "Active representatives" text
    Then I see the following fields having the values:
      | Authorised Representatives content | Name Access rights Work contact details Authorised Representative1 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative2 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative3 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative4 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info Authorised Representative6 Initiate and approve transactions Email dont@care.com Phone number +UK (44)1434634996 more info |
    And The page "contains" the "Request update" text
