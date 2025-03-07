package com.sauceLabs.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.BrowserWinUtils;
import com.sauceLabs.common.ui.uiAutomation.SeleUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OverviewPage extends BaseWebDriver {
    private static final By itemTotal = By.xpath("//div[@class='summary_subtotal_label']");
    private static final By finishBtn = By.id("finish");

    private final BrowserWinUtils browserWinUtils = new BrowserWinUtils();
    private final SeleUtils seleUtils = new SeleUtils();


    public void clickFinish() {
        seleUtils.clickOnElement(finishBtn);
    }

    public void validateTotalPrice() {
        String itemTotalPrice = seleUtils.getText(itemTotal).replace("Item total: $", "");
        double total = 0.0;
        List<WebElement> priceElements = seleUtils.getElements(By.className("inventory_item_price"));
        for (WebElement priceElement : priceElements) {
            String priceText = priceElement.getText().replace("$", "");
            double price = Double.parseDouble(priceText);
            total += price;
        }
        String totalPrice = Double.toString(total);
        Assertions.assertEquals(itemTotalPrice,totalPrice, "Total price and tem total price are not equals");
    }

    public void assertUrl(String expectedUrl) {
        seleUtils.isUrlToBe(expectedUrl);
    }
}
