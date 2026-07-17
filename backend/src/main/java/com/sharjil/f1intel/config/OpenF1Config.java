package com.sharjil.f1intel.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class OpenF1Config {

    @Bean
    public RestClient openF1RestClient(@Value("${openf1.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl).build();
    }

    @Bean
    public JsonMapper openF1JsonMapper() {
        return JsonMapper.builder().build();
    }
}
