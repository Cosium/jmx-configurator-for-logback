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
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides access to logback components via JMX.
 *
 * <p>Since this class implements {@link JMXConfiguratorMBean} it must be named <code>
 * JMXConfigurator</code>.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author S&eacute;bastien Pennec
 * @author Sebastian Davids
 * @author Réda Housni Alaoui
 */
public class JMXConfigurator implements JMXConfiguratorMBean {

  private static final String EMPTY = "";

  private final ContextAwareBase context;
  private final LoggerContext loggerContext;

  public JMXConfigurator(ContextAwareBase context, LoggerContext loggerContext) {
    this.context = context;
    this.loggerContext = loggerContext;
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
}
