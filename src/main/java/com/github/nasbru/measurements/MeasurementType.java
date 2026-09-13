package com.github.nasbru.measurements;

public enum MeasurementType {
	TEMPERATURE("temperature", "Temperature", 2, "°C", null, "temperature"),
	PRESSURE("pressure", "Pressure", 2, "hPa", null, "pressure"),
	HUMIDITY("humidity", "Humidity", 2, "%", null, "humidity"),
	IAQ("iaq", "IAQ", 0, "", null, ""),
	VOC("voc", "VOC", 0, "ppm", null, ""),
	PM1_0("pm1_0", "PM1.0", 0, "µg/m³", null, "pm25"),
	PM2_5("pm2_5", "PM2.5", 0, "µg/m³", new Norm(25, 10), "pm25"),
	PM10("pm10", "PM10", 0, "µg/m³", new Norm(50, 20), "pm25"),
	CO2("co2", "CO2", 0, "ppm", new Norm(400, 400), "carbon_dioxide"),
	WIND("wind", "Wind", 1, "km/h", null, "");

	private final String name;
	private final String displayName;
	private final String unit;
	private final int scale;
	private final Norm norm;
	private final String deviceClass;

	MeasurementType(String name, String displayName, int scale, String unit, Norm norm, String deviceClass) {
		this.name = name;
		this.displayName = displayName;
		this.unit = unit;
		this.scale = scale;
		this.norm = norm;
		this.deviceClass = deviceClass;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public int getScale() {
		return scale;
	}

	public boolean hasNorm() {
		return norm != null;
	}

	public Norm getNorm() {
		return norm;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDeviceClass() {
		return deviceClass;
	}

	public record Norm(int daily, int annual) {
	}
}
