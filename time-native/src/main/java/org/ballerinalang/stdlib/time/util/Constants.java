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

package org.ballerinalang.stdlib.time.util;

import java.math.BigDecimal;

/**
 * Constants used in Ballerina Time library.
 *
 * @since 1.1.0
 */
public class Constants {
    private Constants() {}
    public static final String RECORD_UTC = "Utc";
    public static final BigDecimal ANALOG_GIGA = new BigDecimal(1000000000);

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

}

