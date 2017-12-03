package com.bogolyandras.iotlogger.value.logs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class NewLog {

    private Instant timestamp;

    private final BigDecimal metric1;
    private final BigDecimal metric2;
    private final BigDecimal metric3;

    @JsonCreator
    public NewLog(
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("metric1") BigDecimal metric1,
            @JsonProperty("metric2") BigDecimal metric2,
            @JsonProperty("metric3") BigDecimal metric3) {
        this.timestamp = timestamp;
        this.metric1 = metric1;
        this.metric2 = metric2;
        this.metric3 = metric3;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getMetric1() {
        return metric1;
    }

    public BigDecimal getMetric2() {
        return metric2;
    }

    public BigDecimal getMetric3() {
        return metric3;
    }

}
