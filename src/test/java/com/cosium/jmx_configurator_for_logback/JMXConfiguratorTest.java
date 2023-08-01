package com.cosium.jmx_configurator_for_logback;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.LifeCycle;
import java.io.ByteArrayInputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Réda Housni Alaoui
 */
@ExtendWith(ContextExtension.class)
class JMXConfiguratorTest {

  @BeforeEach
  void beforeEach() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.reset();
    context.start();
  }

  @Test
  @DisplayName("By default the registered context is 'default'")
  void test1() {
    Logger logger = LoggerFactory.getLogger(JMXConfiguratorTest.class);
    assertThat(logger).isNotNull();
    assertThat(isMBeanRegistered("default")).isTrue();

    LifeCycle lifeCycle = (LifeCycle) LoggerFactory.getILoggerFactory();
    lifeCycle.stop();
    assertThat(isMBeanRegistered("default")).isFalse();
  }

  @Test
  @DisplayName("Context name customized with Joran is reflected on the MBean ObjectName")
  void test2(TestContext testContext) throws JoranException, InterruptedException {
    JoranConfigurator configurator = new JoranConfigurator();
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    configurator.setContext(context);

    CountDownLatch latch = new CountDownLatch(1);
    testContext.jmxRegistrationCallback = latch::countDown;
    configurator.doConfigure(
        new ByteArrayInputStream(
            """
                    <configuration debug="true">
                      <contextName>test</contextName>
                    </configuration>
                    """
                .getBytes(StandardCharsets.UTF_8)));
    latch.await();

    assertThat(isMBeanRegistered("default")).isFalse();
    assertThat(isMBeanRegistered("test")).isTrue();
  }

  private boolean isMBeanRegistered(String contextName) {
    return ManagementFactory.getPlatformMBeanServer()
        .isRegistered(ObjectNames.INSTANCE.createObjectName(contextName));
  }
}
