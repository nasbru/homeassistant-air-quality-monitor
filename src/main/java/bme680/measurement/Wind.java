package bme680.measurement;

import java.math.BigDecimal;

public class Wind extends Measurement {
	private Direction direction;
	
	public Wind(float value, String direction) {
		super("wind", new BigDecimal(Float.toString(value)), 1, "km/h");
		this.direction = Direction.valueOf(direction);
	}
	
	public String getDirection() {
		return direction.name();
	}

	@Override
	public boolean hasNorm() {
		return false;
	}

	private enum Direction {
		N, NNE, NE, ENE, E, ESE, SE, SSE, S, SSW, SW, WSW, W, WNW, NW, NNW
	}
}
