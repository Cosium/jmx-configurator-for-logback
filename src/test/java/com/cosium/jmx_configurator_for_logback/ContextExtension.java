package com.cosium.jmx_configurator_for_logback;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * @author Réda Housni Alaoui
 */
class ContextExtension implements ParameterResolver, BeforeEachCallback {
  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return TestContext.class.equals(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return getTestContext();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    getTestContext().reset();
  }

  private TestContext getTestContext() {
    return ConfigurationCustomizers.INSTANCE.list().stream()
        .filter(TestContext.class::isInstance)
        .map(TestContext.class::cast)
        .findFirst()
        .orElseThrow();
  }
}
