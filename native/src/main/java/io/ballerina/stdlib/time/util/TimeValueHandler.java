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

import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.time.nativeimpl.Civil;
import io.ballerina.stdlib.time.nativeimpl.Utc;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

/**
 * A util class for the time package's native implementation.
 *
 * @since 0.95.4
 */
public class TimeValueHandler {

    public static BMap<BString, Object> createCivilFromZoneDateTime(ZonedDateTime zonedDateTime) {

        return new Civil(zonedDateTime).build();
    }

    public static BMap<BString, Object> createCivilFromZoneDateTimeString(String zonedDateTimeString) {

        return new Civil(zonedDateTimeString, Constants.CivilInputStringTypes.RFC3339_STRING).buildWithZone();
    }

    public static BMap<BString, Object> createCivilFromEmailString(String zonedDateTimeString) {

        return new Civil(zonedDateTimeString, Constants.CivilInputStringTypes.EMAIL_STRING).buildWithZone();
    }

    public static ZonedDateTime createZoneDateTimeFromCivilValues(long year, long month, long day, long hour,
                                                                  long minute, BDecimal second, long zoneHour,
                                                                  long zoneMinute, BDecimal zoneSecond,
                                                                  BString zoneAbbr, String zoneHandling) {

        return Utils.createZoneDateTimeFromCivilValues(year, month, day, hour, minute, second, zoneHour, zoneMinute,
                zoneSecond, zoneAbbr, zoneHandling);
    }

    public static Map<String, Integer> zoneOffsetMapFromString(String dateTime) {

        return Utils.zoneOffsetMapFromString(dateTime);
    }

    public static BArray createUtcFromDate(Date date) {

        return new Utc(date).build();
    }

    public static BArray createUtcFromMilliSeconds(long millis) {

        return new Utc(millis).build();
    }

    // Return the ZonedDateTime value that belongs to the `Z` time zone.
    public static ZonedDateTime createZonedDateTimeFromUtc(BArray utc) {

        return new Utc(utc).generateZonedDateAtZ();
    }
}
