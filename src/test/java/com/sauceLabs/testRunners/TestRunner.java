package com.sauceLabs.testRunners;

import org.junit.platform.suite.api.*;

import java.util.stream.Stream;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.sauceLabs.stepDefinitions,com.sauceLabs.common.utils"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber/report.html,json:target/cucumber/report.json")
})
public class TestRunner {
    private static String[] defaultOptions = {
            "--glue", "com.sauceLabs.stepDefinitions",
            "--glue", "com.sauceLabs.common.utils",
            "--plugin", "pretty",
            "--plugin", "html:target/cucumber/report.html",
            "--plugin", "json:target/cucumber/report.json",
            "src/test/resources/features"
    };

    public static void main(String[] args) {
        Stream<String> cucumberOptions = Stream.concat(Stream.of(defaultOptions), Stream.of(args));
        io.cucumber.core.cli.Main.main(cucumberOptions.toArray(String[]::new));
    }
}