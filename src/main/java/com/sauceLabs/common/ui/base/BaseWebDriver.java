package com.sauceLabs.common.ui.base;

import com.sauceLabs.common.utils.logs.MyLogger;
import com.sauceLabs.common.utils.properties.PropertiesManager;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.logging.log4j.core.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Collections;
import java.util.HashMap;

/**
 * This is a base driver class used for managing WebDriver instances.
 * Extend this class and use 'getDriver' to access the WebDriver object.
 * This class supports configuration for Chrome and Firefox browsers.
 *
 * @author Mahmoud Osama
 */
public class BaseWebDriver {
    public static Logger log = new MyLogger().getLogger();
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private final PropertiesManager propertiesManager = new PropertiesManager();

    /**
     * Retrieves the current WebDriver instance.
     * Logs a message if the WebDriver has not been initialized.
     *
     * @return The current WebDriver instance, or null if it has not been initialized.
     */
    public WebDriver getDriver() {
        WebDriver currentDriver = driver.get();
        if (currentDriver == null) {
            log.info("WebDriver has not been initialized.");
        }
        return currentDriver;
    }

    /**
     * Checks if the browser is available and opened.
     *
     * @return true if the browser is available and active, false otherwise.
     */
    public boolean isDriverActive() {
        WebDriver currentDriver = getDriver();

        if (currentDriver == null || currentDriver.toString().contains("(null)")) {
            return false;
        }

        try {
            currentDriver.getWindowHandle();
        } catch (NoSuchSessionException | NoSuchWindowException e) {
            log.info("Session or window not found: {}", e.getMessage());
            return false;
        } catch (WebDriverException e) {
            if (e.getMessage().contains("not reachable") || e.getMessage().contains("localhost")) {
                log.info("WebDriver exception: {}", e.getMessage());
                return false;
            } else {
                log.error("Unexpected WebDriver exception: ", e);
            }
        }
        return true;
    }


    /**
     * Quits the WebDriver instance and removes it from the thread-local storage.
     * Logs a message when the WebDriver is quitting.
     */
    public void quitAndRemoveDriver() {
        if (isDriverActive()) {
            log.info("Quitting WebDriver instance.");
            getDriver().quit();
        } else {
            log.warn("WebDriver instance is not active. No need to quit.");
        }
        driver.remove();
    }


    /**
     * Opens the specified browser based on the operating system.
     * This method determines the current operating system and then calls the appropriate method
     * to open the specified browser. If the OS is Windows, it calls `openWindowsBrowser()`.
     * For Unix-based systems, it calls `openUnixBrowser()`.
     *
     * @param browserName The name of the browser to open (e.g., "firefox", "chrome").
     * @return The WebDriver instance for the opened browser.
     */
    public WebDriver openBrowser(String browserName) {
        log.info("Opening {} browser", browserName);
        String localOS = System.getProperty("os.name").toLowerCase();

        if (localOS.contains("windows")) {
            openWindowsBrowser(browserName);
        } else {
            openUnixBrowser(browserName);
        }
        if (!isDriverActive()) {
            log.error("Failed to open {} browser on {} OS.", browserName, localOS);
            throw new WebDriverException("Failed to initialize WebDriver.");
        }
        return driver.get();
    }


    /**
     * Opens the specified browser on a Windows OS.
     * This method uses WebDriverManager to set up the browser drivers and start the specified browser.
     *
     * @param browserName The name of the browser to open (e.g., "firefox", "chrome", "edge").
     * @throws IllegalArgumentException if the specified browser is not supported.
     */
    private void openWindowsBrowser(String browserName) {
        switch (browserName.toLowerCase()) {
            case "firefox":
                log.info("Setting up Firefox browser");
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up Firefox WebDriver.");
                    WebDriverManager.firefoxdriver().setup();
                }
                setDriverConfiguration("firefox");
                break;
            case "chrome":
                log.info("Setting up Chrome browser");
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up Chrome WebDriver.");
                    WebDriverManager.chromedriver().setup();
                }
                setDriverConfiguration("chrome");
                break;
            case "edge":
                log.info("Setting up Edge browser");
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up Edge WebDriver.");
                    WebDriverManager.edgedriver().setup();
                }
                setDriverConfiguration("edge");
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }


    /**
     * Opens the specified browser on a Unix-based OS.
     * This method checks the specified browser name and sets up the appropriate WebDriver.
     * If the browser is not recognized or not found, it throws an error.
     *
     * @param browserName The name of the browser to open (e.g., "firefox", "chrome", "edge").
     * @throws Error if the specified browser is not installed or not found.
     */
    private void openUnixBrowser(String browserName) {
        if (browserName.equalsIgnoreCase("firefox")) {
            log.info("Setting up firefox browser on Unix");
            if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                log.info("WebDriverManager is enabled. Setting up firefox WebDriver on Unix.");
                setWebDriverOnUnix(WebDriverManager.firefoxdriver());
            }
            setDriverConfiguration("firefox");
        } else if (browserName.equalsIgnoreCase("chrome")) {
            if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                log.info("WebDriverManager is enabled. Setting up chrome WebDriver on Unix.");
                setWebDriverOnUnix(WebDriverManager.chromedriver());
            }
            log.info("Setting up chrome browser on Unix");
            setDriverConfiguration("chrome");
        } else if (browserName.equalsIgnoreCase("edge")) {
            if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                log.info("WebDriverManager is enabled. Setting up edge WebDriver on Unix.");
                setWebDriverOnUnix(WebDriverManager.edgedriver());
            }
            log.info("Setting up edge browser on Unix");
            setDriverConfiguration("edge");
        } else {
            throw new Error("Given browser name either not installed or not found!!!");
        }
    }


    /**
     * Sets up the WebDriver on a Unix-based system with proxy settings.
     *
     * @param webDriverManager The WebDriverManager instance for the specific browser.
     */
    private void setWebDriverOnUnix(WebDriverManager webDriverManager) {
        String userName = propertiesManager.getProp("VPN.Proxy.Local.Username");
        String passWord = propertiesManager.getProp("VPN.Proxy.Local.Password");
        String host = propertiesManager.getProp("VPN.Proxy.Host");
        String port = propertiesManager.getProp("VPN.Proxy.Port");

        if (passWord != null && !passWord.isEmpty()) {
            webDriverManager.proxyUser(userName)
                    .proxyPass(passWord)
                    .proxy("http://" + host + ":" + port)
                    .proxy("https://" + host + ":" + port)
                    .setup();
        } else {
            log.info("WebDriver setup without proxy authentication completed.");
            webDriverManager.setup();
        }
    }

    /**
     * Configures and sets up the WebDriver based on the specified browser name.
     * This method sets desired capabilities, proxy settings, browser options, and initializes the WebDriver.
     *
     * @param browserName The name of the browser to configure (e.g., "firefox", "chrome", "edge").
     * @return The configured WebDriver instance.
     */
    private WebDriver setDriverConfiguration(String browserName) {
        WebDriver webDriver;
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Proxy proxy = new Proxy();
        proxy.setProxyAutoconfigUrl(propertiesManager.getProp("Pac.File.Proxy"));
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        switch (browserName.toLowerCase()) {
            case "firefox":
                log.info("Starting Firefox Browser");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.merge(capabilities);
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up proxy for firefox.");
                    firefoxOptions.setProxy(proxy);
                }
                setBrowserPreferences(firefoxOptions, browserName);
                webDriver = new FirefoxDriver(firefoxOptions);
                log.info("Firefox Opened Successfully");
                break;
            case "chrome":
                log.info("Starting Chrome Browser");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.merge(capabilities);
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up proxy for chrome.");
                    chromeOptions.setProxy(proxy);
                }
                setBrowserPreferences(chromeOptions, browserName);
                webDriver = new ChromeDriver(chromeOptions);
                log.info("Chrome Opened Successfully");
                break;
            case "edge":
                log.info("Starting Edge Browser");
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.merge(capabilities);
                if (Boolean.parseBoolean(propertiesManager.getProp("WebDriverManager"))) {
                    log.info("WebDriverManager is enabled. Setting up proxy for edge.");
                    edgeOptions.setProxy(proxy);
                }
                setBrowserPreferences(edgeOptions, browserName);
                webDriver = new EdgeDriver(edgeOptions);
                log.info("Edge Opened Successfully");
                break;
            default:
                log.info("Browser not specified or unsupported. Using Chrome as default browser.");
                ChromeOptions defaultChromeOptions = new ChromeOptions();
                defaultChromeOptions.merge(capabilities);
                defaultChromeOptions.setProxy(proxy);
                setBrowserPreferences(defaultChromeOptions, "chrome");
                webDriver = new ChromeDriver(defaultChromeOptions);
                break;
        }
        driver.set(webDriver);
        return webDriver;
    }


    /**
     * Sets the browser-specific preferences for the given options.
     *
     * @param options     The browser options (FirefoxOptions, ChromeOptions, or EdgeOptions).
     * @param browserType The type of the browser ("firefox", "chrome", or "edge").
     */
    private void setBrowserPreferences(Object options, String browserType) {
        String localOS = System.getProperty("os.name").toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(propertiesManager.getProp("HeadlessBrowser"));
        if (!localOS.contains("windows")) {
            isHeadless = true;
        }

        if (browserType.equalsIgnoreCase("firefox")) {
            FirefoxOptions firefoxOptions = (FirefoxOptions) options;
            if (isHeadless) {
                firefoxOptions.addArguments("-headless");
            }
            firefoxOptions.setAcceptInsecureCerts(true);
            firefoxOptions.addArguments("--disable-notifications");
            firefoxOptions.addArguments("--no-sandbox");
            firefoxOptions.addArguments("--verbose");
            firefoxOptions.addArguments("--disable-gpu");
            firefoxOptions.addArguments("--disable-software-rasterizer");
            firefoxOptions.addArguments("--start-maximized");
        } else if (browserType.equalsIgnoreCase("chrome")) {
            ChromeOptions chromeOptions = (ChromeOptions) options;
            if (isHeadless) {
                chromeOptions.addArguments("--headless=new");
            }
            HashMap<String, Object> chromeOptionsMap = new HashMap<>();
            chromeOptions.addArguments("--fast-start");
            chromeOptions.addArguments("--start-fullscreen");
            chromeOptions.addArguments("disable-infobars");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--ignore-ssl-errors=yes");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptionsMap.put("plugins.plugins_disabled", new String[]{"Chrome PDF Viewer"});
            chromeOptionsMap.put("plugins.always_open_pdf_externally", true);
            chromeOptionsMap.put("download.prompt_for_download", false);
            chromeOptionsMap.put("profile.default_content_settings.popups", 0);
            chromeOptionsMap.put("safebrowsing.enabled", false);
            chromeOptions.setExperimentalOption("prefs", chromeOptionsMap);
            chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
        } else if (browserType.equalsIgnoreCase("edge")) {
            EdgeOptions edgeOptions = (EdgeOptions) options;
            if (isHeadless) {
                edgeOptions.addArguments("--headless=new");
            }
            edgeOptions.addArguments("--disable-notifications");
            edgeOptions.addArguments("--no-sandbox");
            edgeOptions.addArguments("--verbose");
            edgeOptions.addArguments("--disable-gpu");
            edgeOptions.addArguments("--disable-software-rasterizer");
            edgeOptions.addArguments("--start-maximized");
        }
    }
}

