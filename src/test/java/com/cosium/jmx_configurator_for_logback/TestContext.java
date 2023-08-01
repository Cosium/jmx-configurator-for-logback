package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.LoggerContext;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;

/**
 * @author Réda Housni Alaoui
 */
public class TestContext implements ConfigurationCustomizer {

  public Runnable jmxRegistrationCallback;

  public void reset() {
    jmxRegistrationCallback = null;

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.stop();
    LoggerFactoryFriend.reset();
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
