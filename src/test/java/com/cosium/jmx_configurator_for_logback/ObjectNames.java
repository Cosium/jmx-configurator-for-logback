package com.cosium.jmx_configurator_for_logback;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author Réda Housni Alaoui
 */
enum ObjectNames {
  INSTANCE;

  public ObjectName createDefaultObjectName() {
    return createObjectName("default");
  }

  public ObjectName createObjectName(String contextName) {
    try {
      return ObjectName.getInstance(
          "com.cosium.jmx_configurator_for_logback:Name=%s,Type=com.cosium.jmx_configurator_for_logback.JMXConfigurator"
              .formatted(contextName));
    } catch (MalformedObjectNameException e) {
      throw new RuntimeException(e);
    }
  }
}
