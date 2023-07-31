package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
 * @author Réda Housni Alaoui
 */
class JMXConfiguratorSession implements AutoCloseable {

  private final ContextAwareBase context;
  private final String contextName;
  private final MBeanServer mBeanServer;
  private final ObjectName objectName;

  private JMXConfiguratorSession(
      ContextAwareBase context, LoggerContext loggerContext, String contextName)
      throws StartException {

    context.addInfo(
        "Starting %s for context named '%s'"
            .formatted(JMXConfiguratorSession.class.getName(), contextName));

    this.context = context;
    this.contextName = contextName;

    String objectNameAsStr =
        "com.cosium.jmx_configurator_for_logback:Name=%s,Type=%s"
            .formatted(contextName, JMXConfigurator.class.getName());

    objectName = convertStringToObjectName(loggerContext, objectNameAsStr);
    if (objectName == null) {
      throw new StartException("Failed construct ObjectName for [%s]".formatted(objectNameAsStr));
    }

    mBeanServer = ManagementFactory.getPlatformMBeanServer();
    if (mBeanServer.isRegistered(objectName)) {
      return;
    }

    try {
      mBeanServer.registerMBean(new JMXConfigurator(context, loggerContext), objectName);
    } catch (RuntimeException
        | InstanceAlreadyExistsException
        | MBeanRegistrationException
        | NotCompliantMBeanException e) {
      this.context.addError("Failed to create mbean", e);
    }
  }

  public static JMXConfiguratorSession start(
      ContextAwareBase context, LoggerContext loggerContext, String contextName)
      throws StartException {
    return new JMXConfiguratorSession(context, loggerContext, contextName);
  }

  private ObjectName convertStringToObjectName(Context context, String objectNameAsStr) {
    String msg = "Failed to convert [%s] to ObjectName".formatted(objectNameAsStr);

    StatusUtil statusUtil = new StatusUtil(context);
    try {
      return new ObjectName(objectNameAsStr);
    } catch (MalformedObjectNameException | NullPointerException e) {
      statusUtil.addError(this, msg, e);
      return null;
    }
  }

  @Override
  public void close() {
    context.addInfo(
        "Stopping %s for context named '%s'"
            .formatted(JMXConfiguratorSession.class.getName(), contextName));

    if (!mBeanServer.isRegistered(objectName)) {
      context.addInfo(
          "mbean [%s] was not in the mbean registry. This is OK.".formatted(objectName));
      return;
    }

    try {
      context.addInfo("Unregistering mbean [%s]".formatted(objectName));
      mBeanServer.unregisterMBean(objectName);
    } catch (InstanceNotFoundException e) {
      // this is theoretically impossible
      context.addError(
          "Unable to find a verifiably registered mbean [%s]".formatted(objectName), e);
    } catch (MBeanRegistrationException e) {
      context.addError("Failed to unregister [%s]".formatted(objectName), e);
    }
  }

  public static class StartException extends Exception {

    private StartException(String message) {
      super(message);
    }
  }
}
