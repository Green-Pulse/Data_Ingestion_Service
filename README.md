#  Data Ingestion Service

The **Data Ingestion Service** is responsible for fetching weather data from a public API, transforming it into internal format and forwarding it for analytics and storage. It acts as a bridge between **external weather APIs** and internal systems (AI, Kafka, monitoring).

---

##  Technologies

- Java 21  
- Spring Boot 3.4.4  
- RestTemplate (for HTTP requests)  
- Kafka (message queue for sending weather data)  
- Prometheus + Grafana (for observability)  
- SLF4J + Logback (logging to files)

---


##  External API

The service fetches weather data from **Open-Meteo** API

Configured for a specific location using:

```java
latitude = 
longitude = 
```

---

##  Weather Data Model (`WeatherDataEvent`)

```java
public class WeatherDataEvent {
    private String city;
    private double temperature;
    private double humidity;
    private LocalDateTime timestamp;
}
```

This data is published to **Kafka**

---

##  Scheduled Fetching (optional)

You can configure the service to pull data every X minutes using `@Scheduled`:

```java
@Scheduled(fixedRate = 60000)
public void fetchAndSend() {
    WeatherDataEvent event = openWeatherService.fetchWeatherData();
    kafkaTemplate.send("weather-data-event-topic", event);
}
```

---

##  Sample Response from OpenWeather

```json
{
  "hourly": {
    "time": ["2024-01-01T00:00", "2024-01-01T01:00"],
    "temperature_2m": [2.1, 1.9],
    "relative_humidity_2m": [87, 85],
    "windspeed_10m": [1.2, 1.1]
  }
}
```

---

##  Logging (Logback)

Weather logs are saved to:

```
./logs/app.log
```

---

##  Monitoring with Grafana

The service exposes Prometheus metrics at:

```
/actuator/prometheus
```

Prometheus config example:

```yaml
scrape_configs:
  - job_name: 'data-ingestion-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
```

Grafana can visualize metrics such as:

- HTTP request duration
- Memory usage
- Uptime

---

##  Running the service

```bash
./gradlew bootRun
```

Make sure:

- Kafka is running
- Weather API is accessible
- Prometheus + Grafana are configured (optional)

---

##  Future Improvements

- Error retries on failed requests  
- Location input from frontend  
- Dynamic schedule via DB or API  
- Export to storage/DB  
