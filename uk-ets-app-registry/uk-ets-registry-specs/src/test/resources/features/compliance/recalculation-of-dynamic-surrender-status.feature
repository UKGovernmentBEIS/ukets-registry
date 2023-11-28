@functional-area-compliance

Feature: Compliance - Recalculation of dynamic surrender status

  @test-case-id-23458907101 @exec-manual
  Scenario Outline: As sra I can trigger the Recalculate dynamic surrender status process
    Given I have created an account with the following properties
      | property    | value         |
      | accountType | <accountType> |
    And I sign in as "senior admin" user
    And I am presented with the "Registry dashboard" screen
    # check dynamic surrender status before recalculation
    When I click the "Emissions and Surrenders" link
    Then I see the following fields having the values:
      | fieldName                | field_value |
      | Current surrender status | A           |
    # recalculate
    When I click the "ETS Administration" link
    Then I am presented with the "ETS Administration Propose to issue UK allowances" screen
    When I click the "Recalculate dynamic surrender status" link
    And I click the "Recalculate" button
    And I click the "Recalculate" button
    Then The page "contains" the "You have successfully submitted a recalculation request of the dynamic surrender status of Operator Holding Accounts and Aircraft Operator Holding Accounts" text
    # check dynamic surrender status after recalculation
    When I click the "Emissions and Surrenders" link
    Then I see the following fields having the values:
      | fieldName                | field_value |
      | Current surrender status | B           |

    Examples:
      | accountType                       |
      | OPERATOR_HOLDING_ACCOUNT          |
      | AIRCRAFT_OPERATOR_HOLDING_ACCOUNT |