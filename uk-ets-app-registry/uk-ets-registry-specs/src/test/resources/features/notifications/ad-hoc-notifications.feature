@functional-area-notifications

Feature: Ad hoc notifications

  @test-case-id-836729267121
  Scenario: As sra I can schedule an ad hoc notification
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications" screen
    When I click the "New notification" button
    Then I am presented with the "Notification request" screen
    When I click the "Ad-hoc Notification" button
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    When I enter value "05052030" in "Scheduled Date" field
    And I select the "11:30pm" option
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    When I enter value "system unavailability" in "title" field
    And I enter value "Scheduled maintenance activity between xx/xx and xx/xx." in "content" field
    And I click the "Continue" button
    Then I am presented with the "Notification request" screen
    And The page "contains" the "system unavailability" text
    And The page "contains" the "Scheduled maintenance activity between xx/xx and xx/xx." text
    And I click the "Schedule" button
    Then I am presented with the "Notification request" screen
    And The page "contains" the "You have successfully scheduled the notification." text
    # ensure notification has been scheduled
    When I click the "Go back to notification list" button
    Then I am presented with the "Notifications" screen
    And The page "contains" the "System unavailability" text

  @test-case-id-836729267122 @exec-manual
  Scenario: As sra I update an existing ad hoc notification
    Given I sign in as "senior admin" user
    Then I am presented with the "Registry dashboard" screen
    When I click the "Notifications" link
    Then I am presented with the "Notifications page" screen
    And the following notifications already exist:
      | notification 1 title and content | system unavailability: Scheduled maintenance activity between xx/xx and xx/xx. |
    When I click the "Notification link 1" link
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
    And I enter value "maintenance activity" in "title" field
    And I enter value "the service will be unavailable due to maintenance" in "content" field
    When I click the "Continue" button
    Then I am presented with the "Check the update and confirm" screen
    When I click the "Submit" button
    Then The page "contains" the "You have successfully updated the notification." text

  @test-case-id-836729267123 @exec-manual
  Scenario Outline: As a user I can see an ad hoc notification
    Given I sign in as "<user>" user
    Then I am presented with the "Registry dashboard" screen
    And the following notifications already exist:
      | notification 1 title and content | system unavailability: Scheduled maintenance activity between xx/xx and xx/xx.                             |
      | notification 1 title and content | tentative delays: There may be a delay in processing your application because of the coronavirus outbreak. |

    Examples:
      | user                      |
      | senior admin              |
      | junior admin              |
      | read only admin           |
      | enrolled                  |
      | validated                 |
      | registered                |
      | authorized representative |
      | authority                 |
      
      
   @test-case-id-12423479811 @exec-manual
   Scenario Outline: As junior admin or read only admin I can see the list and the details of the already scheduled notification of all allowed notification types
     Given I sign in as "<user>" user
     Then I am presented with the "Registry dashboard" screen
     And There is already a <notification_type> notification
     When I click the "Notifications" link
     Then I am presented with the "Notifications page" screen
     And the following notifications already exist:
       | notification 1 title and content | system unavailability: Scheduled maintenance activity between xx/xx and xx/xx. |
     When I click the "<notification_type> notification link 1" link
     Then I am presented with the "Notification details" screen
     And the <notification_type> is not editable for <user> user

    Examples:
      | user                      | notification_type                   |
      | junior admin              | Ad-hoc Notification                 |
      | read only admin           | Ad-hoc Notification                 |
      | junior only admin         | Emissions missing for AOHA accounts |
      | read only admin           | Emissions missing for AOHA accounts |
      | junior only admin         | Emissions missing for OHA accounts  |
      | read only admin           | Emissions missing for OHA accounts  |
      | junior only admin         | Surrender deficit for AOHA accounts |
      | read only admin           | Surrender deficit for AOHA accounts |
      | junior only admin         | Surrender deficit for OHA accounts  |
      | read only admin           | Surrender deficit for OHA accounts  |
