@Ui @AddProduct @SauceLabs
Feature: Purchase Flow validation on Sauce Labs Platform

  Background:
    Given user open "getProp.Browser" browser
    Then user navigate to URL "getProp.sauceLabs"
    And user sets correct credentials for SauceLabs
    Then user should be on the "Products" page

  Scenario: Validate product purchase flow from adding items to cart to order completion
    When user adds "fleece jacket" to the cart
    And user adds "backpack" to the cart
    Then user clicks on "cart" icon
    Then user should be on the "cart" page
    Then validate "Fleece Jacket" product is added to cart
    And validate "Backpack" product is added to cart
    Then user clicks on "checkout" button in cart page
    Then user is redirected to "checkout" page
    And user fills required data for checkout
    Then user clicks on "continue" button in checkout page
    Then user is redirected to "overview" page
    Then user verify that URL matched with "https://www.saucedemo.com/checkout-step-two.html"
    Then user validate total price are equal item price
    And user click on Finish
    Then user is redirected to "complete" page
    Then user validate "Thank you" and "order has been dispatched" messages are shown