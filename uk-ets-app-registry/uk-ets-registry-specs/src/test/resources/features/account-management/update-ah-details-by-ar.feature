@functional-area-account-management

Feature: Account management - Update account holder details by AR

  Epic: Account Management
  Version: v3.1 (03/09/2020)
  Story: 5.3.6	Request to update the account holder details

  # Note: accountHolderRepresentative(s) are needed in order to depict primary contact details in Account > Account holder screen.

  @test-case-id-2327964361
  Scenario: As AR I cannot request TRANSFER PENDING account updates as I cannot even see account in Account search results
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountStatus               | TRANSFER_PENDING               |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | holderType                  | ORGANISATION                   |
      | holderName                  | Organisation 1                 |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "Accounts" link
    Then I am presented with the "Account list" screen
    When I click the "Show filters" button
    And I enter value "UK-100-50001-2-11" in "Account name or ID" field
    And I click the "Search" button
    Then The page "does not contain" the "UK-100-50001-2-11" text

  @exec-manual @test-case-id-5992724339
  Scenario Outline: As an AR I can navigate backwards ensuring that data is retained
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | <account_status>               |
      | holderType                  | <holderType>                   |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    When I click the "Request update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the <update_type> details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the fields having the correct values
    When I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the fields having the correct values
    When I click the "Back" button
    Then I am presented with the "Account holder details update" screen
    And I see the fields having the correct values
    When I click the "Back" button
    Then I am presented with the "Account holder details update" screen
    And I see the correct selected radio button "selected"

    Examples:
      | account_status              | holderType   | update_type     |
      | OPEN                        | ORGANISATION | account holder  |
      | OPEN                        | INDIVIDUAL   | account holder  |
      | OPEN                        | ORGANISATION | primary contact |
      | OPEN                        | INDIVIDUAL   | primary contact |
      | ALL_TRANSACTIONS_RESTRICTED | ORGANISATION | primary contact |

  @test-case-id-5992724389
  Scenario Outline: As an AR I can cancel update process
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | <account_status>               |
      | holderType                  | <holderType>                   |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the <update_type> details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I click the "Cancel" button
    Then I am presented with the "Account holder details update" screen
    When I click the "Cancel request" button
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    # ensure Request update, organisation/person details, Address and contact details, primary and alternative contacts are visible as previously
    Then I see the following fields having the values:
      | fieldName         | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
      | Page main content | Account Holder <account_holder_previous_page_content> Identification documentation No items added. Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update |

    Examples:
      | account_status              | holderType   | update_type     | account_holder_previous_page_content                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | OPEN                        | ORGANISATION | account holder  | Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom                                                                                                                                                                                                     |
      | OPEN                        | INDIVIDUAL   | account holder  | Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address dont@care.com |
      | OPEN                        | ORGANISATION | primary contact | Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom                                                                                                                                                                                                     |
      | OPEN                        | INDIVIDUAL   | primary contact | Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address dont@care.com |
      | ALL_TRANSACTIONS_RESTRICTED | ORGANISATION | primary contact | Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom                                                                                                                                                                                                     |

  @test-case-id-2327964486 @exec-manual
  Scenario: i cannot submit an update account details request when no change is applied
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    # try to submit without any change
    When I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the account holder details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I click the "I confirm that the account holder is aged 18 or over" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see an error summary with "You can not make a request without any changes"

  @sampling-smoke @test-case-id-2327964527
  Scenario: As AR I can request an update ah details on OHA where AH is organisation
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the account holder details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I clear the "Name" field
    And I enter value "holderName 2" in "Name" field
    And I click the "I do not have a registration number Company registration number" button
    And I enter value "Company reason 1" in "I do not have a Company registration number reason" field
    And I click the "I do not have a VAT registration number" button
    And I enter value "VAT reason 1" in "I do not have a VAT registration number reason" field
    And I click the "Continue" button
    # fields update: organisation - account holder - page 2:
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I clear the "Address line 2 (optional)" field
    When I clear the "Address line 3 (optional)" field
    And I clear the "Town or city" field
    When I clear the "Postcode" field
    And I enter value "Test address 8" in "Address line 1" field
    And I enter value "Second address line 2" in "Address line 2 (optional)" field
    And I enter value "Third address line 2" in "Address line 3 (optional)" field
    And I enter value "Barcelona" in "Town or city" field
    And I enter value "67890" in "Postcode" field
    And I click the "Continue" button
    # fields update: organisation - account holder - page 3 Change Organisation details:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Organisation details" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "I do not have a Company registration number reason" field
    And I enter value "Company reason 2" in "I do not have a Company registration number reason" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I click the "Continue" button
    # fields update: organisation - account holder - page 3 Change Organisation address:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Organisation address" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I enter value "Test address 9" in "Address line 1" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the following fields having the values:
      | fieldName                    | field_value                                                                                                                                                                                                                                                                                                                                                                                       |
      | type of update content       | Type of update Update the account holder details                                                                                                                                                                                                                                                                                                                                                  |
      | organisation details content | field not updated field current value updated field changed value name name holdername 1 updated name holdername 2 registration number registration number uk1234567890 updated registration number no registration number updated company reason 2 vat registration number vat registration number 123-456-789-0 updated vat registration number no vat registration number updated vat reason 1 |
      | organisation address content | field not updated field current value updated field changed value address address test address 7 updated address test address 9 second address line updated second address line 2 third address line updated third address line 2 town or city town or city london updated town or city barcelona country country uk updated country postcode postcode 12345 updated postcode 67890               |      
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "An update request has been submitted." text
    # ensure that the task has been created
    When I click the "Sign out" button
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Update the account holder details" text

  @sampling-smoke @test-case-id-2327964615
  Scenario: As AR I can request an update of ah details on OHA where AH is individual
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | OPEN                           |
      | holderType                  | INDIVIDUAL                     |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the account holder details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    # fields update: individual - account holder - page 1:
    And I clear the "First and middle names" field
    And I clear the "Last name" field
    And I enter value "holderName 2" in "First and middle names" field
    And I enter value "holderName 1" in "Last name" field
    And I "check" the "I confirm that the account holder is aged 18 or over" checkbox
    And I click the "Continue" button
    # fields update: individual - account holder - page 2:
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I clear the "Address line 2 (optional)" field
    When I clear the "Address line 3 (optional)" field
    And I clear the "Town or city" field
    When I clear the "Postcode" field
    And I clear the "Email address" field
    When I clear the "Confirm their email address" field
    And I enter value "Test address 8" in "Address line 1" field
    And I enter value "Second address line 2" in "Address line 2 (optional)" field
    And I enter value "Third address line 2" in "Address line 3 (optional)" field
    And I enter value "Barcelona" in "Town or city" field
    And I enter value "67890" in "Postcode" field
    And I enter value "email2@email2.com" in "Email address" field
    And I enter value "email2@email2.com" in "Confirm their email address" field
    And I click the "Continue" button
    # fields update: individual - account holder - page 3 Change Individual details:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Individual details" button
    Then I am presented with the "Account holder details update" screen
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I enter value "Test address 9" in "Address line 1" field
    And I click the "Continue" button
    # fields update: individual - account holder - page 3 Change Individual contact details:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Individual contact details" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 2 (optional)" field
    And I enter value "Second address line 3" in "Address line 2 (optional)" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
      | type of update content             | Type of update Update the account holder details                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | Individual details content         | field not updated field current value updated field changed value full name full name individual first name 1 individual last name 1 updated full name holdername 2 holdername 1 country of birth country of birth uk updated country of birth i confirm that the account holder is aged 18 or over i confirm that the account holder is aged 18 or over yes updated i confirm that the account holder is aged 18 or over                                                                                                                                                                             |
      | Individual contact details content | field not updated field current value updated field changed value address address test address 7 updated address test address 9 second address line updated second address line 3 third address line updated third address line 2 town or city town or city london updated town or city barcelona country country uk updated country postcode postcode 12345 updated postcode 67890 phone number 1 phone number 1 1434634996 updated phone number 1 phone number 2 phone number 2 1434634997 updated phone number 2 email address email address dont@care.com updated email address email2@email2.com |
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "Go back to the account" text
    # ensure that the task has been created
    When I click the "Sign out" button
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    When I click the "Show filters" button
    And I click the "Search" button
    Then I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Update the account holder details" text

  @sampling-smoke @test-case-id-2327964714
  Scenario: As AR I can request an update of primary contact details on OHA where AH is organisation
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
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the primary contact details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    # fields update: organisation - account holder - page 1:
    And I clear the "First and middle names" field
    And I clear the "Last name" field
    And I clear the "Also known as (optional)" field
    And I enter value "AccountHolder 2" in "First and middle names" field
    And I enter value "second last name" in "Last name" field
    And I enter value "AKA 2" in "Also known as (optional)" field
    # uncheck the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "Continue" button
    # Then I see an error summary with "Confirm that the alternative primary contact is aged 18 or over"
    # check the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "I confirm that the alternative primary contact is aged 18 or over" button
    And I click the "Continue" button
    # fields update: organisation - primary contact - page 2:
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I clear the "company position" field
    And I enter value "company position 2" in "company position" field
    And I enter value "Test address 8" in "Address line 1" field
    And I click the "Continue" button
    # fields update: organisation - primary contact - page 3 Change Organisation details:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Primary contact details" button
    Then I am presented with the "Account holder details update" screen
    And I clear the "First and middle names" field
    And I enter value "AccountHolder 3" in "First and middle names" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "company position" field
    And I enter value "company position 3" in "company position" field
    And I click the "Continue" button
    # fields update: organisation - primary contact - page 3 Change Organisation address:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Primary contact work details" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 2 (optional)" field
    And I enter value "Second address line 4" in "Address line 2 (optional)" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the following fields having the values:
      | fieldName                            | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | type of update content               | Type of update Update the primary contact details                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | Primary contact details content      | field not updated field current value updated field changed value first and middle names first and middle names accountholder updated first and middle names accountholder 3 last name last name rep1 updated last name second last name also known as also known as aka updated also known as aka 2 i confirm that the primary contact is aged 18 or over i confirm that the primary contact is aged 18 or over yes updated i confirm that the primary contact is aged 18 or over                                                                                                                                                           |
      | Primary contact work details content | field not updated field current value updated field changed value company position company position updated company position company position 3 address address test address 7 updated address test address 8 second address line updated second address line 4 third address line updated town or city town or city london updated town or city country country uk updated country postcode postcode 12345 updated postcode phone number 1 phone number 1 uk (44) 1434634996 updated phone number 1 phone number 2 phone number 2 uk (44) 1434634997 updated phone number 2 email address email address dont@care.com updated email address |
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "Go back to the account" text
    # ensure that the task has been created
    When I click the "Sign out" button
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Update the primary contact details" text

  @sampling-smoke @test-case-id-2327964806
  Scenario: As AR I can request an update of primary contact details on OHA where AH is individual
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | OPEN                           |
      | holderType                  | INDIVIDUAL                     |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Request Update" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "Select type of update" text
    When I click the "Update the primary contact details" button
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    # fields update: individual - account holder - page 1:
    And I clear the "First and middle names" field
    And I clear the "Last name" field
    And I clear the "Also known as (optional)" field
    And I enter value "AccountHolder 2" in "First and middle names" field
    And I enter value "second last name" in "Last name" field
    And I enter value "AKA 2" in "Also known as (optional)" field
    # uncheck the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "Continue" button
    # Then I see an error summary with "Confirm that the alternative primary contact is aged 18 or over"
    # check the "I confirm that the alternative primary contact is aged 18 or over" button
    # And I click the "I confirm that the alternative primary contact is aged 18 or over" button
    And I click the "Continue" button
    # fields update: individual - primary contact - page 2:
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 1" field
    And I clear the "company position" field
    And I enter value "company position 2" in "company position" field
    And I enter value "Test address 8" in "Address line 1" field
    And I click the "Continue" button
    # fields update: individual - primary contact - page 3 change individual details:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Primary contact details" button
    Then I am presented with the "Account holder details update" screen
    And I clear the "First and middle names" field
    And I enter value "AccountHolder 3" in "First and middle names" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "company position" field
    And I enter value "company position 3" in "company position" field
    And I click the "Continue" button
    # fields update: individual - primary contact - page 3 change individual address:
    Then I am presented with the "Account holder details update" screen
    And I click the "Change Primary contact work details" button
    Then I am presented with the "Account holder details update" screen
    When I clear the "Address line 2 (optional)" field
    And I enter value "Second address line 4" in "Address line 2 (optional)" field
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And I see the following fields having the values:
      | fieldName                            | field_value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
      | type of update content               | Type of update Update the primary contact details                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | Primary contact details content      | field not updated field current value updated field changed value first and middle names first and middle names accountholder updated first and middle names accountholder 3 last name last name rep1 updated last name second last name also known as also known as aka updated also known as aka 2 i confirm that the primary contact is aged 18 or over i confirm that the primary contact is aged 18 or over yes updated i confirm that the primary contact is aged 18 or over                                                                                                                                                           |
      | Primary contact work details content | field not updated field current value updated field changed value company position company position updated company position company position 3 address address test address 7 updated address test address 8 second address line updated second address line 4 third address line updated town or city town or city london updated town or city country country uk updated country postcode postcode 12345 updated postcode phone number 1 phone number 1 uk (44) 1434634996 updated phone number 1 phone number 2 phone number 2 uk (44) 1434634997 updated phone number 2 email address email address dont@care.com updated email address |
    And I click the "Continue" button
    Then I am presented with the "Account holder details update" screen
    And The page "contains" the "An update request has been submitted." text
    And The page "contains" the "Go back to the account" text
    # ensure that the task has been created
    When I click the "Sign out" button
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I click the "Show filters" button
    And I click the "Search" button
    And I see "1" elements of "Task list returned result rows"
    And The page "contains" the "Update the primary contact details" text

  @sampling-smoke @test-case-id-5992724852
  Scenario Outline: ah request data update is correctly implemented when and only when task is approved for individual holder type
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | OPEN                           |
      | holderType                  | INDIVIDUAL                     |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "senior admin" user
    # authorised representative should initiate the task in this case
    And I have created the following tasks
      | account_id | claimed_by | status                     | outcome | type               | initiated_by | completed_by | diff                          | before |
      | 100000001  |            | SUBMITTED_NOT_YET_APPROVED |         | <task_update_type> | 100000001    |              | <task_update_type>:INDIVIDUAL |        |
    # data in account holder details before task handling
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<task_status>" text
    # ensure that account holder details data is correct after task handling
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                   |
      | Page main content | <account_details_content_after_task_handling> |

    Examples:
      | task_update_type                       | task_action | task_status | account_details_content_after_task_handling                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
      | ACCOUNT_HOLDER_UPDATE_DETAILS          | Approve     | Approved    | Account Holder Account Holder ID 100001 Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 9 Second address line 3 Third address line 2 Town or city Barcelona Postcode 67890 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address email2@email2.com Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer                                         |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS | Approve     | Approved    | Account Holder Account Holder ID 100001 Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address dont@care.com Identification documentation No items added. Request documents Primary contact AccountHolder 3 second last name Primary contact details First and middle names AccountHolder 3 Last name second last name Also known as AKA 2 I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position company position 3 Work address Test address 8 Second address line 4 Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer |
      | ACCOUNT_HOLDER_UPDATE_DETAILS          | Reject      | Rejected    | Account Holder Account Holder ID 100001 Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address dont@care.com Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer                                                    |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS | Reject      | Rejected    | Account Holder Account Holder ID 100001 Individual's details First and middle names individual first name 1 Last name individual last name 1 Country of birth United Kingdom I confirm that the account holder is aged 18 or over Yes Individual's contact details Residential address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Phone number 1 +UK (44)1434634996 Phone number 2 +UK (44)1434634997 Email address dont@care.com Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer                                                    |

  @sampling-smoke @test-case-id-5992724910
  Scenario Outline: ah request data update is not available for registered user
    Given I have created an account with the following properties
      | property                    | value                          |
      | accountType                 | OPERATOR_HOLDING_ACCOUNT       |
      | accountHolderRepresentative | AccountHolder Rep1 PRIMARY     |
      | accountHolderRepresentative | AccountHolder Rep2 ALTERNATIVE |
      | accountStatus               | OPEN                           |
      | holderType                  | INDIVIDUAL                     |
      | holderName                  | holderName 1                   |
      | legalRepresentative         | Legal Rep1                     |
      | legalRepresentative         | Legal Rep2                     |
      | authorisedRepresentative    | Authorised Representative6     |
    And I sign in as "authorized representative" user
    # authorised representative should initiate the task in this case
    And I have created the following tasks
      | account_id | claimed_by | status                     | outcome | type               | initiated_by | completed_by | diff                          | before |
      | 100000001  |            | SUBMITTED_NOT_YET_APPROVED |         | <task_update_type> | 100000001    |              | <task_update_type>:INDIVIDUAL |        |
    # data in account holder details before task handling
    Then I am presented with the "Registry dashboard" screen
    When I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I see "0" elements of "Task list returned result rows"

    Examples:
      | task_update_type                       |
      | ACCOUNT_HOLDER_UPDATE_DETAILS          |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS |

  @sampling-smoke @test-case-id-5992724940
  Scenario Outline: ah request data update is correctly implemented when and only when task is approved for organisation holder type
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
    And I sign in as "senior admin" user
    # authorised representative should initiate the task in this case
    And I have created the following tasks
      | account_id | claimed_by | status                     | outcome | type               | initiated_by | completed_by | diff                            | before |
      | 100000001  |            | SUBMITTED_NOT_YET_APPROVED |         | <task_update_type> | 100000001    |              | <task_update_type>:ORGANISATION |        |
    # data in account holder details before task handling
    Then I am presented with the "Registry dashboard" screen
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    And I click the "Tasks" link
    Then I am presented with the "Task List" screen
    And I "check" the "Select All Result Rows" checkbox
    And I click the "Upper Claim" button
    Then I am presented with the "Task List Bulk Claim" screen
    And I enter value "claim comment" in "Enter some comments" field
    When I click the "Claim task" button
    Then I am presented with the "Task List" screen
    When I access "100000001" in "task-details"
    Then I am presented with the "Task Details" screen
    And I click the "<task_action>" button
    Then I am presented with the "Task Details" screen
    And I enter value "comment v1" in "comment area" field
    When I click the "Complete task" button
    Then I am presented with the "Task Details" screen
    And The page "contains" the "<task_status>" text
    # ensure that account holder details data is correct after task handling
    When I access "50001" in "account-details"
    Then I am presented with the "View account" screen
    When I click the "Account holder item" link
    Then I see the following fields having the values:
      | fieldName         | field_value                                   |
      | Page main content | <account_details_content_after_task_handling> |

    Examples:
      | task_update_type                       | task_action | task_status | account_details_content_after_task_handling                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
      | ACCOUNT_HOLDER_UPDATE_DETAILS          | Approve     | Approved    | Account Holder Account Holder ID 100001 Organisation details Name holderName 2 Reason for not providing a Registration number Company reason 2 Reason for not providing a VAT registration number VAT reason 1 Organisation address Address Test address 9 Second address line 2 Third address line 2 Town or city Barcelona Postcode 67890 Country United Kingdom Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS | Approve     | Approved    | Account Holder Account Holder ID 100001 Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Primary contact AccountHolder 3 second last name Primary contact details First and middle names AccountHolder 3 Last name second last name Also known as AKA 2 I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position company position 3 Work address Test address 8 Second address line 4 Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer              |
      | ACCOUNT_HOLDER_UPDATE_DETAILS          | Reject      | Rejected    | Account Holder Account Holder ID 100001 Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer                                                                 |
      | ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS | Reject      | Rejected    | Account Holder Account Holder ID 100001 Organisation details Name holderName 1 Registration number UK1234567890 VAT registration number 123-456-789-0 Organisation address Address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Identification documentation No items added. Request documents Primary contact AccountHolder Rep1 Primary contact details First and middle names AccountHolder Last name Rep1 Also known as AKA I confirm that the primary contact is aged 18 or over Yes Primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Alternative primary contact AccountHolder Rep2 Alternative primary contact details First and middle names AccountHolder Last name Rep2 Also known as AKA I confirm that the alternative primary contact is aged 18 or over Yes Alternative primary contact work details Company position Work address Test address 7 Second address line Third address line Town or city London Postcode 12345 Country United Kingdom Work phone number 1 +UK (44)1434634996 Work phone number 2 +UK (44)1434634997 Email address dont@care.com Request Update Request Account Transfer                                                                 |
