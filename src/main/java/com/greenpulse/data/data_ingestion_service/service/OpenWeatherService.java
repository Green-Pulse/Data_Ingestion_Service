package com.greenpulse.data.data_ingestion_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.data.data_ingestion_service.model.SensorDataEvent;
import com.greenpulse.data.data_ingestion_service.model.WeatherDataEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class OpenWeatherService {

    private static final String LAT = "40.409264"; // Baku
    private static final String LON = "49.867092";
    private static final String URL = "https://api.open-meteo.com/v1/forecast?latitude=" + LAT +
            "&longitude=" + LON + "&hourly=temperature_2m,relative_humidity_2m,windspeed_10m";
    private static final String SensorURL = "https://api.open-meteo.com/v1/forecast"
            + "?latitude=" + LAT
            + "&longitude=" + LON
            + "&hourly=temperature_2m,pressure_msl,dewpoint_2m,rain,windspeed_10m,wind_direction_10m"
            + "&forecast_days=1"
            + "&timezone=auto";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherDataEvent fetchWeatherData() {
        try {

            String json = restTemplate.getForObject(URL, String.class);
            JsonNode root = objectMapper.readTree(json);

            JsonNode hourly = root.path("hourly");

            JsonNode timeArray = hourly.path("time");
            int index = findCurrentTimeIndex(timeArray);

            double temperature = hourly.path("temperature_2m").get(index).asDouble();
            double humidity = hourly.path("relative_humidity_2m").get(index).asDouble();
            double windSpeed = hourly.path("windspeed_10m").get(index).asDouble();
            String time = timeArray.get(index).asText();

            return new WeatherDataEvent(
                    "Baku",
                    temperature,
                    humidity,
                    LocalDateTime.parse(time)
            );

        } catch (Exception e) {
            System.err.println("Error while getting weather data: " + e.getMessage());
            return null;
        }
    }

    public SensorDataEvent fetchSensorData() {
        try {
            String json = restTemplate.getForObject(SensorURL, String.class);
            JsonNode root = objectMapper.readTree(json);

            JsonNode hourly = root.path("hourly");

            JsonNode timeArray = hourly.path("time");
            int index = findCurrentTimeIndex(timeArray);

            double temp = hourly.path("temperature_2m").get(index).asDouble();
            double pres = hourly.path("pressure_msl").get(index).asDouble();
            double dewp = hourly.path("dewpoint_2m").get(index).asDouble();
            double rain = hourly.path("rain").get(index).asDouble();
            double windSpeed = hourly.path("windspeed_10m").get(index).asDouble();
            int windDir = hourly.path("wind_direction_10m").get(index).asInt();
            String time = timeArray.get(index).asText();

            boolean wdNE = isDirectionBetween(windDir, 22, 67);
            boolean wdSE = isDirectionBetween(windDir, 112, 157);
            boolean wdNW = isDirectionBetween(windDir, 292, 337);

            return new SensorDataEvent(
                    "Baku",
                    temp,
                    pres,
                    dewp,
                    rain,
                    windSpeed,
                    wdNE,
                    wdNW,
                    wdSE,
                    LocalDateTime.parse(time)
            );

        } catch (Exception e) {
            System.err.println("Error while getting sensor data: " + e.getMessage());
            return null;
        }
    }

    private boolean isDirectionBetween(int degrees, int min, int max) {
        return degrees >= min && degrees <= max;
    }

    private int findCurrentTimeIndex(JsonNode timeArray) {
        String currentTime = LocalDateTime.now().withSecond(0).withNano(0).toString();
        for (int i = 0; i < timeArray.size(); i++) {
            String t = timeArray.get(i).asText();
            if (t.equals(currentTime)) {
                return i;
            }
        }
        return 0; // fallback на 00:00 если текущего нет
    }

}
