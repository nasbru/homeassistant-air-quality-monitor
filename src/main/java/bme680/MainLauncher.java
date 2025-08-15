package bme680;

public class MainLauncher {
	public static void main(String[] args) {
		System.out.println("Start");
		BME680Reader reader = new BME680Reader(30);
		SensorDataHandler sdh = new SensorDataHandler();
		reader.addListener(sdh);
		
		Thread thread = new Thread(reader);
		thread.start();
	}
}
