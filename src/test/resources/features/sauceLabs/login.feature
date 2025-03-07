@Ui @logInPage @SauceLabs
Feature: Login page validation for Sauce Labs Platform

  Background:
    Given user open "getProp.Browser" browser
    Then user navigate to URL "getProp.sauceLabs"
    When user should be on the "login" page

  @loginPositive
  Scenario Outline: Positive Scenario - Login to sauceLabs website with correct credentials
    And user set the username: "<userName>"
    And user set the Password: "secret_sauce"
    Then user click on "Login" button
    And user should be on the "Products" page

    Examples:
      | userName                |
      | standard_user           |
      | problem_user            |
      | performance_glitch_user |
      | error_user              |
      | visual_user             |

  @loginNegative
  Scenario Outline: Negative Scenarios - Login with invalid credentials
    And user set the username: "<userName>"
    And user set the Password: "<passWord>"
    Then user click on "Login" button
    Then validate error message "<errorMessage>" is displayed

    Examples:
      | userName      | passWord      | errorMessage                                                              |
      | standard_user | wrongPassword | Epic sadface: Username and password do not match any user in this service |
      | wrongUser     | secret_sauce  | Epic sadface: Username and password do not match any user in this service |
      | wrongUser     | wrongPassword | Epic sadface: Username and password do not match any user in this service |
      |               | secret_sauce  | Epic sadface: Username is required                                        |
      | standard_user |               | Epic sadface: Password is required                                        |
      |               |               | Epic sadface: Username is required                                        |