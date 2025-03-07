package com.sauceLabs.common.ui.uiAutomation;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.utils.helpers.DataList;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Cookie;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



/**
 * this class contains utilities for interacting with browser window
 *
 * @author MahmoudOsama
 */
public class BrowserWinUtils extends BaseWebDriver {
    private static ThreadLocal<DataList> tabs = new ThreadLocal<DataList>() {
        @Override
        public DataList initialValue() {
            return new DataList();
        }
    };
    private final JSUtils jsUtils = new JSUtils();


    /**
     * Method to quit the current WebDriver instance and close the browser.
     * Ensures that the WebDriver session is properly terminated.
     */
    public void quitBrowser() {
        try {
            getDriver().quit();
            log.info("Browser session quit successfully.");
        } catch (Exception e) {
            log.error("Failed to quit the browser session.", e);
            Assertions.fail("Failed to quit the browser session.");
        }
    }


    /**
     * Method to navigate to any URL.
     *
     * @param url the URL to navigate to.
     */
    public void navigateToUrl(String url) {
        try {
            getDriver().navigate().to(url);
            log.info("Navigated to URL: {}", url);
        } catch (Exception e) {
            log.error("Failed to navigate to URL: {}", url, e);
            Assertions.fail("Failed to navigate to URL: " + url);
        }
    }


    /**
     * Method to refresh the current page and wait for the document to be fully loaded.
     */
    public void refreshPage() {
        try {
            getDriver().navigate().refresh();
            log.info("Page refreshed successfully.");
            jsUtils.waitDocumentReady();
            log.info("Document is fully loaded after refresh.");
        } catch (Exception e) {
            log.error("Failed to refresh the page.", e);
            Assertions.fail("Failed to refresh the page: " + e.getMessage());
        }
    }

    /**
     * Method to navigate to the previous page in the browser history and wait for the document to be fully loaded.
     */
    public void navigateBack() {
        try {
            getDriver().navigate().back();
            log.info("Navigated back to the previous page successfully.");
            jsUtils.waitDocumentReady();
            log.info("Document is fully loaded after navigating back.");
        } catch (Exception e) {
            log.error("Failed to navigate back.", e);
            Assertions.fail("Failed to navigate back: " + e.getMessage());
        }
    }


    /**
     * Method to navigate to the next page in the browser history and wait for the document to be fully loaded.
     */
    public void navigateForward() {
        try {
            getDriver().navigate().forward();
            log.info("Navigated forward to the next page successfully.");
            jsUtils.waitDocumentReady();
            log.info("Document is fully loaded after navigating forward.");
        } catch (Exception e) {
            log.error("Failed to navigate forward.", e);
            Assertions.fail("Failed to navigate forward: " + e.getMessage());

        }
    }


    /**
     * Method to delete all cookies from the current WebDriver session.
     * This can be useful for clearing session data between tests or interactions.
     */
    public void deleteAllCookies() {
        try {
            getDriver().manage().deleteAllCookies();
            log.info("All cookies deleted successfully.");
        } catch (Exception e) {
            log.error("Failed to delete cookies.", e);
            Assertions.fail("Failed to delete cookies: " + e.getMessage());
        }
    }

    /**
     * Method to delete a cookie by its name from the current WebDriver session.
     *
     * @param cookieName The name of the cookie to delete.
     */
    public void deleteCookieNamed(String cookieName) {
        try {
            getDriver().manage().deleteCookieNamed(cookieName);
            log.info("Cookie deleted successfully: {}", cookieName);
        } catch (Exception e) {
            log.error("Failed to delete cookie named: {}", cookieName, e);
            Assertions.fail("Failed to delete cookie named: " + cookieName);

        }
    }

    /**
     * Method to add a new cookie to the current WebDriver session.
     *
     * @param cookieName  The name of the cookie to add.
     * @param cookieValue The value of the cookie to add.
     */
    public void addCookie(String cookieName, String cookieValue) {
        try {
            Cookie cookie = new Cookie(cookieName, cookieValue);
            getDriver().manage().addCookie(cookie);
            log.info("Cookie added successfully: {}={}", cookieName, cookieValue);
        } catch (Exception e) {
            log.error("Failed to add cookie: {}={}", cookieName, cookieValue, e);
            Assertions.fail("Failed to add cookie: " + cookieName);
        }
    }

    /**
     * Method to retrieve a cookie by its name.
     *
     * @param cookieName The name of the cookie to retrieve.
     * @return The Cookie object if found, or null if not found.
     */
    public Cookie getCookieNamed(String cookieName) {
        try {
            Cookie cookie = getDriver().manage().getCookieNamed(cookieName);
            if (cookie != null) {
                log.info("Cookie retrieved: {}={}", cookie.getName(), cookie.getValue());
            } else {
                log.info("Cookie with name {} not found.", cookieName);
            }
            return cookie;
        } catch (Exception e) {
            log.error("Failed to retrieve cookie named: {}", cookieName, e);
            return null;
        }
    }

    /**
     * Method to retrieve all cookies from the current WebDriver session.
     *
     * @return A Set of Cookie objects.
     */
    public Set<Cookie> getAllCookies() {
        try {
            Set<Cookie> cookies = getDriver().manage().getCookies();
            log.info("Retrieved {} cookies from the session.", cookies.size());
            return cookies;
        } catch (Exception e) {
            log.error("Failed to retrieve cookies.", e);
            return Collections.emptySet();
        }
    }


    /**
     * Method to update a cookie's value by its name.
     *
     * @param cookieName  The name of the cookie to update.
     * @param cookieValue The new value for the cookie.
     */
    public void updateCookie(String cookieName, String cookieValue) {
        try {
            deleteCookieNamed(cookieName);  // First, delete the existing cookie
            addCookie(cookieName, cookieValue);  // Then, add a new cookie with the updated value
            log.info("Cookie updated successfully: {}={}", cookieName, cookieValue);
        } catch (Exception e) {
            log.error("Failed to update cookie: {}={}", cookieName, cookieValue, e);
            Assertions.fail("Failed to update cookie: " + cookieName);

        }
    }


    /**
     * Method to maximize the current browser window.
     * Ensures that the window is expanded to its maximum size.
     */
    public void maximizeWindow() {
        try {
            getDriver().manage().window().maximize();
            log.info("Browser window maximized successfully.");
        } catch (Exception e) {
            log.error("Failed to maximize the browser window.", e);
            Assertions.fail("Failed to maximize the browser window: " + e.getMessage());
        }
    }

    /**
     * Method to minimize the current browser window.
     * Minimizes the window, typically to the taskbar (on most systems).
     */
    public void minimizeWindow() {
        try {
            getDriver().manage().window().minimize();
            log.info("Browser window minimized successfully.");
        } catch (Exception e) {
            log.error("Failed to minimize the browser window.", e);
            Assertions.fail("Failed to minimize the browser window: " + e.getMessage());
        }
    }

    /**
     * Retrieves the current zoom percentage of the browser window.
     * <p>
     * This method uses the executeScript utility to get the zoom level from the browser
     * and includes an assertion to validate the retrieved zoom level.
     *
     * @param expectedZoom The expected zoom percentage to assert against.
     * @return The current zoom percentage as a String (e.g., "100" for 100%).
     */
    public String getZoomPercentage(String expectedZoom) {
        try {
            String script = "return Math.round(window.devicePixelRatio * 100);";
            List<Object> results = jsUtils.executeScript(script, new Object[]{});
            // Handle valid result
            if (!results.isEmpty() && results.get(0) instanceof Long zoomLevel) {
                String zoomLevelStr = zoomLevel.toString();
                log.info("Current browser zoom level is {}%", zoomLevelStr);
                Assertions.assertNotEquals( expectedZoom, zoomLevelStr,"Zoom level does not match the expected value");
                return zoomLevelStr;
            }
            // If the result is not valid, directly throw an exception
            throw new RuntimeException("Failed to retrieve a valid zoom percentage. Results are either empty or of an unexpected type.");
        } catch (Exception e) {
            log.error("Failed to retrieve the browser zoom percentage", e);
            Assertions.fail("Exception occurred while retrieving the zoom percentage.");
            return "Unknown";
        }
    }


    /**
     * Toggles the browser window between full-screen mode and normal mode based on the provided flag.
     *
     * @param enterFullScreen If true, the method will attempt to enter full-screen mode.
     *                        If false, the method will attempt to exit full-screen mode.
     */
    public void toggleFullScreen(boolean enterFullScreen) {
        try {
            String checkFullScreenScript = "return document.fullscreenElement != null;";
            List<Object> results = jsUtils.executeScript(checkFullScreenScript, new Object[]{});
            boolean isFullScreenBefore = !results.isEmpty() && Boolean.TRUE.equals(results.get(0));
            if (enterFullScreen) {
                if (!isFullScreenBefore) {
                    // Enter full-screen mode
                    jsUtils.executeScript("document.documentElement.requestFullscreen();", new Object[]{});
                    log.info("Entering full-screen mode");
                    // Assert that the browser has entered full-screen mode
                    results = jsUtils.executeScript(checkFullScreenScript, new Object[]{});
                    Assertions.assertEquals(Boolean.TRUE, results.get(0),"Browser should be in full-screen mode");
                } else {
                    log.info("Browser is already in full-screen mode");
                }
            } else {
                if (isFullScreenBefore) {
                    // Exit full-screen mode
                    jsUtils.executeScript("document.exitFullscreen();", new Object[]{});
                    log.info("Exiting full-screen mode");
                    // Assert that the browser has exited full-screen mode
                    results = jsUtils.executeScript(checkFullScreenScript, new Object[]{});
                    Assertions.assertNotEquals(Boolean.TRUE, results.get(0).toString(),"Browser should have exited full-screen mode");
                } else {
                    log.info("Browser is not in full-screen mode");
                }
            }
        } catch (Exception e) {
            log.error("Failed to toggle full-screen mode", e);
            Assertions.fail("Exception occurred while toggling full-screen mode.");
        }
    }


    /**
     * Sets the zoom level of the page by modifying the document's body style.
     * Note: This approach works in most modern browsers, but the `zoom` property is not a standard CSS property.
     *
     * @param percentage Zoom percentage (e.g., "70%").
     */
    public void setZoomPercentage(String percentage) {
        jsUtils.executeScript("if(document.body && typeof (document.body.style.zoom) != undefined) document.body.style.zoom = '" + percentage + "'", new Object[]{});
    }


    /**
     * open new tab then nav to url
     *
     * @param site website URL
     * @return tab object
     */
    public String openUrlNewTab(String site) {
        log.info("Opening URL>> {} in a new empty tab.", site);
        tabs.get().addItem(getDriver().getWindowHandle());
        String next_tab = "";
        openNewEmptyTab();
        Iterator<String> tabsArr = getDriver().getWindowHandles().iterator();
        while (tabsArr.hasNext()) {
            next_tab = tabsArr.next();
            if (!tabs.get().getList().contains(next_tab)) {
                getDriver().switchTo().window(next_tab);
                jsUtils.navigateToUrl(site);
            }
        }
        return next_tab;
    }

    /**
     * Switches to the last opened browser tab.
     */
    public void moveLastTab() {
        Set<String> windowHandles = getDriver().getWindowHandles();
        if (windowHandles.size() > 1) {
            String lastHandle = null;
            for (String handle : windowHandles) {
                lastHandle = handle;
            }
            if (lastHandle != null) {
                getDriver().switchTo().window(lastHandle);
                log.info("Switched to the last tab with URL: {}", getDriver().getCurrentUrl());
            } else {
                log.warn("No valid tab handle found.");
            }
        } else {
            log.info("Only one tab is open. No need to switch.");
        }
    }


    /**
     * used to close current tab then navigate to Previous tab
     */
    public void movePreviousTab() {
        log.info("New tabs count: {}", tabs.get().getList().size());
        if (tabs.get().getList().isEmpty())
            getDriver().getWindowHandles().forEach(e -> {
                tabs.get().addItem(e);
            });
        getDriver().close();
        String previousTab = tabs.get().getItem(tabs.get().getList().size() - 1);
        getDriver().switchTo().window(previousTab);
        tabs.get().removeItem(previousTab);
        log.info("Moving to Previous Tab with Url: {}", getDriver().getCurrentUrl());
    }

    /**
     * used to open an empty tab
     */
    public void openNewEmptyTab() {
        log.info("Opening a new empty tab.");
        // add original tab
        tabs.get().addItem(getDriver().getWindowHandle());
        // handling chrome bug
        String err = (String) jsUtils.executeScript("return document.URL", new Object[]{}).get(0);
        if (err.contains("chrome-error:"))
            jsUtils.executeScript(" window.history.go(-1); setTimeout(function(){window.history.go(1)}, 500);", new Object[]{});
        jsUtils.executeScript("document.open( 'about:blank','_blank' , '')", new Object[]{});
    }

    /**
     * Closes tabs with URLs that contain the specified host and then returns to the original tab.
     *
     * @param host The host value to match in the tab URLs.
     */
    public void closeTabsWithHost(String host) {
        String originalTabHandle = getDriver().getWindowHandle();
        String currentUrl = getDriver().getCurrentUrl();
        log.info("Closing tabs with host: {}", host);
        for (String tabHandle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(tabHandle);
            jsUtils.waitDocumentReady();
            if (getDriver().getCurrentUrl().contains(host)) {
                jsUtils.executeScript("setTimeout(()=>{window.open('','_self').close()}, 10)", new Object[]{});
            }
        }
        if (!currentUrl.contains(host)) {
            getDriver().switchTo().window(originalTabHandle);
        }
    }


    /**
     * navigate to tab which it's url contain urlHost value
     *
     * @param urlHost tab url host
     */
    public void navToTabWithHost(String urlHost) {
        tabs.get().addItem(getDriver().getWindowHandle());
        Set<String> tabs = getDriver().getWindowHandles();
        for (String t : tabs) {
            jsUtils.waitDocumentReady();
            if (getDriver().getCurrentUrl().contains(urlHost)) {
                log.info("Navigating to tab with URL: {}", getDriver().getCurrentUrl());
                break;
            }
            getDriver().switchTo().window(t);
        }
        if (!getDriver().getCurrentUrl().contains(urlHost))
            throw new Error("Couldn't navigate to tab which url contains>>" + urlHost);
    }

}
