# JMX Configurator for Logback

Deploys a JMX endpoint allowing to configure logback on-the-fly.

# Quick start
1. Add the dependency:
    ```xml
    <dependency>
       <groupId>com.cosium.jmx_configurator_for_logback</groupId>
       <artifactId>jmx-configurator-for-logback</artifactId>
       <version>${jmx-configurator-for-logback.version}</version>
       <scope>test</scope>
    </dependency>
    ```
2. Use the JMX endpoint deployed at `com.cosium.jmx_configurator_for_logback:Name=default,Type=com.cosium.jmx_configurator_for_logback.JMXConfigurator`
