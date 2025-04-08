package com.greenpulse.data.data_ingestion_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherDataEvent fetchWeatherData() {
        try {
            String json = restTemplate.getForObject(URL, String.class);
            JsonNode root = objectMapper.readTree(json);

            JsonNode hourly = root.path("hourly");
            JsonNode temperature = hourly.path("temperature_2m").get(0);
            JsonNode humidity = hourly.path("relative_humidity_2m").get(0);
            JsonNode windSpeed = hourly.path("windspeed_10m").get(0);
            String time = hourly.path("time").get(0).asText();

            return new WeatherDataEvent(
                    "Baku",
                    temperature.asDouble(),
                    humidity.asDouble(),
                    LocalDateTime.parse(time)
            );

        } catch (Exception e) {
            System.err.println("Error while getting weather data: " + e.getMessage());
            return null;
        }
    }
}
