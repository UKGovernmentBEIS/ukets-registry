@functional-area-kp-transactions

Feature: KP transactions - View ITL notifications and View and Send ITL messages

  Epic: ITL notifications and messages (v1.1 (23/10/2020))
  Story: &4.1.6 View ITL messages, &4.1.7 Send message to ITL

  @exec-manual @test-case-id-89850721126
  Scenario: As a Senior Admin I can see a home page alert when there is at least one itl notification
    # create a "Type 1 - Net source cancellation" itl notification
    Given I have the following itl notifications
      | notificationId | notificationType | notificationStatus |
      | 101            | 1                | 1                  |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I "see" an alert stating "There are ITL notification(s) that need your attention."
    When I clear the ITL notification(s)
    Then I "do not see" an alert stating "There are ITL notification(s) that need your attention."

  @test-case-id-35942821007 @defect-UKETS-6668
  Scenario Outline: As a sra I can navigate forth to and back from an itl notification
    Given I have the following itl notifications
      | notificationId   | notificationType       | notificationStatus       |
      | <notificationId> | <notificationTypeEnum> | <notificationStatusEnum> |
    When I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    When I click the "ITL Notifications" link
    Then I am presented with the "KP Administration ITL Notifications" screen
    And I see the following fields having the values:
      | fieldName              | field_value            |
      | first type result name | <notificationTypeName> |
    When I click the "first notification id name" link
    Then I am presented with the "KP Administration ITL specific Notification" screen
    And The page "contains" the "Initial" text
    And The page "contains" the "<notificationTypeName>" text
    When I click the "Initial" button
    Then I am presented with the "KP Administration ITL specific Notification type details" screen
    And The page "contains" the "Open" text
    And The page "contains" the "<notificationTypeName>" text
    When I click the "Back" button
    Then I am presented with the "KP Administration ITL specific Notification" screen
    And The page "contains" the "Initial" text
    And The page "contains" the "<notificationTypeName>" text
    When I click the "Back to ITL Notification list" button
    Then I am presented with the "KP Administration ITL Notifications" screen
    And The page "contains" the "<notificationTypeName>" text

    @sampling-smoke @sampling-mvp-smoke
    Examples:
      # check all values in notificationStatus
      | notificationId | notificationTypeEnum | notificationStatusEnum | notificationTypeName             |
      | 101            | 1                    | 1                      | Type 1 - Net source cancellation |

    @exec-manual
    Examples:
      | notificationId | notificationTypeEnum | notificationStatusEnum | notificationTypeName                                                  |
      | 102            | 1                    | 2                      | Type 1 - Net source cancellation                                      |
      | 103            | 1                    | 3                      | Type 1 - Net source cancellation                                      |
      | 104            | 1                    | 4                      | Type 1 - Net source cancellation                                      |
      # check all values in notificationType
      | 105            | 2                    | 1                      | Type 2 - Non-compliance cancellation                                  |
      | 106            | 3                    | 1                      | Type 3 - Impending tCER/lCER expiry                                   |
      | 107            | 4                    | 1                      | Type 4 - Reversal of storage for CDM project                          |
      | 108            | 5                    | 1                      | Type 5 - Non-submission of certification report for CDM project       |
      | 109            | 6                    | 1                      | Type 6 - Excess issuance for CDM project                              |
      | 110            | 7                    | 1                      | Type 7 - Commitment Period reserve                                    |
      | 111            | 8                    | 1                      | Type 8 - Unit carry-over                                              |
      | 112            | 9                    | 1                      | Type 9 - Expiry date change                                           |
      | 113            | 10                   | 1                      | Type 10 - Notification update                                         |
      | 114            | 11                   | 1                      | Type 14 - EU15 Commitment Period reserve                              |
      | 115            | 12                   | 1                      | Type 12 - Net reversal of storage of a CDM CCS project                |
      | 116            | 13                   | 1                      | Type 13 - Non-submission of verification report for a CDM CCS project |

  @sampling-smoke @test-case-id-89850721195 @sampling-mvp-smoke
  Scenario: As a Senior Admin I can send ITL messages
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    When I click the "ITL Messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    When I enter value "message 1" in "itl message content" field
    And I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    And The page "contains" the "You have sent a message to the ITL" text
    And The page "contains" the "The message ID is" text
    And I see the following fields having the values:
      | fieldName                         | field_value                |
      | confirmation itl message id value | [not empty nor null value] |

  @sampling-smoke @test-case-id-89850721215
  Scenario: As a Senior Admin I send and then access ITL messages
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    When I click the "ITL Messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    When I enter value "message 1" in "itl message content" field
    And I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    And The page "contains" the "You have sent a message to the ITL" text
    And The page "contains" the "The message ID is" text
    And I see the following fields having the values:
      | fieldName                         | field_value                |
      | confirmation itl message id value | [not empty nor null value] |
    When I click the "Back to ITL messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Show filters" button
    And I click the "Search" button
    And The page "contains" the "message 1" text
    When I click the "result 1 message ID" link
    Then I am presented with the "KP Administration specific ITL Message" screen
    And The page "contains" the "ITL message details" text
    And The page "contains" the "message 1" text

  @test-case-id-89850721243
  Scenario: I cannot send an empty ITL message
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    When I click the "ITL Messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Send message" button
    Then I see an error summary with "Message content is required"
    And I see an error detail with id "event-name-error" and content "Message content is required."

  @test-case-id-89850721257 @exec-manual
  Scenario: Ensure that ITL message search filters work as expected
    And I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    And I click the "KP Administration" link
    Then I am presented with the "KP Administration" screen
    When I click the "ITL Messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    When I enter value "message 1" in "itl message content" field
    And I click the "Send message" button
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Back to ITL messages" link
    Then I am presented with the "KP Administration ITL Messages" screen
    When I click the "Search" button
    And The page "contains" the "message 1" text
    When I click the "Show filters" button
    # invalid dates case
    And I enter value "01011990" in "Received on from date" field
    And I enter value "01011991" in "Received on to date" field
    And I click the "Search" button
    And The page "does not contain" the "message 1" text
    When I click the "Show filters" button
    # valid dates
    And I enter value "01012000" in "Received on from date" field
    And I enter value "01012050" in "Received on to date" field
    And I click the "Search" button
    And The page "contains" the "message 1" text
