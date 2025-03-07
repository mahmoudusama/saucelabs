package com.sauceLabs.common.ui.uiAutomation;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.utils.helpers.CommonUtility;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * this class used for interacting with webElements,
 * webDriver object is initiated in BaseWebDriver class
 *
 * @author ShoumanM & Ahmed Alaa
 */
public class SeleUtils extends BaseWebDriver {
    JSUtils jsUtils = new JSUtils();
    CommonUtility commonUtility = new CommonUtility();
    private static final int defaultTimeoutInMillis = 500;
    private static final int defaultTimeoutInSec = 30;
    private static final int defaultTimeoutInMin = 1;


    /**
     * convert By object to select object
     *
     * @param by By Object for the inspected element
     * @return select object
     */
    public Select getSelectObj(By by) {
        WebElement e = getDriver().findElement(by);
        Select slct = new Select(e);
        return slct;
    }

    /**
     * auto select first value in mandatory dropdown menus which contains 'required' attribute
     *
     * @param menuParentElement WebElement which contains the dropdown menus
     */
    public void autoSelectAllMenus(WebElement menuParentElement) {
        List<WebElement> mandatoryMenus = menuParentElement.findElements(By.tagName("select"));
        for (WebElement e : mandatoryMenus) {
            if (e.getAttribute("required") != null) {
                new WebDriverWait(getDriver(), Duration.ofSeconds(5)).until(ExpectedConditions
                        .presenceOfNestedElementLocatedBy(e, By.tagName("option")));
                new Select(e).selectByIndex(1);
            }
        }
    }


    /**
     * get parent element with the given tagName
     *
     * @param elem          Element object
     * @param parentElemTag desired tagName
     * @return parent WebElement object
     */
    public WebElement getParentElem(WebElement elem, String parentElemTag) {
        log.info("getting parent element with tag: {}", parentElemTag);
        String script = "return arguments[0].parentElement";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        WebElement parent = null;
        do {
            if (parent == null) {
                parent = (WebElement) js.executeScript(script, elem);
            } else {
                parent = (WebElement) js.executeScript(script, parent);
            }
//              log.info(parent.getTagName());
        } while (!parent.getTagName().equalsIgnoreCase(parentElemTag) && !parentElemTag.isEmpty());
        return parent;
    }

    /**
     * get displayed parent element
     *
     * @param elem Element object
     * @return parent WebElement object
     */
    public WebElement getDisplayedParentElem(WebElement elem) {
        WebElement parent = null;
        do {
            parent = parent == null ? getParentElem(elem, "") : getParentElem(parent, "");
        } while (!new JSUtils().isElementDisplayed(parent));
        return parent;
    }

    /**
     * get displayed element which contains given text
     *
     * @param optParentElem
     * @param text          displayed text content
     * @return object WebElement which contains text
     */
    public WebElement getElemWithText(WebElement optParentElem, String text) {
        List<WebElement> t = new JSUtils().getElementsBySelectorFilter(optParentElem, "*", " if(i.innerText && i.innerText.toLowerCase().indexOf('" + text.replaceAll("'", "\\\\'") + "'.toLowerCase())>-1 && i.offsetParent){ return true;}");
        log.info("{} count of elements with text>>{}", t.size(), text);
        if (t.isEmpty()) return null;
        else return t.get(t.size() - 1);
    }


    /**
     * get button element with displayed text
     *
     * @param btnName button's displayed text
     * @return button WebElement object
     */
    public List<WebElement> getButtonsWithText(String btnName) {
        return new JSUtils().getButtonWithText(btnName);
    }


    /**
     * Retrieves a list of WebElements based on the provided locator.
     * Ensures the elements are present before returning the list.
     *
     * @param locator The By locator used to find the elements.
     * @param timeInSec Optional parameter specifying the maximum wait time in seconds for elements to be visible.
     * @return A list of WebElements matching the locator; empty if no elements found within the timeout.
     */
    public List<WebElement> getElements(By locator, int... timeInSec) {
        List<WebElement> elements = new ArrayList<>();
        try {
            validateStateOfElement(ExpectedConditions.visibilityOfElementLocated(locator), timeInSec);
            elements = getDriver().findElements(locator);
            log.info("Found {} elements for locator: {}", elements.size(), locator);
        } catch (Exception e) {
            log.warn("Failed to retrieve elements for locator: {}", locator, e);
        }
        return elements;
    }



    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *                             Wait Methods
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */


    /**
     * Sets the implicit wait timeout for the WebDriver in milliseconds.
     *
     * @param timeoutMillis The timeout duration in milliseconds.
     */
    public WebDriverWait setImplicitWaitMs(int... timeoutMillis) {
        int waitTime = (timeoutMillis.length > 0 && timeoutMillis[0] >= 0) ? timeoutMillis[0] : defaultTimeoutInMillis;
        log.info("Setting implicit wait timeout to {} milliseconds.", timeoutMillis);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofMillis(waitTime));
        return new WebDriverWait(getDriver(), Duration.ofMillis(waitTime));
    }

    /**
     * Sets the implicit wait timeout for the WebDriver in seconds.
     *
     * @param timeoutSec The timeout duration in seconds.
     */
    public WebDriverWait setImplicitWaitSec(int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        log.info("Setting implicit wait timeout to {} seconds.", timeoutSec);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTime));
        return new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
    }

    /**
     * Sets the implicit wait timeout for the WebDriver in minutes.
     *
     * @param timeoutMin The timeout duration in minutes.
     */
    public WebDriverWait setImplicitWaitMin(int... timeoutMin) {
        int waitTime = (timeoutMin.length > 0 && timeoutMin[0] >= 0) ? timeoutMin[0] : defaultTimeoutInMin;
        log.info("Setting implicit wait timeout to {} minutes.", waitTime);
        getDriver().manage().timeouts().implicitlyWait(Duration.ofMinutes(waitTime));
        return new WebDriverWait(getDriver(), Duration.ofMinutes(waitTime));
    }

    /**
     * Sets the implicit wait timeout for the WebDriver in seconds.
     *
     * @param timeoutSec The timeout duration in seconds.
     */
    public WebDriverWait setExplicitWaitSec(int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        log.info("Explicit wait timeout is set to zero.");
        return new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
    }

    /**
     * Sets the WebDriverWait for the WebDriver with a specified timeout and polling interval in milliseconds.
     *
     * @param timeoutMs The timeout duration in milliseconds.
     * @param pollingMs The polling interval in milliseconds.
     * @return A WebDriverWait instance with the specified timeout and polling interval.
     */
    public WebDriverWait setFluentWaitMs(int pollingMs, int... timeoutMs) {
        int waitTime = (timeoutMs.length > 0 && timeoutMs[0] >= 0) ? timeoutMs[0] : defaultTimeoutInMillis;
        log.info("Setting WebDriverWait timeout to {} milliseconds with a polling interval of {} milliseconds.", waitTime, pollingMs);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofMillis(waitTime));
        wait.pollingEvery(Duration.ofMillis(pollingMs));
        return wait;
    }


    /**
     * Sets the WebDriverWait for the WebDriver with a specified timeout and polling interval in seconds.
     *
     * @param timeoutSec The timeout duration in seconds.
     * @param pollingSec The polling interval in seconds.
     * @return A WebDriverWait instance with the specified timeout and polling interval.
     */
    public WebDriverWait setFluentWaitSec(int pollingSec, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        log.info("Setting WebDriverWait timeout to {} seconds with a polling interval of {} seconds.", waitTime, pollingSec);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
        wait.pollingEvery(Duration.ofSeconds(pollingSec));
        return wait;
    }

    /**
     * Sets the WebDriverWait for the WebDriver with a specified timeout and polling interval in minutes.
     *
     * @param timeoutMin The timeout duration in minutes.
     * @param pollingMin The polling interval in minutes.
     * @return A WebDriverWait instance with the specified timeout and polling interval.
     */
    public WebDriverWait setFluentWaitMin(int pollingMin, int... timeoutMin) {
        int waitTime = (timeoutMin.length > 0 && timeoutMin[0] >= 0) ? timeoutMin[0] : defaultTimeoutInMin;
        log.info("Setting WebDriverWait timeout to {} minutes with a polling interval of {} minutes.", waitTime, pollingMin);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofMinutes(waitTime));
        wait.pollingEvery(Duration.ofMinutes(pollingMin));
        return wait;
    }

    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *           Expected Conditions with wait & Assertion Methods
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */

    /**
     * Validates the presence of a web element based on a specified condition.
     *
     * @param condition The ExpectedCondition used to validate the element (e.g., visibility of element).
     */
    public void validateStateOfElement(ExpectedCondition<?> condition, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        try {
            log.info("Validating the element under the specified condition with timeout of {} seconds: {}", waitTime, condition);
            setExplicitWaitSec(waitTime).until(condition);
        } catch (TimeoutException e) {
            log.error("Element condition timed out: {}", condition, e);
            throw new AssertionError("Element condition timed out: " + condition, e);
        } catch (Exception e) {
            log.error("Failed to validate element under the specified condition: {}", condition, e);
            throw new AssertionError("Error occurred while validating element condition: " + condition, e);
        }
    }

    /* -------------------------------------------------- */
    /* ----------------Click-ability Methods------------- */
    /* -------------------------------------------------- */

    /**
     * Checks whether a web element is clickable or not.
     *
     * @param element    The locator of the element to be checked.
     * @param timeoutSec Optional timeout in seconds to wait for the element to become visible. If not provided, uses a default timeout.
     */
    public void isElementClickable(Object element, int... timeoutSec) {
        ExpectedCondition<WebElement> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.elementToBeClickable(locator);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.elementToBeClickable(webElement);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Element is clickable");
        } catch (AssertionError e) {
            log.error("Element is not clickable: {}", e.getMessage());
            Assertions.fail("Element is not clickable: " + element + ". Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------ */
    /* ----------------Element selection state Methods------------- */
    /* ------------------------------------------------------------ */

    public void isElementSelectionStateToBe(Object element, boolean selected, int... timeInSec) {
        ExpectedCondition<Boolean> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.elementSelectionStateToBe(locator, selected);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.elementSelectionStateToBe(webElement, selected);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Element selection state is as expected: {}", selected);
        } catch (AssertionError e) {
            log.error("Element selection state is not as expected: {}", e.getMessage());
            Assertions.fail("Element selection state is not as expected: " + element + ". Exception: " + e.getMessage());
        }
    }


    /* ----------------------------------------------- */
    /* ----------------Visibility Methods------------- */
    /* ----------------------------------------------- */

    /**
     * Checks whether a web element is displayed on the page.
     *
     * @param element    The locator of the element to be checked.
     * @param timeoutSec Optional timeout in seconds to wait for the element to become visible. If not provided, uses a default timeout.
     */
    public void isElementVisible(Object element, int... timeoutSec) {
        ExpectedCondition<WebElement> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.visibilityOfElementLocated(locator);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.visibilityOf(webElement);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Element is displayed");
        } catch (AssertionError e) {
            log.error("Element is not displayed: {}", e.getMessage());
            Assertions.fail("Element is not displayed: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void areAllElementsVisible(Object elements, int... timeoutSec) {
        ExpectedCondition<List<WebElement>> condition;

        if (elements instanceof By) {
            condition = ExpectedConditions.visibilityOfAllElementsLocatedBy((By) elements);
        } else if (elements instanceof List) {
            condition = ExpectedConditions.visibilityOfAllElements((List<WebElement>) elements);
        } else {
            throw new AssertionError("Invalid elements type provided. Must be By or List WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Elements are displayed");
        } catch (AssertionError e) {
            log.error("Elements are not displayed: {}", e.getMessage());
            Assertions.fail("Elements are not displayed: " + elements + ". Exception: " + e.getMessage());
        }
    }

    public void areAllNestedElementsVisible(Object elements, By childLocator ,int... timeoutSec) {
        ExpectedCondition<List<WebElement>> condition;

        if (elements instanceof By) {
            condition = ExpectedConditions.visibilityOfNestedElementsLocatedBy((By) elements, childLocator);
        } else if (elements instanceof WebElement webElement) {
            condition = ExpectedConditions.visibilityOfNestedElementsLocatedBy(webElement, childLocator);
        } else {
            throw new AssertionError("Invalid elements type provided. Must be By or List WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Nested elements are displayed for parent: {} and child locator: {}", elements, childLocator);
        } catch (AssertionError e) {
            log.error("Nested elements are not displayed for parent: {} and child locator: {}. Exception: {}", elements, childLocator, e.getMessage());
            Assertions.fail("Nested elements are not displayed for parent: " + elements + " and child locator: " + childLocator + ". Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------------- */
    /* ----------------Invisibility Methods------------- */
    /* ------------------------------------------------- */

    /**
     * This method is used to wait for an element to become invisible.
     *
     * @param element    The locator of the element to be checked.
     * @param timeoutSec Optional timeout in seconds to wait for the element to become invisible. If not provided, a default timeout will be used.
     */
    public void isElementInvisible(Object element, int... timeoutSec) {
        ExpectedCondition<Boolean> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.invisibilityOfElementLocated(locator);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.invisibilityOf(webElement);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Element is invisible");
        } catch (AssertionError e) {
            log.error("Element is not invisible: {}", e.getMessage());
            Assertions.fail("Element is not invisible: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void isElementWithTextInvisible(By element, String text, int... timeoutSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.invisibilityOfElementWithText(element, text);
        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Element with text {} is invisible", text);
        } catch (AssertionError e) {
            log.error("Element with text {} is not invisible: {}", text, e.getMessage());
            Assertions.fail("Element " + element + " with text " + text + " is not invisible: Exception: " + e.getMessage());
        }
    }

    public void areAllWebelementsInvisible(List<WebElement> elements, int... timeoutSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.invisibilityOfAllElements(elements);
        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("List of WebElements are invisible");
        } catch (AssertionError e) {
            log.error("List of WebElements are not invisible: {}", e.getMessage());
            Assertions.fail("WebElements " + elements + " are not invisible: Exception: " + e.getMessage());
        }
    }

    /* --------------------------------------------- */
    /* ----------------Presence Methods------------- */
    /* --------------------------------------------- */

    /**
     * Checks whether an element is present in the DOM.
     *
     * @param element    The locator of the element to be checked.
     * @param timeoutSec Optional timeout in seconds to wait for the element to be present. If not provided, uses a default timeout.
     */
    public void isElementPresent(By element, int... timeoutSec) {
        ExpectedCondition<WebElement> condition = ExpectedConditions.presenceOfElementLocated(element);
        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Element is presence");
        } catch (AssertionError e) {
            log.error("Element is not presence: {}", e.getMessage());
            Assertions.fail("Element is not presence: " + element + ". Exception: " + e.getMessage());
        }
    }

    public boolean isNestedElementPresent(Object element, By childLocator ,int... timeoutSec) {
        ExpectedCondition<WebElement> condition;
        if (element instanceof By locator) {
            condition = ExpectedConditions.presenceOfNestedElementLocatedBy(locator, childLocator);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.presenceOfNestedElementLocatedBy(webElement, childLocator);
        } else {
            throw new AssertionError("Invalid elements type provided. Must be By or List WebElement.");
        }

        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Nested element is presence for parent: {} and child locator: {}", element, childLocator);
            return true;
        } catch (AssertionError e) {
            log.error("Nested element is not presence for parent: {} and child locator: {}. Exception: {}", element, childLocator, e.getMessage());
            Assertions.fail("Nested element is not presence for parent: " + element + " and child locator: " + childLocator + ". Exception: " + e.getMessage());
            return false;
        }
    }

    public void areAllElementsPresent(By element, int... timeoutSec) {
        ExpectedCondition<List<WebElement>> condition = ExpectedConditions.presenceOfAllElementsLocatedBy(element);
        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Elements are presence");
        } catch (AssertionError e) {
            log.error("Element are not presence: {}", e.getMessage());
            Assertions.fail("Elements are not presence: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void areAllNestedElementsPresent(By element, By childLocator ,int... timeoutSec) {
        ExpectedCondition<List<WebElement>> condition = ExpectedConditions.presenceOfNestedElementsLocatedBy(element, childLocator);
        try {
            validateStateOfElement(condition, timeoutSec);
            log.info("Nested elements are present for parent locator: {} and child locator: {}", element, childLocator);
        } catch (AssertionError e) {
            log.error("Nested elements are not present for parent locator: {} and child locator: {}. Exception: {}", element, childLocator, e.getMessage());
            Assertions.fail("Nested elements are not present for parent locator: " + element + " and child locator: " + childLocator + ". Exception: " + e.getMessage());
        }
    }

    /* ---------------------------------------------- */
    /* ----------------Attribute Methods------------- */
    /* ---------------------------------------------- */

    public void isAttrContains(Object element, String attribute, String value, int... timeInSec) {
        ExpectedCondition<Boolean> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.attributeContains(locator, attribute, value);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.attributeContains(webElement, attribute, value);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Attribute contains that value");
        } catch (AssertionError e) {
            log.error("Attribute does not contain that value: {}", e.getMessage());
            Assertions.fail("Attribute does not contain that value: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void isAttrToBe(Object element, String attribute, String value, int... timeInSec) {
        ExpectedCondition<Boolean> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.attributeToBe(locator, attribute, value);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.attributeToBe(webElement, attribute, value);
        } else {
            throw new AssertionError("Invalid element type provided. Must be By or WebElement.");
        }

        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Attribute '{}' of element '{}' is '{}'", attribute, element, value);
        } catch (AssertionError e) {
            log.error("Attribute '{}' of element '{}' is not '{}'. Exception: {}", attribute, element, value, e.getMessage());
            Assertions.fail("Attribute '" + attribute + "' of element '" + element + "' is not '" + value + "'. Exception: " + e.getMessage());
        }
    }

    public void isAttrNotEmpty(WebElement element, String attribute, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.attributeToBeNotEmpty(element, attribute);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Attribute '{}' of element '{}' is not empty", attribute, element);
        } catch (AssertionError e) {
            log.error("Attribute '{}' of element '{}' is empty or not present. Exception: {}", attribute, element, e.getMessage());
            Assertions.fail("Attribute '" + attribute + "' of element '" + element + "' is empty or not present. Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------ */
    /* ----------------Text Methods-------------- */
    /* ------------------------------------------ */

    /**
     * Checks whether an element containing the specified inner text is displayed.
     *
     * @param text      The inner text content to look for within elements.
     * @param timeInSec Optional timeout value in seconds for waiting for the element.
     */
    public void isTextDisplayed(Object element, String text, int... timeInSec) {
        ExpectedCondition<Boolean> condition;

        if (element instanceof By locator) {
            condition = ExpectedConditions.textToBePresentInElementLocated(locator, text);
        } else if (element instanceof WebElement webElement) {
            condition = ExpectedConditions.textToBePresentInElement(webElement, text);
        } else {
            throw new AssertionError("Argument must be either By or WebElement");
        }

        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Text '{}' is displayed in the element.", text);
        } catch (AssertionError e) {
            log.error("Text does not displayed: {}", e.getMessage());
            Assertions.fail("Text does not displayed: " + element + ". Exception: " + e.getMessage());
        }
    }


    /**
     * Waits for the text of an element to change to the specified value.
     *
     * @param element      The locator of the element whose text is being checked.
     * @param expectedText The expected text value.
     * @param timeInSec    Optional timeout in seconds to wait for the text change. If not provided, uses a default timeout.
     * @return True if the text changes to the expected value within the timeout, false otherwise.
     */
    public void isTextChange(By element, String expectedText, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.textToBe(element, expectedText);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Text of the element with locator '{}' has changed to '{}'.", element, expectedText);
        } catch (Exception e) {
            log.error("An error occurred while waiting for the text of element with locator '{}' to change: {}", element, e.getMessage());
            Assertions.fail("An error occurred while waiting for the text of element to change : " + element + ". Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------ */
    /* ----------------Title Methods------------- */
    /* ------------------------------------------ */

    /**
     * Checks whether the page title contains the specified text.
     *
     * @param text      The text content to look for in the page title.
     * @param timeInSec Optional timeout value in seconds for waiting for the page title to contain the text.
     * @return true if the page title contains the specified text, false otherwise.
     */
    public void isTitleContains(String text, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.titleContains(text);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Page title contains '{}'.", text);
        } catch (AssertionError e) {
            log.error("Page title does not contain '{}': {}", text, e.getMessage());
            Assertions.fail("Failed to verify that the page title contains the specified text : " + text + ". Exception: " + e.getMessage());
        }
    }

    public void isTitleMatches(String text, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.titleIs(text);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Page title matches '{}'.", text);
        } catch (AssertionError e) {
            log.error("Page title does not match '{}': {}", text, e.getMessage());
            Assertions.fail("Failed to verify that the page title matches the specified text : " + text + ". Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------ */
    /* ----------------URL Methods--------------- */
    /* ------------------------------------------ */

    public void isUrlContains(String fraction, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.urlContains(fraction);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Page url contains '{}'.", fraction);
        } catch (AssertionError e) {
            log.error("Error while checking if the page url contains '{}': {}", fraction, e.getMessage());
            Assertions.fail("Failed to verify that the page url contains " + fraction + ". Exception: " + e.getMessage());
        }
    }

    public void isUrlToBe(String url, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.urlToBe(url);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Page url is '{}'.", url);
        } catch (AssertionError e) {
            log.error("Error while checking if the page url '{}': {}", url, e.getMessage());
            Assertions.fail("Failed to verify that the page url: " + url + ". Exception: " + e.getMessage());
        }
    }

    /* ----------------------------------------------------- */
    /* ----------------Count Element Methods---------------- */
    /* ----------------------------------------------------- */


    public void isNumberOfElementsMoreThan(By element, int number, int... timeInSec) {
        ExpectedCondition<List<WebElement>> condition = ExpectedConditions.numberOfElementsToBeMoreThan(element, number);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Number of elements located by '{}' is more than '{}'", element, number);
        } catch (AssertionError e) {
            log.error("Number of elements located by '{}' is not more than '{}'. Exception: {}", element, number, e.getMessage());
            Assertions.fail("Number of elements located by '" + element + "' is not more than '" + number + "'. Exception: " + e.getMessage());
        }
    }

    public void isNumberOfElementsLessThan(By element, int number, int... timeInSec) {
        ExpectedCondition<List<WebElement>> condition = ExpectedConditions.numberOfElementsToBeLessThan(element, number);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Number of elements located by '{}' is less than '{}'", element, number);
        } catch (AssertionError e) {
            log.error("Number of elements located by '{}' is not less than '{}'. Exception: {}", element, number, e.getMessage());
            Assertions.fail("Number of elements located by '" + element + "' is not less than '" + number + "'. Exception: " + e.getMessage());
        }
    }

    public void isNumberOfElementsToBe(By element, int number, int... timeInSec) {
        ExpectedCondition<List<WebElement>> condition = ExpectedConditions.numberOfElementsToBe(element, number);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Number of elements located by '{}' equals '{}'", element, number);
        } catch (AssertionError e) {
            log.error("Number of elements located by '{}' equals '{}'. Exception: {}", element, number, e.getMessage());
            Assertions.fail("Number of elements located by '" + element + "' equals '" + number + "'. Exception: " + e.getMessage());
        }
    }

    /* ------------------------------------------------- */
    /* ----------------Windows Count Method------------- */
    /* ------------------------------------------------- */

    public void isNumberOfWindowsToBe(int numberOfWindows, int... timeInSec) {
        ExpectedCondition<Boolean> condition = ExpectedConditions.numberOfWindowsToBe(numberOfWindows);
        try {
            validateStateOfElement(condition, timeInSec);
            log.info("Number of windows is '{}'", numberOfWindows);
        } catch (AssertionError e) {
            log.error("Number of windows is not '{}'. Exception: {}", numberOfWindows, e.getMessage());
            Assertions.fail("Number of windows is not '" + numberOfWindows + "'. Exception: " + e.getMessage());
        }
    }


    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *                       Element State Methods
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */


    public boolean isElementDisplayed(By element) {
        try {
            log.info("Checking if the element with locator: {} is displayed", element);
            boolean displayed = getDriver().findElement(element).isDisplayed();
            log.info("Element with locator: {} is displayed: {}", element, displayed);
            return displayed;
        } catch (Exception e) {
            log.warn("Element with locator: {} is not displayed: {}", element, e);
            return false;
        }
    }

    public boolean isElementEnabled(By element) {
        try {
            log.info("Checking if the element with locator: {} is enabled", element);
            boolean displayed = getDriver().findElement(element).isEnabled();
            log.info("Element with locator: {} is enabled: {}", element, displayed);
            return displayed;
        } catch (Exception e) {
            log.warn("Element with locator: {} is not enabled: {}", element, e);
            return false;
        }
    }

    public boolean isElementSelected(By element) {
        try {
            log.info("Checking if the element with locator: {} is selected", element);
            boolean displayed = getDriver().findElement(element).isSelected();
            log.info("Element with locator: {} is selected: {}", element, displayed);
            return displayed;
        } catch (Exception e) {
            log.warn("Element with locator: {} is not selected: {}", element, e);
            return false;
        }
    }


    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *                  Element Interaction Methods
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */

    /* ----------------Click Methods------------- */

    /**
     * Attempts to click on a web element identified by the given locator.
     *
     * @param element The locator (By) of the element to be clicked.
     */
    public void clickOnElement(By element) {
        try {
            log.info("Attempting to click on element with locator: {}", element);
            getDriver().findElement(element).click();
            log.info("Element with locator: {} has been clicked successfully.", element);
        } catch (Exception e) {
            log.error("Standard click failed on element: {}", element, e);
            Assertions.fail("Failed to click on element: " + element + ". Exception: " + e.getMessage());
        }
    }

    /* ----------------Send Keys Methods------------- */

    /**
     * Sends the specified text to the web element identified by the given locator.
     *
     * @param element The locator (By) of the element where the text will be sent.
     * @param text    The text to be sent to the element.
     */
    public void setText(By element, String text) {
        try {
            log.info("Setting text: '{}' to the element with locator: {}", text, element);
            getDriver().findElement(element).sendKeys(text);
            log.info("Text has been successfully sent to the element with locator: {}", element);
        } catch (Exception e) {
            log.error("Failed to send text to element: {}", element, e);
            Assertions.fail("Failed to send text to element: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void setTextAndPressEnterKey(By element, String text) {
        try {
            log.info("Setting text: '{}' to the element with locator: {} then press enter ", text, element);
            getDriver().findElement(element).sendKeys(text, Keys.ENTER);
            log.info("Text has been successfully sent to the element with locator: {} then enter key pressed", element);
        } catch (Exception e) {
            log.error("Failed to send text to element then press enter: {}", element, e);
            Assertions.fail("Failed to send text to element then press enter: " + element + ". Exception: " + e.getMessage());
        }
    }

    /* ----------------Clear Methods------------- */

    /**
     * Clears the text from the web element identified by the given locator.
     *
     * @param element The locator (By) of the element from which the text will be cleared.
     */
    public void clearText(By element) {
        try {
            log.info("Clearing text from the element with locator: {}", element);
            getDriver().findElement(element).clear();
            log.info("Text has been successfully cleared from the element with locator: {}", element);
        } catch (Exception e) {
            log.error("Failed to clear text of element: {}", element, e);
            Assertions.fail("Failed to clear text of element: " + element + ". Exception: " + e.getMessage());
        }
    }

    /**
     * Clears the text using backspace from the web element identified by the given locator.
     *
     * @param element The locator (By) of the element from which the text will be cleared.
     */
    public void clearTextWithBackSpace(By element) {
        try {
            log.info("Clearing text using backspace from the element with locator: {}", element);
            getDriver().findElement(element).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
            log.info("Text has been successfully cleared using backspace from the element with locator: {}", element);
        } catch (Exception e) {
            log.error("Failed to clear text using backspace of element: {}", element, e);
            Assertions.fail("Failed to clear text using backspace of element: " + element + ". Exception: " + e.getMessage());
        }
    }

    /* ----------------DropDown Methods------------- */

    public void isDropdownPopulated(By dropdownElement, By dropdownOptions , int... timeoutSec) {
        try {
            isElementPresent(dropdownElement, timeoutSec);
            areAllElementsPresent(dropdownOptions, timeoutSec);
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            boolean isPopulated = !select.getOptions().isEmpty();
            log.info("Dropdown identified by {} is {}populated.", dropdownElement, isPopulated ? "" : "not ");
        } catch (Exception e) {
            log.error("Failed to check if dropdown is populated: {}", e.getMessage());
            Assertions.fail("Failed to check if dropdown is populated: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void selectTextFromDropDown(By dropdownElement, String text) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.selectByVisibleText(text);
            log.info("Successfully selected text '{}' from dropdown.", text);
        } catch (Exception e) {
            log.error("The text '{}' is not found in the dropdown identified by {}.", text, dropdownElement);
            Assertions.fail("Failed to select text from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void selectValueFromDropDown(By dropdownElement, String value) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.selectByValue(value);
            log.info("Successfully selected value '{}' from dropdown.", value);
        } catch (Exception e) {
            log.error("The value '{}' is not found in the dropdown identified by {}.", value, dropdownElement);
            Assertions.fail("Failed to select value from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void selectIndexFromDropDown(By dropdownElement, int index) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.selectByIndex(index);
            log.info("Successfully selected index '{}' from dropdown.", index);
        } catch (Exception e) {
            log.error("The index '{}' is not found in the dropdown identified by {}.", index, dropdownElement);
            Assertions.fail("Failed to select index from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void deselectTextFromDropDown(By dropdownElement, String text) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.deselectByVisibleText(text);
            log.info("Successfully deselected text '{}' from dropdown.", text);
        } catch (Exception e) {
            log.error("The text '{}' is not found to deselect in the dropdown identified by {}.", text, dropdownElement);
            Assertions.fail("Failed to deselect text from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void deselectValueFromDropDown(By dropdownElement, String value) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.deselectByValue(value);
            log.info("Successfully deselected value '{}' from dropdown.", value);
        } catch (Exception e) {
            log.error("The value '{}' is not found to deselect in the dropdown identified by {}.", value, dropdownElement);
            Assertions.fail("Failed to deselect value from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void deselectIndexFromDropDown(By dropdownElement, int index) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.deselectByIndex(index);
            log.info("Successfully deselected index '{}' from dropdown.", index);
        } catch (Exception e) {
            log.error("The index '{}' is not found to deselect in the dropdown identified by {}.", index, dropdownElement);
            Assertions.fail("Failed to deselect index from dropdown of element: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public void deselectAllFromDropDown(By dropdownElement) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            select.deselectAll();
            log.info("Successfully deselected all from dropdown.");
        } catch (Exception e) {
            log.error("Failed to deselect all from the dropdown identified by {}.", dropdownElement);
            Assertions.fail("Failed to deselect all from the dropdown identified by: " + dropdownElement + ". Exception: " + e.getMessage());
        }
    }

    public WebElement getFirstSelectedOptionFromDropDown(By dropdownElement) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            WebElement selectedOptions = select.getFirstSelectedOption();
            log.info("Successfully retrieved first selected option from dropdown identified by {}.", dropdownElement);
            return selectedOptions;
        } catch (Exception e) {
            log.warn("Failed to retrieve first selected option from the dropdown identified by {}.", dropdownElement);
            return null;
        }
    }


    public List<WebElement> getAllSelectedOptionsFromDropDown(By dropdownElement) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            List<WebElement> selectedOptions = select.getAllSelectedOptions();
            log.info("Successfully retrieved all selected options from dropdown identified by {}.", dropdownElement);
            return selectedOptions;
        } catch (Exception e) {
            log.warn("Failed to retrieve selected options from the dropdown identified by {}.", dropdownElement);
            return null;
        }
    }

    public List<WebElement> getAllOptionsFromDropDown(By dropdownElement) {
        try {
            WebElement dropdown = getDriver().findElement(dropdownElement);
            Select select = new Select(dropdown);
            List<WebElement> selectedOptions = select.getOptions();
            log.info("Successfully retrieved all options from dropdown identified by {}.", dropdownElement);
            return selectedOptions;
        } catch (Exception e) {
            log.warn("Failed to retrieve all options from the dropdown identified by {}.", dropdownElement);
            return null;
        }
    }


    /* ----------------Get Methods------------- */
    
    /**
     * Retrieves the text of the specified WebElement.
     *
     * @param element The locator of the WebElement whose text is to be retrieved.
     * @return The text content of the WebElement, or an empty string if not found.
     * @throws IllegalArgumentException if the element locator is null.
     */
    public String getText(By element) {
        String text = "";
        try {
            log.info("Getting text of the element with locator: {}", element);
            text = getDriver().findElement(element).getText();
            log.info("Text has been successfully retrieved from the element with locator: {}", element);
        } catch (Exception e) {
            log.error("Failed to get text from the element: {}", element, e);
        }
        return text;
    }


    /**
     * Gets the value of the specified attribute from the element after waiting for it to be present.
     *
     * @param element       The locator of the element to get the attribute from.
     * @param attributeName The name of the attribute whose value is to be retrieved.
     * @return The value of the attribute.
     */
    public String getElementAttribute(By element, String attributeName) {
        try {
            return getDriver().findElement(element).getAttribute(attributeName);
        } catch (Exception e) {
            log.error("An error occurred while getting the attribute '{}' from the element with locator '{}': {}", attributeName, element, e.getMessage());
            return null;
        }
    }

    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *                    iFrame UTILITY METHODS
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */

    /**
     * Switches to an iframe based on the provided identifier, which can be an index (Integer),
     * name/ID (String), By, or WebElement. It validates the state of the iframe using the
     * validateStateOfElement method with optional timeout.
     *
     * @param iframeIdentifier The identifier of the iframe, can be a WebElement, By, String (name/ID), or Integer (index).
     * @param timeInSec        Optional timeout parameter, allows overriding default wait time.
     * @return true if successfully switched to the iframe, false otherwise.
     */
    public void switchToIframe(Object iframeIdentifier, int... timeInSec) {
        try {
            if (iframeIdentifier instanceof WebElement) {
                validateStateOfElement(ExpectedConditions.frameToBeAvailableAndSwitchToIt((WebElement) iframeIdentifier), timeInSec);
                log.info("Switched to iframe by WebElement");
            } else if (iframeIdentifier instanceof By) {
                validateStateOfElement(ExpectedConditions.frameToBeAvailableAndSwitchToIt((By) iframeIdentifier), timeInSec);
                log.info("Switched to iframe by By Locator: {}", iframeIdentifier);
            } else if (iframeIdentifier instanceof String) {
                validateStateOfElement(ExpectedConditions.frameToBeAvailableAndSwitchToIt((String) iframeIdentifier), timeInSec);
                log.info("Switched to iframe by name or ID: {}", iframeIdentifier);
            } else if (iframeIdentifier instanceof Integer) {
                validateStateOfElement(ExpectedConditions.frameToBeAvailableAndSwitchToIt((Integer) iframeIdentifier), timeInSec);
                log.info("Switched to iframe by index: {}", iframeIdentifier);
            }
        } catch (Exception e) {
            log.error("Failed to switch to iframe: {}", e.getMessage());
            Assertions.fail("Failed to switch to iframe: " + iframeIdentifier + ". Exception: " + e.getMessage());

        }
    }


    /**
     * Switches the driver back to the default frame (main content), validates the switch,
     * and logs the action. Asserts that the switch is successful by checking a known element
     * in the default content.
     */
    public void switchToDefaultContent() {
        try {
            getDriver().switchTo().defaultContent();
            log.info("Switched back to default content and validated the switch");
        } catch (Exception e) {
            log.error("Failed to switch back to default content: {}", e.getMessage());
            Assertions.fail("Exception occurred while switching back to default content: " + e.getMessage());
        }
    }

    /*
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     *                    ALERT HANDLING UTILITY METHODS
     ****************************************************************************
     ****************************************************************************
     ****************************************************************************
     */


    /**
     * Checks if an alert is currently present and fails the test if not.
     *
     * @return true if an alert is present, false otherwise.
     */
    public boolean isAlertPresent() {
        try {
            getDriver().switchTo().alert();
            log.info("Alert is present.");
            return true;
        } catch (NoAlertPresentException e) {
            log.warn("No alert present: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Accepts the current alert if it is present.
     * Fails the test if the alert cannot be accepted.
     */
    public void acceptAlert() {
        try {
            getDriver().switchTo().alert().accept();
            log.info("Alert accepted");
        } catch (Exception e) {
            log.error("Failed to accept alert", e);
            Assertions.fail("Failed to accept the alert: " + e.getMessage());
        }
    }


    /**
     * Dismisses the current alert if it is present.
     * Fails the test if the alert cannot be dismissed.
     */
    public void dismissAlert() {
        try {
            getDriver().switchTo().alert().dismiss();
            log.info("Alert dismissed");
        } catch (Exception e) {
            log.error("Failed to dismiss alert", e);
            Assertions.fail("Failed to dismiss the alert: " + e.getMessage());
        }
    }

    /**
     * Sends the specified text to the alert prompt after triggering it through the specified element,
     * and asserts that the alert was present before sending the text.
     *
     * @param element The locator of the element that triggers the alert.
     * @param text    The text to send to the alert.
     */
    public void sendKeysToAlert(By element, String text) {
        try {
            getDriver().findElement(element).click();
            Alert alert = getDriver().switchTo().alert();
            alert.sendKeys(text);
            log.info("Sent text to alert: {}", text);
        } catch (Exception e) {
            log.error("Failed to send text to alert ", e);
            Assertions.fail("Failed to send text to alert: " + e.getMessage());
        }
    }


    /**
     * Retrieves the text of the current alert, asserts that the alert was present,
     * and verifies that the text matches the expected value.
     *
     * @return The text of the alert if present; otherwise, a message indicating the alert is not present.
     */
    public String getAlertText() {
        try {
            Alert alert = getDriver().switchTo().alert();
            String alertText = alert.getText();
            log.info("Alert text is: {}", alertText);
            return alertText;
        } catch (Exception e) {
            log.warn("Failed to retrieve the alert text", e);
            return "Failed to retrieve alert text";
        }
    }
}


