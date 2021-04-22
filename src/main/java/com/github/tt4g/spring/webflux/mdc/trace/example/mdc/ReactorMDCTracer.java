// I wrote this code based on the original code which has the following license:
//
// https://github.com/archie-swif/webflux-mdc/blob/9e10c34dc2790d04f4d9cfe228c8be56a2ab920c/src/main/java/com/example/webfluxmdc/MdcContextLifter.java


package com.github.tt4g.spring.webflux.mdc.trace.example.mdc;

import java.util.Objects;
import java.util.Optional;

import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceId;
import com.github.tt4g.spring.webflux.mdc.trace.example.trace.TraceIdWebFilter;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
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
        this.coreSubscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        withTraceId(() -> this.coreSubscriber.onNext(t));
    }

    @Override
    public void onError(Throwable t) {
        withTraceId(() -> this.coreSubscriber.onError(t));
    }

    @Override
    public void onComplete() {
        withTraceId(() -> this.coreSubscriber.onComplete());
    }

    @SuppressWarnings("try")
    private void withTraceId(Runnable body) {
        Context context = this.coreSubscriber.currentContext();
        Optional<TraceId> traceIdOptional = context.getOrEmpty(TraceIdWebFilter.TRACE_ID_CONTEXT_KEY);

        traceIdOptional.ifPresentOrElse(traceId -> {
                try (MDCCloseable mdcCloseable = MDC.putCloseable(MDC_KEY, traceId.render())) {
                    body.run();
                }
            },
            body);
    }

}
