package com.github.tt4g.spring.webflux.mdc.trace.example.trace;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.util.StringUtils;

public class TraceId implements Serializable {

    private static final long serialVersionUID = -486040577445365882L;

    private final String traceId;

    TraceId(String traceId) {
        if (StringUtils.isEmpty(traceId)) {
            throw new IllegalArgumentException("traceId is empty.");
        }

        this.traceId = traceId;
    }

    public String render() {
        return this.traceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraceId traceId1 = (TraceId) o;
        return Objects.equals(traceId, traceId1.traceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceId);
    }

    @Override
    public String toString() {
        return "TraceId(" + this.traceId + ")";
    }
}
