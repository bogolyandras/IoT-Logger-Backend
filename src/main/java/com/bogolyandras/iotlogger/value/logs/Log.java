package com.bogolyandras.iotlogger.value.logs;

import java.math.BigDecimal;
import java.time.Instant;

public class Log {

    private final String id;
    private final Instant timestamp;

    private final BigDecimal metric1;
    private final BigDecimal metric2;
    private final BigDecimal metric3;

    public Log(String id, Instant timestamp, BigDecimal metric1, BigDecimal metric2, BigDecimal metric3) {
        this.id = id;
        this.timestamp = timestamp;
        this.metric1 = metric1;
        this.metric2 = metric2;
        this.metric3 = metric3;
    }

    public String getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
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
