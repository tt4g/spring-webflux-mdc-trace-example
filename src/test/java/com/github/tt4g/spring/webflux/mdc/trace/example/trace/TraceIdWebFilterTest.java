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
        mockLoadTraceId(this.traceIdRepository);
        mockGenerateTraceId(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .loadTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generateTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenGetTraceIdFromHeader(
        @Mock WebFilterChain webFilterChain) {

        mockFilter(webFilterChain);
        mockSaveTraceId(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/").header("X-Trace-Id", "foo"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .loadTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .generateTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenTraceIdHeaderIsEmpty(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoadTraceId(this.traceIdRepository);
        mockGenerateTraceId(this.traceIdRepository, traceId);
        mockSaveTraceId(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/").header("X-Trace-Id", ""));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .loadTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generateTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenLoadTraceId(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoadTraceId(this.traceIdRepository, traceId);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .loadTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .generateTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.never())
            .saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    @Test
    public void filterWhenGenerateTraceId(
        @Mock WebFilterChain webFilterChain,
        @Mock TraceId traceId) {

        mockFilter(webFilterChain);
        mockLoadTraceId(this.traceIdRepository);
        mockGenerateTraceId(this.traceIdRepository, traceId);
        mockSaveTraceId(this.traceIdRepository);

        MockServerWebExchange mockServerWebExchange =
            MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        this.traceIdWebFilter.filter(mockServerWebExchange, webFilterChain)
            .block();

        Mockito.verify(webFilterChain, Mockito.times(1))
            .filter(Mockito.eq(mockServerWebExchange));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .loadTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .generateTraceId(Mockito.any(ServerWebExchange.class));
        Mockito.verify(this.traceIdRepository, Mockito.times(1))
            .saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class));
    }

    private void mockFilter(WebFilterChain webFilterChain) {
        Mockito.when(webFilterChain.filter(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty().then());
    }

    private void mockLoadTraceId(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.loadTraceId(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty());
    }

    private void mockLoadTraceId(TraceIdRepository traceIdRepository, TraceId traceId) {
        Mockito.when(
            traceIdRepository.loadTraceId(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.just(traceId));
    }

    private void mockGenerateTraceId(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.generateTraceId(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.empty());
    }

    private void mockGenerateTraceId(TraceIdRepository traceIdRepository, TraceId traceId) {
        Mockito.when(
            traceIdRepository.generateTraceId(Mockito.any(ServerWebExchange.class)))
            .thenReturn(Mono.just(traceId));
    }

    private void mockSaveTraceId(TraceIdRepository traceIdRepository) {
        Mockito.when(
            traceIdRepository.saveTraceId(
                Mockito.any(ServerWebExchange.class),
                Mockito.any(TraceId.class)))
            .thenReturn(Mono.empty().then());
    }

}
