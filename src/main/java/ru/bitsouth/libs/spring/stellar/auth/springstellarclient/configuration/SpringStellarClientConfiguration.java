package ru.bitsouth.libs.spring.stellar.auth.springstellarclient.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import shadow.com.moandjiezana.toml.Toml;

import java.time.Duration;


@EnableConfigurationProperties(StellarAuthClientConfigurationProperties.class)
@Configuration
public class SpringStellarClientConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }

    @Bean
    public Toml toml(RestTemplate restTemplate, StellarAuthClientConfigurationProperties properties) {
        String tomlStr = restTemplate.getForObject(properties.getTomlUrl(), String.class);
        return new Toml().read(tomlStr);
    }
}
