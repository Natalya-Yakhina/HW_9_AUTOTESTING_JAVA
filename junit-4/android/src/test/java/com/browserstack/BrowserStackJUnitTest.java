package com.browserstack;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.After;
import org.junit.Before;
import org.yaml.snakeyaml.Yaml;

public class BrowserStackJUnitTest {
    public AndroidDriver driver;
    public String userName;
    public String accessKey;
    public UiAutomator2Options options;
    public static Map<String, Object> browserStackYamlMap;
    public static final String USER_DIR = "user.dir";

    public BrowserStackJUnitTest() {
        File file = new File(getUserDir() + "/browserstack.yml");
        this.browserStackYamlMap = convertYamlFileToMap(file, new HashMap<>());
    }

    @Before
    public void setUp() throws Exception {
        options = new UiAutomator2Options();
        userName = System.getenv("BROWSERSTACK_USERNAME") != null ? System.getenv("BROWSERSTACK_USERNAME") : (String) browserStackYamlMap.get("userName");
        accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY") != null ? System.getenv("BROWSERSTACK_ACCESS_KEY") : (String) browserStackYamlMap.get("accessKey");
        options.setCapability("appium:app", "bs://8d7cfab6f121e413c0ab00113194613b4902f479");  //sample.app
        options.setCapability("appium:deviceName", "Samsung Galaxy S22 Ultra");
        options.setCapability("appium:platformVersion", "12.0");

        driver = new AndroidDriver(new URL(String.format("https://%s:%s@hub.browserstack.com/wd/hub", userName , accessKey)), options);
    }

    @After
    public void tearDown() throws Exception {
        // Invoke driver.quit() to indicate that the test is completed. 
        // Otherwise, it will appear as timed out on BrowserStack.
        driver.quit();
    }

    private String getUserDir() {
        return System.getProperty(USER_DIR);
    }

    private Map<String, Object> convertYamlFileToMap(File yamlFile, Map<String, Object> map) {
        try {
            InputStream inputStream = Files.newInputStream(yamlFile.toPath());
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);
            map.putAll(config);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Malformed browserstack.yml file - %s.", e));
        }
        return map;
    }
}
