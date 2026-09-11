# homeassistant-air-quality-monitor

A Java application for monitoring air quality with automatic integration into **Home Assistant** via MQTT Discovery.

Supports the **BME680** sensor (temperature, humidity, pressure, IAQ, CO₂, VOC) and **Plantower PMS** particulate matter sensors (PM1.0, PM2.5, PM10).

---

## Features

- 🌡️ Data readout from the **BME680** sensor (BSEC) – temperature, humidity, pressure, IAQ, eCO₂, VOC
- 💨 Data readout from **Plantower PMS** particulate sensors (e.g. PMS7003, PMS5003) – PM1.0, PM2.5, PM10
- 📡 Publishing readings to an **MQTT** broker
- 🏠 Automatic entity registration in **Home Assistant** via MQTT Discovery
- ⚙️ Configurable calibration offsets for BME680
- 🔄 Automatic reconnection to the MQTT broker after connection loss

---

## Requirements

### Hardware
- **BME680** sensor connected via I2C
- **Plantower PMS** particulate sensor (e.g. PMS7003, PMS5003) connected via serial port
- A Linux-based device (e.g. Raspberry Pi)

### Software
- Java 17+
- Maven 3.x
- A running MQTT broker (e.g. Mosquitto)
- Home Assistant with MQTT integration enabled
- `bsec_bme680` binary (from Bosch BSEC SDK) placed in the `data/` directory *(required for BME680)*

---

## Obtaining the `bsec_bme680` binary

The BME680 sensor requires the `bsec_bme680` binary, which is built from the official **Bosch BSEC SDK** (proprietary, requires accepting a license agreement). The recommended way to build it on Linux/Raspberry Pi is via the [`bsec_bme680_linux`](https://github.com/alexh-name/bsec_bme680_linux) project.

### Steps

1. **Download the BSEC library** from the [Bosch Sensortec website](https://www.bosch-sensortec.com/software-tools/software/bsec/).
   Accept the license agreement and download the `.zip` package.

2. **Clone the build helper repository** on your device:

   ```bash
   git clone https://github.com/alexh-name/bsec_bme680_linux.git
   cd bsec_bme680_linux
   ```

3. **Place the BSEC SDK** inside the `src/` subdirectory:

   ```bash
   mkdir src
   # unzip the downloaded Bosch package into src/
   unzip /path/to/BSEC_*.zip -d src/
   ```

4. **Edit `make.config`** to match your setup:
   - `BSEC_DIR` — set to the exact name of the extracted directory inside `src/`
   - `ARCH` — select the correct path for your hardware architecture (e.g. `normal_version/bin/RaspberryPi/PiThree_ArmV8-a-64bits` for 64-bit RPi 3/4/5, or `normal_version/bin/RaspberryPi/PiZero_ArmV6-32bits` for Pi Zero)

5. **Compile:**

   ```bash
   ./make.sh
   ```

6. **Copy the resulting binary** to the `data/` directory of this project:

   ```bash
   cp bsec_bme680 /opt/air-monitor/data/
   chmod +x /opt/air-monitor/data/bsec_bme680
   ```

> [!NOTE]
> The `bsec_bme680` binary must be present in the `data/` directory (the application's working directory) before starting the service. The BSEC library is proprietary — review Bosch's license terms before distributing your build.

---

## Building

```bash
mvn package
```

This creates `target/homeassistant-air-quality-monitor-<version>.jar` — a fat JAR with all dependencies included.

---

## Configuration

The configuration file is located at `data/config.properties`. Example content:

```properties
# APP
# Data publishing interval (seconds)
app.interval = 30

# MQTT
mqtt.broker = tcp://localhost:1883
mqtt.discovery_prefix = homeassistant

# BME680
bme680.enabled = true
# Temperature offset (°C)
bme680.temperature.offset = -0.9
# Humidity offset (%)
bme680.humidity.offset = 0
# Pressure offset (hPa)
bme680.pressure.offset = 0

# Particulate sensor (e.g. PMS7003, PMS5003)
pms.enabled = true
pms.name = PMS7003
pms.device = /dev/ttyUSB0
```

### Parameters

| Parameter | Default value | Description |
|---|---|---|
| `app.interval` | `30` | Reading publish interval (seconds) |
| `mqtt.broker` | `tcp://localhost:1883` | MQTT broker address |
| `mqtt.discovery_prefix` | `homeassistant` | Home Assistant MQTT Discovery prefix |
| `bme680.enabled` | `true` | Enables BME680 sensor support |
| `bme680.temperature.offset` | `0` | Temperature reading correction (°C) |
| `bme680.humidity.offset` | `0` | Humidity reading correction (%) |
| `bme680.pressure.offset` | `0` | Pressure reading correction (hPa) |
| `pms.enabled` | `true` | Enables PMS sensor support |
| `pms.name` | `PMS7003` | Sensor name (visible in Home Assistant) |
| `pms.device` | `/dev/ttyS0` | Serial port path for the PMS sensor |

---

## Running

```bash
java -jar target/homeassistant-air-quality-monitor-*.jar
```

You can also run it directly via Maven:

```bash
mvn exec:java
```

> **Note:** The working directory must contain the `data/` subdirectory with `config.properties` and the `bsec_bme680` binary (if using the BME680 sensor).

---

## MQTT Topics

Readings are published to:

```
home/<sensor_name>_1/<measurement_type>
```

Examples for BME680:
```
home/BME680_1/temperature
home/BME680_1/humidity
home/BME680_1/pressure
home/BME680_1/iaq
home/BME680_1/co2
home/BME680_1/voc
```

Examples for PMS7003:
```
home/PMS7003_1/pm1_0
home/PMS7003_1/pm2_5
home/PMS7003_1/pm10
```

MQTT Discovery configuration is published automatically on first launch to:
```
homeassistant/sensor/<sensor_name>_1_<measurement_type>/config
```

Examples:
```
homeassistant/sensor/BME680_1_temperature/config
homeassistant/sensor/PMS7003_1_pm2_5/config
```

---

## Running as a systemd Service

The service should start **after** Home Assistant is ready, since it publishes MQTT Discovery messages on startup. The configuration differs depending on whether this application runs on the **same device** as Home Assistant or on a **separate device** (e.g. a Raspberry Pi dedicated to sensors).

### Home Assistant on a remote device

If Home Assistant runs on a different machine, there is no local service dependency. The only requirement is an active network connection before starting.

```ini
[Unit]
Description=Home Assistant Air Quality Monitor
After=network-online.target
Wants=network-online.target

[Service]
WorkingDirectory=/opt/air-monitor
Type=simple
ExecStart=/usr/bin/java -jar /opt/air-monitor/homeassistant-air-quality-monitor.jar
Restart=on-failure
RestartSec=10
User=youruser

[Install]
WantedBy=multi-user.target
```

> The application will automatically reconnect to the MQTT broker if it is not yet available at startup.

### Home Assistant on the same device — running in Docker

Waits for the `homeassistant` container to be in a running state before starting the application.

```ini
[Unit]
Description=Home Assistant Air Quality Monitor
After=network.target docker.service
Requires=docker.service

[Service]
WorkingDirectory=/opt/air-monitor
Type=simple
ExecStartPre=/bin/sh -c 'until [ "$(docker inspect -f {{.State.Running}} homeassistant 2>/dev/null)" = "true" ]; do sleep 2; done'
ExecStart=/usr/bin/java -jar /opt/air-monitor/homeassistant-air-quality-monitor.jar
Restart=on-failure
User=youruser

[Install]
WantedBy=multi-user.target
```

> Replace `homeassistant` with the actual name of your Home Assistant container if it differs.

### Home Assistant on the same device — native systemd service

If Home Assistant runs directly as a systemd service (e.g. `home-assistant.service`), use `After=` and `Wants=` to order the services correctly.

```ini
[Unit]
Description=Home Assistant Air Quality Monitor
After=network.target home-assistant.service
Wants=home-assistant.service

[Service]
WorkingDirectory=/opt/air-monitor
Type=simple
ExecStart=/usr/bin/java -jar /opt/air-monitor/homeassistant-air-quality-monitor.jar
Restart=on-failure
RestartSec=10
User=youruser

[Install]
WantedBy=multi-user.target
```

> The exact service name may vary — check with `systemctl list-units | grep home` to find the correct one.

Enable and start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now air-monitor
```

---

## Dependencies

| Library | Version | Description |
|---|---|---|
| [plantower-pms-java](https://github.com/nasbru/plantower-pms-java) | v0.1.0 | Plantower PMS sensor support |
| Eclipse Paho MQTT Client | 1.2.5 | MQTT client |
| Apache Log4j 2 | 2.24.3 | Logging |
| SLF4J API | 2.0.17 | Logging facade |

---

## License

This project is available as open source. See the `LICENSE` file for details.
