package com.cosium.jmx_configurator_for_logback;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.core.spi.LifeCycle;
import java.lang.management.ManagementFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Réda Housni Alaoui
 */
class JMXConfiguratorTest {

  @Test
  void test() throws MalformedObjectNameException {
    Logger logger = LoggerFactory.getLogger(JMXConfiguratorTest.class);
    assertThat(logger).isNotNull();
    assertThat(isMBeanRegistered()).isTrue();

    LifeCycle lc = (LifeCycle) LoggerFactory.getILoggerFactory();
    lc.stop();
    assertThat(isMBeanRegistered()).isFalse();
  }

  private boolean isMBeanRegistered() throws MalformedObjectNameException {
    return MBeanUtil.isRegistered(
        ManagementFactory.getPlatformMBeanServer(),
        ObjectName.getInstance(
            "com.cosium.jmx_configurator_for_logback:Name=default,Type=com.cosium.jmx_configurator_for_logback.JMXConfigurator"));
  }
}
