package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@MockitoSettings
class TraceIdWebFilterTest {

    @Mock
    private TraceIdRepository traceIdRepository;

    @InjectMocks
    private TraceIdWebFilter traceIdWebFilter;

    @Test
    public void filterWhenCouldNotGetTraceId(
        @Mock WebFilterChain webFilterChain) {

        mockFilter(webFilterChain);
        mockLoad(this.traceIdRepository);
        mockGenerate(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .load(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generate(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenGetTraceIdFromHeader(
        @Mock WebFilterChain webFilterChain) {

        mockFilter(webFilterChain);
        mockSave(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/").header("X-Trace-Id", "foo"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .load(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .generate(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenTraceIdHeaderIsEmpty(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoad(this.traceIdRepository);
        mockGenerate(this.traceIdRepository, traceId);
        mockSave(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/").header("X-Trace-Id", ""));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .load(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generate(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenLoadTraceId(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoad(this.traceIdRepository, traceId);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .load(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .generate(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenGenerateTraceId(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoad(this.traceIdRepository);
        mockGenerate(this.traceIdRepository, traceId);
        mockSave(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .load(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generate(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    private void mockFilter(WebFilterChain webFilterChain) {
        Mockito.when(webFilterChain.filter(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty().then());
    }

    private void mockLoad(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.load(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty());
    }

    private void mockLoad(TraceIdRepository traceIdRepository, TraceId traceId) {
        Mockito.when(
            traceIdRepository.load(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.just(traceId));
    }

    private void mockGenerate(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.generate(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty());
    }

    private void mockGenerate(TraceIdRepository traceIdRepository, TraceId traceId) {
        Mockito.when(
            traceIdRepository.generate(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.just(traceId));
    }

    private void mockSave(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.save(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class)))
            .thenReturn(Mono.empty().then());
    }

}
