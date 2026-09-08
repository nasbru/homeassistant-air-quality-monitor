package com.github.nasbru;

import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nasbru.sensors.BME680Reader;
import com.github.nasbru.sensors.PMSensorReader;

public class MainLauncher {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainLauncher.class);

	public static void main(String[] args) {
		Config config = new Config();
		int interval = config.getAppInterval();
		boolean bme680Enabled = config.isBme680Enabled();
		boolean pmsEnabled = config.isPmsEnabled();

		if (!bme680Enabled && !pmsEnabled) {
			LOGGER.error("No sensors enabled in config file. Exiting.");
			return;
		}

		String broker = config.getMqttBroker();
		String prefix = config.getMqttDiscoveryPrefix();

		CountDownLatch shutdownLatch = new CountDownLatch(1);
		Thread mainThread = Thread.currentThread();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOGGER.info("Shutting down...");
			shutdownLatch.countDown();
			try {
				mainThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}));
		
		String bme680Name = config.getBme680Name();
		String pmsName = config.getPmsName();

		try (
			SensorDataHandler bme680DataHandler = bme680Enabled ? new SensorDataHandler(bme680Name) : null;
			SensorDataHandler pmsDataHandler = pmsEnabled ? new SensorDataHandler(pmsName) : null;
			BME680Reader bme680Reader = bme680Enabled ? new BME680Reader(interval, config) : null;
			PMSensorReader pmsReader = pmsEnabled ? new PMSensorReader(pmsName, interval, config.getPmsDevice()) : null
		) {
			if (bme680Enabled) {
				bme680DataHandler.setMqttConfig(broker, prefix);
				bme680DataHandler.initMqtt();
				bme680Reader.addListener(bme680DataHandler);
				Thread bme680Thread = new Thread(bme680Reader);
				bme680Thread.start();
			}

			if (pmsEnabled) {
				pmsDataHandler.setMqttConfig(broker, prefix);
				pmsDataHandler.initMqtt();
				pmsReader.addListener(pmsDataHandler);
				pmsReader.start();
			}
			
			shutdownLatch.await();

		} catch (InterruptedException e) {
			LOGGER.warn("Main thread interrupted while waiting for shutdown", e);
			Thread.currentThread().interrupt();
		} catch (MqttException e) {
			LOGGER.error("Error initializing MQTT: " + e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error("Error running application: " + e.getMessage(), e);
		}
	}
}
