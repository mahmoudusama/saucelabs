package com.sauceLabs.stepDefinitions.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.sauceLabs.CartPage;
import io.cucumber.java.en.Then;

public class cartPageStepDef extends BaseWebDriver {
    private final CartPage cartPage  = new CartPage();

    @Then("validate {string} product is added to cart")
    public void validateErrorMessage(String product) {
        cartPage.assertProductInCart(product);
    }
    @Then("user clicks on {string} button in cart page")
    public void buttonsInCart(String button) {
        switch (button.toLowerCase()) {
            case "continue shopping":
                cartPage.clickConShopping();
                break;
            case "checkout":
                cartPage.clickCheckout();
                break;
            default:
                log.warn("Unknown button: {}", button);
                break;
        }
    }
}