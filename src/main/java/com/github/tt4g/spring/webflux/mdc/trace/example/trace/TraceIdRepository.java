package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface TraceIdRepository {

    Mono<TraceId> generate(ServerWebExchange exchange);

    Mono<TraceId> load(ServerWebExchange exchange);

    Mono<Void> save(ServerWebExchange exchange, TraceId traceId);

}
