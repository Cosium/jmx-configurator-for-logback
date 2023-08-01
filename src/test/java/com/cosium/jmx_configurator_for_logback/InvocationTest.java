package com.cosium.jmx_configurator_for_logback;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.UUID;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

/**
 * @author Réda Housni Alaoui
 */
@ExtendWith(ContextExtension.class)
class InvocationTest {
  private Logger logger;

  @BeforeEach
  void beforeEach() {
    logger = (Logger) LoggerFactory.getLogger(UUID.randomUUID().toString());
    logger.setLevel(Level.OFF);
  }

  @Test
  @DisplayName("setLoggerLevel")
  void test1() throws InstanceNotFoundException, ReflectionException, MBeanException {
    ManagementFactory.getPlatformMBeanServer()
        .invoke(
            ObjectNames.INSTANCE.createDefaultObjectName(),
            "setLoggerLevel",
            new Object[] {logger.getName(), "DEBUG"},
            new String[] {String.class.getName(), String.class.getName()});

    assertThat(logger.getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  @DisplayName("getLoggerLevel")
  void test2() throws InstanceNotFoundException, ReflectionException, MBeanException {
    Object result =
        ManagementFactory.getPlatformMBeanServer()
            .invoke(
                ObjectNames.INSTANCE.createDefaultObjectName(),
                "getLoggerLevel",
                new Object[] {logger.getName()},
                new String[] {String.class.getName()});

    assertThat(result).isEqualTo("OFF");
  }

  @Test
  @DisplayName("getLoggerEffectiveLevel")
  void test3() throws InstanceNotFoundException, ReflectionException, MBeanException {
    Object result =
        ManagementFactory.getPlatformMBeanServer()
            .invoke(
                ObjectNames.INSTANCE.createDefaultObjectName(),
                "getLoggerEffectiveLevel",
                new Object[] {logger.getName()},
                new String[] {String.class.getName()});

    assertThat(result).isEqualTo("OFF");
  }

  @Test
  @DisplayName("getLoggerList")
  void test4() throws InstanceNotFoundException, ReflectionException, MBeanException {
    Object result =
        ManagementFactory.getPlatformMBeanServer()
            .invoke(
                ObjectNames.INSTANCE.createDefaultObjectName(),
                "getLoggerList",
                new Object[0],
                new String[0]);

    assertThat(result).isInstanceOf(List.class);
    assertThat((List<String>) result).contains(logger.getName());
  }

  @Test
  @DisplayName("getStatuses")
  void test5() throws InstanceNotFoundException, ReflectionException, MBeanException {
    Object result =
        ManagementFactory.getPlatformMBeanServer()
            .invoke(
                ObjectNames.INSTANCE.createDefaultObjectName(),
                "getStatuses",
                new Object[0],
                new String[0]);

    assertThat(result).isInstanceOf(List.class);
    assertThat((List<String>) result)
        .anyMatch(
            s ->
                s.contains(
                    "Starting com.cosium.jmx_configurator_for_logback.JMXConfiguratorSession"));
  }
}
