package com.anshul.jobprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@EnableScheduling
@SpringBootApplication
public class JobProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobProcessingApplication.class, args);
    }

    @Bean
    public RestTemplate buildRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        restTemplateBuilder.setReadTimeout(Duration.ofSeconds(30));
        restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(30));
        return restTemplateBuilder.build();
    }

}
