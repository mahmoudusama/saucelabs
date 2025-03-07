package com.sauceLabs.stepDefinitions.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.JSUtils;
import com.sauceLabs.sauceLabs.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class loginPageStepDef extends BaseWebDriver {
    private final JSUtils jsUtils = new JSUtils();
    private final LoginPage loginPage = new LoginPage();

    @And("user set the username: {string}")
    public void setTheUserName(String userName) {
        loginPage.TypeOnUserNameTextBox(userName);
    }

    @And("user set the Password: {string}")
    public void setThePassword(String password) {
        loginPage.TypeOnPassWordTextBox(password);
    }

    @Then("user click on {string} button")
    public void clickOnLogin(String button) {
        switch (button.toLowerCase()) {
            case "login":
                loginPage.clickLogin();
                break;
            default:
                log.warn("Unknown button: {}", button);
                break;
        }
    }

    @And("user sets correct credentials for SauceLabs")
    public void setUserNameAndPassword(){
        jsUtils.waitDocumentReady();
        loginPage.userOnLoginPage();
        loginPage.enterName();
        loginPage.enterPassword();
        loginPage.clickLogin();
        jsUtils.waitDocumentReady();
    }

    @Then("user is redirected to {string} page")
    @When("user should be on the {string} page")
    public void validateLoginPage(String page) {
        jsUtils.waitDocumentReady();
        switch (page.toLowerCase()){
            case "login":
                Assertions.assertTrue(loginPage.userOnLoginPage());
                break;
            case "products":
                loginPage.validatePageTitle("products");
                break;
            case "cart":
                loginPage.validatePageTitle("your cart");
                break;
            case "checkout":
                loginPage.validatePageTitle("Checkout: Your Information");
                break;
            case "overview":
                loginPage.validatePageTitle("Checkout: Overview");
                break;
            case "complete":
                loginPage.validatePageTitle("Checkout: Complete!");
                break;
            default:
                log.error("Unknown page: {}", page);
                break;
        }
    }

    @Then("validate error message {string} is displayed")
    public void validateErrorMessage(String message) {
        loginPage.errorMessage(message);
    }
}