@functional-area-user-registration
@exec-manual

Feature: User registration - Work details registration

  Epic: User registration and sign-in
  Version: 1.0 (19/09/2019)
  Story: Register personal and work contact details
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20User%20registration%20and%20Sign-in.docx?version=12&amp;modificationDate=1568901007000&amp;api=v2

  Background:
    * I navigate to the "Enter Work Contact Details" screen
    * I accept all cookies

  @test-case-id-931814231116
  Scenario: As user I can successful submit work contact details
    When I enter the mandatory fields
    And I click the "Continue" button
    Then I am presented with the "Choose Password" screen

  @test-case-id-72915630914
  Scenario Outline: As user I can successful submit phone numbers
    And I enter the following values to the fields:
      | field                          | fieldValue       |
      | country-code                   | UK               |
      | phone-number                   | <phone_number>   |
      | workEmailAddress               | test@test.gov.uk |
      | workEmailAddressConfirmation   | test@test.gov.uk |
      | workBuildingAndStreet          | street 1         |
      | workBuildingAndStreetOptional  | street 2         |
      | workBuildingAndStreetOptional2 | street 3         |
      | workPostCode                   | 45556            |
      | workTownOrCity                 | London           |
      | workCountry                    | UK               |
    And I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen

    Examples:
      | phone_number |
      | 07911 125556 |
      | 7911 123456  |
      | 07911 123456 |

  @test-case-id-72915630937
  Scenario Outline: As user I cannot submit invalid phone numbers
    And I enter value "<phone_number>" in "phone-number" field
    And I enter the following values to the fields:
      | field                          | fieldValue       |
      | country-code                   | UK               |
      | phone-number                   | <phone_number>   |
      | workEmailAddress               | test@test.gov.uk |
      | workEmailAddressConfirmation   | test@test.gov.uk |
      | workBuildingAndStreet          | street 1         |
      | workBuildingAndStreetOptional  | street 2         |
      | workBuildingAndStreetOptional2 | street 3         |
      | workPostCode                   | 45556            |
      | workTownOrCity                 | London           |
      | workCountry                    | UK               |
    And I click the "Continue" button
    Then I see an error summary with "Enter a phone number"
    And I see an error detail for field "event-name" with content "Error: Enter a phone number"

    Examples:
      | phone_number |
      | 2100op89989  |
      | 345+8078801  |
      | 7788         |

  @test-case-id-72915630962
  Scenario Outline: As user I cannot submit empty mandatory fields in work details registration
    When I enter the mandatory fields leaving the mandatory field "<fieldName>" empty
    And I click the "Continue" button
    Then I see an error summary with "<error>"
    And I see an error detail for field "event-name" with content "Error: <error>"

    Examples:
      | fieldName                    | error                                |
      | phone-number                 | Please enter your work phone number. |
      | workEmailAddress             | Enter your email address.            |
      | workEmailAddressConfirmation | Retype your email address.           |
      | workBuildingAndStreet        | Enter your address line 1.           |

  @test-case-id-931814231187
  Scenario: As user I must verify my email address by retyping it
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | country-code                 | GR            |
      | phone-number                 | 2102233444    |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testB@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
    When I click the "Continue" button
    Then I see an error summary with "Invalid re-typed email address. The email address and the re-typed email address should match"
    And I see an error detail for field "event-name" with content "Error: Invalid re-typed email address. The email address and the re-typed email address should match."

  @test-case-id-931814231201
  Scenario: As user when I come back from Choose Password screen I see data retained
    And I enter the mandatory fields which are
      | fieldId                      | field_value   |
      | country-code                 | GR            |
      | phone-number                 | 2102233444    |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | GR            |
      | workTownOrCity               | Athens        |
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
    When I click the "Back" link
    Then I am presented with the "Enter Work Contact Details" screen
    And I see the following values in the fields
      | fieldId                      | field_value   |
      | country-code                 | GR (30)       |
      | phone-number                 | 2102233444    |
      | workEmailAddress             | testA@test.gr |
      | workEmailAddressConfirmation | testA@test.gr |
      | workBuildingAndStreet        | Kifisias 43   |
      | workCountry                  | Greece        |
      | workTownOrCity               | Athens        |

  @test-case-id-931814231226
  Scenario: As user I cannot enter an email address longer than 256 characters type 257 characters and ensure that only first 256 are set
    And I enter the mandatory fields which are
      | fieldId                      | field_value                                                                                                                                                                                                                                                       |
      | country-code                 | GR                                                                                                                                                                                                                                                                |
      | phone-number                 | 2102233444                                                                                                                                                                                                                                                        |
      | workBuildingAndStreet        | Kifisias 43                                                                                                                                                                                                                                                       |
      | workCountry                  | GR                                                                                                                                                                                                                                                                |
      | workEmailAddress             | negativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativescenari@negative.comm |
      | workEmailAddressConfirmation | negativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativescenari@negative.comm |
    Then I see the following values in the fields
      | fieldId                      | field_value                                                                                                                                                                                                                                                      |
      | workEmailAddress             | negativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativescenari@negative.com |
      | workEmailAddressConfirmation | negativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativetestcaseemailwith257charactersnegativescenari@negative.com | @test-case-id-931814231241

  Scenario: As user I can enter an email address with large length characters
    And I enter the mandatory fields which are
      | fieldId                      | field_value                                                        |
      | country-code                 | GR                                                                 |
      | phone-number                 | 2102233444                                                         |
      | workBuildingAndStreet        | Kifisias 43                                                        |
      | workTownOrCity               | Smyrna                                                             |
      | workCountry                  | GR                                                                 |
      | workEmailAddress             | mdtrhfdyhytemililtemdtrhfdyhytemililtemdtrhfdyhytemil@emailsdf.com |
      | workEmailAddressConfirmation | mdtrhfdyhytemililtemdtrhfdyhytemililtemdtrhfdyhytemil@emailsdf.com |
    Then I see the following values in the fields
      | fieldId                      | field_value                                                        |
      | workEmailAddress             | mdtrhfdyhytemililtemdtrhfdyhytemililtemdtrhfdyhytemil@emailsdf.com |
      | workEmailAddressConfirmation | mdtrhfdyhytemililtemdtrhfdyhytemililtemdtrhfdyhytemil@emailsdf.com |
    When I click the "Continue" button
    Then I am presented with the "Memorable phrase" screen
