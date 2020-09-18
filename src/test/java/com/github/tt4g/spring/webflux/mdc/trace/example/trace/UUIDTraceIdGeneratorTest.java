package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDTraceIdGeneratorTest {

    @Test
    public void testGenerate() {
        UUIDTraceIdGenerator uuidTraceIdGenerator = new UUIDTraceIdGenerator();

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        Mono<TraceId> traceIdMono =
            uuidTraceIdGenerator.generate(mockServerWebExchange);
        TraceId traceId = traceIdMono.block();

        assertThat(traceId.render()).isNotEmpty();
    }

}
