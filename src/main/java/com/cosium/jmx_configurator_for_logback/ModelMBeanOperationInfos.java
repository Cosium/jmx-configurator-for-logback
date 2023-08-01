package com.cosium.jmx_configurator_for_logback;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

/**
 * @author Réda Housni Alaoui
 */
enum ModelMBeanOperationInfos {
  INSTANCE;

  public ModelMBeanOperationInfo[] parseFrom(Class<?> jmxBeanType) {
    return Stream.of(jmxBeanType.getDeclaredMethods())
        .map(this::parseFrom)
        .toArray(ModelMBeanOperationInfo[]::new);
  }

  private ModelMBeanOperationInfo parseFrom(Method method) {
    MBeanParameterInfo[] parameterInfos =
        Stream.of(method.getParameters())
            .map(
                parameter ->
                    new MBeanParameterInfo(
                        parameter.getName(), parameter.getType().getName(), null))
            .toArray(MBeanParameterInfo[]::new);

    boolean isSetter = method.getName().startsWith("set");
    Class<?> returnType = method.getReturnType();
    boolean isVoid = Set.of(Void.class, Void.TYPE).contains(returnType);

    int impact;
    if (isSetter && isVoid) {
      impact = MBeanOperationInfo.ACTION;
    } else if (isSetter) {
      impact = MBeanOperationInfo.ACTION_INFO;
    } else if (!isVoid) {
      impact = MBeanOperationInfo.INFO;
    } else {
      impact = MBeanOperationInfo.UNKNOWN;
    }

    return new ModelMBeanOperationInfo(
        method.getName(), null, parameterInfos, returnType.getName(), impact);
  }
}
