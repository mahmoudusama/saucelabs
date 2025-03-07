package com.sauceLabs.common.utils.helpers;

import com.sauceLabs.common.utils.logs.MyLogger;
import com.sauceLabs.common.utils.timer.MyTimer;
import org.apache.logging.log4j.core.Logger;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MahmoudOsama
 */
public class CommonUtility {
    private static AtomicInteger count = new AtomicInteger(0);
    private static Logger log = new MyLogger().getLogger();


    /**
     * get unique IDs
     *
     * @param idsCount      count of IDs generated
     * @param IdDigitsCount ID digit count
     * @return generated IDs
     */
    public List<String> getUniqueIds(String idsCount, int IdDigitsCount) {
        // generate 1000 unique ids
        List<String> uniqueIDs = new ArrayList<>();
        int counter = count.incrementAndGet();
        String id = "";
        do {
            String currentTime = new MyTimer().getCurrentTimeInString().replaceAll("_", "");
            id = currentTime + counter;
            // adjust id length to be 15 digits
            while (id.length() != IdDigitsCount) {
                if (id.length() < IdDigitsCount) {
                    id += "0";
                } else {
                    id = id.substring(1, (id.length()));
                }
            }
            if (!uniqueIDs.contains(id)) {
                uniqueIDs.add(id);
            }
            counter++;
        } while (uniqueIDs.size() < Integer.parseInt(idsCount));
        log.info("Generated Unique Ids: {}", uniqueIDs);
        return uniqueIDs;
    }

    /**
     * Compares two strings for equality, ignoring spaces and case.
     *
     * @param a the first string
     * @param b the second string
     * @return true if the strings match, false otherwise
     */
    public boolean compareString(String a, String b) {
        if (a == null || b == null) {
            log.warn("One or both strings are null: a='{}', b='{}'", a, b);
            return false;
        }
        // Normalize the strings by removing spaces and converting to lowercase
        String normalizedA = a.replaceAll("\\s+", "").toLowerCase();
        String normalizedB = b.replaceAll("\\s+", "").toLowerCase();
        boolean match = normalizedA.equals(normalizedB);
        log.info("Comparing strings: '{}' with '{}' | Normalized: '{}' with '{}' | Match: {}", a, b, normalizedA, normalizedB, match);
        return match;
    }

    /**
     * comparing dates
     *
     * @param dateFormat date format of dates
     * @param date1      first date
     * @param date2      second date
     * @return date 2 older than date 1 status
     */
    public boolean isDate2OlderthanDate1(String dateFormat, String date1, String date2) {
        //"yyyy-MM-dd hh:mm:ss"
        boolean isD2olderthanD1 = false;
        Date d1 = getFormattedDate(dateFormat, date1);
        Date d2 = getFormattedDate(dateFormat, date2);
        if (d1 == null || d2 == null)
            throw new Error("Date Formatting issue.");

        if (d1.compareTo(d2) > 0)
            isD2olderthanD1 = true;

        return isD2olderthanD1;
    }

    /**
     * Converts a string to a Date object based on the specified format.
     *
     * @param formatString the format of the date string
     * @param dateString   the date as a string
     * @return a Date object, or null if parsing fails
     */
    public Date getFormattedDate(String formatString, String dateString) {
        // Create a SimpleDateFormat instance with the provided format
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        Date targetDate;
        try {
            // Parse the date string into a Date object
            targetDate = formatter.parse(dateString);
        } catch (ParseException ex) {
            log.info("Couldn't format: {}, to date: {}", formatString, dateString);
            return null; // Return null if parsing fails
        }
        // Create a calendar instance to manipulate the date
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(new Date());
        // Adjust the target date based on the format
        Calendar targetCal = Calendar.getInstance();
        targetCal.setTime(targetDate);
        switch (formatString) {
            case "hh:mm a" -> {
                nowCal.set(Calendar.HOUR_OF_DAY, targetCal.get(Calendar.HOUR_OF_DAY));
                nowCal.set(Calendar.MINUTE, targetCal.get(Calendar.MINUTE));
            }
            case "EEE hh:mm a" -> {
                nowCal.set(Calendar.DAY_OF_WEEK, targetCal.get(Calendar.DAY_OF_WEEK));
                nowCal.set(Calendar.HOUR_OF_DAY, targetCal.get(Calendar.HOUR_OF_DAY));
                nowCal.set(Calendar.MINUTE, targetCal.get(Calendar.MINUTE));
            }
            case "EEE MM/dd" -> {
                nowCal.set(Calendar.MONTH, targetCal.get(Calendar.MONTH));
                nowCal.set(Calendar.DAY_OF_MONTH, targetCal.get(Calendar.DAY_OF_MONTH));
                nowCal.set(Calendar.HOUR_OF_DAY, targetCal.get(Calendar.HOUR_OF_DAY));
                nowCal.set(Calendar.MINUTE, targetCal.get(Calendar.MINUTE));
            }
            default -> {
                // For other formats, retain the parsed date
                return targetDate;
            }
        }
        // Return the adjusted date
        Date finalDate = nowCal.getTime();
        log.info("Formatted Date >> {}", finalDate);
        return finalDate;
    }



    public String getDateFormatted(String formatPattern, Date date) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(date);
        SimpleDateFormat fm = new SimpleDateFormat(formatPattern);
        return fm.format(date);
    }

    /**
     * Compares two dates to determine if date2 is older than date1.
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return true if date2 is older than date1, false otherwise
     */
    public boolean isDate2OlderThanDate1(Date date1, Date date2) {
        // Check for null dates
        if (date1 == null || date2 == null) {
            log.warn("One or both dates are null: date1='{}', date2='{}'", date1, date2);
            return false;
        }
        // Convert Date to LocalDateTime for better comparison
        LocalDateTime localDateTime1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTime2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        boolean isD2OlderThanD1 = localDateTime2.isBefore(localDateTime1);
        log.info("Comparing dates: date1='{}' | date2='{}' | Is date2 older than date1? {}", date1, date2, isD2OlderThanD1);
        return isD2OlderThanD1;
    }

    /**
     * get local tine for a date with time zone
     *
     * @param date date object
     * @return datetine object
     */
    public LocalDateTime getLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * get duration in minutes between two dates
     *
     * @param olderDate date with older time
     * @param newerDate date with newer time
     * @return minutes between dates
     */
    public long getDiffInMin(Date olderDate, Date newerDate) {
        Duration dur = Duration.between(getLocalDateTime(olderDate), getLocalDateTime(newerDate));
        return dur.toMinutes();
    }

    /**
     * get duration in seconds between two dates
     *
     * @param olderDate date with older time
     * @param newerDate date with newer time
     * @return seconds between dates
     */
    public long getDiffInSec(Date olderDate, Date newerDate) {
        Duration dur = Duration.between(getLocalDateTime(olderDate), getLocalDateTime(newerDate));
        return dur.getSeconds();
    }

    /**
     * get user input 'integer'
     *
     * @return entered values
     */
    public int promptIntInput() {
        int id = 0;
        try (Scanner input = new Scanner(System.in)) {
            log.info(" ===>>");
            id = input.nextInt();
            return id;
        } catch (Exception e) {
            System.err.println("Only Integer numbers allowed!!!");
            promptIntInput();
        }
        return id;
    }

    /**
     * get user input 'String'
     *
     * @return entered values
     */
    public String promptStringInput() {
        System.out.print(" ===>>");
        try (Scanner input = new Scanner(System.in)) {
            return input.nextLine();
        }
    }

    public Object setAnnValue(Annotation annotation, String key, Object newValue) {
        Object oldValue = null;
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> memberValues = (Map<String, Object>) f.get(handler);
            oldValue = memberValues.get(key);
            if (oldValue != newValue)
                oldValue = memberValues.put(key, newValue);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            log.catching(e);
        }
        return oldValue;
    }

    /**
     * minimize all opened application
     */
    public void minimizeAllApps() {
        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (AWTException e) {
            log.catching(e);
        }
    }


//    public String getBarcodeKey(String base64Image) {
//        String qrCode = "";
//        BASE64Decoder decoder = new BASE64Decoder();
//        log.info("Image >>" + base64Image);
//        if (base64Image == null || base64Image.equals(""))
//            throw new Error("Empty source image!");
//        byte[] imageByte;
//        ByteArrayInputStream imgInputStream = null;
//        BufferedImage image = null;
//        try {
//            imageByte = decoder.decodeBuffer(base64Image.split(",")[1]);
//            imgInputStream = new ByteArrayInputStream(imageByte);
//            image = ImageIO.read(imgInputStream);
//            BinaryBitmap ss = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
//            Map<DecodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
//            Result qrResult = new MultiFormatReader().decode(ss, hintMap);
//            URI resultURI = new URI(qrResult.getText());
//            List<NameValuePair> params = URLEncodedUtils.parse(resultURI, Charset.forName("UTF-8"));
//            qrCode = params.stream().filter(i -> i.getName().equals("secret")).map(NameValuePair::getValue).findFirst().orElse("");
//        } catch (NotFoundException | URISyntaxException | IOException e) {
//            log.catching(e);
//        }
//        log.info("generated barcode key >>" + qrCode);
//        return qrCode;
//    }

        /**
     * Sets environment variables at runtime.
     * Note: This is a potentially unsafe operation and should be used with caution.
     *
     * @param environmentVariables Map of environment variables to set
     * @throws SecurityException if access to system environment is denied
     * @throws IllegalArgumentException if the input map is null
     */
    public static void setEnvironmentVariables(Map<String, String> environmentVariables) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            
            // Update main environment map
            Field environmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            environmentField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> environment = (Map<String, String>) environmentField.get(null);
            environment.putAll(environmentVariables);

            // Update case-insensitive environment map
            Field caseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            caseInsensitiveEnvironmentField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, String> caseInsensitiveEnvironment = (Map<String, String>) caseInsensitiveEnvironmentField.get(null);
            caseInsensitiveEnvironment.putAll(environmentVariables);
            
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            // Fallback for different JVM implementations
            try {
                Map<String, String> environment = System.getenv();
                for (Class<?> clazz : Collections.class.getDeclaredClasses()) {
                    if ("java.util.Collections$UnmodifiableMap".equals(clazz.getName())) {
                        Field mapField = clazz.getDeclaredField("m");
                        mapField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Map<String, String> map = (Map<String, String>) mapField.get(environment);
                        map.putAll(environmentVariables);
                        break;
                    }
                }
            } catch (Exception fallbackException) {
                log.error("Failed to set environment variables", fallbackException);
                throw new SecurityException("Unable to set environment variables", fallbackException);
            }
        }
    }

    public static String appendStringWith_DateTime(String inputString) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = currentDateTime.format(formatter);
        return inputString + formattedDateTime;
    }

    public int generateRandomNumberWithinRange(int start, int end) {
        // Validate inputs
        if (start >= end) {
            throw new IllegalArgumentException("End must be greater than start");
        }
        // Create an instance of the Random class
        Random random = new Random();
        // Generate a random number within the specified range
        return random.nextInt(end - start + 1) + start;
    }

    public int generateRandomNumberWithinRangeExcludingNo(int start, int end, int excluded) {
        // Validate inputs
        if (start >= end) {
            throw new IllegalArgumentException("End must be greater than start");
        }
        // Create an instance of the Random class
        Random random = new Random();
        // Generate a random number within the specified range
        int randomNumber;
        do {
            randomNumber = random.nextInt((end - start) + 1) + start;
        } while (randomNumber == excluded);
        return randomNumber;
    }

    public String getSubstringWithRegex(String in, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);
        if (matcher.find())
        {
            in = matcher.group();
        }
        return in;
    }

    // Generates random Sring of mix between alphabets and digits with specified length.
    public static String generateRandomStringWithLength(int length) {
        final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";
        final Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                sb.append(alphabets.charAt(random.nextInt(alphabets.length())));
            } else {
                sb.append(numbers.charAt(random.nextInt(numbers.length())));
            }
        }
        return sb.toString();
    }

    public static String generateFutureRandomDate(String dateFormat) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        int randomDays = ThreadLocalRandom.current().nextInt(1, 91); // Generate random number of days between 1 and 91
        LocalDate randomDate = tomorrow.plusDays(randomDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);// example format > dd-MM-yyyy
        return randomDate.format(formatter);
    }
}
