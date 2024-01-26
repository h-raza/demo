package com.example.demo;

import com.google.common.base.Joiner;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.util.List;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  protected static final String HEALTH_METRIC_NAME = "health";
  protected static final String SERVICE_TYPE_LABEL = "service";
  protected static final String DETAILS_TYPE = "details";
  protected static final String APP_NAME_LABEL = "application";
  protected static final String APP_NAME_VALUE = "demo-server";

  @Bean
  public MeterRegistryCustomizer<MeterRegistry> healthMetricsRegistration(
      HealthContributorRegistry healthRegistry) {
    return meterRegistry -> healthRegistry
        .stream()
        .forEach(contributer -> {
          Tags tags = generateMetricLabels(contributer);
          registerHealthGauge(healthRegistry, meterRegistry, contributer, tags);
        });
  }

  private Tags generateMetricLabels(NamedContributor<HealthContributor> contributor) {
    String healthDetails = extractHealthDetails(contributor);
    return Tags.of(
        List.of(
            Tag.of(SERVICE_TYPE_LABEL, contributor.getName()),
            Tag.of(APP_NAME_LABEL, APP_NAME_VALUE),
            Tag.of(DETAILS_TYPE, healthDetails)
        )
    );
  }

  private void registerHealthGauge(HealthContributorRegistry healthRegistry,
      MeterRegistry meterRegistry, NamedContributor<HealthContributor> contributor, Tags tags) {
    meterRegistry.gauge(HEALTH_METRIC_NAME, tags, healthRegistry, health -> {
      Status status = ((HealthIndicator) health.getContributor(contributor.getName())).getHealth(
          false).getStatus();
      return healthStatusToCode(status);
    });

  }

  private String extractHealthDetails(NamedContributor<HealthContributor> contributor) {
    return Joiner.on(",")
        .withKeyValueSeparator(":")
        .join(((HealthIndicator) contributor.getContributor()).getHealth(true).getDetails());
  }

  private static int healthStatusToCode(Status status) {
    return status.equals(Status.UP) ? 1 : 0;
  }

}
