package com.bogolyandras.iotlogger.value.logs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public class LogAggregationRequest {

    @NotNull
    private final BigDecimal offset;

    @NotNull
    private final LogAggregationByType aggregationBy;

    @NotNull
    private final Instant lowTimestampFilter;

    @NotNull
    private final Instant highTimestampFilter;

    @JsonCreator
    public LogAggregationRequest(
            @JsonProperty("offset") BigDecimal offset,
            @JsonProperty("aggregationBy") LogAggregationByType aggregationBy,
            @JsonProperty("lowTimestampFilter") Instant lowTimestampFilter,
            @JsonProperty("highTimestampFilter") Instant highTimestampFilter) {
        this.offset = offset;
        this.aggregationBy = aggregationBy;
        this.lowTimestampFilter = lowTimestampFilter;
        this.highTimestampFilter = highTimestampFilter;
    }

    public BigDecimal getOffset() {
        return offset;
    }

    public LogAggregationByType getAggregationBy() {
        return aggregationBy;
    }

    public Instant getLowTimestampFilter() {
        return lowTimestampFilter;
    }

    public Instant getHighTimestampFilter() {
        return highTimestampFilter;
    }

    public enum LogAggregationByType {
        Yearly, Monthly, Daily
    }

}
