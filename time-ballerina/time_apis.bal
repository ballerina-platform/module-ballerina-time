// Copyright (c) 2021 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
import ballerina/jballerina.java;

# Returns Utc representing current time(current instant of the system clock as seconds from the epoch of 1970-01-01T00:00:00).
# + precision - Specifies number of zeros after decimal point (e.g. 3 would give millisecond precision
# and nil means native precision(nanosecond precision 9) of clock)
# ```ballerina
# time:Utc utc = time:utcNow();
# ```
# + return - The `time:Utc` value corresponding to the current UTC time
public isolated function utcNow(int? precision = ()) returns Utc {
    return externUtcNow(precision ?: -1);
}

# Returns no of seconds from unspecified epoch.
# This API guarantees consistent value increase in subsequent calls with nanoseconds precision.
# ```ballerina
# decimal seconds = time:monotonicNow();
# ```
# + return - Number of seconds from an unspecified epoch
public isolated function monotonicNow() returns decimal {
    return externMonotonicNow();
}

# Converts from RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`) to Utc.
# ```ballerina
# time:Utc|time:Error utc = time:utcFromString("2007-12-0310:15:30.00Z");
# ```
# + timestamp - RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`) value as a string
# + return - The corresponding `time:Utc` or a `time:Error` when the specified timestamp
# is not adhere to the RFC 3339 format(e.g. `2007-12-03T10:15:30.00Z`)
public isolated function utcFromString(string timestamp) returns Utc|Error {
    return externUtcFromString(timestamp);
}

# Converts a given `time:Utc` time to a RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`).
# ```ballerina
# string utcString = time:utcToString(time:utcNow());
# ```
# + utc - Utc time as a tuple `[int, decimal]`
# + return - The corresponding RFC 3339 timestamp string
public isolated function utcToString(Utc utc) returns string {
    return externUtcToString(utc);
}

# Returns Utc time that occurs seconds after `utc`. This assumes that all days have 86400 seconds except when utc
# represents a time during a positive leap second, in which case the corresponding day will be assumed to have 86401 seconds.
# ```ballerina
# time:Utc utc = time:utcAddSeconds(time:utcNow(), 20.900);
# ```
# + utc - Utc time as a tuple `[int, decimal]`
# + seconds - Number of seconds to be added
# + return - The resulted `time:Utc` value after the summation
public isolated function utcAddSeconds(Utc utc, Seconds seconds) returns Utc {
    [int, decimal] [secondsFromEpoch, lastSecondFraction] = utc;
    secondsFromEpoch = secondsFromEpoch + <int>seconds.floor();
    lastSecondFraction = lastSecondFraction + (seconds - seconds.floor());
    if (lastSecondFraction >= 1.0d) {
        secondsFromEpoch = secondsFromEpoch + <int>lastSecondFraction.floor();
        lastSecondFraction = lastSecondFraction - lastSecondFraction.floor();
    }
    return [secondsFromEpoch, lastSecondFraction];
}

# Returns difference in seconds between utc1 and utc2.
# This will be positive if utc1 occurs after utc2
# ```ballerina
# time:Utc utc1 = time:utcNow();
# time:Utc utc2 = check time:utcFromString("2021-04-12T23:20:50.520Z");
# time:Seconds seconds = time:utcDiffSeconds(utc1, utc2);
# ```
# + utc1 - 1st Utc time as a tuple `[int, decimal]`
# + utc2 - 2nd Utc time as a tuple `[int, decimal]`
# + return - The difference between `utc1` and `utc2` as `Seconds`
public isolated function utcDiffSeconds(Utc utc1, Utc utc2) returns Seconds {
    return externUtcDiffSeconds(utc1, utc2);
}

# Check that days and months are within range as per Gregorian calendar rules.
# ```ballerina
# time:Date date = {year: 1994, month: 11, day: 7};
# time:Error? isValid = time:dateValidate(date);
# ```
# + date - The date to be validated
# + return - `()` if the `date` is valid or else `time:Error`
public isolated function dateValidate(Date date) returns Error? {
    return externDateValidate(date);
}

# Get the day of week for a specified date.
# ```ballerina
# time:Date date = {year: 1994, month: 11, day: 7};
# time:DayOfWeek day = time:dayOfWeek(date);
# ```
# + date - Date value
# + return - `DayOfWeek` if the `date` is valid or else panic
public isolated function dayOfWeek(Date date) returns DayOfWeek {
    DayOfWeek[] daysOfWeek = [SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY];
    return daysOfWeek[checkpanic externDayOfWeek(date)];
}

# Converts a given `Utc` timestamp to a `Civil` value.
# ```ballerina
# time:Utc utc = time:utcNow();
# time:Civil civil = time:utcToCivil(utc);
# ```
# + utc - `Utc` timestamp
# + return - The corresponding `Civil` value
public isolated function utcToCivil(Utc utc) returns Civil {
    return externUtcToCivil(utc);
}

# Converts a given `Civil` value to an `Utc` timestamp.
# ```ballerina
# time:Civil civil = time:utcToCivil(time:utcNow());
# time:Utc utc = time:utcFromCivil(civil);
# ```
# + civilTime - `Civil` time
# + return - The corresponding `Utc` value or an error if `civilTime.utcOffset` is missing
public isolated function utcFromCivil(Civil civilTime) returns Utc|Error {
    if (civilTime?.utcOffset is ()) {
        return error FormatError("civilTime.utcOffset must not be null");
    }
    ZoneOffset utcOffset = <ZoneOffset>civilTime?.utcOffset;
    decimal? civilTimeSecField = civilTime?.second;
    decimal? utcOffsetSecField = utcOffset?.seconds;
    decimal civilTimeSeconds = (civilTimeSecField is Seconds) ? civilTimeSecField : 0.0;
    decimal utcOffsetSeconds = (utcOffsetSecField is decimal) ? utcOffsetSecField : 0.0;

    return externUtcFromCivil(civilTime.year, civilTime.month, civilTime.day, civilTime.hour, civilTime.minute, 
    civilTimeSeconds, utcOffset.hours, utcOffset.minutes, utcOffsetSeconds);
}

# Converts a given RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`) to `time:Civil`.
# ```ballerina
# time:Civil|time:Error civil1 = time:civilFromString("2021-04-12T23:20:50.520+05:30[Asia/Colombo]");
# time:Civil|time:Error civil2 = time:civilFromString("2007-12-03T10:15:30.00Z");
# ```
# + dateTimeString - RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`) as a string
# + return - The corresponding `time:Civil` value or an error if the given `dateTimeString` is invalid
public isolated function civilFromString(string dateTimeString) returns Civil|Error {
    return check externCivilFromString(dateTimeString);
}

# Obtain a RFC 3339 timestamp(e.g. `2007-12-03T10:15:30.00Z`) from a given `time:Civil`.
# ```ballerina
# time:Civil civil = check time:civilFromString("2007-12-03T10:15:30.00Z");
# string|time:Error civilString = time:civilToString(civil);
# ```
# + civil - `time:Civil` that needs to be converted
# + return - The corresponding string value or an error if the specified `time:Civil` contains invalid parameters(e.g. `month` > 12)
public isolated function civilToString(Civil civil) returns string|Error {
    if (civil?.utcOffset is ()) {
        return error FormatError("civil.utcOffset must not be null");
    }
    ZoneOffset utcOffset = <ZoneOffset>civil?.utcOffset;
    decimal? civilTimeSecField = civil?.second;
    decimal? utcOffsetSecField = utcOffset?.seconds;
    decimal civilTimeSeconds = (civilTimeSecField is Seconds) ? civilTimeSecField : 0.0;
    decimal utcOffsetSeconds = (utcOffsetSecField is decimal) ? utcOffsetSecField : 0.0;

    return externCivilToString(civil.year, civil.month, civil.day, civil.hour, civil.minute, civilTimeSeconds, utcOffset.
    hours, utcOffset.minutes, utcOffsetSeconds);
}

# Converts a given UTC to an email formatted string(e.g `Mon, 3 Dec 2007 10:15:30 GMT`).
# ```ballerina
# time:Utc utc = time:utcNow();
# string emailFormattedString = time:utcToEmailString(utc);
# ```
# + utc - The UTC value to be formatted
# + zh - Type of the zone value to be added
# + return - The corresponding formatted string
public isolated function utcToEmailString(Utc utc, UtcZoneHandling zh = "0") returns string {
    return externUtcToEmailString(utc, zh);
}

# Converts a given RFC 5322 formatted(e.g `Wed, 10 Mar 2021 19:51:55 -0800 (PST)`) string to a civil record.
# ```ballerina
# time:Civil|time:Error emailDateTime = time:civilFromEmailString("Wed, 10 Mar 2021 19:51:55 -0820");
# ```
# + dateTimeString - RFC 5322 formatted(e.g `Wed, 10 Mar 2021 19:51:55 -0800 (PST)`) string to be converted
# + return - The corresponding civil record or an error if the given string is incorrectly formatted.
public isolated function civilFromEmailString(string dateTimeString) returns Civil|Error {
    return check externCivilFromEmailString(dateTimeString);
}

# Converts a given Civil record to RFC 5322 format(e.g `Wed, 10 Mar 2021 19:51:55 -0800 (PST)`).
# ```ballerina
# time:Civil civil = check time:civilFromString("2021-04-12T23:20:50.520+05:30[Asia/Colombo]");
# string|time:Error emailDateTime = time:civilToEmailString(civil, time:PREFER_ZONE_OFFSET);
# ```
# + civil - The civil record to be converted
# + zoneHandling - Indicate how to handle the zone by specifying the preference whether to give preference to zone
# offset or time abbreviation. Also, this can configure to use zone offset to the execution and use time abbreviation as a comment.
# + return - RFC 5322 formatted(e.g `Wed, 10 Mar 2021 19:51:55 -0800 (PST)`) string or
# an error if the specified `time:Civil` contains invalid parameters(e.g. `month` > 12)
public isolated function civilToEmailString(Civil civil, HeaderZoneHandling zoneHandling) returns string|Error {
    if civil?.utcOffset is () {
        return error FormatError("civil.utcOffset must not be null");
    }
    ZoneOffset utcOffset = <ZoneOffset>civil?.utcOffset;
    decimal? civilTimeSecField = civil?.second;
    decimal? utcOffsetSecField = utcOffset?.seconds;
    string? timeAbbrevField = civil?.timeAbbrev;
    decimal civilTimeSeconds = (civilTimeSecField is Seconds) ? civilTimeSecField : 0.0;
    decimal utcOffsetSeconds = (utcOffsetSecField is decimal) ? utcOffsetSecField : 0.0;
    string timeAbbrev = (timeAbbrevField is string) ? timeAbbrevField : "";

    return externCivilToEmailString(civil.year, civil.month, civil.day, civil.hour, civil.minute, civilTimeSeconds, 
    utcOffset.hours, utcOffset.minutes, utcOffsetSeconds, timeAbbrev, zoneHandling);
}

isolated function externUtcNow(int precision) returns Utc = @java:Method {
    name: "externUtcNow",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externMonotonicNow() returns Seconds = @java:Method {
    name: "externMonotonicNow",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcFromString(string str) returns Utc|Error = @java:Method {
    name: "externUtcFromString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcToString(Utc utc) returns string = @java:Method {
    name: "externUtcToString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcDiffSeconds(Utc utc1, Utc utc2) returns Seconds = @java:Method {
    name: "externUtcDiffSeconds",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externDateValidate(Date date) returns Error? = @java:Method {
    name: "externDateValidate",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externDayOfWeek(Date date) returns int|Error = @java:Method {
    name: "externDayOfWeek",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcToCivil(Utc utc) returns Civil = @java:Method {
    name: "externUtcToCivil",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcFromCivil(int year, int month, int day, int hour, int minute, decimal second, int zoneHour, 
                                     int zoneMinute, decimal zoneSecond) returns Utc|Error = @java:Method {
    name: "externUtcFromCivil",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externCivilFromString(string dateTimeString) returns Civil|Error = @java:Method {
    name: "externCivilFromString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externCivilToString(int year, int month, int day, int hour, int minute, decimal second, int zoneHour, 
                                      int zoneMinute, decimal zoneSecond) returns string|Error = @java:Method {
    name: "externCivilToString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externUtcToEmailString(Utc utc, string zh) returns string = @java:Method {
    name: "externUtcToEmailString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externCivilFromEmailString(string dateTimeString) returns Civil|Error = @java:Method {
    name: "externCivilFromEmailString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;

isolated function externCivilToEmailString(int year, int month, int day, int hour, int minute, decimal second, 
                                           int zoneHour, int zoneMinute, decimal zoneSecond, string timeAbber, 
                                           HeaderZoneHandling zoneHandling) returns string|Error = @java:Method {
    name: "externCivilToEmailString",
    'class: "org.ballerinalang.stdlib.time.nativeimpl.ExternMethods"
} external;
