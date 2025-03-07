@Ui
Feature: Web sample to open browser then navigate to url

  @webTestTrial
  Scenario Outline: Trial to open browser then navigate to M2M URL
    Given user open "getProp.Browser" browser
    And   user maximize browser
    Given user navigate to URL "<url>"
    And   user Wait for "1" seconds

    Examples:
      | url                       |
      | https://www.saucelabs.com |

  @web
  Scenario Outline: Trial to open browser then navigate to Google URL
    Given user open "getProp.Browser" browser
    And   user maximize browser
    Given user navigate to URL "<url>"
    And   user Wait for "1" seconds

    Examples:
      | url                    |
      | https://www.google.com |


  ##############################################################
  ##############################################################
  ################## Common Browser Step Def ###################
  ##############################################################
  ##############################################################

  @allWebSamplesStepDef
  Scenario: Browser Common Step Definition
    Given user open "getProp.Browser" browser
    Given user navigate to URL "projectUrl.com"
    Given user close browser

  @allWebSamplesStepDef
  Scenario: Navigation Common Step Definition
    Given user navigates back
    Given user navigates forward
    Given user refresh page

  @allWebSamplesStepDef
  Scenario: Cookies Common Step Definition
    And   user Clear Cookies
    Given Delete the cookie with name "cookieName"
    Given user adds a cookie named "cookieName" with value "cookieValue"
    Then  user retrieves all cookies
    When  user updates the cookie named "cookieName" with value "cookieValue"
    Then  user retrieves the cookie named "cookieName"

  @allWebSamplesStepDef
  Scenario: Zoom Common Step Definition
    And  user maximize browser
    And  user minimize browser
    Then the browser zoom should be "expectedPercentage"
    And  user toggles full-screen mode to "mode"
    Given  the browser zoom is set to "percentage"

  @allWebSamplesStepDef
  Scenario: Alerts Common Step Definition
    Then user checks if alert is present
    Then user checks that alert should not be present
    Then user accepts the alert
    Then  user dismisses the alert
    Then  the alert text should be "expectedText"

  @allWebSamplesStepDef
  Scenario: Tabs Common Step Definition
    And  user open URL "url" in a new tab
    And  user navigate to tab which it's URL contain "host"
    And  user navigate to previous tab

  @allWebSamplesStepDef
  Scenario: Waits Common Step Definition
    And  user Wait for "minutes" minutes
    And  user Wait for "seconds" seconds