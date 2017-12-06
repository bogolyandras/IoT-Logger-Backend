package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.LogRepository;
import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.LogAggregation;
import com.bogolyandras.iotlogger.value.logs.LogAggregationRequest;
import com.bogolyandras.iotlogger.value.logs.NewLog;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

@Repository
@Profile("mongodb")
public class LogRepositoryImplementation implements LogRepository {

    private final MongoCollection<Document> deviceLogs;
    private static final Logger logger = LoggerFactory.getLogger(LogRepositoryImplementation.class);

    public LogRepositoryImplementation(MongoDatabase database) {
        deviceLogs = database.getCollection("deviceLogs");
    }

    @Override
    public List<Log> getLogsForDevice(String deviceId) {

        FindIterable<Document> documents = deviceLogs.find(
                eq("deviceId", deviceId)
        );
        MongoCursor<Log> iterator = documents.map(this::documentToLog).iterator();
        List<Log> logs = new ArrayList<>();
        while (iterator.hasNext()) {
            logs.add(iterator.next());
        }
        return logs;
    }

    @Override
    public LogAggregation getLogAggregation(String deviceId, LogAggregationRequest logAggregationRequest) {

        Document groupByFormat;
        switch (logAggregationRequest.getAggregationBy()) {
            case Yearly:
                groupByFormat = new Document("year", "$year");
                break;
            case Monthly:
                groupByFormat = new Document("year", "$year").append("month", "$month");
                break;
            case Daily:
            default:
                groupByFormat = new Document("year", "$year").append("month", "$month").append("day", "$day");
        }

        AggregateIterable<Document> aggregate = deviceLogs.aggregate(
            Arrays.asList(
                match(
                    and(
                        eq("deviceId", deviceId),
                        gte("dataTime",Date.from(logAggregationRequest.getLowTimestampFilter())),
                        lte("dataTime", Date.from(logAggregationRequest.getHighTimestampFilter()))
                    )
                ),
                project(
                    fields(
                        excludeId(),
                        computed("year", new Document("$year", "$dataTime")),
                        computed("month", new Document("$month", "$dataTime")),
                        computed("day", new Document("$dayOfMonth", "$dataTime")),
                        include("metric1"),
                        include("metric2"),
                        include("metric3")
                    )
                ),
                group(
                    groupByFormat,

                    min("metric1Minimum", "$metric1"),
                    avg("metric1Mean", "$metric1"),
                    max("metric1Maximum", "$metric1"),

                    min("metric2Minimum", "$metric2"),
                    avg("metric2Mean", "$metric2"),
                    max("metric2Maximum", "$metric2"),

                    min("metric2Minimum", "$metric2"),
                    avg("metric2Mean", "$metric2"),
                    max("metric2Maximum", "$metric2")
                ),
                sort(descending("_id.year", "_id.month", "_id.day"))
            )
        );

        Map<String, LogAggregation.MetricContainer> container = new LinkedHashMap<>();

        MongoCursor<Document> iterator = aggregate.iterator();
        while(iterator.hasNext()) {
            Document next = iterator.next();

            String key;
            Document keySource = (Document) next.get("_id");
            switch (logAggregationRequest.getAggregationBy()) {
                case Yearly:
                    key = keySource.get("year").toString();
                    break;
                case Monthly:
                    key = keySource.get("year").toString() + "-" + keySource.get("month").toString();
                    break;
                case Daily:
                default:
                    key = keySource.get("year").toString() + "-" + keySource.get("month").toString() + "-" + keySource.get("day").toString();
            }

            container.put(key,
                new LogAggregation.MetricContainer(
                    new LogAggregation.LogAggregationRecord(
                        decimal128ToBigDecimal((Decimal128)next.get("metric1Minimum")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric1Mean")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric1maximum"))
                    ),
                    new LogAggregation.LogAggregationRecord(
                        decimal128ToBigDecimal((Decimal128)next.get("metric2Minimum")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric2Mean")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric2maximum"))
                    ),
                    new LogAggregation.LogAggregationRecord(
                        decimal128ToBigDecimal((Decimal128)next.get("metric3Minimum")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric3Mean")),
                        decimal128ToBigDecimal((Decimal128)next.get("metric3maximum"))
                    )
                )
            );
        }

        return new LogAggregation(container);
    }

    @Override
    public Log storeLog(String deviceId, NewLog newLog) {

        Document document = new Document("dataTime", Date.from(newLog.getTimestamp()))
                .append("deviceId", deviceId)
                .append("metric1", bigDecimalToDecimal128(newLog.getMetric1()))
                .append("metric2", bigDecimalToDecimal128(newLog.getMetric2()))
                .append("metric3", bigDecimalToDecimal128(newLog.getMetric3()));

        deviceLogs.insertOne(document);

        return new Log(
                document.getObjectId("_id").toString(),
                newLog.getTimestamp(),
                newLog.getMetric1(),
                newLog.getMetric2(),
                newLog.getMetric3()
        );
    }

    private Log documentToLog(Document document) {
        return new Log(
                document.getObjectId("_id").toString(),
                document.getDate("dataTime").toInstant(),
                decimal128ToBigDecimal((Decimal128)document.get("metric1")),
                decimal128ToBigDecimal((Decimal128)document.get("metric2")),
                decimal128ToBigDecimal((Decimal128)document.get("metric3"))
        );
    }

    private Decimal128 bigDecimalToDecimal128(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        } else {
            return new Decimal128(bigDecimal);
        }
    }

    private BigDecimal decimal128ToBigDecimal(Decimal128 decimal128) {
        if (decimal128 == null) {
            return null;
        } else {
            return decimal128.bigDecimalValue();
        }
    }

}
