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

package io.ballerina.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.time.util.Constants;
import io.ballerina.stdlib.time.util.Errors;
import io.ballerina.stdlib.time.util.TimeValueHandler;
import io.ballerina.stdlib.time.util.Utils;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Extern methods used in Ballerina Time library.
 *
 * @since 1.1.0
 */
public class ExternMethods {

    private ExternMethods() {

    }

    public static BArray externUtcNow(int precision) {

        Instant currentUtcTimeInstant = Instant.now();
        int precisionValue = 9;
        if (precision > 0 && precision <= 9) {
            precisionValue = precision;
        }
        return new Utc(currentUtcTimeInstant, precisionValue).build();
    }

    public static BDecimal externMonotonicNow() {

        long time = System.nanoTime();
        return ValueCreator.createDecimalValue(new BigDecimal(time).divide(Constants.ANALOG_GIGA));
    }

    public static Object externUtcFromString(BString str) {

        try {
            Instant utcTimeInstant = ZonedDateTime.parse(str.getValue()).toInstant();
            return new Utc(utcTimeInstant).build();
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError,
                    "Provided '" + str.getValue() + "' is not adhere to the expected format '2007-12-03T10:15:30.00Z'");
        }
    }

    public static BString externUtcToString(BArray utc) {

        Instant time = new Utc(utc).generateInstant();
        return StringUtils.fromString(time.toString());
    }

    public static BDecimal externUtcDiffSeconds(BArray utc1, BArray utc2) {

        Instant time1 = new Utc(utc1).generateInstant();
        Instant time2 = new Utc(utc2).generateInstant();
        time1 = time1.minusNanos(time2.getNano());
        time1 = time1.minusSeconds(time2.getEpochSecond());
        BigDecimal nanoSeconds = new BigDecimal(time1.getNano()).divide(Constants.ANALOG_GIGA);
        BigDecimal seconds = new BigDecimal(time1.getEpochSecond()).add(nanoSeconds);
        return ValueCreator.createDecimalValue(seconds);
    }

    public static Object externDateValidate(BMap date) {

        int year = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_YEAR_BSTRING));
        int month = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_MONTH_BSTRING));
        int day = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_DAY_BSTRING));
        try {
            LocalDate.of(year, month, day);
            return null;
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static Object externDayOfWeek(BMap date) {

        int year = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_YEAR_BSTRING));
        int month = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_MONTH_BSTRING));
        int day = Math.toIntExact(date.getIntValue(Constants.DATE_RECORD_DAY_BSTRING));
        try {
            return ((LocalDate.of(year, month, day).getDayOfWeek().getValue()) % 7);
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static BMap externUtcToCivil(BArray utc) {

        Instant time = new Utc(utc).generateInstant();
        ZonedDateTime zonedDateTime = time.atZone(ZoneId.of("Z"));
        return TimeValueHandler.createCivilFromZoneDateTime(zonedDateTime);
    }

    public static Object externUtcFromCivil(long year, long month, long day, long hour, long minute, BDecimal second,
                                            long zoneHour, long zoneMinute, BDecimal zoneSecond) {

        try {
            ZonedDateTime dateTime = TimeValueHandler.createZoneDateTimeFromCivilValues(year, month, day, hour,
                    minute, second, zoneHour, zoneMinute, zoneSecond, null,
                    Constants.HeaderZoneHandling.PREFER_ZONE_OFFSET.toString());
            return new Utc(dateTime.toInstant()).build();
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static Object externCivilFromString(BString dateTimeString) {

        try {
            return TimeValueHandler.createCivilFromZoneDateTimeString(dateTimeString.getValue());
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static Object externCivilFromEmailString(BString dateTimeString) {

        try {
            return TimeValueHandler.createCivilFromEmailString(dateTimeString.getValue());
        } catch (DateTimeException | IllegalArgumentException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static Object externCivilToString(long year, long month, long day, long hour, long minute, BDecimal second,
                                             long zoneHour, long zoneMinute, BDecimal zoneSecond) {

        try {
            ZonedDateTime dateTime = TimeValueHandler.createZoneDateTimeFromCivilValues(year, month, day, hour,
                    minute, second, zoneHour, zoneMinute, zoneSecond, null,
                    Constants.HeaderZoneHandling.PREFER_ZONE_OFFSET.toString());
            return StringUtils.fromString(dateTime.toInstant().toString());
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static BString externUtcToEmailString(BArray utc, BString zh) {

        Instant time = new Utc(utc).generateInstant();
        String zhString = zh.getValue();
        if (zhString.equals("0")) {
            zhString = "+0000";
        }
        return StringUtils.fromString(ZonedDateTime.ofInstant(time,
                        ZoneId.of(Constants.GMT_STRING_VALUE)).format(DateTimeFormatter.RFC_1123_DATE_TIME)
                .replace(Constants.GMT_STRING_VALUE, zhString).replace(Constants.ZERO_ZONE_STRING_VALUE, zhString));
    }

    public static Object externCivilToEmailString(long year, long month, long day, long hour, long minute,
                                                  BDecimal second, long zoneHour, long zoneMinute, BDecimal zoneSecond,
                                                  BString zoneAbbr, BString zoneHandling) {

        try {
            ZonedDateTime dateTime = TimeValueHandler.createZoneDateTimeFromCivilValues(year, month, day, hour,
                    minute, second, zoneHour, zoneMinute, zoneSecond, zoneAbbr, zoneHandling.getValue());
            if (Constants.HeaderZoneHandling.PREFER_ZONE_OFFSET.toString().equals(zoneHandling.getValue())) {
                return StringUtils.fromString(dateTime.format(DateTimeFormatter.ofPattern(
                        Constants.EMAIL_DATE_TIME_FORMAT_WITHOUT_COMMENT)));
            }
            return StringUtils.fromString(dateTime.format(DateTimeFormatter.ofPattern(
                    Constants.EMAIL_DATE_TIME_FORMAT)));
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

}
