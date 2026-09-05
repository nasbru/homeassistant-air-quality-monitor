package com.github.nasbru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nasbru.measurements.Measurement;
import com.github.nasbru.sensors.PMSensorReader;

public class PMS7003Launcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(PMS7003Launcher.class);

	public static void main(String[] args) {
		String device = args.length > 0 ? args[0] : "/dev/ttyUSB0";
		String name = args.length > 1 ? args[1] : "PMS7003";

		try (PMSensorReader reader = new PMSensorReader(name, 30, device)) {
			Measurement[] m = reader.getMeasurements();
			for (Measurement measurement : m) {
				LOGGER.info(measurement.toString());
			}
		}
	}
}
