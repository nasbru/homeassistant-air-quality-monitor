package bme680;

public class MainLauncher {
	public static void main(String[] args) {
		BME680Reader reader = new BME680Reader(30);
		Thread thread = new Thread(reader);
		thread.start();
	}
}
