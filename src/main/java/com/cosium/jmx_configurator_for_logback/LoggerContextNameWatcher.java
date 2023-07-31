package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.LoggerContext;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author Réda Housni Alaoui
 */
class LoggerContextNameWatcher implements AutoCloseable {

  private final ScheduledExecutorService executorService;

  private LoggerContextNameWatcher(
      Configuration configuration, LoggerContext loggerContext, Consumer<String> handler) {
    executorService = Executors.newScheduledThreadPool(1, configuration.threadFactory());
    triggerHandlerIfNeeded(
        configuration.refreshDelay(),
        executorService,
        loggerContext,
        new AtomicReference<>(),
        handler);
  }

  public static LoggerContextNameWatcher start(
      Configuration configuration, LoggerContext loggerContext, Consumer<String> handler) {
    return new LoggerContextNameWatcher(configuration, loggerContext, handler);
  }

  private static void triggerHandlerIfNeeded(
      Duration refreshDelay,
      ScheduledExecutorService executorService,
      LoggerContext loggerContext,
      AtomicReference<String> lastValue,
      Consumer<String> handler) {
    String currentValue = loggerContext.getName();
    if (!Objects.equals(currentValue, lastValue.get())) {
      lastValue.set(currentValue);
      handler.accept(currentValue);
    }
    executorService.schedule(
        () ->
            triggerHandlerIfNeeded(
                refreshDelay, executorService, loggerContext, lastValue, handler),
        refreshDelay.toMillis(),
        TimeUnit.MILLISECONDS);
  }

  @Override
  public void close() {
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
