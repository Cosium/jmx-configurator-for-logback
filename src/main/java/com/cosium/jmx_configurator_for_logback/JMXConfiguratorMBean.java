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

import java.util.List;

/**
 * @author Réda Housni Alaoui
 */
public interface JMXConfiguratorMBean {

  void setLoggerLevel(String loggerName, String levelStr);

  String getLoggerLevel(String loggerName);

  String getLoggerEffectiveLevel(String loggerName);

  List<String> getLoggerList();

  List<String> getStatuses();
}
