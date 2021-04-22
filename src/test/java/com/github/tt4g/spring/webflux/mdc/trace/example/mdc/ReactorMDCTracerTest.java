package com.github.tt4g.spring.webflux.mdc.trace.example.mdc;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceId;
import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceIdWebFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.MDC;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class ReactorMDCTracerTest {

    private static String TRACE_ID = "EGXNtjjJY4XZ2MPtSKnF4CdwW3xx75be";

    @Mock
    private TraceId traceId;

    @BeforeAll
    static void beforeAll() {
        ReactorMDCTracer.installHook();
    }

    @AfterAll
    static void afterAll() {
        ReactorMDCTracer.resetHook();
    }

    @BeforeEach
    void setUp() {
        BDDMockito.given(this.traceId.render())
            .willReturn(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContext() {
        AtomicReference<String> traceId = new AtomicReference<>(null);

        Mono.just("foo")
            .contextWrite(createContext())
            .map(element -> {
                traceId.set(MDC.get(ReactorMDCTracer.MDC_KEY));

                return element;
            })
            .block(Duration.ofMillis(500));

        assertThat(traceId).hasValue(TRACE_ID);
    }

    private Context createContext() {
        return Context.of(TraceIdWebFilter.TRACE_ID_CONTEXT_KEY, this.traceId);
    }

}
