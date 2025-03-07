package com.sauceLabs.common.utils.helpers.windows;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * this class is used to interact with windows command prompt
 *
 * @author MahmoudOsama
 */
public class WinCMD {
    private final Logger log = new MyLogger().getLogger();

    /**
     * Executes a command on the Windows command prompt.
     *
     * @param command the CMD command to execute
     * @return a list of output lines from the command
     */
    public ArrayList<String> execCMDCommand(String command) {
        ArrayList<String> output = new ArrayList<>();
        Process process = null;
        log.info("Executing CMD command: {}", command);
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor(1, TimeUnit.SECONDS);
            // Capture error stream
            captureErrorStream(process, output);
            // Capture standard output stream
            captureOutputStream(process, output);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: {}", command, e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output;
    }

    /**
     * Captures the error stream from the process.
     *
     * @param process the process to read from
     * @param output  the output list to add error messages
     */
    private void captureErrorStream(Process process, ArrayList<String> output) {
        new Thread(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    log.error("CMD Error: {}", line);
                    output.add(line);
                }
            } catch (IOException e) {
                log.error("Error reading from error stream", e);
            }
        }).start();
    }

    /**
     * Captures the standard output stream from the process.
     *
     * @param process the process to read from
     * @param output  the output list to add result messages
     */
    private void captureOutputStream(Process process, ArrayList<String> output) {
        try (BufferedReader resultReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = resultReader.readLine()) != null) {
                output.add(line);
            }
        } catch (IOException e) {
            log.error("Error reading from output stream", e);
        }
    }
}
