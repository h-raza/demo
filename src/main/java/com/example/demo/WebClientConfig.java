package com.example.demo;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  private static final int CODEC = 1024 * 1024;
  private static final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
      .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(CODEC))
      .build();

  @Bean(name = "testWebClient")
  public WebClient testWebClient(@Autowired WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl("https://fc7f6dff-b1b0-424e-bb81-8323a101fdb9.mock.pstmn.io/healthcheck")
        .exchangeStrategies(exchangeStrategies).build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "PATCH", "OPTIONS"));
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
