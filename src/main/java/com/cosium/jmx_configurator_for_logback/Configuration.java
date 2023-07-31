package com.cosium.jmx_configurator_for_logback;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Réda Housni Alaoui
 */
public class Configuration {

  private final List<JMXRegistrationListener> jmxRegistrationListeners;
  private final ThreadFactory threadFactory;
  private final Duration refreshDelay;

  private Configuration(Builder builder) {
    jmxRegistrationListeners = List.copyOf(builder.jmxRegistrationListeners);
    threadFactory = requireNonNull(builder.threadFactory);
    refreshDelay = requireNonNull(builder.refreshDelay);
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<JMXRegistrationListener> jmxRegistrationListeners() {
    return jmxRegistrationListeners;
  }

  public ThreadFactory threadFactory() {
    return threadFactory;
  }

  public Duration refreshDelay() {
    return refreshDelay;
  }

  public static class Builder {

    private final List<JMXRegistrationListener> jmxRegistrationListeners = new ArrayList<>();
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private Duration refreshDelay = Duration.of(5, ChronoUnit.SECONDS);

    private Builder() {}

    public Builder addJmxRegistrationListener(JMXRegistrationListener listener) {
      jmxRegistrationListeners.add(listener);
      return this;
    }

    public Builder threadFactory(ThreadFactory threadFactory) {
      this.threadFactory = threadFactory;
      return this;
    }

    public Builder refreshDelay(Duration refreshDelay) {
      this.refreshDelay = refreshDelay;
      return this;
    }

    public Configuration build() {
      return new Configuration(this);
    }
  }
}
