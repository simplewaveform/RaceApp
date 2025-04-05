package com.example.raceapp.config;

import com.example.raceapp.filter.VisitCounterFilter;
import com.example.raceapp.service.VisitCounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class FilterConfig {
    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/logs",
            "/swagger",
            "/v3/api-docs"
    );

    @Bean
    public VisitCounterFilter visitCounterFilter(VisitCounterService visitCounterService) {
        return new VisitCounterFilter(visitCounterService, EXCLUDED_URLS);
    }
}