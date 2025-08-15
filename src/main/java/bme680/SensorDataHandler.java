package bme680;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bme680.measurement.Measurement;

public class SensorDataHandler implements SensorListener{
	private Logger logger = LoggerFactory.getLogger(SensorDataHandler.class); 

	@Override
	public void onDataReceived(Measurement[] measurement, String name) {
		StringBuilder sb = new StringBuilder();
		
		for (Measurement m : measurement) {
			sb.append(m.toString());
			if(m != measurement[5]) sb.append(" | ");
		}

		logger.info(sb.toString());
	}
	
}
