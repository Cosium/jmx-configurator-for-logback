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

/**
 * @author Réda Housni Alaoui
 */
public class JMXLogbackConfigurator extends ContextAwareBase implements Configurator {

  @Override
  public ExecutionStatus configure(LoggerContext loggerContext) {
    Configuration.Builder configurationBuilder = Configuration.builder();
    ConfigurationCustomizers.INSTANCE
        .list()
        .forEach(customizer -> customizer.customize(configurationBuilder));
    Configuration configuration = configurationBuilder.build();

    loggerContext.addListener(new JMXConfigurators(this, configuration));
    return ExecutionStatus.NEUTRAL;
  }
}
