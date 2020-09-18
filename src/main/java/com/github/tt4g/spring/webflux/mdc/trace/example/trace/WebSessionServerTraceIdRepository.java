// I wrote this code based on the original code which has the following license:
//
// https://github.com/spring-projects/spring-security/blob/5.3.4.RELEASE/web/src/main/java/org/springframework/security/web/server/csrf/CookieServerCsrfTokenRepository.java
//
// Copyright 2002-2019 the original author or authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import java.util.Map;
import java.util.Objects;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WebSessionServerTraceIdRepository implements TraceIdRepository {

    private static final String SESSION_ATTRIBUTE_NAME =
        WebSessionServerTraceIdRepository.class.getName().concat(".TRACE_ID");

    private TraceIdGenerator traceIdGenerator;

    public WebSessionServerTraceIdRepository(TraceIdGenerator traceIdGenerator) {
        this.traceIdGenerator = Objects.requireNonNull(traceIdGenerator);
    }

    @Override
    public Mono<TraceId> generateTraceId(ServerWebExchange exchange) {
        return this.traceIdGenerator.generate(exchange);
    }

    @Override
    public Mono<TraceId> loadTraceId(ServerWebExchange exchange) {
        return exchange.getSession()
            .filter(webSession -> webSession.getAttributes().containsKey(SESSION_ATTRIBUTE_NAME))
            .map(webSession -> webSession.getAttribute(SESSION_ATTRIBUTE_NAME));
    }

    @Override
    public Mono<Void> saveTraceId(ServerWebExchange exchange, TraceId traceId) {
        return exchange.getSession()
            .doOnNext(webSession -> {
                Map<String, Object> attributes = webSession.getAttributes();
                if (traceId == null) {
                    attributes.remove(SESSION_ATTRIBUTE_NAME);
                } else {
                    attributes.put(SESSION_ATTRIBUTE_NAME, traceId);
                }
            })
            .then();
    }

}
