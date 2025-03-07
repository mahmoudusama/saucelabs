package com.sauceLabs.common.utils.proxy;

import com.sauceLabs.common.utils.logs.MyLogger;
import com.sauceLabs.common.utils.properties.PropertiesManager;
import com.sauceLabs.common.utils.helpers.Connections;
import org.apache.logging.log4j.core.Logger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Properties;

/**
 * Class to manage the proxy configuration, depending on the variables defined in global.propertiesManager.
 * This class can set the proxy for Jenkins or for a local machine.
 *
 * If you want to use it on your local machine, you need to set the following variables:
 *  - VPN.Proxy.Host
 *  - VPN.Proxy.Port
 * If authentication is required on the proxy, you also need to set:
 *  - VPN.Proxy.Local.Username
 *  - VPN.Proxy.Local.Password
 */
public class Proxy {
    private final PropertiesManager propertiesManager = new PropertiesManager();
    public static Logger log = new MyLogger().getLogger();


    /**
     * Sets the proxy configuration based on the operating system and provided properties.
     * For Linux, sets the Jenkins proxy.
     * For Windows and Mac, sets the local proxy with authentication.
     * Throws an exception if the necessary properties are not provided or if the connection cannot be established.
     */
    public void setProxy() {
        log.info("Setting proxy...");

        // Check the Enable.Proxy and proceed only if it's true
        if (isProxyDisabled()) {
            log.info("Proxy flag is set to false. Skipping proxy configuration.");
            return;
        }

        Connections connect = new Connections();
        Properties systemSettings = System.getProperties();

        String jenkinsProxyHost = propertiesManager.getProp("Jenkins.Proxy.Host");
        String jenkinsProxyPort = propertiesManager.getProp("Jenkins.Proxy.Port");

        String localProxyHost = propertiesManager.getProp("VPN.Proxy.Host");
        String localProxyPort = propertiesManager.getProp("VPN.Proxy.Port");
        String localProxyUsername = propertiesManager.getProp("VPN.Proxy.Local.Username");
        String localProxyPassword = propertiesManager.getProp("VPN.Proxy.Local.Password");

        // Check the OS and set the appropriate proxy
        String osName = System.getProperty("os.name").toLowerCase();
        switch (osName) {
            case "linux":
                if (isEmpty(jenkinsProxyHost) || isEmpty(jenkinsProxyPort)) {
                    throw new RuntimeException("Jenkins proxy host and port must be provided.");
                }
                if (!connect.checkConnection(jenkinsProxyHost, Integer.parseInt(jenkinsProxyPort))) {
                    throw new RuntimeException("Couldn't reach Jenkins proxy at host: " + jenkinsProxyHost + " and port: " + jenkinsProxyPort);
                }
                setProxyProperties(jenkinsProxyHost, jenkinsProxyPort, systemSettings);
                break;
            case "windows":
            case "mac":
                if (isEmpty(localProxyHost) || isEmpty(localProxyPort)) {
                    throw new RuntimeException("Local proxy host and port must be provided.");
                }
                if (isEmpty(localProxyUsername) || isEmpty(localProxyPassword)) {
                    throw new RuntimeException("Proxy credentials (username and password) must be provided.");
                }
                if (!connect.checkConnection(localProxyHost, Integer.parseInt(localProxyPort))) {
                    throw new RuntimeException("No connection to local proxy host: " + localProxyHost + " & port: " + localProxyPort);
                }
                setProxyAuthentication(systemSettings, localProxyUsername, localProxyPassword);
                setProxyProperties(localProxyHost, localProxyPort, systemSettings);
                break;
            default:
                throw new RuntimeException("Unrecognized OS, unable to set proxy settings.");
        }
        log.info("Proxy successfully set.");
    }

    /**
     * Sets the proxy configuration using a PAC file.
     * The PAC file URL must be provided in the properties.
     * Throws an exception if the PAC file URL is not provided or if the configuration fails.
     */
    public void setProxyWithPacFile() {
        log.info("Setting proxy using PAC file...");

        // Check the Enable.Proxy and proceed only if it's true
        if (isProxyDisabled()) {
            log.info("Proxy flag is set to false. Skipping proxy configuration using Pac File.");
            return;
        }

        Properties systemSettings = System.getProperties();
        String pacFileUrl = propertiesManager.getProp("Pac.File.Proxy");
        log.info("PAC file URL: {}", pacFileUrl);
        if (isEmpty(pacFileUrl)) {
            log.info("PAC file URL is empty");
            throw new RuntimeException("PAC file URL is not provided.");
        }
        try {
            // Enable system proxies and set the PAC file URL
            systemSettings.put("java.net.useSystemProxies", "true");
            systemSettings.put("java.net.pacfile.url", pacFileUrl);
            log.info("Proxy configured using PAC file: {}", pacFileUrl);
        } catch (Exception e) {
            log.error("Failed to set proxy using PAC file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set proxy using PAC file.", e);
        }
    }

    /**
     * Clears all proxy settings from the system properties.
     * This method should be called when proxy settings are no longer needed or require resetting.
     */
    public void clearProxy() {
        log.info("Clearing all proxy settings from system propertiesManager.");
        // Remove HTTP proxy settings
        System.clearProperty("http.proxySet");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        // Remove HTTPS proxy settings
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
        log.info("Proxy settings cleared successfully.");
    }

    /**
     * Attempts to connect to the specified target URL.
     * If the connection attempt times out, the method will enable proxy settings.
     *
     * @param targetURL The URL to which the connection is being attempted.
     */
    public void enableProxyIfAvailable(String targetURL) {
        try {
            // Attempt to open a connection to the target URL
            HttpURLConnection connection = (HttpURLConnection) URI.create(targetURL)
                    .toURL()
                    .openConnection();
            // Get the response code to verify the connection
            int responseCode = connection.getResponseCode();
            log.info("Successfully connected to the target URL: {} with response code: {}", targetURL, responseCode);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            // Check if the exception message contains "Connection timed out"
            if (errorMessage != null && errorMessage.contains("Connection timed out")) {
                // Log a warning message and enable the proxy
                log.warn("Connection to {} timed out. Enabling proxy...", targetURL);
                setProxy();
            } else {
                // Log other errors encountered during the connection attempt
                log.warn("Failed to connect to {}. Error: {}", targetURL, errorMessage);
            }
        }
    }

    /**
     * Sets the proxy host and port properties for HTTPS and HTTP protocols.
     *
     * @param host The proxy host.
     * @param port The proxy port.
     * @param systemSettings The system properties to update.
     */
    private void setProxyProperties(String host, String port, Properties systemSettings) {
        systemSettings.put("https.proxyHost", host);
        systemSettings.put("https.proxyPort", port);
        systemSettings.put("http.proxyHost", host);
        systemSettings.put("http.proxyPort", port);
    }

    /**
     * Sets the proxy authentication properties for HTTPS and HTTP protocols.
     *
     * @param systemSettings The system properties to update.
     * @param username The proxy username.
     * @param password The proxy password.
     */
    private void setProxyAuthentication(Properties systemSettings, String username, String password) {
        systemSettings.put("http.proxyUser", username);
        systemSettings.put("http.proxyPassword", password);
        systemSettings.put("https.proxyUser", username);
        systemSettings.put("https.proxyPassword", password);
    }

    private boolean isProxyDisabled() {
        return !Boolean.parseBoolean(propertiesManager.getProp("Enable.Proxy"));
    }

    // Helper method to check if a string is null or empty
    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
