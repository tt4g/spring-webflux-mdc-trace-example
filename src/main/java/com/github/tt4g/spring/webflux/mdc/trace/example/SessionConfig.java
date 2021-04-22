package com.github.tt4g.spring.webflux.mdc.trace.example;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.session.MapSession;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

@EnableSpringWebSession
public class SessionConfig {

    @Bean
    public ReactiveSessionRepository<MapSession> reactiveSessionRepository(
        SessionProperties sessionProperties) {
        ReactiveMapSessionRepository sessionRepository =
            new ReactiveMapSessionRepository(new ConcurrentHashMap<>());

        // Set session timeout manually.
        // In Spring Boot 2.3.2, WebSession does not allow you to set the
        // timeout interval in a configuration file such as application.properties.
        // Must be used Spring Session and call
        // ReactiveMapSessionRepository#setDefaultMaxInactiveInterval() to set timeout interval.
        // See: https://stackoverflow.com/questions/62133366/spring-security-session-timeout-for-spring-reactive
        // Issue: https://github.com/spring-projects/spring-boot/issues/15757, https://github.com/spring-projects/spring-boot/issues/23151
        int timeout = Math.toIntExact(sessionProperties.getTimeout().toSeconds());
        sessionRepository.setDefaultMaxInactiveInterval(timeout);

        return sessionRepository;
    }
}
