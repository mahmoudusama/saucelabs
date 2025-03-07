package com.sauceLabs.common.utils.cucumber;

import com.sauceLabs.common.ui.base.BaseWebDriver;
import com.sauceLabs.common.utils.screenshot.ScreenShot;
import com.sauceLabs.common.utils.timer.MyTimer;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class CucumberHooks {
    private final BaseWebDriver baseWebDriver = new BaseWebDriver();
    public static Logger log = new MyLogger().getLogger();
    private static String dataAttached = "";
    private static int count = 0;

    public static void setTextToAttach(String content) {
        dataAttached = content;
    }


    private static void addTextBox(Scenario scenario) {
        if (!dataAttached.isEmpty()) {
            scenario.attach(dataAttached, "text/plain", "HTTP data " + count++);
        }
    }


    private File attachStepScreenshot(Scenario scenario, String fileName) {
        File screenShotFile = null;
        ScreenShot screenShot = new ScreenShot();
        boolean reduceScreenshotSize = Boolean.parseBoolean(System.getProperty("ReduceScreenshotSize", "false"));
        if (baseWebDriver.isDriverActive()) {
            try {
                if (reduceScreenshotSize) {
                    // Attach the resized image to the scenario
                    scenario.attach(screenShot.TakeReducedDimensionScreenShots(baseWebDriver.getDriver(), fileName), "image/png", "screenshot " + fileName);
                    log.info("Attached the resized screenShot to the scenario");
                } else {
                    screenShotFile = new ScreenShot().takeWebScreenShot(baseWebDriver.getDriver(), fileName);
                    scenario.attach(Files.readAllBytes(screenShotFile.toPath()), "image/png", "screenshot " + screenShotFile.getName());
                    log.info("Attached the normal size of screenShot to the scenario");
                }
            } catch (IOException e) {
                log.error("Error attaching screenshot.\n {}", e.getMessage());
            }
        }
        return screenShotFile;
    }


    /*********************
     * Web Ui Cucumber Hooks
     * ******************/

    @AfterStep("@ui or @UI or @Ui")
    public void afterStepUI(Scenario scenario) {
        // Generate a safe file name based on the scenario name
        String fileStepName = scenario.getName()
                .replaceAll("[ <>&:']", "_")
                .replaceAll("\"", "")
                .replaceAll("/", "");

        // Ensure the file name is within length limits
        String fileName = new MyTimer().getCurrentTimeInString() + fileStepName;
        if (fileName.length() > 170) {
            fileName = fileName.substring(0, 170);
        }

        log.info("Creating screenshot with file name: {}", fileName);

        try {
            if (attachStepScreenshot(scenario, fileName) != null) {
                log.info("After step screenshot attached.");
            } else {
                log.warn("Failed to attach screenshot for scenario: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.warn("Error attaching screenshot for scenario: {}\n{}", scenario.getName(), e.getMessage());
        }
    }

    @After("@ui or @UI or @Ui")
    public void closeBrowser() {
        if (baseWebDriver.isDriverActive()) {
            baseWebDriver.quitAndRemoveDriver();
            log.info("WebDriver quit successfully.");
        } else {
            log.info("No active WebDriver instance to quit.");
        }
    }

    /*******************************
     * Generic Cucumber Hooks
     * *****************************/

    @AfterStep
    public static void afterStep(Scenario scenario) {
        addTextBox(scenario);
        dataAttached = "";
    }

    /*******************************
     * Logs organizer Cucumber Hooks
     * *****************************/

    @Before
    public void executionStarted() {
        log.info("""
                ###########################################################
                ###########################################################
                #################### Execution Started. ###################
                ###########################################################
                ###########################################################
                """);
    }

    @After
    public void executionFinished() {
        log.info("""
                ############################################################
                ############################################################
                #################### Execution Finished. ###################
                ############################################################
                ############################################################
                """);
    }
}
