package com.oraise.client;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.refinitiv.ema.access.Data.DataCode;
import com.refinitiv.ema.access.DataType.DataTypes;
import com.refinitiv.ema.access.FieldEntry;
import com.refinitiv.ema.access.FieldList;
import com.refinitiv.ema.access.OmmDate;
import com.refinitiv.ema.access.OmmDateTime;
import com.refinitiv.ema.access.OmmTime;
import com.refinitiv.ema.rdm.DataDictionary;

abstract class AbstractMarketDataClient extends NoopClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMarketDataClient.class);

    private final DateTimeFormatter formatTime;
    private final DateTimeFormatter formatDate;
    private final DateTimeFormatter formatDateTime;
    private final String identifier;
    private final DataDictionary dataDictionary;

    protected AbstractMarketDataClient(String identifer, DataDictionary dataDictionary) {
        this.identifier = identifer;
        this.dataDictionary = dataDictionary;
        this.formatDate = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US);
        this.formatDateTime = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss", Locale.US);
        this.formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    protected final String identifier() {
        return identifier;
    }

    protected Map<Integer, String> unpack(FieldList fieldList) {
        java.util.Map<Integer, String> result = new HashMap<>();
        Iterator<FieldEntry> itEntries = fieldList.iterator();
        while (itEntries.hasNext()) {
            FieldEntry fieldEntry = itEntries.next();

            if (DataCode.BLANK == fieldEntry.code()) {
                continue;
            }
            extractField(fieldEntry).ifPresent(v -> result.put(fieldEntry.fieldId(), v));
        }

        return result;
    }

    private Optional<String> extractField(FieldEntry fieldEntry) {
        try {
            final Object o;

            switch (fieldEntry.loadType()) {
            case DataTypes.REAL:
                o = fieldEntry.real().asDouble();
                break;
            case DataTypes.ENUM:
                o = dataDictionary.enumType(fieldEntry.fieldId(), fieldEntry.enumValue());
                break;
            case DataTypes.UINT:
                o = fieldEntry.uintValue();
                break;
            case DataTypes.DATE:
                o = toDate(fieldEntry.date());
                break;
            case DataTypes.DATETIME:
                o = toDateTime(fieldEntry.dateTime());
                break;
            case DataTypes.TIME:
                o = toTime(fieldEntry.time());
                break;
            case DataTypes.INT:
                o = fieldEntry.intValue();
                break;
            case DataTypes.FLOAT:
                o = fieldEntry.floatValue();
                break;
            case DataTypes.DOUBLE:
                o = fieldEntry.doubleValue();
                break;
            case DataTypes.ASCII:
                o = fieldEntry.ascii().ascii();
                break;
            case DataTypes.RMTES:
                o = new String(fieldEntry.rmtes().rmtes().asUTF8().array(), StandardCharsets.UTF_8);
                break;
            case DataTypes.NO_DATA:
            case DataTypes.ERROR:
                o = null;
                break;
            default:
                LOGGER.debug("[{}] Not handling field [{}] of type [{}]", identifier, fieldEntry.fieldId(),
                        fieldEntry.loadType());
                o = null;
                break;
            }
            return Optional.ofNullable(o).map(Object::toString);
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to extract field [{}/{}]: {}", identifier, fieldEntry.fieldId(),
                    fieldEntry.loadType(), e.getMessage());
        }

        return Optional.empty();
    }

    private String toTime(OmmTime time) {
        return LocalTime.of(time.hour(), time.minute(), time.second()).format(formatTime);
    }

    private String toDate(OmmDate date) {
        return LocalDate.of(date.year(), date.month(), date.day()).format(formatDate).toUpperCase();
    }

    private String toDateTime(OmmDateTime dateTime) {
        return LocalDateTime.of(dateTime.year(), dateTime.month(), dateTime.day(), dateTime.hour(), dateTime.minute(),
                dateTime.second()).format(formatDateTime).toUpperCase();
    }

}
