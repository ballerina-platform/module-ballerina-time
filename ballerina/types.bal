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

# Holds the seconds as a decimal value.  
public type Seconds decimal;

# Point on UTC time-scale.
# This is represented by a tuple of length 2.
# The tuple is an ordered type and so the values can be
# compared using the Ballerina <, <=, >, >= operators.
# The first member of the tuple is int representing an integral number of
# seconds from the epoch.
# Epoch is the traditional UNIX epoch of `1970-01-01T00:00:00Z`.
# The second member of the tuple is a decimal giving the fraction of
# a second.
# For times before the epoch, n is negative and f is
# non-negative. In other words, the UTC time represented
# is on or after the second specified by n.
# Leap seconds are handled as follows. The first member
# of the tuple ignores leap seconds: it assumes that every day
# has 86400 seconds. The second member of the tuple is >= 0.
# and is < 1 except during positive leaps seconds in which it
# is >= 1 and < 2. So given a tuple [n,f] after the epoch,
# n / 86400 gives the day number, and (n % 86400) + f gives the
# time in seconds since midnight UTC (for which the limit is
# 86401 on day with a positive leap second).
public type Utc readonly & [int, decimal];

# Represents Sunday from integer 0.
public const int SUNDAY = 0;
# Monday represents from integer 1.
public const int MONDAY = 1;
# Tuesday represents from integer 2.
public const int TUESDAY = 2;
# Wednesday represents from integer 3.
public const int WEDNESDAY = 3;
# Thursday represents from integer 4.
public const int THURSDAY = 4;
# Friday represents from integer 5.
public const int FRIDAY = 5;
# Saturday represents from integer 6.
public const int SATURDAY = 6;

# The day of week according to the US convention.
public type DayOfWeek SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY;

# Fields of the Date record.
#
# + month - Month as an integer (1 <= month <= 12)
# + year - Year as an integer
# + day - Day as an integer (1 <= day <= 31)
type DateFields record {
    int year;
    int month;
    int day;
};

# Fields of the TimeOfDay record.
# + hour - Hour as an integer(0 <= hour <= 23)
# + minute - Minute as an integer(0 <= minute <= 59)
# + second - Second as decimal value with nanoseconds precision
type TimeOfDayFields record {
    int hour;
    int minute;
    Seconds second?;
};

# Date in proleptic Gregorian calendar with all the fields beign optional.
#
# + month - Month as an integer(1 <= month <= 12)
# + year - Year as an integer
# + day - Day as an integer (1 <= day <= 31)
type OptionalDateFields record {
    int year?;
    int month?;
    int day?;
};

# TimeOfDay with all the fields beign optional.
#
# + hour - Hour as an integer(0 <= hour <= 23)
# + minute - Minute as an integer(0 <= minute <= 59)
# + second - Second as decimal value with nanoseconds precision
type OptionalTimeOfDayFields record {
    int hour?;
    int minute?;
    Seconds second?;
};

# Date in proleptic Gregorian calendar.
#
# + utcOffset - Optional zone offset
public type Date record {
    *DateFields;
    *OptionalTimeOfDayFields;
    ZoneOffset utcOffset?;
};

# Time within a day
# Not always duration from midnight.
#
# + utcOffset - Optional zone offset
public type TimeOfDay record {
    *OptionalDateFields;
    *TimeOfDayFields;
    ZoneOffset utcOffset?;
};

# This is closed so it is a subtype of Delta
# Fields can negative
# if any of the three fields are > 0, then all must be >= 0
# if any of the three fields are < 0, then all must be <= 0
# Semantic is that durations should be left out
public type ZoneOffset readonly & record {|
    int hours;
    int minutes = 0;
    # IETF zone files have historical zones that are offset by
    # integer seconds; we use Seconds type so that this is a subtype
    # of Delta
    decimal seconds?;
|};

type ReadWriteZoneOffset record {|
    int hours;
    int minutes = 0;
    decimal seconds?;
|};

# Represents the `Z` zone, hours: 0 and minutes: 0.
public final ZoneOffset Z = {hours: 0};

# Represents the type that can be either zero or one.
public type ZERO_OR_ONE 0|1;

# Time within some region relative to a
# time scale stipulated by civilian authorities.
# + utcOffset - An optional zone offset
# + timeAbbrev - If present, abbreviation for the local time (e.g., EDT, EST) in effect at the time represented by this record;
# this is quite the same as the name of a time zone one time zone can have two abbreviations: one for
# standard time and one for daylight savings time
# + which - when the clocks are put back at the end of DST,
# one hour's worth of times occur twice
# i.e. the local time is ambiguous
# this says which of those two times is meant
# same as fold field in Python
# see https://www.python.org/dev/peps/pep-0495/
# is_dst has similar role in struct tm,
# but with confusing semantics
# + dayOfWeek - Day of the week (e.g., SUNDAY, MONDAY, TUESDAY, ... SATURDAY)
public type Civil record {
    // the date time in that region
    *DateFields;
    *TimeOfDayFields;
    ZoneOffset utcOffset?;
    string timeAbbrev?;
    ZERO_OR_ONE which?;
    DayOfWeek dayOfWeek?;
};

# Default zone value representation in different formats.
public type UtcZoneHandling "0"|"GMT"|"UT"|"Z";

# Represents the time duration used to adjust a civil date-time value by a specified amount.
# The duration can be added to or subtracted from the civil time.
# Fields in the record can be negative, in which case the duration is subtracted.
public type Duration record {|
    # The duration in years.
    int years = 0;
    # The duration in months.
    int months = 0;
    # The duration in weeks.
    int weeks = 0;
    # The duration in days.
    int days = 0;
    # The duration in hours.
    int hours = 0;
    # The duration in minutes.
    int minutes = 0;
    # The duration in seconds.
    Seconds seconds = 0.0;
|};

# Indicate how to handle both `zoneOffset` and `timeAbbrev`.
public enum HeaderZoneHandling {
    PREFER_TIME_ABBREV,
    PREFER_ZONE_OFFSET,
    ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT
}

# Abstract object representation to handle time zones.  
public type Zone readonly & object {

    # If always at a fixed offset from Utc, then this function returns it; otherwise nil.
    #
    # + return - The fixed zone offset or nil
    public isolated function fixedOffset() returns ZoneOffset?;

    # Converts a given `time:Civil` value to an `time:Utc` timestamp based on the time zone value.
    #
    # + civil - The `time:Civil` value to be converted
    # + return - The corresponding `time:Utc` value or an error if `civil.timeAbbrev` is missing
    public isolated function utcFromCivil(Civil civil) returns Utc|Error;

    # Converts a given `time:Utc` timestamp to a `time:Civil` value based on the time zone value.
    #
    # + utc - The `time:Utc` timestamp value to be converted
    # + return - The corresponding `time:Civil` value
    public isolated function utcToCivil(Utc utc) returns Civil;

    # Adds the given time duration to the specified civil date-time based on the time zone.
    # The operation assumes that all days have exactly 86,400 seconds.
    #
    # + civil - The civil time to which the duration should be added
    # + duration - The date-time duration to be added
    # + return - The civil time after adding the duration
    public isolated function civilAddDuration(Civil civil, Duration duration) returns Civil|Error;

};

# Localized time zone implementation to handle time zones.  
public readonly class TimeZone {
    *Zone;

    # Initialize a TimeZone class using a zone ID.
    #
    # + zoneId - Zone ID as a string or nil to initialize a TimeZone object with the system default time zone
    # + return - An `time:Error` if the zone ID is invalid, otherwise nil
    public isolated function init(string? zoneId = ()) returns Error? {
        if zoneId is string {
            externTimeZoneInitWithId(self, zoneId);
        } else {
            check externTimeZoneInitWithSystemZone(self);
        }
    }

    # If always at a fixed offset from Utc, then this function returns it; otherwise nil.
    #
    # + return - The fixed zone offset or nil
    public isolated function fixedOffset() returns ZoneOffset? {
        return externTimeZoneFixedOffset(self);
    }

    # Converts a given `time:Civil` value to an `time:Utc` timestamp based on the time zone value.
    #
    # + civil - The `time:Civil` value to be converted
    # + return - The corresponding `time:Utc` value or an error if `civil.timeAbbrev` is missing
    public isolated function utcFromCivil(Civil civil) returns Utc|Error {
        string? timeAbbrev = civil?.timeAbbrev;
        if timeAbbrev is () {
            return error FormatError("Abbreviation for the local time is required for the conversion");
        }
        decimal? civilTimeSecField = civil?.second;
        decimal civilTimeSeconds = (civilTimeSecField is Seconds) ? civilTimeSecField : 0.0;

        return externTimeZoneUtcFromCivil(self, civil.year, civil.month, civil.day, civil.hour, civil.minute, civilTimeSeconds, timeAbbrev, PREFER_TIME_ABBREV);
    }

    # Converts a given `time:Utc` timestamp to a `time:Civil` value based on the time zone value.
    #
    # + utc - The `time:Utc` timestamp value to be converted
    # + return - The corresponding `time:Civil` value
    public isolated function utcToCivil(Utc utc) returns Civil {
        return externTimeZoneUtcToCivil(self, utc);
    }

    # Adds the given time duration to the specified civil date-time based on the time zone.
    # The operation assumes that all days have exactly 86,400 seconds.
    # ```ballerina
    # time:TimeZone timeZone = check new("Asia/Colombo");
    # time:Civil civil = check time:civilFromString("2025-04-25T10:15:30.00Z");
    # time:Civil|time:Error updatedCivil = timeZone.civilAddDuration(civil, {years: 1, days: 3, hours: 4});
    # ```
    # + civil - The civil time to which the duration should be added
    # + duration - The date-time duration to be added
    # + return - The civil time after adding the duration
    public isolated function civilAddDuration(Civil civil, Duration duration) returns Civil|Error {
        ZoneOffset? utcOffset = civil?.utcOffset;
        string? timeAbbrev = civil?.timeAbbrev;
        HeaderZoneHandling zoneHandling = PREFER_ZONE_OFFSET;
        if utcOffset is () && timeAbbrev is () {
            return error FormatError("The civil value should have either `utcOffset` or `timeAbbrev`");
        } else if utcOffset is () && timeAbbrev is string {
            zoneHandling = PREFER_TIME_ABBREV;
        }
        int utcOffsetHours = utcOffset?.hours ?: 0;
        int utcOffsetMinutes = utcOffset?.minutes ?: 0;
        decimal utcOffsetSeconds = utcOffset?.seconds ?: 0.0;
        decimal civilTimeSeconds = civil?.second ?: 0.0;

        return externTimeZoneCivilAddDuration(self, civil.year, civil.month, civil.day, civil.hour, civil.minute,
                civilTimeSeconds, utcOffsetHours, utcOffsetMinutes, utcOffsetSeconds, timeAbbrev ?: "", zoneHandling,
                duration.years, duration.months, duration.days, duration.hours, duration.minutes, duration.seconds);
    }
}

# Load the default time zone of the system.
# ```ballerina
# time:Zone|time:Error zone = time:loadSystemZone();
# ```
# + return - Zone value or error when the zone ID of the system is in invalid format. 
public isolated function loadSystemZone() returns Zone|Error {
    return check new TimeZone();
}

# Return the time zone object of a given zone ID.
# ```ballerina
# time:Zone? zone = time:getZone("Asia/Colombo");
# ```
# + id - Time zone ID in the format of ("Continent/City")
# + return - Corresponding time zone object or null
public isolated function getZone(string id) returns Zone? {
    TimeZone|Error timeZone = new TimeZone(id);
    if timeZone is TimeZone {
        return timeZone;
    }
    return;
}

isolated function externTimeZoneInitWithSystemZone(TimeZone timeZone) returns Error? = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;

isolated function externTimeZoneInitWithId(TimeZone timeZone, string zoneId) = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;

isolated function externTimeZoneFixedOffset(TimeZone timeZone) returns ZoneOffset? = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;

isolated function externTimeZoneUtcToCivil(TimeZone timeZone, Utc utc) returns Civil = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;

isolated function externTimeZoneUtcFromCivil(TimeZone timeZone, int year, int month, int day,
        int hour, int minute, decimal second, string timeAbber, HeaderZoneHandling zoneHandling)
returns Utc|Error = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;

isolated function externTimeZoneCivilAddDuration(TimeZone timeZone, int year, int month, int day, int hour, int minute,
        decimal second, int zoneHour, int zoneMinute, decimal zoneSecond, string timeAbbrev,
        HeaderZoneHandling zoneHandling, int duYear, int duMonth, int duDay, int duHour, int duMinute,
        decimal duSecond) returns Civil|Error = @java:Method {
    'class: "io.ballerina.stdlib.time.nativeimpl.TimeZoneExternUtils"
} external;
