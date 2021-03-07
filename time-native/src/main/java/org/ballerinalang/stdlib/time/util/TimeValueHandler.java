/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.ballerinalang.stdlib.time.util;

import io.ballerina.runtime.api.PredefinedTypes;
import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.TupleType;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.ballerinalang.stdlib.time.util.Constants.ANALOG_GIGA;

/**
 * A util class for the time package's native implementation.
 *
 * @since 0.95.4
 */
public class TimeValueHandler {

     static final TupleType UTC_TUPLE_TYPE = TypeCreator.createTupleType(
            Arrays.asList(PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_DECIMAL));

    public static BArray createUtcFromInstant(Instant instant, int precision) {

        long secondsFromEpoc = instant.getEpochSecond();
        BigDecimal lastSecondFraction = new BigDecimal(instant.getNano()).divide(ANALOG_GIGA)
                .setScale(precision, RoundingMode.HALF_UP);
        BArray utcTuple = ValueCreator.createTupleValue(UTC_TUPLE_TYPE);
        utcTuple.add(0, secondsFromEpoc);
        utcTuple.add(1, ValueCreator.createDecimalValue(lastSecondFraction));
        return utcTuple;
    }

    public static BArray createUtcFromInstant(Instant instant) {

        long secondsFromEpoc = instant.getEpochSecond();
        BigDecimal lastSecondFraction = new BigDecimal(instant.getNano()).divide(ANALOG_GIGA);
        BArray utcTuple = ValueCreator.createTupleValue(UTC_TUPLE_TYPE);
        utcTuple.add(0, secondsFromEpoc);
        utcTuple.add(1, ValueCreator.createDecimalValue(lastSecondFraction));
        return utcTuple;
    }

    public static Instant createInstantFromUtc(BArray utc) {

        long secondsFromEpoc = 0;
        BigDecimal lastSecondFraction = new BigDecimal(0);
        if (utc.getLength() == 2) {
            secondsFromEpoc = utc.getInt(0);
            lastSecondFraction = new BigDecimal(utc.getValues()[1].toString()).multiply(Constants.ANALOG_GIGA);
        } else if (utc.getLength() == 1) {
            secondsFromEpoc = utc.getInt(0);
        }
        return Instant.ofEpochSecond(secondsFromEpoc, lastSecondFraction.intValue());
    }

    public static BMap<BString, Object> createCivilFromZoneDateTime(ZonedDateTime zonedDateTime) {

        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA));
        BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
                Constants.CIVIL_RECORD);
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_YEAR), zonedDateTime.getYear());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_MONTH), zonedDateTime.getMonthValue());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_DAY), zonedDateTime.getDayOfMonth());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_HOUR), zonedDateTime.getHour());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_MINUTE), zonedDateTime.getMinute());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_SECOND),
                ValueCreator.createDecimalValue(second));
        civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_TIME_ABBREV),
                StringUtils.fromString(zonedDateTime.getZone().toString()));
        return civilMap;
    }

    public static BMap<BString, Object> createCivilFromZoneDateTimeString(String zonedDateTimeString) {

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedDateTimeString);
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA));
        BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
                Constants.CIVIL_RECORD);
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_YEAR), zonedDateTime.getYear());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_MONTH), zonedDateTime.getMonthValue());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_DAY), zonedDateTime.getDayOfMonth());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_HOUR), zonedDateTime.getHour());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_MINUTE), zonedDateTime.getMinute());
        if (isSecondExists(zonedDateTimeString)) {
            civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_SECOND),
                    ValueCreator.createDecimalValue(second));
        }
        civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_TIME_ABBREV),
                StringUtils.fromString(zonedDateTime.getZone().toString()));
        return civilMap;
    }

    public static Object createZoneOffsetDateTime(String zonedDateTimeStr) {

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedDateTimeStr);
        BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
                Constants.READABLE_ZONE_OFFSET_RECORD);
        if (isLocalTimeZoneExists(zonedDateTimeStr)) {
            Map<String, Integer> zoneInfo = zoneOffsetMapFromString(zonedDateTime.getOffset().toString());
            if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR) != null) {
                civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_HOUR),
                        zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR).longValue());
            } else {
                civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_HOUR), 0);
            }

            if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE) != null) {
                civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_MINUTE),
                        zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE).longValue());
            } else {
                civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_MINUTE), 0);
            }

            if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND) != null) {
                civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_SECOND),
                        zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND).longValue());
            }
            return civilMap;
        }
        return null;
    }

    public static ZonedDateTime createZoneDateTimeFromCivilValues(long year, long month, long day, long hour,
                                                                  long minute, BDecimal second, long zoneHour,
                                                                  long zoneMinute, BDecimal zoneSecond)
            throws DateTimeException {

        int intSecond = second.decimalValue().setScale(0, RoundingMode.FLOOR).intValue();
        int intNanoSecond = second.decimalValue().subtract(new BigDecimal(intSecond)).multiply(Constants.ANALOG_GIGA)
                .setScale(0, RoundingMode.HALF_UP).intValue();
        int intZoneSecond = zoneSecond.decimalValue().setScale(0, RoundingMode.HALF_UP).intValue();
        ZoneId zoneId = ZoneId.of(ZoneOffset.ofHoursMinutesSeconds(
                Long.valueOf(zoneHour).intValue(), Long.valueOf(zoneMinute).intValue(), intZoneSecond).toString());
        ZonedDateTime dateTime = ZonedDateTime.of(
                Long.valueOf(year).intValue(), Long.valueOf(month).intValue(), Long.valueOf(day).intValue(),
                Long.valueOf(hour).intValue(), Long.valueOf(minute).intValue(), intSecond, intNanoSecond, zoneId);
        return dateTime;
    }

    public static BError createError(Errors errorType, String errorMsg, String details) {

        return ErrorCreator.createError(TypeCreator.createErrorType(errorType.name(), ModuleUtils.getModule()),
                StringUtils.fromString(errorMsg), StringUtils.fromString(details));
    }

    public static BError createError(Errors errorType, String errorMsg) {

        return ErrorCreator.createDistinctError(errorType.name(), ModuleUtils.getModule(),
                StringUtils.fromString(errorMsg));
    }

    public static boolean isLocalTimeZoneExists(String time) {

        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2}\\.\\d+)?(Z$)");
        return !pattern.matcher(time).find();
    }

    public static boolean isSecondExists(String time) {

        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?");
        return pattern.matcher(time).find();
    }

    public static Map<String, Integer> zoneOffsetMapFromString(String dateTime) {

        Map<String, Integer> zone = new HashMap<>();
        if (dateTime.strip().startsWith("+")) {
            dateTime = dateTime.replaceFirst("\\+", "");
            String[] zoneInfo = dateTime.split(":");
            if (zoneInfo.length > 0 && zoneInfo[0] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_HOUR, Integer.parseInt(zoneInfo[0]));
            }
            if (zoneInfo.length > 1 && zoneInfo[1] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_MINUTE, Integer.parseInt(zoneInfo[1]));
            }
            if (zoneInfo.length > 2 && zoneInfo[2] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_SECOND, Integer.parseInt(zoneInfo[2]));
            }
        } else if (dateTime.strip().startsWith("-")) {
            dateTime = dateTime.replaceFirst("\\+", "");
            String[] zoneInfo = dateTime.split(":");
            if (zoneInfo.length > 0 && zoneInfo[0] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_HOUR, Integer.parseInt(zoneInfo[0]) * -1);
            }
            if (zoneInfo.length > 1 && zoneInfo[1] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_MINUTE, Integer.parseInt(zoneInfo[1]) * -1);
            }
            if (zoneInfo.length > 2 && zoneInfo[2] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_SECOND, Integer.parseInt(zoneInfo[2]) * -1);
            }
        }
        return zone;
    }

}
