@functional-area-account-management

Feature: Account management - TAL update request account add or remove

  Epic: Account Management
  Version: 2.2 (19/03/2020)
  Story: (5.2.1) Request to update the trusted account list
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  # TRUSTED ACCOUNT STATUS can have the values of:
  # PENDING_APPROVAL
  # PENDING_ACTIVATION
  # ACTIVE
  # REJECTED

  @exec-manual @test-case-id-6075822391
  Scenario Outline: As admin I can access View account screen
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
    And I see the following fields having the values:
      | fieldName          | field_value                 |
      | Account name label | <account_name__info>        |
      | Available quantity | Total available quantity: 0 |

    Examples:
      | user         | account_name__info                                              |
      | senior admin | Account name: Operator holding account 50001 OPEN Change status |
      | junior admin | Account name: Operator holding account 50001 OPEN               |

  @sampling-smoke @test-case-id-38405342461 @sampling-mvp-smoke
  Scenario: As admin I cannot add a PROPOSED status account to tal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat     | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | PROPOSED | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN     | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    When I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
    And I click the "continue" button
    Then I see an error summary with "Invalid account number - The account number does not exist in the registry"

  @sampling-smoke @test-case-id-7868064012 @sampling-mvp-smoke
  Scenario: As admin I cannot add a CLOSED status account to tal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat   | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | CLOSED | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN   | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    When I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
    And I click the "continue" button
    When I click the "Submit request" button
    Then I see an error summary with "A TAL update cannot be submitted for candidate account with CLOSURE PENDING or CLOSED account status"

  @test-case-id-3840534246122
  Scenario: As admin I can add an external account in tal
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
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    When I enter the following values to the fields:
      | field                     | fieldValue |
      | user defined country code | JP         |
      | user defined account type | 100        |
      | user defined account id   | 123456     |
    And I click the "continue" button
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text

  @test-case-id-38405342509
  Scenario Outline: As admin I can add an ets oha account to kp tal and vice versa and relevant warning appears
    Given the following accounts have been created
      | account_id        | kyoto_account_type     | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-121-50031-2-60 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50031     | 1                   |
      | GB-121-50032-2-58 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50032     | 1                   |
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
    And I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    When I access "<access_account>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    # case: no selection of radio button
    And I click the "Continue" button
    And I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add trusted account in list, set account - without comment:
    And I enter the following values to the fields:
      | field                            | fieldValue                         |
      | user defined country code        | <user_defined_country_code>        |
      | user defined account type        | <user_defined_account_type>        |
      | user defined account id          | <user_defined_account_id>          |
      | user defined account period      | <user_defined_account_period>      |
      | user defined check digits period | <user_defined_check_digits_period> |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    And The page "contains" the "The following account will be added to the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
    # ensure warning exists in case of kp to ets addition ot ets to kp addition
    And The page "contains" the "<warning>" text
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    When I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Close account" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I access "<access_account>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then The page "contains" the "<trusted_account>" text

    @sampling-smoke
    Examples:
      | user         | access_account | user_defined_country_code | user_defined_account_type | user_defined_account_id | user_defined_account_period | user_defined_check_digits_period | trusted_account   | warning                                                                                                                             |
      # access kp account (gb) and add the other kp (gb) account to tal
      | senior admin | 50032          | GB                        | 121                       | 50031                   | 2                           | 60                               | GB-121-50031-2-60 | Check the update request and confirm                                                                                                |
      # access kp account (gb) and add the other kp (gb) account to tal
      | junior admin | 50032          | GB                        | 121                       | 50031                   | 2                           | 60                               | GB-121-50031-2-60 | Check the update request and confirm                                                                                                |
      # access kp account (gb) and add the ets oha to tal
      | senior admin | 50032          | UK                        | 100                       | 50001                   | 2                           | 11                               | UK-100-50001-2-11 | The selected account is not a KP account. Transactions cannot be made to this account if it is added to the trusted account list.   |
      
    @exec-manual
    Examples:
      | user         | access_account | user_defined_country_code | user_defined_account_type | user_defined_account_id | user_defined_account_period | user_defined_check_digits_period | trusted_account   | warning                                                                                                                             |
      # access ets oha account and add kp (gb) account to tal
      | senior admin | 50001          | GB                        | 121                       | 50031                   | 2                           | 60                               | GB-121-50031-2-60 | The selected account is not an ETS account. Transactions cannot be made to this account if it is added to the trusted account list. |

  @test-case-id-38405342510
  Scenario Outline: As AR I can add an ets oha account to kp tal and vice versa and relevant warning appears
    Given the following accounts have been created
      | account_id        | kyoto_account_type     | registry_account_type | account_name     | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-121-50031-2-60 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50031     | 1                   |
      | GB-121-50032-2-58 | PERSON_HOLDING_ACCOUNT | Null                  | Person Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 50032     | 1                   |
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "<access_account>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    # case: no selection of radio button
    And I click the "Continue" button
    And I see an error summary with "Select a type of update"
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add trusted account in list, set account - without comment:
    And I enter the following values to the fields:
      | field                            | fieldValue                         |
      | user defined country code        | <user_defined_country_code>        |
      | user defined account type        | <user_defined_account_type>        |
      | user defined account id          | <user_defined_account_id>          |
      | user defined account period      | <user_defined_account_period>      |
      | user defined check digits period | <user_defined_check_digits_period> |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    And The page "contains" the "The following account will be added to the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
    # ensure warning exists in case of kp to ets addition ot ets to kp addition
    And The page "contains" the "<warning>" text
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    Given I sign in as "senior admin 2" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I select the "Task status: All" option
    And I click the "Advanced search" button
    And I select the "Exclude user tasks: No" option
    When I click the "Search" button
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment 1" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    And The page "contains" the "Close account" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    When I access "<access_account>" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then The page "contains" the "<trusted_account>" text

    Examples:
      | access_account | user_defined_country_code | user_defined_account_type | user_defined_account_id | user_defined_account_period | user_defined_check_digits_period | trusted_account   | warning                                                                                                                             |
      # access kp account (gb) and add the other kp (gb) account to tal
      | 50032          | GB                        | 121                       | 50031                   | 2                           | 60                               | GB-121-50031-2-60 | Check the update request and confirm                                                                                                |
      # access kp account (gb) and add the ets oha to tal
      | 50032          | UK                        | 100                       | 50001                   | 2                           | 11                               | UK-100-50001-2-11 | The selected account is not a KP account. Transactions cannot be made to this account if it is added to the trusted account list.   |
      # access ets oha account and add kp (gb) account to tal
      | 50001          | GB                        | 121                       | 50031                   | 2                           | 60                               | GB-121-50031-2-60 | The selected account is not an ETS account. Transactions cannot be made to this account if it is added to the trusted account list. |

  @test-case-id-38405342593
  Scenario: As admin I can add an account to tal WITH COMMENT
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And The page "contains" the "Add account" text
    And The page "contains" the "Remove account(s)" text
    # case: no selection of radio button
    And I click the "Continue" button
    Then I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add trusted account in list, set account - without comment:
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
      | comment area                     | comment 1  |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    And The page "contains" the "The following account will be added to the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
      | account area content   | Account Description GB-100-1002-1-84 comment 1   |
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                            |
      | Page main content | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending gb-100-1002-1-84 comment 1 manually added pending approval |
    # ensure task has been created:
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Add trusted account" text

  @test-case-id-38405342679
  Scenario: As admin I cannot add the same account to tal twice
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                 |
      | Page main content | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending gb-100-1002-1-84 manually added |
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    Then The page "contains" the "Add account" text
    And The page "contains" the "Remove account(s)" text
    # case: no selection of radio button
    And I click the "Continue" button
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add trusted account in list, set account - without comment:
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
      | comment area                     | comment 1  |
    When I click the "continue" button
    Then I see an error summary with "the selected account has already been added to the trusted account list"

  @test-case-id-38505342679
  Scenario: As authorised representative user I can change description of tal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    Then I click the "Change description" link
    And I enter the following values to the fields:
      | field                   | fieldValue       |
      | change description area | Some description |
    And I click the "Continue" link
    Then I am presented with the "View account" screen
    Then I see the following fields having the values:
      | fieldName   | field_value      |
      | description | Some description |
    And I click the "Submit" link
    And The page "contains" the "The account description has been updated" text
    Then I click the "Back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                         |
      | Page main content | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending gb-100-1002-1-84 some description change manually added |

  @test-case-id-38405342744
  Scenario: As junior admin I can cancel a tal request update
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And The page "contains" the "Add account" text
    And The page "contains" the "Remove account(s)" text
    # case: no selection of radio button
    And I click the "Continue" button
    Then I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # cancel process during tal addition
    When I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
      | comment area                     | comment 1  |
    Then I am presented with the "Account trusted account list" screen
    And I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    And The page "contains" the "The following account will be added to the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
      | account area content   | Account Description GB-100-1002-1-84 comment 1   |
    When I click the "Cancel" link
    Then I am presented with the "Account trusted account list" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                 |
      | Page main content | trusted accounts request update about the trusted account list hide filters account number enter text account name or description enter text trusted account type select from list automatically trusted manually added search clear account number account name / description trusted account type pending |

  @test-case-id-38405342824
  Scenario: As a user I can see data retained while navigating backwards during a tal update
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And The page "contains" the "Add account" text
    And The page "contains" the "Remove account(s)" text
    # case: no selection of radio button
    And I click the "Continue" button
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add first trusted account in list, set account - with or without comment:
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
      | comment area                     | comment 1  |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Request to update the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
      | account area content   | Account Description GB-100-1002-1-84 comment 1   |
    When I click the "Back" link
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | user defined country code        | GB        |
      | user defined account type        | 100       |
      | user defined account id          | 1002      |
      | user defined account period      | 1         |
      | user defined check digits period | 84        |
      | comment area                     | comment 1 |

  @test-case-id-38405342901
  Scenario: As admin I cannot remove an account from tal when account status is PENDING APPROVAL on another tal task
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    # check in trusted account list:
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And The page "contains" the "Add account" text
    And The page "contains" the "Remove account(s)" text
    # case: no selection of radio button
    And I click the "Continue" button
    And I see an error detail for field "updateType" with content "Error: Select a type of update"
    # case: select: add account
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    # case: Error for no account set:
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And I see an error summary with "Enter account number"
    # case: add trusted account in list, set account
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
      | comment area                     | comment 1  |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Request to update the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
      | account area content   | Account Description GB-100-1002-1-84 comment 1   |
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And The page "contains" the "Pending approval" text
    # try to remove the account that has been added before, but it is is status pending approval so it cannot be removed before "actually" added:
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I select the "Remove account(s)" option
    When I click the "Continue" button
    Then I see an error summary with "There are no trusted accounts eligible for removal"

  @test-case-id-38405342984
  Scenario: As admin user I can remove an account
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
      | trustedAccount           | GB-100-1001-1-89 REJECTED  |
    And I sign in as "junior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then The page "contains" the "GB-100-1002-1-84" text
    # check in trusted account list:
    When I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    # check that I cannot proceed with trusted account removal without specifying the account
    When I click the "Continue" button
    Then I see an error detail for field "updateType" with content "Error: Select a type of update"
    # remove the trusted account from list
    And I select the "Remove account(s)" option
    When I click the "Continue" button
    # try to continue again without checking the checkbox of of the account
    And I click the "Continue" button
    Then I see an error summary with "Select the account(s) from the trusted account list that should be removed"
    # check the account checkbox and continue
    When I click the "first account checkbox" button
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "GB-100-1002-1-84" text
    Then I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    Then The page "contains" the "Remove account(s)" text
    And The page "contains" the "The following account(s) will be deleted from the trusted account list" text
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Remove trusted account" text

  @test-case-id-38405343050
  Scenario: As AR I can access View account screen and navigate to other sections of tal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
      | trustedAccount           | GB-100-1001-1-89 REJECTED  |
    Given I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000002 |
      | ACTIVE | INITIATE_AND_APPROVE | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "History and comments item" link
    Then The page "contains" the "No items added." text

  @test-case-id-6075823060
  Scenario Outline: As AR with incorrect access rights I cannot add or remove TAL request update
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    When I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    And I see the following fields having the values:
      | fieldName | field_value |
    When I click the "Trusted accounts item" link
    Then The page "does not contain" the "Request update" text

    Examples:
      | access_rights |
      | APPROVE       |
      | READ_ONLY     |

  @test-case-id-6075823098
  Scenario Outline: As AR with incorrect access rights I can add an account to TAL
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And The page "contains" the "Add account" text
    When I select the "Add account" option
    And I click the "Continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Example: UK-100-123456-0-12 for UK account, JP-100-123456 for non-UK account" text
    And I enter the following values to the fields:
      | field                            | fieldValue |
      | user defined country code        | GB         |
      | user defined account type        | 100        |
      | user defined account id          | 1002       |
      | user defined account period      | 1          |
      | user defined check digits period | 84         |
    When I click the "continue" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "Request to update the trusted account list" text
    Then I see the following fields having the values:
      | fieldName              | field_value                                      |
      | type of update content | Type of update Change type of update Add account |
      | account area content   | Account Description GB-100-1002-1-84             |
    When I click the "Submit request" button
    Then I am presented with the "Account trusted account list" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    When I click the "Go back to the account" link
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And The page "contains" the "Pending approval" text
    Then The page "contains" the "Request update" text
    # ensure task has been created:
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Add trusted account" text

    Examples:
      | access_rights        |
      | INITIATE             |
      | INITIATE_AND_APPROVE |

  @sampling-smoke @test-case-id-6075823170
  Scenario Outline: As AR with correct access rights I can remove an account from TAL
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal  | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 2000 | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1002      | 1                   |
      | GB-100-1001-1-89 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 4 | OPEN | 101  | RMU   | true       | true      | 1300     | 1400   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1001      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
      | trustedAccount           | GB-100-1001-1-89 REJECTED  |
    When I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | <access_rights> | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    And I click the "Request update" button
    Then I am presented with the "Account trusted account list" screen
    And I see the following fields having the values:
      | fieldName                                  | field_value                                                                            |
      | Request to update the trusted account list | Request to update the trusted account list                                             |
      | Select type of update                      | Request to update the trusted account list Select type of update Select type of update |
    And I select the "Remove account(s)" option
    When I click the "Continue" button
    And I click the "first account checkbox" button
    And I click the "Continue" button
    And The page "contains" the "GB-100-1002-1-84" text
    Then I see the following fields having the values:
      | fieldName        | field_value                                                                     |
      | Check the update | Request to update the trusted account list Check the update request and confirm |
    Then The page "contains" the "The following account(s) will be deleted from the trusted account list" text
    When I click the "Submit request" button
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "The request ID is" text
    # ensure task is created
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And The page "contains" the "Remove trusted account" text

    Examples:
      | access_rights        |
      | INITIATE             |
      | INITIATE_AND_APPROVE |

  @test-case-id-6075823171
  Scenario: Closed account does not appear in tal
    Given the following accounts have been created
      | account_id       | kyoto_account_type    | registry_account_type | account_name    | stat | bal | un_ty | tr_out_tal | ap_sec_ar | ub_start | ub_end | ub_or_cp | ub_ap_cp | ub_activity                     | ub_un_ty | ub_acc_id | acc__commitm_period |
      | GB-100-1002-1-84 | PARTY_HOLDING_ACCOUNT | Null                  | Party Holding 1 | OPEN | 0   | RMU   | true       | true      | 1        | 2000   | CP2      | CP2      | AFFORESTATION_AND_REFORESTATION | RMU      | 1000      | 1                   |
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
      | trustedAccount           | GB-100-1002-1-84 ACTIVE    |
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then The page "contains" the "GB-100-1002-1-84" text
    When I access "1002" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Close account" button
    Then I am presented with the "Account closure" screen
    When I enter value "reason sample" in "comment" field
    And I click the "Continue" button
    Then I am presented with the "Account closure" screen
    When I click the "Submit" button
    Then I am presented with the "Account closure" screen
    And The page "contains" the "Close request has been submitted" text
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
    And The page "contains" the "Close account" text
    And I click the "Request id result 1" button
    Then I am presented with the "Task Details" screen
    When I click the "Approve" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment 1" in "comment area" field
    And I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "Close request has been approved" text
    # ensure account status is closed after account closure task complete
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Trusted accounts item" link
    Then The page "does not contain" the "GB-100-1002-1-84" text
