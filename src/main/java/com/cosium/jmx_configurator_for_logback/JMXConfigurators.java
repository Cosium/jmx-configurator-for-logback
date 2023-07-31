package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * @author Réda Housni Alaoui
 */
class JMXConfigurators implements LoggerContextListener {

  private final ContextAwareBase context;
  private final Configuration configuration;
  private LoggerContextNameWatcher loggerContextNameWatcher;
  private JMXConfiguratorSession currentSession;

  public JMXConfigurators(ContextAwareBase context, Configuration configuration) {
    this.context = context;
    this.configuration = configuration;
  }

  @Override
  public synchronized void onStart(LoggerContext loggerContext) {
    loggerContextNameWatcher =
        LoggerContextNameWatcher.start(
            configuration,
            loggerContext,
            loggerContextName -> start(loggerContext, loggerContextName));
  }

  private void start(LoggerContext loggerContext, String contextName) {
    stopSession();
    startSession(loggerContext, contextName);
  }

  private void startSession(LoggerContext loggerContext, String contextName) {
    try {
      currentSession = JMXConfiguratorSession.start(context, loggerContext, contextName);
      configuration.jmxRegistrationListeners().forEach(JMXRegistrationListener::execute);
    } catch (JMXConfiguratorSession.StartException e) {
      context.addError(e.getMessage(), e);
    }
  }

  @Override
  public synchronized void onStop(LoggerContext loggerContext) {
    stopLoggerContextNameWatcher();
    stopSession();
  }

  private void stopLoggerContextNameWatcher() {
    if (loggerContextNameWatcher == null) {
      return;
    }
    loggerContextNameWatcher.close();
    loggerContextNameWatcher = null;
  }

  private void stopSession() {
    if (currentSession == null) {
      return;
    }
    currentSession.close();
    currentSession = null;
  }

  @Override
  public void onReset(LoggerContext loggerContext) {
    // Do nothing
  }

  @Override
  public boolean isResetResistant() {
    return true;
  }

  @Override
  public void onLevelChange(Logger logger, Level level) {
    // nothing to do
  }
}
