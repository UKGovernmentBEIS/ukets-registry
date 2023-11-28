Feature: Security - Security API tests

  @api-security @test-case-id-96565226536
  Scenario: Example test case for Registry API
    Given the client has been authenticated as "registry admin" user
    When the client requests "GET" "/admin/users.list?page=0&pageSize=10&sortField=registeredOn&sortDirection=DESC"
    Then the response status code should be 200
    And the response contains element "totalResults" with value:
    """
    16
    """
