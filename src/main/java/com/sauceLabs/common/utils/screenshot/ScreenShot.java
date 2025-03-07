package com.sauceLabs.common.utils.screenshot;

import com.sauceLabs.common.utils.logs.MyLogger;
import com.sauceLabs.common.utils.files.FileUtil;
import com.sauceLabs.common.utils.properties.PropertiesManager;
import org.apache.logging.log4j.core.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author MahmoudOsama
 */
public class ScreenShot {
    private static final Logger log = new MyLogger().getLogger();
    private final PropertiesManager propertiesManager = new PropertiesManager();
    String screenShotDirectory = propertiesManager.getProp("Screenshot.Directory");
    /**
     * take screenshot from browser
     *
     * @param driver   WebDriver object
     * @param FileName generated screenshot name
     * @return screenshot file object
     */
    public File takeWebScreenShot(WebDriver driver, String FileName) {
        log.info("Taking screenshot...");
        TakesScreenshot tsc = (TakesScreenshot) driver;
        FileUtil parentOutputDirectory = new FileUtil();
        parentOutputDirectory.createDirectory(screenShotDirectory);
        log.info("Directory created.");
        File scFile = tsc.getScreenshotAs(OutputType.FILE);
        log.info("Screenshot captured");
        File screenshotFile = parentOutputDirectory.createFile(screenShotDirectory+"/", FileName + ".png");
        log.info("Screenshot file created");
        parentOutputDirectory.copyFile(scFile, screenshotFile);
        return screenshotFile;
    }

    public byte[] TakeReducedDimensionScreenShots(WebDriver driver, String FileName) throws IOException {
        File screenShotFile = takeWebScreenShot(driver, FileName);
        // Read the screenshot file into a BufferedImage
        BufferedImage originalImage = ImageIO.read(screenShotFile);
        // Resize the image
        int newWidth = (int) (originalImage.getWidth() *0.6);
        int newHeight = (int) (originalImage.getHeight() *0.6);
        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedBufferedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();
        // Convert the resized BufferedImage back to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedBufferedImage, "png", baos);
        return baos.toByteArray();
    }
}
