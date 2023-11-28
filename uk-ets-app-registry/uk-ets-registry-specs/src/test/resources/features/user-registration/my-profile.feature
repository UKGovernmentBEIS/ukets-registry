@functional-area-user-registration

Feature: User registration - My Profile

  Epic: User validation, enrolment and management
  Version: 2.2 (10/09/2020)
  Story: (4.2.2) as user I want to view my own user details
  URL: https://pmo.trasys.be/confluence/pages/viewpage.action?pageId=124686949

  @test-case-id-077089329879
  Scenario: As validated user I can view my own user details
    # create three accounts
    Given I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 0                        |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 1           |
      | legalRepresentative | Legal Rep1               |
      | legalRepresentative | Legal Rep2               |
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 1                        |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 2           |
      | legalRepresentative | Legal Rep3               |
      | legalRepresentative | Legal Rep4               |
    And I have created an account with the following properties
      | property            | value                    |
      | accountType         | OPERATOR_HOLDING_ACCOUNT |
      | accountIndex        | 2                        |
      | holderType          | ORGANISATION             |
      | holderName          | Organisation 3           |
      | legalRepresentative | Legal Rep5               |
      | legalRepresentative | Legal Rep6               |
    # sign in with specific access rights to the accounts created above
    When I sign in as "validated" user with the following status and access rights to accounts:
      | ACTIVE    | INITIATE_AND_APPROVE | 100000001 |
      | ACTIVE    | INITIATE             | 100000002 |
      | SUSPENDED | APPROVE              | 100000003 |
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    And The page "contains" the "VALIDATED USER" text
    Then I see the following fields having the values:
      | fieldName                                     | field_value                                                                                                                                                                                                                                                                                                                    |
      | Last signed in value                          | [not empty nor null value]                                                                                                                                                                                                                                                                                                     |
      | Authorised Representative in accounts content | Authorised Representative in accounts Account Account Holder Access rights Status Operator holding account 50001 Organisation 1 Initiate and approve transactions and trusted account list (tal) updates ACTIVE Operator holding account 50002 Organisation 2 Initiate transfers and trusted account list (tal) updates ACTIVE |
      | Identification documentation content          | Identification documentation No items added.                                                                                                                                                                                                                                                                                   |
      | Registration details content                  | Registration details User ID UK977538690871 Status VALIDATED Email address test_validated_user@test.com Change your email address Password Change your password Two factor authentication Change two factor authentication                                                                                                     |
      | Personal details content                      | Personal details First and middle names VALIDATED Last name USER Known as Home address Test Address 7 Second address line Third address line Town or city London Postcode 12345 Country UK Date of birth Country of birth UK                                                                                                   |
      | Work contact details content                  | Work contact details Work contact (default) Phone number 1 +UK (44)1434634996 Email address dont@care.com Address Test work address 7 Second work address line Third work address line                                                                                                                                         |

  @test-case-id-077089329925 @exec-manual
  Scenario: As validated user I can view my own user details regarding already existing documents
    Given I sign in as "validated" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I already have uploaded the "Document1.pdf" file
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure that the document uploaded is visible
    Then I see the following fields having the values:
      | fieldName                    | field_value                                          |
      | Identification documentation | File Document1.pdf Type Document type 01 Uploaded on |

  @exec-manual @test-case-id-077089329939
  Scenario: As validated user I can access account page by using view my own user details screen
    Given I sign in as "validated" user with the following status and access rights to accounts:
      | ACTIVE | INITIATE_AND_APPROVE | 100000001 |
    And I already have uploaded the "Document1.pdf" file
    Then I am presented with the "Registry dashboard" screen
    When I click the "My Profile" link
    Then I am presented with the "My Profile" screen
    # ensure that the account link click redirects to the correct page
    When I click the "Account 1" link in "Authorised Representatives" area
    Then I am presented with the "Account Overview" screen
