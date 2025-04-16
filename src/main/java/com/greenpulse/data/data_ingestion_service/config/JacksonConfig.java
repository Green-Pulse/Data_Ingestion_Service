package com.greenpulse.data.data_ingestion_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Добавляем поддержку Java 8 времени
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO формат
        return mapper;
    }
}
