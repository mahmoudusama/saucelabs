package com.sauceLabs.stepDefinitions.common.web;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.ui.uiAutomation.BrowserWinUtils;
import com.sauceLabs.common.ui.uiAutomation.SeleUtils;
import com.sauceLabs.common.ui.uiAutomation.JSUtils;
import com.sauceLabs.common.utils.properties.PropertiesManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class BrowserStepDef extends BaseWebDriver {
    public static ThreadLocal<Integer> demoCounter = new ThreadLocal<>();
    private final BrowserWinUtils browserWinUtils = new BrowserWinUtils();
    private final SeleUtils seleUtils = new SeleUtils();
    private final JSUtils jsUtils = new JSUtils();
    private final PropertiesManager propertiesManager = new PropertiesManager();
    /**
     * All Step Definitions related to Browser
     */


    @Given("^user open \"(.*)\" browser$")
    public void startBrowser(String browserName) {
        if (browserName.startsWith("getProp.")) {
            String propKey = browserName.substring("getProp.".length());
            browserName = propertiesManager.getProp(propKey);
        }
        openBrowser(browserName);
    }


    @Given("^user navigate to URL \"(.*)\"")
    public void openUrl(String url) {
        if (url.startsWith("getProp.")) {
            String propKey = url.substring("getProp.".length());
            url = propertiesManager.getProp(propKey);
        }
        browserWinUtils.navigateToUrl(url);
        jsUtils.waitDocumentReady();
    }

    @And("^user close browser$")
    public void closeBrowser() {
        browserWinUtils.quitBrowser();
    }

    /**
     * All Step Definitions related to Navigation
     */

    @Given(("^user refresh page"))
    public void refreshPage() {
        browserWinUtils.refreshPage();
    }

    @Given("^user navigates back$")
    public void navigateBack() {
        browserWinUtils.navigateBack();
    }

    @Given("^user navigates forward$")
    public void navigateForward() {
        browserWinUtils.navigateForward();
    }


    /**
     * All Step Definitions related to Cookies
     */

    @And("^user Clear Cookies")
    public void clearCookies() {
        browserWinUtils.deleteAllCookies();
    }

    @Given("Delete the cookie with name {string}")
    public void deleteCookieByName(String cookieName) {
        browserWinUtils.deleteCookieNamed(cookieName);
    }

    @Given("^user adds a cookie named \"([^\"]*)\" with value \"([^\"]*)\"$")
    public void addCookieToBrowser(String cookieName, String cookieValue) {
        browserWinUtils.addCookie(cookieName,cookieValue);
    }

    @Then("^user retrieves all cookies$")
    public void getAllCookies() {
        browserWinUtils.getAllCookies();
    }

    @When("^user updates the cookie named \"([^\"]*)\" with value \"([^\"]*)\"$")
    public void updateCookie(String cookieName, String cookieValue) {
        browserWinUtils.updateCookie(cookieName,cookieValue);
    }

    @Then("^user retrieves the cookie named \"([^\"]*)\"$")
    public void getCookieByName(String cookieName) {
        browserWinUtils.getCookieNamed(cookieName);
    }


    /**
     * All Step Definitions related to Zoom
     */

    @And("user maximize browser")
    public void userMaximizeBrowser() {
        browserWinUtils.maximizeWindow();
    }

    @And("user minimize browser")
    public void userMinimizeBrowser() {
        browserWinUtils.minimizeWindow();
    }

    @Then("the browser zoom should be {string}")
    public void verifyBrowserZoom(String expectedPercentage) {
        browserWinUtils.getZoomPercentage(expectedPercentage);
    }

    @And("user toggles full-screen mode to {string}")
    public void toggleFullScreenMode(String mode) {
        boolean enterFullScreen = switch (mode.toLowerCase()) {
            case "enter" -> true;
            case "exit" -> false;
            default -> {
                log.warn("Unexpected full-screen mode: '{}'. Defaulting to 'exit'.", mode);
                yield false;
            }
        };
        log.info("Toggling full-screen mode to: {}", enterFullScreen ? "enter" : "exit");
        browserWinUtils.toggleFullScreen(enterFullScreen);
    }


    @Given("the browser zoom is set to {string}")
    public void setBrowserZoom(String percentage) {
        browserWinUtils.setZoomPercentage(percentage);
    }

    /**
     * All Step Definitions related to Alerts
     */
    @Then("^user checks if alert is present$")
    public void checkIfAlertIsPresent() {
        boolean isPresent = seleUtils.isAlertPresent();
        Assertions.assertTrue(isPresent,"Alert should be present");
    }

    @Then("^user checks that alert should not be present$")
    public void checkThatAlertShouldNotBePresent() {
        boolean isPresent = seleUtils.isAlertPresent();
        Assertions.assertFalse(isPresent, "Alert should not be present");
    }

    @Then("^user accepts the alert$")
    public void acceptAlert() {
        seleUtils.acceptAlert();
    }

    @Then("^user dismisses the alert$")
    public void dismissAlert() {
        seleUtils.dismissAlert();
    }

    @Then("^the alert text should be \"([^\"]*)\"$")
    public void verifyAlertText(String expectedText) {
        if (seleUtils.isAlertPresent()) {
            String alertText = seleUtils.getAlertText();
            assertEquals(expectedText, alertText, "Alert text does not match the expected value.");
        }
    }


    /**
     * All Step Definitions related to Tabs
     */

    @And("^user open URL \"(.*)\" in a new tab$")
    public void openUrlNewTab(String url) {
        browserWinUtils.openUrlNewTab(url);
    }

    @And("^user navigate to tab which it's URL contain \"(.*)\"")
    public void navigateToTabWithHost(String host) {
        browserWinUtils.navToTabWithHost( host );
    }

    @And("^user navigate to previous tab$")
    public void moveToPreviousTab() {
        browserWinUtils.movePreviousTab();
    }

    @And("Open new empty tab")
    public void openNewEmptyTab() {
        browserWinUtils.openNewEmptyTab();
        browserWinUtils.moveLastTab();
    }

    @And("^fail after (.*) step")
    public void failForOnceStep(String s) {
        if (demoCounter.get() == null) demoCounter.set(Integer.parseInt(s));
        demoCounter.set(demoCounter.get() + 1);
    }


    /**
     * Step Definitions related to Wait
     */

    @And("^user Wait for \"(.*)\" minutes")
    public void waitForGivenMinutes(String min) {
        long waitInMin = (long) (Double.parseDouble(min) * 60 * 1000) + 5000;
        log.info("sleeping for: {} minutes", waitInMin);
        try {
            Thread.sleep(waitInMin);
            log.info("done waiting");
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
    }

    @And("^user Wait for \"(.*)\" seconds")
    public void waitForGivenSecs(String sec) {
        long waitInSec = (long) (Double.parseDouble(sec) * 1000);
        log.info("sleeping for: {} seconds", waitInSec);
        try {
            Thread.sleep(waitInSec);
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
    }

}
