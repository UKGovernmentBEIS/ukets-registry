@functional-area-notifications

Feature: Email notifications

  @test-case-id-238974419081 @exec-manual
  Scenario Outline: As sra I can schedule an email notification
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications" screen
    When I click the "New notification" button
    Then I am presented with the "Notification request" screen
    When I click the "<notification_type> Notification" button
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    When I enter value "05052030" in "Scheduled Date" field
    And I pick the "today" option in schedule date field
    And I select the "11:30pm" option
    And I "check" the "Set notification recurrence" checkbox
    And I enter value "123" in "recur every" field
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    And I click the "Schedule" button
    Then I am presented with the "Notification request" screen
    And The page "contains" the "You have successfully scheduled the notification." text
    # ensure notification has been scheduled
    When I click the "Go back to notification list" button
    Then I am presented with the "Notifications" screen
    And The page "contains" the "<notification_type>" text

    Examples:
      | notification_type                                                   |
      | Emissions missing for AOHA accounts                                 |
      | Emissions missing for OHA accounts                                  |
      | Surrender deficit for AOHA accounts                                 |
      | Surrender deficit for AOHA accounts                                 |
      | Yearly introduction to OHA/AOHA accounts with reporting obligations |

  @test-case-id-238974419082 @exec-manual
  Scenario Outline: As sra I update an existing email notification
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications page" screen
    And the following notifications already exist:
      | notification 1 title and content | <notification_type> |
    When I click the "Notification <notification_type> link 1" link
    Then I am presented with the "Notification details" screen
    When I click the "Update notification" button
    Then I am presented with the "Request to update the notification" screen
    # update from "no expiration date" to "expires by"
    When I click the "Expires by" button
    And I enter value "current date plus one day" in "expiration date" field
    And I enter value "current time plus two hour" in "expiration time" field
    When I click the "Continue" button
    # update title and content
    When I clear the "title" field
    And I enter value "Compliance update" in "title" field
    And I enter value "Compliance update content" in "content" field
    When I click the "Continue" button
    Then I am presented with the "Check the update and confirm" screen
    When I click the "Submit" button
    Then The page "contains" the "You have successfully updated the notification." text

    Examples:
      | notification_type                                                   |
      | Emissions missing for AOHA accounts                                 |
      | Emissions missing for OHA accounts                                  |
      | Surrender deficit for AOHA accounts                                 |
      | Surrender deficit for AOHA accounts                                 |
      | Yearly introduction to OHA/AOHA accounts with reporting obligations |

  @test-case-id-987147647131 @exec-manual
  Scenario Outline: As sra I can filter the notification list based on notification type
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications page" screen
    And the following notifications already exist:
      | notification 1 title and content | Emissions missing for AOHA accounts                                 |
      | notification 2 title and content | Emissions missing for OHA accounts                                  |
      | notification 3 title and content | Surrender deficit for AOHA accounts                                 |
      | notification 4 title and content | Surrender deficit for AOHA accounts                                 |
      | notification 5 title and content | Yearly introduction to OHA/AOHA accounts with reporting obligations |
    When I click the "Show filters" button
    When I select the "notification type: <filter>" option
    And I see the following fields having the values:
      | fieldName                 | field_value |
      | notifications result list | <filter>    |

    Examples:
      | filter                                                              |
      | Emissions missing for AOHA accounts                                 |
      | Emissions missing for OHA accounts                                  |
      | Surrender deficit for AOHA accounts                                 |
      | Surrender deficit for AOHA accounts                                 |
      | Yearly introduction to OHA/AOHA accounts with reporting obligations |

  @test-case-id-23747912719101 @exec-manual
  Scenario Outline: As an sra I can see the total number of tentative recipients displayed before email notification scheduling
    # schedule an email notification
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications" screen
    When I click the "New notification" button
    Then I am presented with the "Notification request" screen
    When I click the "<notification_type> Notification" button
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    When I enter value "05052030" in "Scheduled Date" field
    And I pick the "today" option in schedule date field
    And I select the "11:30pm" option
    And I "check" the "Set notification recurrence" checkbox
    And I enter value "123" in "recur every" field
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    # ensure total number of tentative recipients is displayed before email notification scheduling
    And I see the following fields having the values:
      | fieldName                            | field_value |
      | Total number of tentative recipients | 120         |

    Examples:
      | notification_type                                                                         |
      | Emissions missing for AOHA accounts - with 120 recipients                                 |
      | Emissions missing for OHA accounts - with 120 recipients                                  |
      | Surrender deficit for AOHA accounts - with 120 recipients                                 |
      | Surrender deficit for AOHA accounts - with 120 recipients                                 |
      | Yearly introduction to OHA/AOHA accounts with reporting obligations - with 120 recipients |