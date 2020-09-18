package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import java.util.Objects;
import java.util.Optional;

import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class TraceIdWebFilter implements WebFilter {

    public static final String TRACE_ID_CONTEXT_KEY = "TRACE_ID";

    private static final String TRACE_ID_HEADER_NAME = "X-Trace-Id";

    private final TraceIdRepository traceIdRepository;

    public TraceIdWebFilter(TraceIdRepository traceIdRepository) {
        this.traceIdRepository = Objects.requireNonNull(traceIdRepository);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return this.loadTraceId(exchange)
            .flatMap(traceId -> {
                // Set X-Trace-Id: <trace-id> to response header.
                exchange.getResponse().getHeaders().add(TRACE_ID_HEADER_NAME, traceId.render());

                return chain.filter(exchange)
                    // Set TraceId to Reactor Context.
                    .subscriberContext(context -> context.put(TRACE_ID_CONTEXT_KEY, traceId));
            });
    }

    private Mono<TraceId> loadTraceId(ServerWebExchange exchange) {
        // TraceId priority:
        // 1. X-Trace-Id header that specified by HTTP request.
        // 2. loaded by TraceIdRepository.
        // 3. generated by TraceIdRepository.

        return parseHeader(exchange)
            .orElseGet(() ->
                // If X-Trace-Id header is not specified, load TraceId.
                this.traceIdRepository.loadTraceId(exchange)
                    // If cannot load TraceId, generate new TraceId.
                    .switchIfEmpty(Mono.defer(() -> this.generateTraceId(exchange))));
    }

    private Optional<Mono<TraceId>> parseHeader(ServerWebExchange exchange) {
        // Get X-Trace-Id header value.
        Optional<String> traceIdOptional =
            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER_NAME));

        return traceIdOptional
            // ignore null or empty.
            .filter(traceId -> !StringUtils.isEmpty(traceId))
            .map(nonEmptyTraceId ->
                Mono.just(new TraceId(nonEmptyTraceId))
                    // Save TraceId from X-Trace-Id header.
                    .flatMap(traceId ->
                        this.traceIdRepository.saveTraceId(exchange, traceId)
                            .thenReturn(traceId)));
    }

    private Mono<TraceId> generateTraceId(ServerWebExchange exchange) {
        return this.traceIdRepository.generateTraceId(exchange)
            // Save generated TraceId.
            .flatMap(traceId ->
                this.traceIdRepository.saveTraceId(exchange, traceId)
                    .thenReturn(traceId));
    }

}
