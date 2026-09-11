package com.github.nasbru;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Path DEFAULT_PATH = Paths.get("data/config.properties");
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
	private final Properties properties = new Properties();
	private final Path path;

	public Config(Path path) {
		this.path = path;
		load();
	}

	public Config() {
		this(DEFAULT_PATH);
	}

	private void load() {
		try (InputStream in = Files.newInputStream(path)) {
			properties.clear();
			properties.load(in);
		} catch (IOException e) {
			LOGGER.error("Error while reading config file.");
		}
	}
	
	public boolean isBme680Enabled() {
		return Boolean.parseBoolean(properties.getProperty("bme680.enabled", "true"));
	}

	public String getBme680Name() {
		return properties.getProperty("bme680.name", "BME680");
	}

	public boolean isPmsEnabled() {
		return Boolean.parseBoolean(properties.getProperty("pms.enabled", "true"));
	}

	public String getPmsName() {
		return properties.getProperty("pms.name", "PMS7003");
	}

	public String getPmsDevice() {
		return properties.getProperty("pms.device", "/dev/ttyS0");
	}
	
	public int getAppInterval() {
		return Integer.parseInt(properties.getProperty("app.interval", "30"));
	}

	public String getMqttBroker() {
		return properties.getProperty("mqtt.broker", "tcp://localhost:1883");
	}

	public String getMqttDiscoveryPrefix() {
		return properties.getProperty("mqtt.discovery_prefix", "homeassistant");
	}

	public float getBme680TemperatureOffset() {
		return Float.parseFloat(properties.getProperty("bme680.temperature.offset", "0"));
	}

	public float getBme680HumidityOffset() {
		return Float.parseFloat(properties.getProperty("bme680.humidity.offset", "0"));
	}

	public float getBme680PressureOffset() {
		return Float.parseFloat(properties.getProperty("bme680.pressure.offset", "0"));
	}

}
