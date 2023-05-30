[![Build Status](https://github.com/Cosium/jmx-configurator-for-logback/actions/workflows/ci.yml/badge.svg)](https://github.com/Cosium/jmx-configurator-for-logback/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.cosium.jmx_configurator_for_logback/jmx-configurator-for-logback-spring-boot-starter.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cosium.jmx_configurator_for_logback%22%20AND%20a%3A%22jmx-configurator-for-logback-spring-boot-starter%22)

# JMX Configurator for Logback

Deploys a JMX endpoint allowing to configure [logback](https://github.com/qos-ch/logback) on-the-fly.

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

# Prerequisites

- Java 17+
- [logback](https://github.com/qos-ch/logback) 1.4+

# Genesis

This project was created following [the logback commit](https://github.com/qos-ch/logback/commit/fa3de693048d25698af7264fd294a1c9ba6940d1) which removed JMX support from [logback](https://github.com/qos-ch/logback).
