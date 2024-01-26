package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TestService implements ReactiveHealthIndicator {

  private final WebClient webClient;

  private static final String HEALTH_ENDPOINT_FAIL_TYPE = "WARNING";

  @Autowired
  public TestService(@Qualifier("testWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Mono<Health> getHealth(boolean includeDetails) {
    return ReactiveHealthIndicator.super.getHealth(includeDetails);
  }

  @Override
  public Mono<Health> health() {
    return this.webClient.get()
        .uri("https://fc7f6dff-b1b0-424e-bb81-8323a101fdb9.mock.pstmn.io/healthcheck")
        .retrieve()
        .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
            clientResponse -> Mono.empty())
        .toBodilessEntity()
        .map(this::responseToHealth);
  }

  private Health responseToHealth(ResponseEntity<Void> clientResponse) {
    Health.Builder healthBuilder = HttpStatus.OK == clientResponse.getStatusCode() ? Health.up()
        : Health.status(HEALTH_ENDPOINT_FAIL_TYPE);

    return healthBuilder.withDetail("responseStatusCode", clientResponse.getStatusCodeValue())
        .build();
  }
}
