package com.sauceLabs.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.SeleUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ProductsPage extends BaseWebDriver {
    private static final By firstProduct = By.id("add-to-cart-sauce-labs-fleece-jacket");
    private static final By secondProduct = By.id("add-to-cart-sauce-labs-backpack");
    private static final By cartIcon = By.xpath("//a[@class='shopping_cart_link']");
    private static final By sortDropDown = By.xpath("//select[@class='product_sort_container']");
    private static final By pricesElements = By.className("inventory_item_price");
    private static final By productTitles = By.xpath("//div[@class='inventory_item_name ']");


    private final SeleUtils seleUtils = new SeleUtils();

    public void clickFirstProduct() {
        seleUtils.clickOnElement(firstProduct);
    }

    public void clickSecondProduct() {
        seleUtils.clickOnElement(secondProduct);
    }

    public void clickCartIcon() {
        seleUtils.clickOnElement(cartIcon);
    }

    public void selectFromSortDropDown(String dropdownValue) {
        seleUtils.selectTextFromDropDown(sortDropDown, dropdownValue);
    }

    public List<Double> getAllPricesAsDoubles() {
        List<WebElement> priceElementsList = seleUtils.getElements(pricesElements);
        List<Double> prices = new ArrayList<>();
        if (priceElementsList != null && !priceElementsList.isEmpty()) {
            for (WebElement element : priceElementsList) {
                String priceText = element.getText().replace("$", "").trim();
                try {
                    double price = Double.parseDouble(priceText);
                    prices.add(price);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing price: " + priceText);
                    log.error("Error parsing price: {}", priceText);
                }
            }
        }
        return prices;
    }

    public static boolean isDescending(List<Double> prices) {
        if (prices == null || prices.size() <= 1) {
            return true;
        }
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i - 1) < prices.get(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAscending(List<Double> prices) {
        if (prices == null || prices.size() <= 1) {
            return true;
        }
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i - 1) > prices.get(i)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getAllProductsTitle() {
        List<WebElement> productTitleList = seleUtils.getElements(productTitles);
        List<String> titles = new ArrayList<>();
        if (productTitleList != null && !productTitleList.isEmpty()) {
            for (WebElement element : productTitleList) {
                String title = element.getText();
                try {
                    titles.add(title);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing title: " + title);
                    log.error("Error parsing title: {}", title);
                }
            }
        }
        return titles;
    }

    public static boolean isSortedAlphabetically(List<String> productNames) {
        if (productNames == null || productNames.size() <= 1) {
            return true;
        }
        for (int i = 1; i < productNames.size(); i++) {
            if (productNames.get(i - 1).compareTo(productNames.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSortedReverseAlphabetically(List<String> productNames) {
        if (productNames == null || productNames.size() <= 1) {
            return true;
        }
        for (int i = 1; i < productNames.size(); i++) {
            if (productNames.get(i - 1).compareTo(productNames.get(i)) < 0) {
                return false;
            }
        }
        return true;
    }
}
