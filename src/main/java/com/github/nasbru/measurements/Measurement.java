package com.github.nasbru.measurements;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Measurement {
	private final MeasurementType type;
	private final BigDecimal value;

	public Measurement(MeasurementType type, BigDecimal value) {
		this.type = type;
		if ((value.compareTo(BigDecimal.ZERO) < 0) && type != MeasurementType.TEMPERATURE)
			throw new IllegalArgumentException( type.getDisplayName() + " cannot be negative: " + value.toString());
		this.value = value.setScale(type.getScale(), RoundingMode.HALF_UP);
	}

	public MeasurementType getType() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	public BigDecimal getValue() {
		return value;
	}

	public String getUnit() {
		return type.getUnit();
	}

	public int percentValueToNorm() {
		if (!hasNorm())
			throw new IllegalStateException("No norm defined for " + type.getName() + ".");
		float result = (value.floatValue() / type.getNorm().daily()) * 100;
		return (int) result;
	}

	@Override
	public String toString() {
		String result = type.getName() + ": " + getValue() + type.getUnit();
		if (hasNorm())
			result += " (" + percentValueToNorm() + "%)";
		return result;
	}

	public boolean hasNorm() {
		return type.hasNorm();
	}
}