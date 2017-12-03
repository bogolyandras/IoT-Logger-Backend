package com.bogolyandras.iotlogger.repository.mongodb;

import com.bogolyandras.iotlogger.repository.definition.LogRepository;
import com.bogolyandras.iotlogger.value.logs.Log;
import com.bogolyandras.iotlogger.value.logs.NewLog;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
@Profile("mongodb")
public class LogRepositoryImplementation implements LogRepository {

    private final MongoCollection<Document> deviceLogs;

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
