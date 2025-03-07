package com.sauceLabs.common.ui.uiAutomation;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

/**
 * this class contains javascript scripts for handling web based application
 *
 * @author MahmoudOsama
 */
public class JSUtils extends BaseWebDriver {
    private static final int defaultTimeoutInSec = 30;

    /****************************
     * Wait Methods for Elements
     * *************************/

    /**
     * wait for page html to be loaded
     */
    public void waitDocumentReady(int ...timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime)).until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript("return document.readyState").toString().equals("complete");
        });
    }

    /**
     * Waits for the main document and all iframes to be fully loaded (ready state: complete).
     */
    public void waitDocumentReadyIncludingFrames(int ...timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        waitDocumentReady(waitTime);
        // Now, handle all iframes
        List<WebElement> frames = getDriver().findElements(By.tagName("iframe"));
        for (WebElement frame : frames) {
            // Switch to each iframe
            getDriver().switchTo().frame(frame);
            // Wait for the iframe's document to be fully loaded
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    return js.executeScript("return document.readyState").toString().equals("complete");
                }
            });
            // Switch back to the main document
            getDriver().switchTo().defaultContent();
        }
    }

    /**
     * wait till page requests are executed
     * This includes waiting for all asynchronous requests to complete and the page to reach a stable state.
     */
    public void waitForAjax(int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        waitDocumentReady(waitTime);
        if (executeScript(" return typeof(jQuery)=='undefined'", new Object[]{}).get(0).toString().equals("true"))
            return;
        new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                JavascriptExecutor js = (JavascriptExecutor) d;
                return (Boolean) js.executeScript("return typeof(jQuery)!='undefined' && jQuery.active == 0");
            }
        });
        log.info("session$>> {}", ((JavascriptExecutor) getDriver()).executeScript("return (typeof window.$.active) =='number' ? window.$.active : console.log('Undifiend')").toString());
    }

    /**
     * wait till page requests are executed
     * This includes waiting for all asynchronous requests to complete and the page to reach a stable state.
     */
    public void waitForAjaxIncludingFrames(int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        waitDocumentReadyIncludingFrames(waitTime);
        waitForAjax(waitTime);
        // Wait for AJAX requests in all iframes
        List<WebElement> frames = getDriver().findElements(By.tagName("iframe"));
        for (WebElement frame : frames) {
            getDriver().switchTo().frame(frame);
            if (executeScript("return typeof(jQuery)=='undefined'", new Object[]{}).get(0).toString().equals("true")) {
                getDriver().switchTo().defaultContent();
                continue;
            }
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    JavascriptExecutor js = (JavascriptExecutor) d;
                    return (Boolean) js.executeScript("return typeof(jQuery)!='undefined' && jQuery.active == 0");
                }
            });
            getDriver().switchTo().defaultContent();
        }
    }

    /**
     * wait till WebElement display status matches
     *
     * @param elem        WebElement object
     * @param isDisplayed display status
     */
    public void waitTillElemDisplayed(WebElement elem, Boolean isDisplayed) {
        JavascriptExecutor j = (JavascriptExecutor) getDriver();
        setJavaScriptVariable("elem", elem);
        log.info("Waiting for element to be {} Displayed: {}", isDisplayed, elem);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                if (!(isElementExist("elem") == isDisplayed)) {
                    return true;
                } else {
                    if (isElementExist("elem"))
                        return j.executeScript("return !!(arguments[0].offsetParent)", elem).toString().contains(isDisplayed.toString());
                    else return false;
                }
            }
        };
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(7));
        wait.until(expectation);
    }


    /**
     * wait till WebElement have attribute
     *
     * @param element          WebElement object
     * @param attribName attribute name
     */
    public void waitTillElementContainAttribute(WebElement element, String attribName, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        JavascriptExecutor j = (JavascriptExecutor) getDriver();
        log.info("Waiting for element to contain an attribute with name: {}", attribName);
        log.info("element Attribute before waiting are: {}", j.executeScript("return arguments[0].getAttributeNames()", element).toString());
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return j.executeScript("return arguments[0].getAttributeNames()", element).toString().contains(attribName);
            }
        };
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
            wait.until(expectation);
        } catch (TimeoutException timeoutException) {
            log.error("Timeout waiting for Page Load Request to complete.", timeoutException);
        } catch (Throwable error) {
            log.error("An unexpected error occurred while waiting for the element.", error);
        } finally {
            log.info("Element attributes after waiting: {}",
                    j.executeScript("return arguments[0].getAttributeNames()", element).toString());
        }
    }


    /**
     * wait for WebElement's attribute to contain a value
     *
     * @param webElement  WebElement Object
     * @param attribName  attribute name of the WebElement
     * @param attributeValue value of the attribute
     */
    public void waitForAttributeToContain(WebElement webElement, String attribName, String attributeValue, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        JavascriptExecutor j = (JavascriptExecutor) getDriver();
        log.info("Waiting for element attribute: {} to contain value: {}", attribName, attributeValue);
        log.info("element Attribute value before waiting is: {}", j.executeScript("return arguments[0].getAttribute(arguments[1])", webElement, attribName).toString());
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return j.executeScript("return arguments[0].getAttribute(arguments[1])", webElement, attribName).toString().contains(attributeValue);
            }
        };
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
            wait.until(expectation);
        } catch (TimeoutException e) {
            log.error("Timeout waiting for attribute '{}' to contain value '{}'.", attribName, attributeValue, e);
        } catch (Exception e) {
            log.error("An error occurred while waiting for the attribute '{}' to contain value '{}'.", attribName, attributeValue, e);
        } finally {
            String finalAttributeValue = (String) j.executeScript("return arguments[0].getAttribute(arguments[1])", webElement, attribName);
            log.info("Element attribute value after waiting: {}", finalAttributeValue);
        }
    }


    /**
     * wait till WebElement display and status matches
     *
     * @param elemContainer    container Element of WebElement
     * @param selector         css selector of WebElement
     * @param filter           add additional conditions to get WebElement using 'i' object as element
     * @param isCountMoreThan0 matched webElements if more than 0 then it's displayed
     * @param maxWaitSec       max waiting in seconds
     */
    public void waitTillElemDisplayedBySelector(WebElement elemContainer, String selector, String filter, Boolean isCountMoreThan0, int maxWaitSec) {
        String elemArg;
        if (elemContainer == null) {
            elemArg = "document";
        } else {
            elemArg = "arguments[0]";
        }
        if (Objects.equals(filter, "")) filter = "return true;";
        String sc = "return Array.prototype.slice.call(" + elemArg + ".querySelectorAll(arguments[1])).filter(function(i){" + filter + "}).length > 0";
        JavascriptExecutor j = (JavascriptExecutor) getDriver();
        log.info("Waiting for element count with selector>>>{}", selector);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return j.executeScript(sc, elemContainer, selector).toString().contains(isCountMoreThan0.toString());
            }
        };
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(maxWaitSec));
        wait.until(expectation);
    }

    /**
     * wait till WebElement with className and display status matches
     *
     * @param className     class name of WebElement
     * @param isDisplayed   displayed status
     */
    public void waitTillDisplayedByClassName(String className, Boolean isDisplayed, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        JavascriptExecutor j = (JavascriptExecutor) getDriver();
        log.info("Waiting for element with class {} to be {} Displayed", className, isDisplayed);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return j.executeScript("return !!(document.getElementsByClassName('" + className + "')[0].offsetParent)").toString().contains(isDisplayed.toString());
            }
        };
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime));
        wait.until(expectation);
    }


    /**
     * Waits for elements matching a CSS selector to be visible and returns them.
     *
     * @param elemContainer The parent element to search within.
     * @param selector      The CSS selector for the elements.
     * @param filter        Additional filter for selection.
     * @param maxWaitSec   Maximum wait time in seconds.
     * @return A list of matching WebElements.
     * @throws Error if the wait times out.
     */
    public List<WebElement> waitAndGetElem(WebElement elemContainer, String selector, String filter, int maxWaitSec) {
        try {
            waitTillElemDisplayedBySelector(elemContainer, selector, filter, true, maxWaitSec);
        } catch (TimeoutException e) {
            throw new Error("Timeout after waiting for '" + maxWaitSec + "' seconds for CSS '" + selector + "' and Filter '" + filter + "'");
        }
        return getElementsBySelectorFilter(elemContainer, selector, filter);
    }


    /*********************
     *   Check Elements
     * ******************/

    /**
     * get displayed status of webElement
     *
     * @param element WebElement object
     */
    public boolean isElementDisplayed(WebElement element) {
        boolean isDisplayed = false;
        log.info("checking if element is displayed on the page, elem:{}", element);
        String javaScript = "return !!(arguments[0].offsetParent)";
        String result = ((JavascriptExecutor) getDriver()).executeScript(javaScript, element).toString();
        if (result.equalsIgnoreCase("true"))
            isDisplayed = true;
        return isDisplayed;
    }


    /**
     * used after storing WebElement as js var using 'setJavaScriptVariable' method to check if element still exist to avoid element staleness
     *
     * @param varName stored var name
     * @return exist status
     */
    public boolean isElementExist(String varName) {
        String javaScript = "return document.body.contains(" + varName + ")";
        String result = ((JavascriptExecutor) getDriver()).executeScript(javaScript).toString();
        log.info("Check if var with name: {}, still attached to page dom-->{}", varName, result);
        return Boolean.parseBoolean(result);
    }


    /**
     * Check if a WebElement with a specific tag and text is displayed.
     *
     * @param tagName  The tag name of the element
     * @param elemText The innerText of the element
     * @return true if the element is displayed, false otherwise
     */
    public boolean isElemWithTagAndTextDisplayed(String tagName, String elemText) {
        // JavaScript to find elements by tag name and check if they match the given text
        String getElemScript = "var tagName = arguments[0]; " +
                "var textToMatch = arguments[1].toLowerCase(); " +
                "var elems = document.getElementsByTagName(tagName); " +
                "var matchedElems = []; " +
                "for (var i = 0; i < elems.length; i++) { " +
                "   var elem = elems[i]; " +
                "   if (elem.textContent.toLowerCase() === textToMatch && elem.offsetParent !== null) { " +
                "       matchedElems.push(elem); " +
                "   } " +
                "} " +
                "return matchedElems;";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        List<WebElement> matchingElements = (List<WebElement>) js.executeScript(getElemScript, tagName, elemText);
        return !matchingElements.isEmpty();
    }


    /**************************
     *  JS Actions on elements
     * **********************/

    /**
     * Single click on an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     */
    public void click(Object element) {
        WebElement webElement;
        if (element instanceof By) {
            webElement = getDriver().findElement((By) element);
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }
        String javaScript = "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initMouseEvent('click',true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                "arguments[0].focus(); " +
                "arguments[0].dispatchEvent(evObj);";
        ((JavascriptExecutor) getDriver()).executeScript(javaScript, webElement);
    }


    /**
     * Double click on an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     */
    public void doubleClick(Object element) {
        WebElement webElement;
        if (element instanceof By) {
            webElement = getDriver().findElement((By) element);
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }
        String doubleClickJS = "var evObj = new MouseEvent('dblclick', {bubbles: true, cancelable: true, view: window});";
        doubleClickJS += " arguments[0].dispatchEvent(evObj);";
        ((JavascriptExecutor) getDriver()).executeScript(doubleClickJS, webElement);
    }

    /**
     * Send text to an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     * @param text Text to be sent to the element
     */
    public void write(Object element, String text, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        WebElement webElement;
        if (element instanceof By) {
            webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated((By) element));
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOf(webElement));
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }
        String writeSc = "arguments[0].focus(); arguments[0].value = arguments[1];  " +
                "var evt = document.createEvent('Events'); " +
                "evt.initEvent('change', true, true); " +
                "arguments[0].dispatchEvent(evt); " +
                "evt.initEvent('input', true, true); " +
                "arguments[0].dispatchEvent(evt);";
        ((JavascriptExecutor) getDriver()).executeScript(writeSc, webElement, text);
    }

    /**
     * Fire mouseover event on an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     */
    public void mouseover(Object element, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        WebElement webElement;
        if (element instanceof By) {
            webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated((By) element));
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOf(webElement));
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }
        String javaScript = "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initMouseEvent('mouseover', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                "arguments[0].dispatchEvent(evObj);";
        ((JavascriptExecutor) getDriver()).executeScript(javaScript, webElement);
    }


    /**
     * Fire focus event on an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     */
    public void focus(Object element, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        WebElement webElement;
        if (element instanceof By) {
            webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated((By) element));
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOf(webElement));
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }

        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].focus()", webElement);
    }

    /**
     * Selects a value from a dropdown menu.
     *
     * @param selectElem The select WebElement representing the dropdown.
     * @param value      The value to be selected from the dropdown options.
     * @return          true if the value was successfully selected, false otherwise.
     */
    public boolean selectMenuByValue(WebElement selectElem, String value) {
        // Retrieve the options from the dropdown menu and filter by the specified value
        List<WebElement> options = getElementsBySelectorFilter(
                selectElem,
                "option",
                "if (i.innerText.toLowerCase() === '" + value.toLowerCase() + "') { " +
                        "i.closest('select').value = i.value; " +
                        "i.closest('select').dispatchEvent(new Event('change', { bubbles: true })); " +
                        "i.scrollIntoView(); return true; }"
        );
        // Return true if any option was found and selected
        return !options.isEmpty();
    }

    /**************************
     *      Get Methods
     * **********************/

    /**
     * Get text from an element (By or WebElement).
     *
     * @param element Either WebElement object or By locator
     * @return text from the element
     */
    public String getText(Object element, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        WebElement webElement;
        if (element instanceof By) {
            webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated((By) element));
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOf(webElement));
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }

        // Execute JavaScript to extract text
        return (String) ((JavascriptExecutor) getDriver()).executeScript(
                "var a = arguments[0]; if(a.firstChild && a.firstChild.nodeType === 3) return a.firstChild.wholeText; else return a.innerText;",
                webElement
        );
    }


    /**
     * get Button WebELement with the given text
     *
     * @param btnName Button text value
     * @return Button WebElement object
     */
    public List<WebElement> getButtonWithText(String btnName) {
        log.info("Getting button with text : {}", btnName);
        String f = "var text;if(i.tagName=='A' || i.tagName=='BUTTON') {text=i.innerText;}\n" +
                "else if(i.value){text=i.value;}" +
                "if(text&&text.replace(/ /g,'').toLowerCase()=='" + btnName + "'.replace(/ /g,'').toLowerCase() && i.offsetParent && i.getAttribute('disabled')==null)return true";
        waitTillElemDisplayedBySelector(null, "a[href], button, input[type]", "if( i.offsetParent)return true", true, 10);
        waitForAjax();
        List<WebElement> buttons = getElementsBySelectorFilter(null, "a, button, input[type=button], input[type=submit], input[type=reset]", f);
        log.info("Total count of displayed buttons with text: {} : {}", btnName, buttons.size());
        return buttons;
    }


    /**
     * handling to get element if it exist inside shadow root element
     *
     * @param cssElemSelector css selector for element
     * @return Array of matched elements
     */
    public List<Object> getElemFromShadowRoot(String cssElemSelector) {
        String sc = "var target= new Array();\n" +
                "var cssSelector = \"" + cssElemSelector + "\";\n" +
                "function getInnerElemWithShadow(elem){\n" +
                " \t if ( elem.shadowRoot){\n" +
                " \t\t return elem.shadowRoot;  }\n" +
                "\t else{return Array.prototype.slice.call(elem.childNodes).filter(function(i){if(i.shadowRoot != null)return true;});}}\n" +
                "function srch(activeElem){  \n" +
                "\tvar t = activeElem.querySelector(cssSelector);\n" +
                "  if ( t == null){\n" +
                "\t activeElem.childNodes.forEach(function(j){\n" +
                "\t var a = getInnerElemWithShadow(j);\n" +
                "\t if(a instanceof Array ){\n" +
                "\t Array.prototype.slice.call(a).filter(function(i){return srch(i);}).length>0; \n" +
                "\t }\n" +
                "\t \t else srch(a);\n" +
                "\t }); \n" +
                "}\n" +
                " else{  target.push(t); }\n" +
                "}\n" +
                "srch(document.body); return target;";
        return executeScript(sc, new Object[]{});
    }


    /**
     * Get all attribute names of a WebElement.
     *
     * @param webElement WebElement object
     * @return List of attribute names
     */
    public List<String> getElementAttributesName(WebElement webElement) {
        String javaScript = "return arguments[0].getAttributeNames()";
        List<String> atts = (List<String>) ((JavascriptExecutor) getDriver()).executeScript(javaScript, webElement);
        log.info("Element attributes are: {}", atts);
        return atts;
    }


    /**
     * get all input elements exists under the parenElem which match the given conditions
     *
     * @param parentElem               Parent element if needed
     * @param type                     input type attribute value
     * @param getDisabled              whether to get disabled elements or not
     * @param additionalElemConditions for additional conditions use 'And' then use 'elem' var. Ex:" && elem.getAttribute('value')==0"
     * @return List of matched Input webelements
     */
    public List<WebElement> getActiveInputElements(WebElement parentElem, String type, boolean getDisabled, String additionalElemConditions) {String disabledStatus = null;
        if (getDisabled) {
            disabledStatus = "'disabled'";
        }
        String script = "var parentElem = arguments[0];\n" +
                "var inputElems = document.getElementsByTagName('input');\n" +
                "if(parentElem != null){\n" +
                "inputElems = parentElem.getElementsByTagName('input');\n" +
                "}\n" +
                "var matchedElem = new Array();\n" +
                "function getElems(elem){ \n" +
                "if(elem.getAttribute('type')=='" + type + "' && elem.getAttribute('disabled')==" + disabledStatus + " && elem.offsetParent " + additionalElemConditions + " ){\n" +
                "\tmatchedElem.push(elem) ; }\n" +
                "}\n" +
                "[].forEach.call(inputElems, getElems); \n" +
                "return matchedElem;";
        log.info("Running Javascript : {} ", script);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        List<WebElement> buttons = (List<WebElement>) js.executeScript(script, parentElem);
        log.info("Total number input elements with disabled attribute>>{} and type>>{}: {}", disabledStatus, type, buttons.size());
        return buttons;
    }

    /**
     * Retrieves the parent WebElement of the specified child WebElement based on a CSS selector.
     *
     * @param elem     The child WebElement whose parent is to be found.
     * @param selector The CSS selector to match the parent WebElement.
     * @return        The matched parent WebElement, or null if not found.
     */
    public WebElement getParentElem(WebElement elem, String selector) {
        // JavaScript to traverse up the DOM and find the matching parent element
        String getParentScript = "for (; arguments[0]; arguments[0] = arguments[0].parentNode) { " +
                "if (arguments[0] === document) { arguments[0] = undefined; break; } " +
                "else if (arguments[0].matches(arguments[1])) { break; } } " +
                "return arguments[0];";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        WebElement parentElement = (WebElement) js.executeScript(getParentScript, elem, selector);
        // Log a message if the parent element could not be found
        if (parentElement == null) {
            log.info("Couldn't get Parent Element with selector: {}", selector);
        }
        return parentElement;
    }


    /**
     * get WebElements using css selector and filter conditions
     *
     * @param elemContainer parent WebElement if exit
     * @param selector      css selector which match the desired WebElements
     * @param filter        additional condition for webElements is existed using 'i' as var and return true if conditions matched
     * @return list of matched WebElements
     */
    public List<WebElement> getElementsBySelectorFilter(WebElement elemContainer, String selector, String filter) {
        String elemArg;
        if (elemContainer == null) {
            elemArg = "document";
        } else {
            elemArg = "arguments[0]";
        }
        if (Objects.equals(filter, "")) filter = "return true;";
        String getParentScript = "return Array.prototype.slice.call(" + elemArg + ".querySelectorAll(arguments[1])).filter(function(i){" + filter + "})";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        List<WebElement> elements;
        elements = (List<WebElement>) js.executeScript(getParentScript, elemContainer, selector);
        log.info("Elements Count is:{} ,with selector>>>{}", elements.size(), selector);
        return elements;
    }

    /*********************
     *  Scrolls Methods
     * ******************/

    /**
     * Scrolls the page to bring the specified element into view.
     *
     * @param element The locator of the element to scroll into view.
     */
    public void scrollToElement(By element, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        log.info("Scrolling to the element with locator '{}'.", element);
        try {
            WebElement webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", webElement);
            log.info("Successfully scrolled to the element with locator '{}'.", element);
        } catch (TimeoutException e) {
            log.error("Element with locator '{}' was not found or not visible within the timeout period.", element, e);
        } catch (Exception e) {
            log.error("An error occurred while scrolling to the element with locator '{}': {}", element, e.getMessage());
        }
    }

    /**
     * Scrolls the window up by 2500 pixels.
     */
    public void scrollUp() {
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollBy(0,-2500)");
    }

    /**
     * Scrolls the window down to the bottom of the page.
     */
    public void scrollDown() {
        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }


    /************************
     *        Scripts
     * *********************/

    /**
     * Executes JavaScript scripts in the context of the current WebDriver session.
     *
     * @param script The JavaScript code to execute.
     * @param args   The arguments to pass to the script.
     * @return A list containing the result of the script execution.
     */
    public List<Object> executeScript(String script, Object[] args) {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        List<Object> result = new ArrayList<>();
        log.info("Executing script: {}, with args size: {}", script, args.length);
        Object executionResult = executor.executeScript(script, args);
        if (executionResult == null) {
            return result;
        } else if (executionResult instanceof List) {
            return (List<Object>) executionResult;
        } else if (executionResult.getClass().isArray()) {
            return Arrays.asList((Object[]) executionResult);
        } else {
            result.add(executionResult);
            return result;
        }
    }

    /**
     * store WebElement as JS var to avoid element staleness
     *
     * @param varName  js var name
     * @param varValue webElement object
     */
    public void setJavaScriptVariable(String varName, Object varValue) {
        log.info("Creating Javascript variable with name: {}", varName);
        String javaScript = "window." + varName + " = arguments[0];";
        ((JavascriptExecutor) getDriver()).executeScript(javaScript, varValue);
    }


    /**
     * navigate to URL
     *
     * @param url website url
     */
    public void navigateToUrl(String url) {
        executeScript("window.location.href = '" + url + "';", new Object[]{});
        waitDocumentReady();
        waitTillElemDisplayedBySelector(null, "body *", "if(i.offsetParent)return true", true, 20);
    }

    /************************
     *        Actions
     * *********************/

    public void doubleClickOnElement(By element) {
        try {
            log.info("Attempting to double-click on element with locator: {}", element);
            WebElement webElement = getDriver().findElement(element);
            Actions actions = new Actions(getDriver());
            actions.doubleClick(webElement).perform();
            log.info("Element with locator: {} has been double-clicked successfully.", element);
        } catch (Exception e) {
            log.error("Double-click failed on element: {}", element, e);
            Assertions.fail("Failed to double-click on element: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void clickAndHoldOnElement(By element) {
        try {
            log.info("Attempting to click and hold on element with locator: {}", element);
            WebElement webElement = getDriver().findElement(element);
            Actions actions = new Actions(getDriver());
            actions.clickAndHold(webElement).perform();
            log.info("Element with locator: {} has been clicked and held successfully.", element);
        } catch (Exception e) {
            log.error("Click and hold failed on element: {}", element, e);
            Assertions.fail("Failed to click and hold on element: " + element + ". Exception: " + e.getMessage());
        }
    }

    public void moveToElement(Object element, int... timeoutSec) {
        int waitTime = (timeoutSec.length > 0 && timeoutSec[0] >= 0) ? timeoutSec[0] : defaultTimeoutInSec;
        WebElement webElement;
        if (element instanceof By) {
            webElement = new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOfElementLocated((By) element));
        } else if (element instanceof WebElement) {
            webElement = (WebElement) element;
            new WebDriverWait(getDriver(), Duration.ofSeconds(waitTime))
                    .until(ExpectedConditions.visibilityOf(webElement));
        } else {
            throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        }
        Actions actions = new Actions(getDriver());
        actions.moveToElement(webElement).perform();
    }

    public void dragAndDropElement(By sourceElement, By targetElement) {
        try {
            log.info("Attempting to drag and drop from source element to target element.");
            WebElement source = getDriver().findElement(sourceElement);
            WebElement target = getDriver().findElement(targetElement);
            Actions actions = new Actions(getDriver());
            actions.dragAndDrop(source, target).perform();
            log.info("Successfully performed drag and drop.");
        } catch (Exception e) {
            log.error("Drag and drop failed.", e);
            Assertions.fail("Failed to drag and drop. Exception: " + e.getMessage());
        }
    }
}
