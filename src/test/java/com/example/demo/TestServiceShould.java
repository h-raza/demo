package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestServiceShould {

  @Mock
  WireMockServer wireMockServer;

  @Autowired
  WebClient.Builder webClientBuilder;

  private TestService testService;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
    wireMockServer.start();

    this.testService = new TestService(webClientBuilder.baseUrl(wireMockServer.baseUrl()).build());
  }

  @Test
  public void test_one() {
    wireMockServer.stubFor(
        WireMock.get("https://fc7f6dff-b1b0-424e-bb81-8323a101fdb9.mock.pstmn.io/healthcheck")
            .willReturn(WireMock.aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("Hello"))
    );

    Mono<Health> healthMono = testService.health();

    StepVerifier.create(healthMono)
        .expectSubscription()
        .consumeNextWith(health -> System.out.println(health + " OOO"))
        .verifyComplete();
  }


}
