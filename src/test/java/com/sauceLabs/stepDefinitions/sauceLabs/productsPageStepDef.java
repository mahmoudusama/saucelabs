package com.sauceLabs.stepDefinitions.sauceLabs;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.sauceLabs.ProductsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import java.util.List;

public class productsPageStepDef extends BaseWebDriver {
    private static final ProductsPage productsPage = new ProductsPage();

    @Then("user clicks on {string} icon")
    @Then("user adds {string} to the cart")
    public void addProductsCart(String product) {
        switch (product.toLowerCase()) {
            case "fleece jacket":
                productsPage.clickFirstProduct();
                break;
            case "backpack":
                productsPage.clickSecondProduct();
                break;
            case "cart":
                productsPage.clickCartIcon();
                break;
            default:
                log.warn("Unknown product: {}", product);
                break;
        }
    }

    @When("user selects {string} from the sort dropdown")
    public void user_selects_from_the_dropdown(String value) {
        productsPage.selectFromSortDropDown(value);
    }

    @Then("the products should be sorted in {string} order by price")
    public void the_products_should_be_sorted_in_descending_order_by_price(String dropDownValue) {
        List<Double> prices =  productsPage.getAllPricesAsDoubles();
        switch (dropDownValue.toLowerCase()) {
            case "descending":
                Assertions.assertTrue(productsPage.isDescending(prices), "Products are sorted in descending");
                break;
            case "ascending":
                Assertions.assertTrue(productsPage.isAscending(prices), "Products are sorted in Ascending");
                break;
            default:
                log.warn("That value is not in the dropdown list: {}", dropDownValue);
                break;
        }
    }

    @Then("the products should be sorted in {string} order by name")
    public void alphabetical(String value) {
        List<String> allProductsTitle =  productsPage.getAllProductsTitle();
        switch (value.toLowerCase()) {
            case "alphabetical":
                Assertions.assertTrue(productsPage.isSortedAlphabetically(allProductsTitle), "Products are sorted from A to Z");
                break;
            case "reverse alphabetical":
                Assertions.assertTrue(productsPage.isSortedReverseAlphabetically(allProductsTitle), "Products are sorted from Z to A");
                break;
            default:
                log.warn("That value is not in the dropdown list : {}", value);
                break;
        }
    }
}