package com.example.EdutechAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // Importa SerializationFeature
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Importa para el formato de fecha
import java.text.SimpleDateFormat; 
import java.util.TimeZone; 

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Hibernate6Module());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // ISO 8601 con zona horaria
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // O la zona horaria que prefieras

        mapper.setDateFormat(sdf);
        

        return mapper;
    }
}