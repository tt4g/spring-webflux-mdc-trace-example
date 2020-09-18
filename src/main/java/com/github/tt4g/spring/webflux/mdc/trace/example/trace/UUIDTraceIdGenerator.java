package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import java.util.UUID;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class UUIDTraceIdGenerator implements TraceIdGenerator {

    @Override
    public Mono<TraceId> generate(ServerWebExchange exchange) {
        return Mono.fromCallable(this::generateTraceId)
            .subscribeOn(Schedulers.boundedElastic());
    }

    private TraceId generateTraceId() {
        // NOTE: UUID.randomUUID() is blocking operation!
        String uuid = UUID.randomUUID().toString();

        return new TraceId(uuid);
    }

}
