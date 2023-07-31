package com.cosium.jmx_configurator_for_logback;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author Réda Housni Alaoui
 */
public class TestContext implements ConfigurationCustomizer {

  public Runnable jmxRegistrationCallback;

  public void reset() {
    jmxRegistrationCallback = null;
  }

  @Override
  public void customize(Configuration.Builder configurationBuilder) {
    configurationBuilder
        .refreshDelay(Duration.of(50, ChronoUnit.MILLIS))
        .addJmxRegistrationListener(this::onRegistration);
  }

  private void onRegistration() {
    if (jmxRegistrationCallback == null) {
      return;
    }
    jmxRegistrationCallback.run();
    jmxRegistrationCallback = null;
  }
}
