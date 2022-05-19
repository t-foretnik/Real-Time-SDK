package com.oraise.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.exception.ConfigurationException;

public class TestConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);

    private static final String PROPS_TEST = "./test.properties";

    private static final Validator<String, Path> FILE_EXISTS_VALIDATOR = in -> {
        Path p = Paths.get(in);
        if (!Files.exists(p) || !Files.isRegularFile(p) || !Files.isReadable(p)) {
            throw new ConfigurationException("Path '" + p.toAbsolutePath() + "' is not accessible!");
        }
        return p.toAbsolutePath();
    };

    private static final Validator<String, Integer> NUMERIC_VALIDATOR = in -> {
        final Integer intValue;
        try {
            intValue = Integer.parseInt(in);
        } catch (NumberFormatException nfe) {
            throw new ConfigurationException("Invalid numeric value '" + in + "'!");
        }
        return intValue;
    };

    private static final Validator<String, String> NON_EMPTY_STRING_VALIDATOR = in -> {
        if (in == null || in.isEmpty()) {
            throw new ConfigurationException("Encountered empty configuration value!");
        }
        return in;
    };

    private static interface Validator<I, O> {
        O validate(I in) throws ConfigurationException;
    }

    private final Properties props;

    private TestConfig(Properties props) {
        this.props = props;
    }

    public String username() throws ConfigurationException {
        return mandatoryStringProperty("username");
    }

    public String password() throws ConfigurationException {
        return mandatoryStringProperty("password");
    }

    public String applicationId() throws ConfigurationException {
        return mandatoryStringProperty("applicationId");
    }

    public String server() throws ConfigurationException {
        return mandatoryStringProperty("server");
    }

    public String port() throws ConfigurationException {
        return mandatoryStringProperty("port");
    }

    public Path keystore() throws ConfigurationException {
        return mandatoryStringProperty("keystore", FILE_EXISTS_VALIDATOR);
    }

    public String keystorePassword() throws ConfigurationException {
        return mandatoryStringProperty("keystorePassword");
    }

    public Path enumtypeDef() throws ConfigurationException {
        return mandatoryStringProperty("enumtypeDef", FILE_EXISTS_VALIDATOR);
    }

    public Path rdmFieldDictionary() throws ConfigurationException {
        return mandatoryStringProperty("rdmFieldDictionary", FILE_EXISTS_VALIDATOR);
    }

    public Integer startupTimeoutSeconds() throws ConfigurationException {
        return mandatoryStringProperty("startUpTimeoutSeconds", NUMERIC_VALIDATOR);
    }

    public Integer snapshotTimeoutSeconds() throws ConfigurationException {
        return mandatoryStringProperty("snapshotTimeoutSeconds", NUMERIC_VALIDATOR);
    }

    public Integer subscriptionTimeoutSeconds() throws ConfigurationException {
        return mandatoryStringProperty("subscriptionTimeoutSeconds", NUMERIC_VALIDATOR);
    }

    public String serviceName() throws ConfigurationException {
        return mandatoryStringProperty("serviceName", NON_EMPTY_STRING_VALIDATOR);
    }

    private String mandatoryStringProperty(String key) throws ConfigurationException {
        return mandatoryStringProperty(key, NON_EMPTY_STRING_VALIDATOR);
    }

    private <O> O mandatoryStringProperty(String key, Validator<String, O> validator) throws ConfigurationException {
        String value = Optional.ofNullable(props.getProperty(key))
                .orElseThrow(() -> new ConfigurationException("Missing mandatory property '" + key + "'!"));
        try {
            return validator.validate(value);
        } catch (ConfigurationException ce) {
            throw new ConfigurationException("Invalid property '" + key + "': " + ce.getMessage());
        }
    }

    public static TestConfig instance() {
        final Properties props = new Properties();

        final Path baseProps = Paths.get(PROPS_TEST);
        try (InputStream in = Files.newInputStream(baseProps)) {
            props.load(in);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Failed to read test properties: " + ioe.getMessage());
        }

        try (Stream<Path> parentDir = Files.list(baseProps.getParent())) {
            parentDir
                    .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".properties") && !p.equals(baseProps))
                    .forEach(p -> {
                        LOGGER.debug("Loading extra configuration file '{}'", p);
                        try (InputStream in = Files.newInputStream(p)) {
                            props.load(in);
                            LOGGER.debug("Successfully applied values from '{}'", p);
                        } catch (IOException ioe) {
                            LOGGER.debug("Failed to apply values from '{}': {}", p, ioe.getMessage(), ioe);
                        }
                    });
        } catch (IOException ioe) {
            LOGGER.debug("Failed to scan for additional user configuration: {}", ioe.getMessage(), ioe);
        }

        return new TestConfig(props);
    }
}
