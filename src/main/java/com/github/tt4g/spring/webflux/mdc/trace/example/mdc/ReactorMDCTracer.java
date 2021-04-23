// I wrote this code based on the original code:
//
// https://github.com/ProjectEKA/gateway/blob/8b5fb0c56ec278ab20278a11101fe06eda45c41d/src/main/java/in/projecteka/gateway/MdcContextLifter.java
// https://github.com/reactor/reactor-core/issues/1985#issuecomment-824593594


package com.github.tt4g.spring.webflux.mdc.trace.example.mdc;

import java.util.Objects;
import java.util.Optional;

import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceId;
import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceIdWebFilter;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

public class ReactorMDCTracer<T> implements CoreSubscriber<T> {

    private static final String HOOK_KEY = ReactorMDCTracer.class.getName();

    public static final String MDC_KEY = "trace-id";

    private final CoreSubscriber<T> coreSubscriber;

    public static void installHook() {
        Hooks.onEachOperator(
            HOOK_KEY,
            // Can use Operators.list() to create a function that can be
            // used to support a custom operator via Hooks#onEachOperator().
            // See Operators.lift() javadoc.
            Operators.lift((scannable, coreSubscriber) -> new ReactorMDCTracer<>(coreSubscriber)));
    }

    public static void resetHook() {
        Hooks.resetOnEachOperator(HOOK_KEY);
    }

    ReactorMDCTracer(CoreSubscriber<T> coreSubscriber) {
        this.coreSubscriber = Objects.requireNonNull(coreSubscriber);
    }

    @Override
    public Context currentContext() {
        return this.coreSubscriber.currentContext();
    }

    @Override
    public void onSubscribe(Subscription s) {
        replaceMdc();

        this.coreSubscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        replaceMdc();

        this.coreSubscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        replaceMdc();

        this.coreSubscriber.onError(t);
    }

    @Override
    public void onComplete() {
        replaceMdc();

        this.coreSubscriber.onComplete();
    }

    private void replaceMdc() {
        Context context = this.coreSubscriber.currentContext();
        Optional<TraceId> traceIdOptional = context.getOrEmpty(TraceIdWebFilter.TRACE_ID_CONTEXT_KEY);

        traceIdOptional.ifPresentOrElse(
            traceId -> MDC.put(MDC_KEY, traceId.render()),
            () -> MDC.remove(MDC_KEY));
    }

}
