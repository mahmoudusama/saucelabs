package com.sauceLabs.common.utils.timer;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * implementation for a timer
 *
 * @author Mahmoud Osama
 */
public class MyTimer {
    private volatile Long start;
    private volatile Long stopWatch;
    public static Logger log = new MyLogger().getLogger();

    /**
     * Starts a timer by capturing the current system time in nanoseconds and logs the event.
     *
     * This method initializes the `start` variable with the current time using `System.nanoTime()`.
     * It logs the start time and is useful for measuring elapsed time during the execution of a specific block of code.
     * The recorded time can later be used to calculate the duration of an operation or task.
     *
     * Note: The `System.nanoTime()` method provides a high-resolution time source, ideal for measuring
     * elapsed time in performance-critical scenarios. It should not be used for getting the actual system time.
     */
    public void startTimer() {
        start = System.nanoTime();
        log.info("Timer started at: {} nanoseconds", start);
    }


    /**
     * Retrieves the current elapsed time of the timer in the specified time unit and logs the action.
     *
     * This method calculates the time that has passed since the timer was started by subtracting
     * the start time from the current time (`System.nanoTime()`). The elapsed time is then converted
     * to the specified `TimeUnit` (MILLISECONDS, SECONDS, or HOURS).
     *
     * Logs the timer value in the desired unit, and throws an exception if the timer has not been started.
     *
     * @param timeUnit The time unit to which the elapsed time should be converted (MILLISECONDS, SECONDS, HOURS).
     * @return The elapsed time in the specified time unit.
     * @throws IllegalStateException If the timer has not been started.
     * @throws IllegalArgumentException If an unsupported time unit is provided.
     */
    public Long getTimer(TimeUnit timeUnit) {
        if (start == null) {
            log.error("Attempted to get timer value before starting the timer.");
            throw new IllegalStateException("Timer has not been started.");
        }
        long elapsedTime = System.nanoTime() - start;
        log.info("Elapsed time in nanoseconds: {}", elapsedTime);
        switch (timeUnit) {
            case MILLISECONDS:
                stopWatch = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
                log.info("Elapsed time in milliseconds: {}", stopWatch);
                break;
            case SECONDS:
                stopWatch = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
                log.info("Elapsed time in seconds: {}", stopWatch);
                break;
            case HOURS:
                stopWatch = TimeUnit.HOURS.convert(elapsedTime, TimeUnit.NANOSECONDS);
                log.info("Elapsed time in hours: {}", stopWatch);
                break;
            default:
                log.error("Unsupported time unit: {}", timeUnit);
                throw new IllegalArgumentException("Unsupported time unit: " + timeUnit);
        }
        return stopWatch;
    }


    /**
     * Retrieves the current time formatted as a string and logs the action.
     *
     * This method fetches the current time using `LocalDateTime.now()` and formats it according to the pattern
     * "yy_MM_dd_HH_mm_ss". This format includes the year, month, day, hour, minute, and second, making it
     * suitable for file naming or timestamping purposes.
     *
     * Logs the formatted current time for reference.
     *
     * @return The current time formatted as a string in the pattern "yy_MM_dd_HH_mm_ss".
     */
    public String getCurrentTimeInString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy_MM_dd_HH_mm_ss");
        String formattedTime = now.format(formatter);
        log.info("Current time formatted as: {}", formattedTime);
        return formattedTime;
    }

    public static String getTodayDateWithDash() {
        LocalDate currentDate = LocalDate.now();

        // Define a date formatter with hyphens
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format and print the current date
        String formattedDate = currentDate.format(formatter);
        formattedDate="-"+formattedDate;
        return formattedDate;
    }

}