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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.Status;
import java.util.ArrayList;
import java.util.List;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * A class that provides access to logback components via JMX.
 *
 * <p>Since this class implements {@link JMXConfiguratorMBean} it has to be named as
 * JMXConfigurator}.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 *     <p>Contributor: Sebastian Davids See <a
 *     href="http://bugzilla.qos.ch/show_bug.cgi?id=35">http://bugzilla.qos.ch/show_bug.cgi?id=35</a>
 */
class JMXConfigurator implements JMXConfiguratorMBean, LoggerContextListener {

  private static final String EMPTY = "";

  private final ContextAwareBase context;
  private LoggerContext loggerContext;
  private MBeanServer mbs;
  private ObjectName objectName;
  private final String objectNameAsString;

  private boolean started;

  public JMXConfigurator(
      ContextAwareBase context,
      LoggerContext loggerContext,
      MBeanServer mbs,
      ObjectName objectName) {
    started = true;
    this.context = context;
    this.loggerContext = loggerContext;
    this.mbs = mbs;
    this.objectName = objectName;
    this.objectNameAsString = objectName.toString();
    if (previouslyRegisteredListenerWithSameObjectName()) {
      context.addError(
          "Previously registered JMXConfigurator named ["
              + objectNameAsString
              + "] in the logger context named ["
              + loggerContext.getName()
              + "]");
    } else {
      // register as a listener only if there are no homonyms
      loggerContext.addListener(this);
    }
  }

  private boolean previouslyRegisteredListenerWithSameObjectName() {
    List<LoggerContextListener> lcll = loggerContext.getCopyOfListenerList();
    for (LoggerContextListener lcl : lcll) {
      if (lcl instanceof JMXConfigurator jmxConfigurator
          && objectName.equals(jmxConfigurator.objectName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setLoggerLevel(String loggerName, String levelStr) {
    if (loggerName == null) {
      return;
    }
    if (levelStr == null) {
      return;
    }
    loggerName = loggerName.trim();
    levelStr = levelStr.trim();

    context.addInfo("Trying to set level " + levelStr + " to logger " + loggerName);

    Logger logger = loggerContext.getLogger(loggerName);
    if ("null".equalsIgnoreCase(levelStr)) {
      logger.setLevel(null);
    } else {
      Level level = Level.toLevel(levelStr, null);
      if (level != null) {
        logger.setLevel(level);
      }
    }
  }

  @Override
  public String getLoggerLevel(String loggerName) {
    if (loggerName == null) {
      return EMPTY;
    }

    loggerName = loggerName.trim();

    Logger logger = loggerContext.exists(loggerName);
    if (logger != null && logger.getLevel() != null) {
      return logger.getLevel().toString();
    } else {
      return EMPTY;
    }
  }

  @Override
  public String getLoggerEffectiveLevel(String loggerName) {
    if (loggerName == null) {
      return EMPTY;
    }

    loggerName = loggerName.trim();

    Logger logger = loggerContext.exists(loggerName);
    if (logger != null) {
      return logger.getEffectiveLevel().toString();
    } else {
      return EMPTY;
    }
  }

  @Override
  public List<String> getLoggerList() {
    List<String> strList = new ArrayList<>();
    for (Logger log : loggerContext.getLoggerList()) {
      strList.add(log.getName());
    }
    return strList;
  }

  @Override
  public List<String> getStatuses() {
    List<String> list = new ArrayList<>();
    for (Status status : context.getStatusManager().getCopyOfStatusList()) {
      list.add(status.toString());
    }
    return list;
  }

  /** When the associated LoggerContext is stopped, this configurator must be unregistered */
  @Override
  public void onStop(LoggerContext loggerContext) {
    if (!started) {
      context.addInfo(
          "onStop() method called on a stopped JMXActivator [" + objectNameAsString + "]");
      return;
    }
    if (mbs.isRegistered(objectName)) {
      try {
        context.addInfo("Unregistering mbean [" + objectNameAsString + "]");
        mbs.unregisterMBean(objectName);
      } catch (InstanceNotFoundException e) {
        // this is theoretically impossible
        context.addError(
            "Unable to find a verifiably registered mbean [" + objectNameAsString + "]", e);
      } catch (MBeanRegistrationException e) {
        context.addError("Failed to unregister [" + objectNameAsString + "]", e);
      }
    } else {
      context.addInfo(
          "mbean [" + objectNameAsString + "] was not in the mbean registry. This is OK.");
    }
    stop();
  }

  @Override
  public void onLevelChange(Logger logger, Level level) {
    // nothing to do
  }

  @Override
  public void onReset(LoggerContext loggerContext) {
    context.addInfo("onReset() method called JMXActivator [" + objectNameAsString + "]");
  }

  /** JMXConfigurator should not be removed subsequent to a LoggerContext reset. */
  @Override
  public boolean isResetResistant() {
    return true;
  }

  private void clearFields() {
    mbs = null;
    objectName = null;
    loggerContext = null;
  }

  private void stop() {
    started = false;
    clearFields();
  }

  @Override
  public void onStart(LoggerContext context) {
    // nop
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "(" + loggerContext.getName() + ")";
  }
}
