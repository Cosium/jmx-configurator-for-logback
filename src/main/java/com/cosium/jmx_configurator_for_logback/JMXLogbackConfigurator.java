/*
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2023, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package com.cosium.jmx_configurator_for_logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @author Réda Housni Alaoui
 */
public class JMXLogbackConfigurator extends ContextAwareBase implements Configurator {

  @Override
  public ExecutionStatus configure(LoggerContext loggerContext) {
    addInfo("begin");

    String contextName = loggerContext.getName();

    String objectNameAsStr = MBeanUtil.getObjectNameFor(contextName, JMXConfigurator.class);

    ObjectName objectName = MBeanUtil.string2ObjectName(context, this, objectNameAsStr);
    if (objectName == null) {
      addError("Failed construct ObjectName for [" + objectNameAsStr + "]");
      return ExecutionStatus.NEUTRAL;
    }

    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    if (MBeanUtil.isRegistered(mbs, objectName)) {
      return ExecutionStatus.NEUTRAL;
    }

    // register only of the named JMXConfigurator has not been previously
    // registered. Unregistering an MBean within invocation of itself
    // caused jconsole to throw an NPE. (This occurs when the reload* method
    // unregisters the
    JMXConfigurator jmxConfigurator = new JMXConfigurator(this, loggerContext, mbs, objectName);
    try {
      mbs.registerMBean(jmxConfigurator, objectName);
    } catch (Exception e) {
      addError("Failed to create mbean", e);
    }

    return ExecutionStatus.NEUTRAL;
  }
}
