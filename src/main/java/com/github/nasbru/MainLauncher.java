package com.github.nasbru;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nasbru.sensors.BME680Reader;
import com.github.nasbru.sensors.PMSensorReader;

public class MainLauncher {
	private static BME680Reader bme680Reader = null;
	private static PMSensorReader pmsReader = null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainLauncher.class);
	
	public static void main(String[] args) {
		
		Config config = new Config();
		int interval = config.getAppInterval();
		boolean bme680Enabled = config.isBme680Enabled();
		boolean pmsEnabled = config.isPmsEnabled();
		
		if(!bme680Enabled && !pmsEnabled) {
			LOGGER.error("No sensors enabled in config file. Exiting.");
			return;
		}
		
		String broker = config.getMqttBroker();
		String prefix = config.getMqttDiscoveryPrefix();
		
		if(bme680Enabled) {
			bme680Reader = new BME680Reader(interval, config);
			String bme680Name = bme680Reader.getName();
			SensorDataHandler bme680DataHandler = new SensorDataHandler(bme680Name);
			bme680DataHandler.setMqttConfig(broker, prefix);
			try {
				bme680DataHandler.initMqtt();
			} catch (MqttException e) {
				LOGGER.error("Error initializing MQTT for BME680: " + e.getMessage(), e);
				return;
			}
			bme680Reader.addListener(bme680DataHandler);
			Thread bme680Thread = new Thread(bme680Reader);
		    bme680Thread.start();
		}
		if(pmsEnabled) {
			String pmsName = config.getPmsName();
			String pmsDevice = config.getPmsDevice();
			pmsReader = new PMSensorReader(pmsName, interval, pmsDevice);
			String sensorName = pmsReader.getName();
			SensorDataHandler pmsDataHandler = new SensorDataHandler(sensorName);
			pmsDataHandler.setMqttConfig(broker, prefix);
			try {
				pmsDataHandler.initMqtt();
			} catch (MqttException e) {
				LOGGER.error("Error initializing MQTT for " + sensorName + ": " + e.getMessage(), e);
				return;
			}
			pmsReader.addListener(pmsDataHandler);
			pmsReader.start();
		}
	    
	 // Keep application running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down...");
            if(bme680Reader != null) bme680Reader.stopReading();
            if(pmsReader != null) pmsReader.close();
        }));

        // Wait indefinitely
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            LOGGER.error("Main thread interrupted", e);
            Thread.currentThread().interrupt();
        }
	}
}
