package ru.bitsouth.libs.spring.stellar.auth.springstellarclient.configuration;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class StellarAuthClientConfigurationProperties {
    @NotBlank
    @URL(regexp = "(?i)(http|https):")
    private String tomlUrl;
}
