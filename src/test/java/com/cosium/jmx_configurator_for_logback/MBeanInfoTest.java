package com.cosium.jmx_configurator_for_logback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Stream;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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
class MBeanInfoTest {

  @BeforeEach
  void beforeEach() {
    LoggerFactory.getILoggerFactory();
  }

  @Test
  @DisplayName("setLoggerLevel")
  void test1() {
    MBeanInfo mBeanInfo = getMBeanInfo();
    MBeanOperationInfo operationInfo =
        Stream.of(mBeanInfo.getOperations())
            .filter(info -> "setLoggerLevel".equals(info.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(operationInfo.getReturnType()).isEqualTo("void");
    assertThat(operationInfo.getImpact()).isEqualTo(MBeanOperationInfo.ACTION);

    assertThat(Stream.of(operationInfo.getSignature()))
        .hasSize(2)
        .extracting(MBeanParameterInfo::getName, MBeanParameterInfo::getType)
        .containsExactlyInAnyOrder(
            tuple("loggerName", String.class.getName()), tuple("newLevel", String.class.getName()));
  }

  @Test
  @DisplayName("getLoggerLevel")
  void test2() {
    MBeanInfo mBeanInfo = getMBeanInfo();
    MBeanOperationInfo operationInfo =
        Stream.of(mBeanInfo.getOperations())
            .filter(info -> "getLoggerLevel".equals(info.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(operationInfo.getReturnType()).isEqualTo(String.class.getName());
    assertThat(operationInfo.getImpact()).isEqualTo(MBeanOperationInfo.INFO);

    assertThat(Stream.of(operationInfo.getSignature()))
        .hasSize(1)
        .extracting(MBeanParameterInfo::getName, MBeanParameterInfo::getType)
        .containsExactlyInAnyOrder(tuple("loggerName", String.class.getName()));
  }

  @Test
  @DisplayName("getLoggerEffectiveLevel")
  void test3() {
    MBeanInfo mBeanInfo = getMBeanInfo();
    MBeanOperationInfo operationInfo =
        Stream.of(mBeanInfo.getOperations())
            .filter(info -> "getLoggerEffectiveLevel".equals(info.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(operationInfo.getReturnType()).isEqualTo(String.class.getName());
    assertThat(operationInfo.getImpact()).isEqualTo(MBeanOperationInfo.INFO);

    assertThat(Stream.of(operationInfo.getSignature()))
        .hasSize(1)
        .extracting(MBeanParameterInfo::getName, MBeanParameterInfo::getType)
        .containsExactlyInAnyOrder(tuple("loggerName", String.class.getName()));
  }

  @Test
  @DisplayName("getLoggerList")
  void test4() {
    MBeanInfo mBeanInfo = getMBeanInfo();
    MBeanOperationInfo operationInfo =
        Stream.of(mBeanInfo.getOperations())
            .filter(info -> "getLoggerList".equals(info.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(operationInfo.getReturnType()).isEqualTo(List.class.getName());
    assertThat(operationInfo.getImpact()).isEqualTo(MBeanOperationInfo.INFO);

    assertThat(Stream.of(operationInfo.getSignature())).isEmpty();
  }

  @Test
  @DisplayName("getStatuses")
  void test5() {
    MBeanInfo mBeanInfo = getMBeanInfo();
    MBeanOperationInfo operationInfo =
        Stream.of(mBeanInfo.getOperations())
            .filter(info -> "getStatuses".equals(info.getName()))
            .findFirst()
            .orElseThrow();

    assertThat(operationInfo.getReturnType()).isEqualTo(List.class.getName());
    assertThat(operationInfo.getImpact()).isEqualTo(MBeanOperationInfo.INFO);

    assertThat(Stream.of(operationInfo.getSignature())).isEmpty();
  }

  private MBeanInfo getMBeanInfo() {
    try {
      return ManagementFactory.getPlatformMBeanServer()
          .getMBeanInfo(ObjectNames.INSTANCE.createDefaultObjectName());
    } catch (InstanceNotFoundException | IntrospectionException | ReflectionException e) {
      throw new RuntimeException(e);
    }
  }
}
