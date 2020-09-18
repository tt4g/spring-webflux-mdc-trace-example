package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface TraceIdGenerator {

    Mono<TraceId> generate(ServerWebExchange exchange);

}
