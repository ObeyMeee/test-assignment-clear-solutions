package ua.com.andromeda.testassignment;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    @SneakyThrows
    public static Properties loadProperties(String resourceFileName) {
        Properties configuration = new Properties();
        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);
        configuration.load(inputStream);
        inputStream.close();
        return configuration;
    }
}