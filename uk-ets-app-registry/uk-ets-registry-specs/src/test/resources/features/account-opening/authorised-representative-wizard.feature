@functional-area-account-opening

Feature: Account opening - Authorised representative wizard
  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: Request to open a registry account - authorised representative wizard
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  Background:
    # start of prerequisite 1: login
    * I sign in as "senior admin" user
    * I have created an account with the following properties
      | property                 | value                      |
      | accountType              | OPERATOR_HOLDING_ACCOUNT   |
      | holderType               | ORGANISATION               |
      | holderName               | Organisation 1             |
      | primaryContact           | Legal Rep1                 |
      | primaryContact           | Legal Rep2                 |
      | authorisedRepresentative | Authorised Representative1 |
      | authorisedRepresentative | Authorised Representative2 |
      | authorisedRepresentative | Authorised Representative3 |
      | authorisedRepresentative | Authorised Representative4 |
      | authorisedRepresentative | Authorised Representative5 |
      | authorisedRepresentative | Authorised Representative6 |
    * I am "ACTIVE" AR with access right "INITIATE_AND_APPROVE" in the account with ID "100000001"
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
    * I click the "Request account" link
    * I am presented with the "Request to open registry account" screen
    * I click the "Start now" button
    # end of prerequisite 1: login
    # start of prerequisite 2: open account type: "Trading Account"
    * I select the "Trading Account" option
    * I click the "Continue" button
    * I click the "Account holder details" link
    * I select the "Organisation" option
    * I click the "Continue" button
    * I select the "Add a new organisation" option
    * I click the "Continue" button
    * I enter the following values to the fields:
      | field             | fieldValue                      |
      | Organisation name | Organisation maqam al saba name |
    * I select the "Fill Registration number" option
    * I enter the following values to the fields:
      | field                            | fieldValue |
      | Organisation registration number | 12345      |
    * I select the "Fill VAT Registration number" option
    * I enter the following values to the fields:
      | field                                     | fieldValue |
      | VAT registration number with country code | 67890      |
    * I click the "Continue" button
    * I enter the following values to the fields:
      | field             | fieldValue             |
      | Line 1            | line 1 value           |
      | Line 2            | line 2 value           |
      | Line 3            | line 3 value           |
      | Fill Town or city | Smyrna                 |
      | Fill Postcode     | 12345                  |
      | dropdown          | Country United Kingdom |
    * I click the "Continue" button
    * The current page "is" in read-only mode
    * I click the "Continue" button
    # end of prerequisite 2: open account type: "Trading Account"
    # start of prerequisite 3: Add the primary contact
    * I click the "Add the primary contact" link
    * I enter the following values to the fields:
      | field         | fieldValue |
      | First name    | Giannis    |
      | Last name     | Dragatsis  |
      | Also known as | Ogdontakis |
    * I "check" the "I confirm that the primary contact is aged 18 or over" checkbox
    * I click the "Continue" button
    * I enter the following values to the fields:
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
    * I click the "Continue" button
    * I click the "Continue" button
    # end of prerequisite 3: Add the primary contact
    # start of prerequisite 4: Fill account details
    * I click the "Account details" link
    * I enter value "georges lammam" in "Account name" field
    * I "check" the "Billing address is the same as account holder address" checkbox
    * I click the "Continue" button
    * I click the "Continue" button
    # end of prerequisite 4: Fill account details
    # start of prerequisite 5: transaction rules
    * I click the "Choose the transaction rules" link
    * I select the "Yes" option
    * I click the "Continue" button
    * I select the "No" option
    * I click the "Continue" button
    * I click the "Continue" button
  # end of prerequisite 5

  @exec-manual @test-case-id-76128610907
  Scenario: As user I can select an ar from list
    Given I click the "Authorised Representative details" link
    When I click the "Back" link
    When I click the "Authorised Representative details" link
    When I select the "From the list below" option
    And I select the "User registered" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                               |
      | User      | REGISTERED USER (User ID: UK977538690871) |
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                         |
      | Page title | Choose an Authorised Representative |

  @exec-manual @test-case-id-76128610926
  Scenario: As user I cannot submit empty mandatory fields in selection screen
    Given I click the "Authorised Representative details" link
    When I click the "Continue" button
    Then I see an error summary with "Please specify the representative"
    And I see an error detail for field "" with content "Error: Please specify the representative."

  @exec-manual @test-case-id-76128610935
  Scenario: As user I cannot submit empty mandatory fields in access rights screen
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I click the "Continue" button
    Then I see an error summary with "Select the access rights"
    And I see an error detail for field "arAccessRight" with content "Error: Select the access rights."

  @exec-manual @test-case-id-76128610948
  Scenario: As user I can search and select ar
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                         |
      | User      | Authorised Representative1 (User ID: UK88299344979) |

  @exec-manual @test-case-id-76128610961
  Scenario: As user I cannot proceed to next screen if I provide an invalid user ID
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "1234512345123" in "User ID" field
    When I click the "Continue" button
    Then I see an error summary with 'Sorry, there is a problem with the service or your search is invalid. Please try searching again.'

  @test-case-id-40099210871
  Scenario Outline: As user I can apply for an ar using one the from the existing in list
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                         |
      | User      | Authorised Representative1 (User ID: UK88299344979) |
    When I select the "<access_rights_option>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                 |
      | Name                      | Authorised Representative1  |
      | User ID text              | UK88299344979               |
      | Access rights description | <access_rights_description> |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                         |
      | Authorised representatives label | Authorised Representative1 (User ID: UK88299344979) - <access_rights_option> and trusted account list (tal) updates |

    Examples:
      | access_rights_option              | access_rights_description                                                                                                                                                                |
      | Initiate and approve transactions | Initiate and approve transactions and trusted account list (tal) updates This will allow the representative to initiate and approve transactions and changes to the trusted account list |
      | Approve transfers                 | Approve transfers and trusted account list (tal) updates This will allow the representative to approve transactions and changes to the trusted account list                              |
      | Initiate transfers                | Initiate transfers and trusted account list (tal) updates This will allow the representative to initiate transactions and changes to the trusted account list                            |

  @test-case-id-40099210872
  Scenario: As user I can apply for an ar using one the from the existing in list with read only access
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                         |
      | User      | Authorised Representative1 (User ID: UK88299344979) |
    When I select the "Read only" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                |
      | Name                      | Authorised Representative1 |
      | User ID text              | UK88299344979              |
      | Access rights description | Read only                  |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                     |
      | Authorised representatives label | Authorised Representative1 (User ID: UK88299344979) - Read only |

  @exec-manual @test-case-id-76128611004
  Scenario: As user I can use myself as ar and AR dropdown list is disappear after adding myself as AR
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I select myself as authorised representative
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I click the "Add an Authorised Representative" link
    And The dropdown list of authorised representatives is not visible

  @test-case-id-76128611021
  Scenario: As user I can apply for two ar using one from the existing in list
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                 |
      | Name                      | Authorised Representative1                                                                                                                                  |
      | User ID text              | UK88299344979                                                                                                                                               |
      | Access rights description | approve transfers and trusted account list (tal) updates this will allow the representative to approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                    |
      | Authorised representatives label | Authorised Representative1 (User ID: UK88299344979) - Approve transfers and trusted account list (tal) updates |
    # create second authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                   |
      | Name                      | Authorised Representative2                                                                                                                                    |
      | User ID text              | UK689820232063                                                                                                                                                |
      | Access rights description | Initiate transfers and trusted account list (tal) updates This will allow the representative to initiate transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                          | field_value                                                                                                      |
      | Authorised representatives label 1 | Authorised Representative1 (User ID: UK88299344979) - Approve transfers and trusted account list (tal) updates   |
      | Authorised representatives label 2 | Authorised Representative2 (User ID: UK689820232063) - Initiate transfers and trusted account list (tal) updates |

  @test-case-id-76128611065
  Scenario: As user I cannot proceed to next screen if I provide user ID of an existing ar
    # create first authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                 |
      | Name                      | Authorised Representative1                                                                                                                                  |
      | User ID text              | UK88299344979                                                                                                                                               |
      | Access rights description | approve transfers and trusted account list (tal) updates this will allow the representative to approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                    |
      | Authorised representatives label | Authorised Representative1 (User ID: UK88299344979) - Approve transfers and trusted account list (tal) updates |
    # try to create second authorised representative that already exists
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    Then I see an error summary with 'The user is already added as an authorised representative on your request'

  @test-case-id-76128611095
  Scenario: As user I can remove account representative from request
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    When I click the "Continue" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                 |
      | Name                      | Authorised Representative1                                                                                                                                  |
      | User ID text              | UK88299344979                                                                                                                                               |
      | Access rights description | approve transfers and trusted account list (tal) updates this will allow the representative to approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                    |
      | Authorised representatives label | Authorised Representative1 (User ID: UK88299344979) - Approve transfers and trusted account list (tal) updates |
    When I click the "Authorised Representative1" link
    And I click the "Delete" button
    And I am presented with the "Request to open registry account" screen
    Then I see the following fields having the values:
      | fieldName                         | field_value                       |
      | Authorised Representative details | Authorised Representative details |

  @exec-manual @test-case-id-40099211025
  Scenario Outline: As user I can select appropriate access rights according to Business Rules validations
    # create 1st authorised representative
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK977538690871" in "User ID" field
    When I click the "Continue" button
    When I select the "<rights_1>" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create 2nd authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK88299344979" in "User ID" field
    And I click the "Continue" button
    When I select the "<rights_2>" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create 3rd authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK689820232063" in "User ID" field
    And I click the "Continue" button
    When I select the "<rights_3>" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create 4th authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK844883074633" in "User ID" field
    And I click the "Continue" button
    When I select the "<rights_4>" option
    And I click the "Continue" button
    And I click the "Continue" button
    # create 5th authorised representative
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK309464690132" in "User ID" field
    And I click the "Continue" button
    When I select the "<rights_5>" option
    And I click the "Continue" button
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                          | field_value                                                       |
      | Authorised representatives label 1 | REGISTERED USER (User ID: UK977538690871) - <rights_1>            |
      | Authorised representatives label 2 | Authorised Representative1 (User ID: UK88299344979) - <rights_2>  |
      | Authorised representatives label 3 | Authorised Representative2 (User ID: UK689820232063) - <rights_3> |
      | Authorised representatives label 4 | Authorised Representative3 (User ID: UK844883074633) - <rights_4> |
      | Authorised representatives label 5 | Authorised Representative4 (User ID: UK309464690132) - <rights_5> |
    # 6th authorised representative selection options
    And I click the "Add an Authorised Representative" link
    When I select the "By User ID" option
    And I enter value "UK353782343224" in "User ID" field
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName | field_value                                          |
      | User      | Authorised Representative6 (User ID: UK353782343224) |
    Then I see the following fields having the values:
      | fieldName                       | field_value                                          |
      | Access rights available options | <expected_6th_auth_representative_available_options> |

    Examples:
      | rights_1  | rights_2  | rights_3  | rights_4  | rights_5  | expected_6th_auth_representative_available_options                               |
      | Read only | Read only | Read only | Read only | Read only | Initiate and approve transactions Approve transfers Initiate transfers Read only |

    @exec-manual
    Examples:
      | rights_1             | rights_2             | rights_3             | rights_4             | rights_5             | expected_6th_auth_representative_available_options |
      | Approve              | Read only            | Initiate and approve | Approve              | Initiate             | Initiate and approve Approve Initiate Read only    |
      | Approve              | Approve              | Approve              | Approve              | Read only            | Initiate and approve Approve Initiate Read only    |
      | Initiate and approve | Initiate and approve | Initiate and approve | Initiate and approve | Read only            | Initiate and approve Approve Initiate Read only    |
      | Initiate             | Initiate             | Initiate             | Initiate             | Read only            | Initiate and approve Approve Initiate Read only    |
      | Initiate and approve | Initiate and approve | Initiate and approve | Initiate and approve | Initiate and approve | Read only                                          |
      | Approve              | Approve              | Approve              | Approve              | Approve              | Read only                                          |
      | Initiate             | Initiate             | Initiate             | Initiate             | Initiate             | Read only                                          |
      | Initiate             | Approve              | Initiate             | Approve              | Approve              | Read only                                          |
      | Initiate and approve | Approve              | Initiate and approve | Approve              | Approve              | Read only                                          |
      | Initiate             | Initiate and approve | Initiate             | Initiate and approve | Initiate and approve | Read only                                          |

  @test-case-id-76128611223
  Scenario: As user I can change access rights ar before apply
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK309464690132" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                                              |
      | Name                      | Authorised Representative4                                                                                                                                                               |
      | User ID text              | UK309464690132                                                                                                                                                                           |
      | Access rights description | Initiate and approve transactions and trusted account list (tal) updates This will allow the representative to initiate and approve transactions and changes to the trusted account list |
    And I click the "Change Access Rights" link
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                 |
      | Name                      | Authorised Representative4                                                                                                                                  |
      | User ID text              | UK309464690132                                                                                                                                              |
      | Access rights description | approve transfers and trusted account list (tal) updates this will allow the representative to approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                     |
      | Authorised representatives label | Authorised Representative4 (User ID: UK309464690132) - Approve transfers and trusted account list (tal) updates |

  @test-case-id-76128611255
  Scenario: As user I can edit ar after apply
    Given I click the "Authorised Representative details" link
    When I select the "By User ID" option
    And I enter value "UK309464690132" in "User ID" field
    When I click the "Continue" button
    When I select the "Initiate and approve transactions" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                                              |
      | Name                      | Authorised Representative4                                                                                                                                                               |
      | User ID text              | UK309464690132                                                                                                                                                                           |
      | Access rights description | Initiate and approve transactions and trusted account list (tal) updates This will allow the representative to initiate and approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                                     |
      | Authorised representatives label | Authorised Representative4 (User ID: UK309464690132) - Initiate and approve transactions and trusted account list (tal) updates |
    When I click the "Authorised representatives label" link
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                                              |
      | Name                      | Authorised Representative4                                                                                                                                                               |
      | User ID text              | UK309464690132                                                                                                                                                                           |
      | Access rights description | Initiate and approve transactions and trusted account list (tal) updates This will allow the representative to initiate and approve transactions and changes to the trusted account list |
    And I click the "Edit" button
    When I select the "Approve transfers" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                 | field_value                                                                                                                                                 |
      | Name                      | Authorised Representative4                                                                                                                                  |
      | User ID text              | UK309464690132                                                                                                                                              |
      | Access rights description | approve transfers and trusted account list (tal) updates this will allow the representative to approve transactions and changes to the trusted account list |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                        | field_value                                                                                                     |
      | Authorised representatives label | Authorised Representative4 (User ID: UK309464690132) - Approve transfers and trusted account list (tal) updates |
