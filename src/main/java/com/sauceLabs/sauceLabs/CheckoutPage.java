package com.sauceLabs.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.SeleUtils;
import com.sauceLabs.common.utils.generator.Generator;
import org.openqa.selenium.By;

public class CheckoutPage extends BaseWebDriver {
    private static final By firstNameBox = By.id("first-name");
    private static final By lastNameBox = By.id("last-name");
    private static final By postalCodeBox = By.id("postal-code");
    private static final By continueBtn = By.id("continue");
    private static final By cancelBtn = By.id("cancel");
    private final SeleUtils seleUtils = new SeleUtils();


    public void setFirstName() {
        seleUtils.setText(firstNameBox, Generator.generateRandomName(6));
    }

    public void setlastName() {
        seleUtils.setText(lastNameBox, Generator.generateRandomName(6));
    }

    public void setPostalCode() {
        seleUtils.setText(postalCodeBox, Generator.generateRandomNumbers(9));
    }

    public void clickContinue() {
        seleUtils.clickOnElement(continueBtn);
    }

    public void clickCancel() {
        seleUtils.clickOnElement(cancelBtn);
    }
}
