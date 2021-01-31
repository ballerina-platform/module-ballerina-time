/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.PredefinedTypes;
import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.TupleType;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.ballerinalang.stdlib.time.util.ModuleUtils;
import org.ballerinalang.stdlib.time.util.TimeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.ballerinalang.stdlib.time.util.Constants.DAYS;
import static org.ballerinalang.stdlib.time.util.Constants.HOURS;
import static org.ballerinalang.stdlib.time.util.Constants.MILLISECONDS;
import static org.ballerinalang.stdlib.time.util.Constants.MINUTES;
import static org.ballerinalang.stdlib.time.util.Constants.MONTHS;
import static org.ballerinalang.stdlib.time.util.Constants.MULTIPLIER_TO_NANO;
import static org.ballerinalang.stdlib.time.util.Constants.SECONDS;
import static org.ballerinalang.stdlib.time.util.Constants.STRUCT_TYPE_DURATION;
import static org.ballerinalang.stdlib.time.util.Constants.YEARS;

/**
 * Extern methods used in Ballerina Time library.
 *
 * @since 1.1.0
 */
public class ExternMethods {
    private ExternMethods() {}

    private static final TupleType getDateTupleType = TypeCreator.createTupleType(
            Arrays.asList(PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_INT));
    private static final TupleType getTimeTupleType = TypeCreator.createTupleType(
            Arrays.asList(PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_INT,
                    PredefinedTypes.TYPE_INT));

    public static BString toTimeString(BMap<BString, Object> timeRecord) {
        return TimeUtils.getDefaultString(timeRecord);
    }

    public static Object format(BMap<BString, Object> timeRecord, BString pattern) {
        try {
            ZonedDateTime zonedDateTime = TimeUtils.getZonedDateTime(timeRecord);
            switch (pattern.getValue()) {
                case "BASIC_ISO_DATE":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.BASIC_ISO_DATE));
                case "ISO_DATE":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_DATE));
                case "ISO_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_TIME));
                case "ISO_DATE_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
                case "ISO_LOCAL_DATE_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                case "ISO_OFFSET_DATE_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                case "ISO_ZONED_DATE_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
                case "RFC_1123_DATE_TIME":
                    return StringUtils.fromString(zonedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
                default:
                    return TimeUtils.getFormattedString(timeRecord, pattern);
            }
        } catch (IllegalArgumentException e) {
            return TimeUtils.getTimeError("Invalid Pattern: " + pattern.getValue() + ", " + e.getMessage());
        }
    }

    public static long getYear(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getYear();
    }

    public static long getMonth(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getMonthValue();
    }

    public static long getDay(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getDayOfMonth();
    }

    public static Object getWeekday(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return StringUtils.fromString(dateTime.getDayOfWeek().toString());
    }

    public static long getHour(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getHour();
    }

    public static long getMinute(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getMinute();
    }

    public static long getSecond(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getSecond();
    }

    public static long getMilliSecond(BMap<BString, Object> timeRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        return dateTime.getNano() / MULTIPLIER_TO_NANO;
    }

    public static BArray getDate(BMap<BString, Object> timeRecord) {
        BArray date = ValueCreator.createTupleValue(getDateTupleType);
        date.add(0, Long.valueOf(getYear(timeRecord)));
        date.add(1, Long.valueOf(getMonth(timeRecord)));
        date.add(2, Long.valueOf(getDay(timeRecord)));
        return date;
    }

    public static BArray getTime(BMap<BString, Object> timeRecord) {
        BArray time = ValueCreator.createTupleValue(getTimeTupleType);
        time.add(0, Long.valueOf(getHour(timeRecord)));
        time.add(1, Long.valueOf(getMinute(timeRecord)));
        time.add(2, Long.valueOf(getSecond(timeRecord)));
        time.add(3, Long.valueOf(getMilliSecond(timeRecord)));
        return time;
    }

    public static BMap<BString, Object> addDuration(BMap<BString, Object> timeRecord,
                                                    BMap<BString, Object> durationRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        long years = durationRecord.getIntValue(StringUtils.fromString(YEARS));
        long months = durationRecord.getIntValue(StringUtils.fromString(MONTHS));
        long days = durationRecord.getIntValue(StringUtils.fromString(DAYS));
        long hours = durationRecord.getIntValue(StringUtils.fromString(HOURS));
        long minutes = durationRecord.getIntValue(StringUtils.fromString(MINUTES));
        long seconds = durationRecord.getIntValue(StringUtils.fromString(SECONDS));
        long milliSeconds = durationRecord.getIntValue(StringUtils.fromString(MILLISECONDS));
        long nanoSeconds = milliSeconds * MULTIPLIER_TO_NANO;
        dateTime = dateTime.plusYears(years).plusMonths(months).plusDays(days).plusHours(hours).plusMinutes(minutes)
                .plusSeconds(seconds).plusNanos(nanoSeconds);
        long mSec = dateTime.toInstant().toEpochMilli();
        return TimeUtils.createTimeRecord(mSec, TimeUtils.getZoneId(timeRecord));
    }

    public static BMap<BString, Object> subtractDuration(BMap<BString, Object> timeRecord,
                                                         BMap<BString, Object> durationRecord) {
        ZonedDateTime dateTime = TimeUtils.getZonedDateTime(timeRecord);
        long years = durationRecord.getIntValue(StringUtils.fromString(YEARS));
        long months = durationRecord.getIntValue(StringUtils.fromString(MONTHS));
        long days = durationRecord.getIntValue(StringUtils.fromString(DAYS));
        long hours = durationRecord.getIntValue(StringUtils.fromString(HOURS));
        long minutes = durationRecord.getIntValue(StringUtils.fromString(MINUTES));
        long seconds = durationRecord.getIntValue(StringUtils.fromString(SECONDS));
        long milliSeconds = durationRecord.getIntValue(StringUtils.fromString(MILLISECONDS));
        long nanoSeconds = milliSeconds * MULTIPLIER_TO_NANO;
        dateTime = dateTime.minusYears(years).minusMonths(months).minusDays(days).minusHours(hours)
                .minusMinutes(minutes).minusSeconds(seconds).minusNanos(nanoSeconds);
        long mSec = dateTime.toInstant().toEpochMilli();
        return TimeUtils.createTimeRecord(mSec, TimeUtils.getZoneId(timeRecord));
    }

    public static Object toTimeZone(BMap<BString, Object> timeRecord, BString zoneId) {
        try {
            return TimeUtils.changeTimezone(timeRecord, zoneId);
        } catch (BError e) {
            return TimeUtils.getTimeError(e.getMessage());
        }
    }

    public static BMap<BString, Object> currentTime() {
        long currentTime = Instant.now().toEpochMilli();
        return TimeUtils.createTimeRecord(currentTime, StringUtils.fromString(ZoneId.systemDefault().toString()));
    }

    public static Object createTime(long years, long months, long dates, long hours, long minutes,
                                    long seconds, long milliSeconds, BString zoneId) {
        try {
            return TimeUtils.createDateTime((int) years, (int) months, (int) dates, (int) hours, (int) minutes,
                            (int) seconds, (int) milliSeconds, zoneId);
        } catch (BError e) {
            return TimeUtils.getTimeError(e.getMessage());
        }
    }

    public static Object parse(BString dateString, BString pattern) {
        try {
            TemporalAccessor parsedDateTime;
            switch (pattern.getValue()) {
                case "BASIC_ISO_DATE":
                    parsedDateTime = DateTimeFormatter.BASIC_ISO_DATE.parse(dateString.getValue());
                    break;
                case "ISO_DATE":
                    parsedDateTime = DateTimeFormatter.ISO_DATE.parse(dateString.getValue());
                    break;
                case "ISO_TIME":
                    parsedDateTime = DateTimeFormatter.ISO_TIME.parse(dateString.getValue());
                    break;
                case "ISO_DATE_TIME":
                    parsedDateTime = DateTimeFormatter.ISO_DATE_TIME.parse(dateString.getValue());
                    break;
                case "ISO_LOCAL_DATE_TIME":
                    parsedDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(dateString.getValue());
                    break;
                case "ISO_OFFSET_DATE_TIME":
                    parsedDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(dateString.getValue());
                    break;
                case "ISO_ZONED_DATE_TIME":
                    parsedDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(dateString.getValue());
                    break;
                case "RFC_1123_DATE_TIME":
                    parsedDateTime = DateTimeFormatter.RFC_1123_DATE_TIME.parse(dateString.getValue());
                    break;
                default:
                    return TimeUtils.parseTime(dateString, pattern);
            }
            return TimeUtils.getTimeRecord(parsedDateTime, dateString, pattern);
        } catch (BError | DateTimeParseException e) {
            return TimeUtils.getTimeError(e.getMessage());
        }
    }

    public static Object getDifference(BMap<BString, Object> timeRecord1, BMap<BString, Object> timeRecord2) {
        try {
            ZonedDateTime dateTime1 = TimeUtils.getZonedDateTime(timeRecord1);
            ZonedDateTime dateTime2 = TimeUtils.getZonedDateTime(timeRecord2);

            long years = ChronoUnit.YEARS.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusYears(years);

            long months = ChronoUnit.MONTHS.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusMonths(months);

            long days = ChronoUnit.DAYS.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusDays(days);

            long hours = ChronoUnit.HOURS.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusHours(hours);

            long minutes = ChronoUnit.MINUTES.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusMinutes(minutes);

            long seconds = ChronoUnit.SECONDS.between(dateTime1, dateTime2);
            dateTime1 = dateTime1.plusSeconds(seconds);

            long millis = ChronoUnit.MILLIS.between(dateTime1, dateTime2);

            Map<String, Object> durationRecordMap = new HashMap<>();
            durationRecordMap.put(YEARS, years);
            durationRecordMap.put(MONTHS, months);
            durationRecordMap.put(DAYS, days);
            durationRecordMap.put(HOURS, hours);
            durationRecordMap.put(MINUTES, minutes);
            durationRecordMap.put(SECONDS, seconds);
            durationRecordMap.put(MILLISECONDS, millis);
            return ValueCreator.createRecordValue(ModuleUtils.getModule(), STRUCT_TYPE_DURATION, durationRecordMap);
        } catch (BError e) {
            return TimeUtils.getTimeError(e.getMessage());
        }
    }

    public static BArray getTimezones(Object rawOffset) {
        BArray timezoneIds;
        if (rawOffset != null) {
            long offset = (long) rawOffset;
            timezoneIds = StringUtils.fromStringArray(TimeZone.getAvailableIDs((int) offset));
        } else {
            timezoneIds = StringUtils.fromStringArray(TimeZone.getAvailableIDs());
        }
        return timezoneIds;
    }
}
