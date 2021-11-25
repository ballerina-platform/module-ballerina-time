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
package io.ballerina.stdlib.time.util;

import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the generic utility APIs for the time package.
 *
 * @since 2.0.2
 */
public class Utils {

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
            dateTime = dateTime.replaceFirst("\\-", "");
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

    public static BMap<BString, Object> createZoneOffsetFromZoneInfoMap(Map<String, Integer> zoneInfo) {

        BMap<BString, Object> zoneOffsetMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
                Constants.READABLE_ZONE_OFFSET_RECORD);
        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR) != null) {
            zoneOffsetMap.put(Constants.ZONE_OFFSET_RECORD_HOUR_BSTRING,
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR).longValue());
        } else {
            zoneOffsetMap.put(Constants.ZONE_OFFSET_RECORD_HOUR_BSTRING, 0);
        }

        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE) != null) {
            zoneOffsetMap.put(Constants.ZONE_OFFSET_RECORD_MINUTE_BSTRING,
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE).longValue());
        } else {
            zoneOffsetMap.put(Constants.ZONE_OFFSET_RECORD_MINUTE_BSTRING, 0);
        }

        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND) != null) {
            zoneOffsetMap.put(Constants.ZONE_OFFSET_RECORD_SECOND_BSTRING,
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND).longValue());
        }
        zoneOffsetMap.freezeDirect();
        return zoneOffsetMap;
    }

    public static ZonedDateTime createZoneDateTimeFromCivilValues(long year, long month, long day, long hour,
                                                                  long minute, BDecimal second, long zoneHour,
                                                                  long zoneMinute, BDecimal zoneSecond,
                                                                  BString zoneAbbr, String zoneHandling) {

        ZoneId zoneId;
        int intSecond = second.decimalValue().setScale(0, RoundingMode.FLOOR).intValue();
        int intNanoSecond = second.decimalValue().subtract(new BigDecimal(intSecond)).multiply(Constants.ANALOG_GIGA)
                .setScale(0, RoundingMode.HALF_UP).intValue();
        int intZoneSecond = zoneSecond.decimalValue().setScale(0, RoundingMode.HALF_UP).intValue();
        if (Constants.HeaderZoneHandling.PREFER_ZONE_OFFSET.toString().equals(zoneHandling)) {
            zoneId = ZoneId.of(ZoneOffset.ofHoursMinutesSeconds(
                    Long.valueOf(zoneHour).intValue(), Long.valueOf(zoneMinute).intValue(), intZoneSecond).toString());
        } else {
            zoneId = ZoneId.of(zoneAbbr.getValue());
        }
        return ZonedDateTime.of(
                Long.valueOf(year).intValue(), Long.valueOf(month).intValue(), Long.valueOf(day).intValue(),
                Long.valueOf(hour).intValue(), Long.valueOf(minute).intValue(), intSecond, intNanoSecond, zoneId);
    }

    public static BError createError(Errors errorType, String errorMsg) {

        return ErrorCreator.createDistinctError(errorType.name(), ModuleUtils.getModule(),
                StringUtils.fromString(errorMsg));
    }
}
