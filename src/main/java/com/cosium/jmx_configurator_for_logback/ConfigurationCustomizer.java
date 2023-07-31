package com.cosium.jmx_configurator_for_logback;

/**
 * @author Réda Housni Alaoui
 */
@FunctionalInterface
public interface ConfigurationCustomizer {

  void customize(Configuration.Builder configurationBuilder);
}
