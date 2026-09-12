package com.github.nasbru.sensors;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nasbru.PMSensor;
import com.github.nasbru.mqtt.SensorListener;
import com.github.nasbru.measurements.Measurement;
import com.github.nasbru.measurements.PM10;
import com.github.nasbru.measurements.PM1_0;
import com.github.nasbru.measurements.PM2_5;

public class PMSensorReader implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PMSensorReader.class);

	private final String name;
	private final PMSensor sensor;
	private final ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private final int interval;
	private final ArrayList<SensorListener> listeners;

	public PMSensorReader(String name, int period, String serialAddress) {
		this.name = (name == null || name.isBlank()) ? "PMSensor" : name;
		this.interval = period;
		this.listeners = new ArrayList<>();
		this.scheduler = Executors.newScheduledThreadPool(1);

		this.sensor = new PMSensor(serialAddress);
		this.sensor.init();
		this.sensor.passiveMode();
	}

	public PMSensorReader(int period, String serialAddress) {
		this("PMS7003", period, serialAddress);
	}

	public void start() {
		future = scheduler.scheduleAtFixedRate(() -> {
			LOGGER.debug("PMS scheduled task: start");
			try {
				Measurement[] measurements = getMeasurements();
				notifyListeners(measurements);
				for (Measurement m : measurements) {
					LOGGER.debug(m.toString());
				}
			} catch (Exception e) {
				LOGGER.error("Error while reading PMS measurements: " + e.getMessage(), e);
			}
			LOGGER.debug("PMS scheduled task: end");
		}, 30, interval, TimeUnit.SECONDS);
	}

	public Measurement[] getMeasurements() {
		int[] raw = sensor.getMeasurements();
		return new Measurement[] {
			new PM1_0(raw[0]),
			new PM2_5(raw[1]),
			new PM10(raw[2])
		};
	}

	public String getName() {
		return name;
	}

	public void addListener(SensorListener listener) {
		listeners.add(listener);
	}

	protected void notifyListeners(Measurement[] measurements) {
		for (SensorListener listener : listeners) {
			listener.onDataReceived(measurements);
		}
	}

	public void stop() {
		if (future != null) {
			future.cancel(true);
			future = null;
		}
		scheduler.shutdownNow();
	}

	@Override
	public void close() {
		stop();
		sensor.close();
	}
}
