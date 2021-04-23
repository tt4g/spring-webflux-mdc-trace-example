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
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@MockitoSettings
class ReactorMDCTracerTest {

    private static String TRACE_ID = "EGXNtjjJY4XZ2MPtSKnF4CdwW3xx75be";

    @Mock
    private TraceId traceId;

    private AtomicReference<String> traceIdCaptor;

    private Duration maxBlocking = Duration.ofMillis(500);

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

        this.traceIdCaptor = new AtomicReference<>(null);
    }

    @Test
    public void passTraceIdToMdcFromContext() {
        Mono.just("foo")
            .map(element -> {
                this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY));

                return element;
            })
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContextOnNext() {
        Mono.just("foo")
            .doOnNext(element -> {
                this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY));
            })
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContextOnComplete() {
        Mono.just("foo")
            .doOnEach(signal -> {
                if (signal.isOnComplete()) {
                    this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY));
                }
            })
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContextOnError() {
        try {
            Mono.just("foo")
                .<String>map(element -> {
                    throw new RuntimeException("on error");
                })
                .doOnError(ex ->
                    this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY)))
                .contextWrite(createContext())
                .block(this.maxBlocking);

            fail("Should not reach here.");
        } catch (RuntimeException ex) {

        }

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContextWithFlatMap() {
        Mono.just("foo")
            .subscribeOn(Schedulers.parallel())
            .flatMap(element -> {
                this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY));

                return Mono.just(element);
            })
            .subscribeOn(Schedulers.parallel())
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToFromMdcFromContextWithFromRunnable() {
        Mono.fromRunnable(() ->
            this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY)))
            .subscribeOn(Schedulers.parallel())
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    @Test
    public void passTraceIdToMdcFromContextWithDefer() {
        Mono.defer(() ->
            Mono.fromRunnable(() ->
                this.traceIdCaptor.set(MDC.get(ReactorMDCTracer.MDC_KEY))))
            .subscribeOn(Schedulers.parallel())
            .contextWrite(createContext())
            .block(this.maxBlocking);

        assertThat(this.traceIdCaptor).hasValue(TRACE_ID);
    }

    private Context createContext() {
        return Context.of(TraceIdWebFilter.TRACE_ID_CONTEXT_KEY, this.traceId);
    }

}
