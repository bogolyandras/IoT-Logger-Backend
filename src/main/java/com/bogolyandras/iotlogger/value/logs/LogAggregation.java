package com.bogolyandras.iotlogger.value.logs;

import java.math.BigDecimal;
import java.util.Map;

public class LogAggregation {

    private final Map<String, MetricContainer> logs;

    public LogAggregation(Map<String, MetricContainer> logs) {
        this.logs = logs;
    }

    public Map<String, MetricContainer> getLogs() {
        return logs;
    }

    public static class MetricContainer {

        private final LogAggregationRecord metric1;
        private final LogAggregationRecord metric2;
        private final LogAggregationRecord metric3;

        public MetricContainer(LogAggregationRecord metric1, LogAggregationRecord metric2, LogAggregationRecord metric3) {
            this.metric1 = metric1;
            this.metric2 = metric2;
            this.metric3 = metric3;
        }

        public LogAggregationRecord getMetric1() {
            return metric1;
        }

        public LogAggregationRecord getMetric2() {
            return metric2;
        }

        public LogAggregationRecord getMetric3() {
            return metric3;
        }

    }

    public static class LogAggregationRecord {

        private final BigDecimal minimum;
        private final BigDecimal mean;
        private final BigDecimal maximum;

        public LogAggregationRecord(BigDecimal minimum, BigDecimal mean, BigDecimal maximum) {
            this.minimum = minimum;
            this.mean = mean;
            this.maximum = maximum;
        }

        public BigDecimal getMinimum() {
            return minimum;
        }

        public BigDecimal getMean() {
            return mean;
        }

        public BigDecimal getMaximum() {
            return maximum;
        }

    }

}
