package com.sauceLabs.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.SeleUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BaseWebDriver {
    private static final By continueShopping = By.id("continue-shopping");
    private static final By checkout = By.id("checkout");
    private final SeleUtils seleUtils = new SeleUtils();


    public void clickConShopping() {
        seleUtils.clickOnElement(continueShopping);
    }

    public void clickCheckout() {
        seleUtils.clickOnElement(checkout);
    }

    public void assertProductInCart(String partialProductName) {
        String xpath = "//div[@class='cart_list']//div[contains(@class, 'cart_item')]//div[contains(@class, 'cart_item_label')]//div[contains(text(),'" + partialProductName + "')]";
        List<WebElement> matchingProducts = seleUtils.getElements(By.xpath(xpath));
        boolean isProductFound = !matchingProducts.isEmpty();
        Assertions.assertTrue(isProductFound);
    }



}
