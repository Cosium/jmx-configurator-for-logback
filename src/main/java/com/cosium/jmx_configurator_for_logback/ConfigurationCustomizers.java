package com.cosium.jmx_configurator_for_logback;

import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Réda Housni Alaoui
 */
enum ConfigurationCustomizers {
  INSTANCE;

  private final List<ConfigurationCustomizer> customizers;

  ConfigurationCustomizers() {
    customizers =
        ServiceLoader.load(ConfigurationCustomizer.class).stream()
            .map(ServiceLoader.Provider::get)
            .toList();
  }

  public List<ConfigurationCustomizer> list() {
    return customizers;
  }
}
