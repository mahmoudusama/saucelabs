package com.sauceLabs.common.utils.logs;

import com.sauceLabs.common.utils.properties.PropertiesManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.util.Collection;

/**
 * log4j implementation to log into text file and email, to log from any class just call 'getLogger' method,
 * class support logging in temp file then sending it in email
 *
 * @author MahmoudOsama
 */
public class MyLogger {
    private static final Level Level = null;
    private static File logFile;
    private static Logger log;
    private static Appender fileAppender;
    private static LoggerContext ctx;
    private static Configuration config;
    private static LoggerConfig loggerConfig;
    private static ConfigurationBuilder<BuiltConfiguration> builder;
    private static Boolean isFirstRun = true;
    private final PropertiesManager propertiesManager = new PropertiesManager();

    /**
     * Retrieves and returns the logger instance for "testlog".
     *
     * - This method ensures that the necessary appenders are created and initialized before retrieving the logger.
     * - The `appenderCreator()` method is called first to set up any required appenders (file or console).
     * - Once the appenders are set, the logger named "testlog" is retrieved from the logger context (`ctx`).
     * - The logger is returned for use in logging messages.
     *
     * @return The logger instance named "testlog" for logging operations.
     */
    public Logger getLogger() {
        appenderCreator();
        log = ctx.getLogger("testlog");
        return log;
    }


    /**
     * Initializes the logger by creating and adding appropriate appenders (file and console) if they don't already exist.
     * - If the logger has not been initialized, it first calls `initializeLogger()`.
     * - It creates a pattern layout to define the format of the log messages.
     * - For the first run, it adds a file appender and a console appender based on the property `Console.Logs` from the properties file:
     *   - If `Console.Logs` is set to `true`, it adds the console appender to log DEBUG level messages and above.
     *   - Appenders are added only if they do not already exist.
     */
    private void appenderCreator() {
        if (builder == null) {
            initializeLogger();
        }

        String pattern = "[%-5level] %d{yyyy-MM-dd HH:mm:ss} %C{2} - line=%L - %m%n";
        PatternLayout layout = PatternLayout.newBuilder().withPattern(pattern).build();

        if (!isFirstRun) {
            // Create and add file appender
            fileAppender = fileAppender("file", layout, config);
            if (!isAppenderExist("file")) {
                loggerProperties(fileAppender, Level.DEBUG);
            }

            // Create and add console appender
            Appender consoleAppender = consoleAppender("console", layout, config);
            if (!isAppenderExist("console") && Boolean.parseBoolean(System.getProperty("ConsoleLogs", "false"))) {
                loggerProperties(consoleAppender, Level.DEBUG);  // Log DEBUG level and above
                System.out.println("Console appender added.");
            }
        }
        isFirstRun = false;
    }



    /**
     * Configures and adds the specified appender to the logger.
     * - The logger context and configuration are retrieved.
     * - The appender is started and added to the configuration.
     * - An `AppenderRef` is created to associate the appender with the specified log level.
     * - If `loggerConfig` is null, a new root logger configuration is created, allowing logging for all levels (`Level.ALL`) and attaching the appender.
     * - The appender is then added to the logger with the specified log level, and the logger configuration is updated in the context.
     * - Finally, `ctx.updateLoggers()` ensures that the logger is updated with the new configuration.
     *
     * @param appender the appender to add (e.g., file, console)
     * @param level the logging level (e.g., DEBUG, ERROR)
     */
    private void loggerProperties(Appender appender, Level level) {
        ctx = (LoggerContext) LogManager.getContext(false);
        config = ctx.getConfiguration();

        appender.start();
        config.addAppender(appender);

        AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), level, null);
        AppenderRef[] refs = new AppenderRef[]{ref};

        if (loggerConfig == null) {
            loggerConfig = LoggerConfig.RootLogger.createLogger(false, Level.ALL, "testlog", "true", refs, null, config, null);
        }
        loggerConfig.addAppender(appender, level, null);

        config.addLogger("testlog", loggerConfig);
        ctx.updateLoggers();
    }


    /**
     * Initializes the logger configuration with a console appender.
     * - A new `ConfigurationBuilder` is created to build the logger configuration.
     * - The configuration name is set to "BuilderTest" for identification.
     * - The status level is set to `FATAL`, meaning only fatal errors will be logged initially by the logger itself.
     * - A console appender is created with the target `SYSTEM_OUT` to print log output to the console.
     * - The layout is attached to the console appender, and the appender is added to the logger configuration.
     *
     * - A root logger is created and its log level is set to `FATAL`. This means it will initially capture only fatal logs.
     * - The root logger is associated with the console appender, ensuring log messages are sent to the console.
     * - If the logger context (`ctx`) is not initialized, `Configurator.initialize` is called to build and apply the configuration.
     */
    void initializeLogger() {
        builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("BuilderTest");
        builder.setStatusLevel(Level.FATAL);

        LayoutComponentBuilder layout = builder.newLayout("PatternLayout");
        layout.addAttribute("pattern", "[%-5level] %d{yyyy-MM-dd HH:mm:ss} %C{2} - line=%L - %m%n");
        AppenderComponentBuilder console = builder.newAppender("stdout", "Console").addAttribute("target",
                ConsoleAppender.Target.SYSTEM_OUT);
        console.add(layout);
        builder.add(console);

        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.FATAL);
        rootLogger.add(builder.newAppenderRef("stdout"));
        builder.add(rootLogger);
        if (ctx == null) {
            ctx = Configurator.initialize(builder.build());
        }
    }


    /**
     * Creates and configures a file appender for logging.
     *
     * - The method constructs a file appender that writes log messages to a file located at "log.txt" in the current directory.
     * - The `appenderName` is used to identify the appender.
     * - A `PatternLayout` is used to format the log messages based on the provided layout pattern.
     *
     * @param appenderName The name of the appender.
     * @param layout The pattern layout for formatting log messages.
     * @param config The logger configuration used to configure the appender.
     * @return The configured and active `FileAppender`.
     */
    private Appender fileAppender(String appenderName, PatternLayout layout, Configuration config) {
        String tmpPath = System.getProperty("user.dir") + File.separator + "log.txt";
        FileAppender appenderFile = FileAppender.newBuilder().setConfiguration(config).withName(appenderName)
                .withLayout(layout).withFileName(tmpPath).build();
        appenderFile.start();
        return appenderFile;
    }


    /**
     * Creates and returns a ConsoleAppender with the specified name, layout, and configuration.
     *
     * - This method builds a `ConsoleAppender` that logs output to the console (SYSTEM_OUT).
     * - The appender is initialized with the provided `appenderName`, `layout`, and `config`.
     * - Once the appender is created, it is started before being returned to ensure it's ready for logging.
     *
     * @param appenderName The name to be assigned to the console appender.
     * @param layout The `PatternLayout` defining the format of the log messages.
     * @param config The logger configuration to associate with this appender.
     * @return The initialized and started `ConsoleAppender`.
     */
    private Appender consoleAppender(String appenderName, PatternLayout layout, Configuration config) {
        ConsoleAppender appender = ConsoleAppender.newBuilder()
                .setName(appenderName)
                .setLayout(layout)
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .setConfiguration(config)
                .build();
        appender.start();
        return appender;
    }



    /**
     * Checks if an appender with the specified name already exists in the logger configuration.
     *
     * - This method verifies whether an appender with the given `appenderName` is already added to the logger configuration.
     * - It iterates over all appenders in the current `loggerConfig` and checks if any of them contain the specified name.
     * - Returns `true` if an appender with the specified name is found, otherwise returns `false`.
     *
     * @param appenderName The name of the appender to check for.
     * @return `true` if the appender exists, `false` otherwise.
     */
    private boolean isAppenderExist(String appenderName) {
        boolean exist = false;
        if (loggerConfig != null) {
            Collection<Appender> appender = loggerConfig.getAppenders().values();
            for (Appender a : appender) {
                if (a.getName().contains(appenderName)) {
                    exist = true;
                }
            }
        }
        return exist;
    }



    ////////////////////////////////Below NOT USED//////////////////////////////////////////
    ////////////////////////////////Remove Later///////////////////////////////////////////
    ////////////////////////////////Below NOT USED//////////////////////////////////////////
    ////////////////////////////////Remove Later///////////////////////////////////////////
    ////////////////////////////////Below NOT USED//////////////////////////////////////////
    ////////////////////////////////Remove Later///////////////////////////////////////////

    // stop logging
//    public void stopFileAppender() {
//        if (fileAppender != null) {
//            loggerConfig.removeAppender("file");
//            ctx.getRootLogger().removeAppender(fileAppender);
//            fileAppender.stop();
//        }
//        loggerConfig.stop();
//    }
//
//
//    public File TmpLoggerFile() {
//        if (logFile == null) {
//            try {
//                logFile = File.createTempFile("tmp", ".TMP");
//            } catch (IOException e) {
//                log.debug(e);
//            }
//        }
//        return logFile;
//    }
//
//    private Appender mailAppender(String appenderName, PatternLayout layout, Boolean debug) {
//        String isdebug = debug.toString();
//        String mailSubject = "Logger Mail";
//        String bufferSizeStr = "500";
//        Filter filter = null;
//        String mailTo = "qc.automation.mail@gmail.com";
//        String mailCC = "";
//
//        String mailFrom = "";
//        String mailSmtpHost = "";
//        String protocol = "";
//        String hostPort = "";
//        String mailUserName = "";
//        String mailPass = "";
//
//        if (new Connections().controlWithoutLog("smtp.gmail.com")) {
//            //gmail config
//            mailFrom = "qc.automation.mail@gmail.com";
//            mailSmtpHost = "smtp.gmail.com";
//            protocol = "smtps";
//            hostPort = "465";
//            mailUserName = "qc.automation.mail";
//            mailPass = "@TEData#123";
//        }
//
//        Appender mailappend = SmtpAppender.createAppender(null, appenderName, mailTo, mailCC, "", mailFrom, "",
//                mailSubject, protocol, mailSmtpHost, hostPort, mailUserName, mailPass, isdebug, bufferSizeStr, layout,
//                filter, "true");
//
//        mailappend.start();
//        return mailappend;
//    }

}
