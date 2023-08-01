package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.RequiredModelMBean;

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

    RequiredModelMBean requiredModelMBean;
    try {
      requiredModelMBean =
          new RequiredModelMBean(
              new ModelMBeanInfoSupport(
                  JMXConfigurator.class.getName(),
                  "Allows to configure Logback during runtime",
                  new ModelMBeanAttributeInfo[0],
                  new ModelMBeanConstructorInfo[0],
                  ModelMBeanOperationInfos.INSTANCE.parseFrom(JMXConfiguratorMBean.class),
                  new ModelMBeanNotificationInfo[0]));
      requiredModelMBean.setManagedResource(
          new JMXConfigurator(context, loggerContext), "objectReference");
    } catch (MBeanException | InstanceNotFoundException | InvalidTargetObjectTypeException e) {
      throw new StartException(e);
    }

    try {
      mBeanServer.registerMBean(requiredModelMBean, objectName);
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

    private StartException(Throwable cause) {
      super(cause);
    }
  }
}
