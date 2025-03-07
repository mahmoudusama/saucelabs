package com.sauceLabs.stepDefinitions.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.sauceLabs.CompletePage;
import io.cucumber.java.en.Then;

public class completePageStepDef extends BaseWebDriver {
    private final CompletePage completePage  = new CompletePage();

    @Then("user validate {string} and {string} messages are shown")
    public void validateUrl(String firstMessage, String secondMessage) {
        completePage.assertMessage(firstMessage);
        completePage.assertMessage(secondMessage);
    }
}