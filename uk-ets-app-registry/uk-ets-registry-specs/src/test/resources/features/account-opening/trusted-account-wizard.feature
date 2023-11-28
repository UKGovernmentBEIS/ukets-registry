@functional-area-account-opening

Feature: Account opening - Trusted account wizard

  Epic: Account Opening
  Version: 2.8 (12/03/2020)
  Story: (& 6.1.1.5) as user I can configure the transaction rules to my request for a new account
  URL: https://pmo.trasys.be/confluence/download/attachments/124686949/UK%20Registry%20-%20Account%20opening.docx?version=11&modificationDate=1575643746000&api=v2

  # Screens:
  # "Request a new account overview"		  Is the 6.1.1.1 at pg 21 Screen  3: Request to open a registry account
  # "Configure the transaction rules"	      Is the 6.1.1.5 at pg 40 Screen  1: Configure the transaction rules
  # "Configure the transaction rules"	      Is the 6.1.1.5 at pg 40 Screen  2: Configure the transaction rules
  # "Configure the transaction rules view"    Is the 6.1.1.5 at pg 41 Screen  3: Configure the transaction rules - view

  Background:
    # start of prerequisite 1: login
    * I sign in as "registered" user
    * I am presented with the "Registry dashboard" screen
    * I click the "Accounts" link
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
      | field             | fieldValue |
      | Organisation name | Name 1     |
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
      | field             | fieldValue     |
      | Line 1            | Line 1 value   |
      | Fill Town or city | Smyrna         |
      | dropdown          | Country Greece |
    * I click the "Continue" button
    * I click the "Continue" button
    # end of prerequisite 2: open account type: "Trading Account"
    # start of prerequisite 3: create a primary contact
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
    # end of prerequisite 3: create a primary contact
    # start of prerequisite 4: Fill account details
    * I click the "Account details" link
    * I enter value "georges lammam" in "Account name" field
    * I "check" the "Billing address is the same as account holder address" checkbox
    * I click the "Continue" button
    * I click the "Continue" button
  # end of prerequisite 4

  @test-case-id-96330816104
  Scenario Outline: As user when I configured the trust account list and clicked on the APPLY button the settings are reflected in table of contents
    And I click the "Choose the transaction rules" link
    When I select the "<selection_1>" option
    And I click the "Continue" button
    When I select the "<selection_2>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value   |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | <selection_1> |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | <selection_2> |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value |
      | Approval of second authorised representative label | <label>     |
    And The page "does not contain" the "Apply" text

    Examples:
      | selection_1 | selection_2 | label                                                                                                                                                                                      |
      | Yes         | No          | A second authorised representative must approve transfers of units to a trusted account; Transfers of units cannot be made to accounts that are not on the trusted account list            |
      | No          | Yes         | Transfers of units to a trusted account do not need approval by a second authorised representative; Transfers of units can be made to accounts that are not on the trusted account list    |
      | Yes         | Yes         | A second authorised representative must approve transfers of units to a trusted account; Transfers of units can be made to accounts that are not on the trusted account list               |
      | No          | No          | Transfers of units to a trusted account do not need approval by a second authorised representative; Transfers of units cannot be made to accounts that are not on the trusted account list |

  @test-case-id-43736116252
  Scenario: As user I can change the transaction rules before apply changes in second ar approval to accounts
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Do you want a second authorised representative to approve transfers of units to a trusted account change" link
    When I select the "No" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | No          |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | Yes         |

  @test-case-id-43736116275
  Scenario: As user I can change the transaction rules before apply changes in the transfers to accounts not on transaction rules allowed
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I click the "Do you want to allow transfers of units to accounts that are not on the trusted list change" link
    When I select the "Yes" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | Yes         |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | Yes         |

  @test-case-id-43736116295
  Scenario: As user I can edit transaction rules after apply
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | Yes         |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | Yes         |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                  |
      | Approval of second authorised representative label | A second authorised representative must approve transfers of units to a trusted account; Transfers of units can be made to accounts that are not on the trusted account list |
    And I click the "Approval of second authorised representative label" link
    And I click the "Edit" button
    When I select the "No" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | No          |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | No          |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                                |
      | Approval of second authorised representative label | Transfers of units to a trusted account do not need approval by a second authorised representative; Transfers of units cannot be made to accounts that are not on the trusted account list |

  @test-case-id-43736116334
  Scenario: As user I can delete transaction rules after apply
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "Yes" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | Yes         |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | Yes         |
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                          | field_value                                                                                                                                                                  |
      | Approval of second authorised representative label | A second authorised representative must approve transfers of units to a trusted account; Transfers of units can be made to accounts that are not on the trusted account list |
    And I click the "Approval of second authorised representative label" link
    And I click the "Delete" button
    And I see the following fields having the values:
      | fieldName                    | field_value                  |
      | Choose the transaction rules | Choose the transaction rules |

  @exec-manual @test-case-id-43736116362
  Scenario: As user I can go back to previous screen transaction rules 1
    When I click the "Choose the transaction rules" link
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName                    | field_value                  |
      | Choose the transaction rules | Choose the transaction rules |

  @exec-manual @test-case-id-96330816250
  Scenario Outline: As user I can continue to next screen Configure transaction rules transfers outside list
    When I click the "Choose the transaction rules" link
    When I select the "<selection_option>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Choose the transaction rules |

    Examples:
      | selection_option |
      | Yes              |
      | No               |

  @exec-manual @test-case-id-96330816264
  Scenario Outline: As user I can go back to previous screen transaction rules 2
    When I click the "Choose the transaction rules" link
    When I select the "<selection_option>" option
    And I click the "Continue" button
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                  |
      | Page title | Choose the transaction rules |

    Examples:
      | selection_option |
      | Yes              |
      | No               |

  @exec-manual @test-case-id-96330816280
  Scenario Outline: As user I can continue to next screen Configure transaction rules overview
    When I click the "Choose the transaction rules" link
    When I select the "<second_approval_necessary_option>" option
    And I click the "Continue" button
    When I select the "<transfers_outside_list_option>" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName  | field_value                              |
      | Page title | Check the transaction rules you have set |

    Examples:
      | second_approval_necessary_option | transfers_outside_list_option |
      | Yes                              | Yes                           |
      | No                               | No                            |
      | Yes                              | No                            |
      | No                               | Yes                           |

  @exec-manual @test-case-id-96330816299
  Scenario Outline: As user I can go back to previous screen
    When I click the "Choose the transaction rules" link
    When I select the "<second_approval_necessary_option>" option
    And I click the "Continue" button
    When I select the "<transfers_outside_list_option>" option
    And I click the "Continue" button
    When I click the "Back" link
    And I see the following fields having the values:
      | fieldName  | field_value                                                                                   |
      | Page title | Do you want to allow transfers of units to accounts that are not on the trusted account list? |

    Examples:
      | second_approval_necessary_option | transfers_outside_list_option |
      | Yes                              | Yes                           |
      | No                               | No                            |
      | Yes                              | No                            |
      | No                               | Yes                           |

  @exec-manual @test-case-id-43736116444
  Scenario: As user filling screens and then coming back I see my filling data preserved in second approval necessary screen
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I click the "Back" link
    Then I see data retained

  @exec-manual @test-case-id-43736116459
  Scenario: As user filling screens and then coming back I see my fill in data preserved in transfers outside list screen
    When I click the "Choose the transaction rules" link
    When I select the "Yes" option
    And I click the "Continue" button
    When I select the "No" option
    And I click the "Continue" button
    And I see the following fields having the values:
      | fieldName                                                                                         | field_value |
      | Do you want a second authorised representative to approve transfers of units to a trusted account | Yes         |
      | Do you want to allow transfers of units to accounts that are not on the trusted list              | No          |
    When I click the "Back" link
    Then I see data retained
