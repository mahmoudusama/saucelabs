@Ui @SortingProducts @SauceLabs
Feature: Product Sorting on Sauce Labs Platform

  Background:
    Given user open "getProp.Browser" browser
    Then user navigate to URL "getProp.sauceLabs"
    And user sets correct credentials for SauceLabs
    Then user should be on the "Products" page

  Scenario: Products are sorted by Price (high to low)
    When user selects "Price (high to low)" from the sort dropdown
    Then the products should be sorted in "descending" order by price

  Scenario: Products are sorted by Price (low to high)
    When user selects "Price (low to high)" from the sort dropdown
    Then the products should be sorted in "ascending" order by price

  Scenario: Products are sorted by Name (A to Z)
    When user selects "Name (A to Z)" from the sort dropdown
    Then the products should be sorted in "alphabetical" order by name

  Scenario: Products are sorted by Name (Z to A)
    When user selects "Name (Z to A)" from the sort dropdown
    Then the products should be sorted in "reverse alphabetical" order by name