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

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.StatusUtil;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

class MBeanUtil {

  private static final String DOMAIN = "com.cosium.jmx_configurator_for_logback";

  private MBeanUtil() {}

  public static String getObjectNameFor(String contextName, Class<?> type) {
    return DOMAIN + ":Name=" + contextName + ",Type=" + type.getName();
  }

  public static ObjectName string2ObjectName(
      Context context, Object caller, String objectNameAsStr) {
    String msg = "Failed to convert [" + objectNameAsStr + "] to ObjectName";

    StatusUtil statusUtil = new StatusUtil(context);
    try {
      return new ObjectName(objectNameAsStr);
    } catch (MalformedObjectNameException | NullPointerException e) {
      statusUtil.addError(caller, msg, e);
      return null;
    }
  }

  public static boolean isRegistered(MBeanServer mbs, ObjectName objectName) {
    return mbs.isRegistered(objectName);
  }
}
