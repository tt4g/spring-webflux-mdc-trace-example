package com.github.tt4g.spring.webflux.mdc.trace.example;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.tt4g.spring.webflux.mdc.trace.example.mdc.ReactorMDCTracer;
import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceIdWebFilter;
import com.github.tt4g.spring.webflux.mdc.trace.example.trace.WebSessionServerTraceIdRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceConfig {

    @PostConstruct
    public void installReactorHook() {
        ReactorMDCTracer.installHook();
    }

    @PreDestroy
    public void resetReactorHook() {
        ReactorMDCTracer.resetHook();
    }

    @Bean
    public TraceIdWebFilter traceIdWebFilter() {
        WebSessionServerTraceIdRepository webSessionServerTraceIdRepository =
            new WebSessionServerTraceIdRepository();

        return new TraceIdWebFilter(webSessionServerTraceIdRepository);
    }

}
