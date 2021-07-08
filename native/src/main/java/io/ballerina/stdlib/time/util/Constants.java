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

package io.ballerina.stdlib.time.util;

import java.math.BigDecimal;

/**
 * Constants used in Ballerina Time library.
 *
 * @since 1.1.0
 */
public class Constants {
    private Constants() {}
    public static final String RECORD_UTC = "Utc";
    public static final String GMT_STRING_VALUE = "GMT";
    public static final String EMAIL_DATE_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z[ ][(z)]";
    public static final String EMAIL_DATE_TIME_FORMAT_WITHOUT_COMMENT = "EEE, dd MMM yyyy HH:mm:ss Z";
    public static final int UTC_MAX_PRECISION = 9;
    public static final BigDecimal ANALOG_GIGA = new BigDecimal(1000000000);
    public static final BigDecimal ANALOG_KILO = new BigDecimal(1000);

    public static final long SECONDS_PER_DAY = 86400;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int SECONDS_PER_HOUR = 3600;

    // For `time:Date`
    public static final String DATE_RECORD = "Date";
    public static final String DATE_RECORD_YEAR = "year";
    public static final String DATE_RECORD_MONTH = "month";
    public static final String DATE_RECORD_DAY = "day";

    // For `time:TimeOfDay`
    public static final String TIME_OF_DAY_RECORD = "TimeOfDay";
    public static final String TIME_OF_DAY_RECORD_HOUR = "hour";
    public static final String TIME_OF_DAY_RECORD_MINUTE = "minute";
    public static final String TIME_OF_DAY_RECORD_SECOND = "second";

    // For `time:ZoneOffset`
    public static final String READABLE_ZONE_OFFSET_RECORD = "ReadWriteZoneOffset";
    public static final String ZONE_OFFSET_RECORD = "ZoneOffset";
    public static final String ZONE_OFFSET_RECORD_HOUR = "hours";
    public static final String ZONE_OFFSET_RECORD_MINUTE = "minutes";
    public static final String ZONE_OFFSET_RECORD_SECOND = "seconds";

    // For `time:Civil`
    public static final String CIVIL_RECORD = "Civil";
    public static final String CIVIL_RECORD_UTC_OFFSET = "utcOffset";
    public static final String CIVIL_RECORD_TIME_ABBREV = "timeAbbrev";
    public static final String CIVIL_RECORD_WHICH = "which";
    public static final String CIVIL_RECORD_DAY_OF_WEEK = "dayOfWeek";

    /**
     * Mapping enumeration for Ballerina level HeaderZoneHandling.
     *
     */
    public enum HeaderZoneHandling {
        PREFER_TIME_ABBREV,
        PREFER_ZONE_OFFSET,
        ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT
    }
}

