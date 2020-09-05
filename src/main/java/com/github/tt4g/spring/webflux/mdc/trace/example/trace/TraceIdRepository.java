package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface TraceIdRepository {

    Mono<TraceId> generateTraceId(ServerWebExchange exchange);

    Mono<TraceId> loadTraceId(ServerWebExchange exchange);

    Mono<Void> saveTraceId(ServerWebExchange exchange, TraceId traceId);

}
