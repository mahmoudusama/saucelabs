package com.sauceLabs.common.utils.properties;


import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.io.*;
import java.util.*;


public class PropertiesManager {
    private Properties props = null;
    private static final Logger log = new MyLogger().getLogger();

    /**
     * Constructs a PropertiesManager instance.
     *
     * This constructor initializes the PropertiesManager by reading global properties from the
     * "global.properties" file located in the "envProperties" directory. It also reads environment
     * properties and merges them into the properties map.
     *
     * Logs the loading of properties for traceability. If there are issues loading the properties,
     * appropriate error handling should be implemented to inform the user.
     */
    public PropertiesManager() {
        // Load global properties from the specified file
        this.props = readPropertiesFile("envProperties/global.properties");
        // Merge environment properties into the properties map
        this.props.putAll(readEnvProperties());
    }


    /**
     * Retrieves the value associated with the specified key from the loaded properties.
     *
     * @param key The key for which the value is to be retrieved.
     * @return The value associated with the specified key, or null if the key does not exist.
     */
    public String getProp(String key){
        return this.props.getProperty(key);
    }

    /**
     * Reads the environment-specific properties file from the src/main/resources/envProperties directory.
     *
     * The method determines which environment properties file to load based on the "env" system property.
     * It supports "prd" for production, "stg" for staging, and defaults to "e2e" for end-to-end testing.
     *
     * @return Properties object containing the environment-specific properties.
     */
    private static Properties readEnvProperties() {
        String environment = (System.getProperty("env") != null ? System.getProperty("env") : "").toUpperCase();
        Properties properties;
        if (environment.equalsIgnoreCase("prd")) {
            properties = readPropertiesFile("envProperties/prd.properties");
        } else if (environment.equalsIgnoreCase("stg")) {
            properties = readPropertiesFile("envProperties/stg.properties");
        } else {
            properties = readPropertiesFile("envProperties/e2e.properties");
        }
        return properties;
    }


    /**
     * Reads properties from a specified file stored in the src/main/resources directory.
     *
     * @param fileName The name of the properties file to be read.
     * @return A Properties object containing the properties from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    private static Properties readPropertiesFile(String fileName){
        Properties properties = null;
        try (InputStream inputStream = PropertiesManager.class.getClassLoader().getResourceAsStream(fileName)) {
            properties = new Properties();
            properties.load(inputStream);
            return  properties;
        } catch (IOException e) {
            log.error("An IOException occurred while reading properties file");
            System.exit(-1);
        }
        return properties;
    }
}