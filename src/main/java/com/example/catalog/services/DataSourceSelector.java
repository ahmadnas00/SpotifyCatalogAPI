package com.example.catalog.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceSelector {

    @Value("${datasource.type:json}") // Default to 'json' if not specified
    private String dataSourceType;

    @Bean
    public DataSourceService dataSourceService(JSONDataSourceService jsonDataSourceService,
                                               SpotifyAPIDataSources spotifyAPIDataSources) {
        if ("json".equalsIgnoreCase(dataSourceType)) {
            return jsonDataSourceService;
        } else if ("spotify_api".equalsIgnoreCase(dataSourceType)) {
            return spotifyAPIDataSources;
        }else{
                throw new IllegalArgumentException("Invalid data source type: " + dataSourceType);
            }
        }
    }
