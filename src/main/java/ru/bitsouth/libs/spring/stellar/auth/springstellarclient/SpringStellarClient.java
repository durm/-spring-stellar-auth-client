package ru.bitsouth.libs.spring.stellar.auth.springstellarclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.bitsouth.libs.spring.stellar.auth.springstellarclient.models.ChallengeResponse;
import ru.bitsouth.libs.spring.stellar.auth.springstellarclient.models.JwtTokenRequest;
import ru.bitsouth.libs.spring.stellar.auth.springstellarclient.models.JwtTokenResponse;
import shadow.com.moandjiezana.toml.Toml;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class SpringStellarClient {
    private final RestTemplate restTemplate;
    private final Toml toml;

    public @NotBlank String issueToken(@NotBlank String publicKey, @Nullable String homeDomain) {
        log.info("issue jwt token for [{}] and home_domain [{}]", publicKey, homeDomain);

        String webAuthEndpoint = toml.getString("WEB_AUTH_ENDPOINT");
        log.debug("web auth endpoint: {}", webAuthEndpoint);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(webAuthEndpoint)
                .queryParam("account", publicKey);

        if (StringUtils.hasText(homeDomain)) {
            builder = builder.queryParam("home_domain", homeDomain);
        }
        String challengeUrl = builder.build().toUriString();
        log.debug("challenge url: {}", challengeUrl);

        ChallengeResponse challengeResponse = restTemplate.getForObject(challengeUrl, ChallengeResponse.class);

        JwtTokenRequest jwtTokenRequest = JwtTokenRequest.builder()
                .transaction(Objects.requireNonNull(challengeResponse).getTransaction())
                .build();
        log.debug("jwt token request: {}", jwtTokenRequest);

        JwtTokenResponse jwtTokenResponse =
                restTemplate.postForObject(webAuthEndpoint, jwtTokenRequest, JwtTokenResponse.class);
        log.debug("jwt token response: {}", jwtTokenResponse);

        String token = Objects.requireNonNull(jwtTokenResponse).getToken();

        log.info("jwt token for [{}] and home_domain [{}] was issued", publicKey, homeDomain);
        log.debug("token: {}", token);

        return token;
    }

}
