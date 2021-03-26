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

# Holds the seconds as a decimal value.  
public type Seconds decimal;

# Point on UTC time-scale.
# This is represented by a tuple of length 2.
# The tuple is an ordered type and so the values can be
# compared using the Ballerina <, <=, >, >= operators.
# First member of tuple is int representing integral number of
# seconds from the epoch.
# Epoch is traditional Unix epoch of 1970-01-01T00:00:00Z
# Second member of tuple is decimal giving the fraction of
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

# The day of weel according to the US convention.
public type DayOfWeek SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY;

# Fields of the Date record.
#
# + month - month as an integer(1 <= month <= 12)  
# + year - year as an integer  
# + day - day as an integer(1 <= day <= 31)  
type DateFields record {
    int year;
    int month;
    int day;
};

# Fields of the TimeOfDay record.
# + hour - hour as an integer(0 <= hour <= 23)  
# + minute - minute as an integer(0 <= minute <= 59)   
# + second - second as decimal value with nanoseconds precision
type TimeOfDayFields record {
    int hour;
    int minute;
    Seconds second?;
};

# Date in proleptic Gregorian calendar with all the fields beign optional.
#
# + month - month as an integer(1 <= month <= 12)  
# + year - year as an integer  
# + day - day as an integer(1 <= day <= 31)  
type OptionalDateFields record {
    int year?;
    int month?;
    int day?;
};

# TimeOfDay with all the fields beign optional.
#
# + hour - hour as an integer(0 <= hour <= 23)  
# + minute - minute as an integer(0 <= minute <= 59)   
# + second - second as decimal value with nanoseconds precision
type OptionalTimeOfDayFields record {
    int hour?;
    int minute?;
    Seconds second?;
};

# Date in proleptic Gregorian calendar.
#
# + utcOffset - optional zone offset 
public type Date record {
    *DateFields;
    *OptionalTimeOfDayFields;
    ZoneOffset utcOffset?;
};

# Time within a day
# Not always duration from midnight.
#
# + utcOffset - optional zone offset 
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
# + utcOffset - optional zone offset 
# + timeAbbrev - the string representation of the time zone
# + which - if present, abbreviation for the local time (e.g. EDT, EST) in effect at the time represented by this record;
# this is quite the same as the name of a time zone one time zone can have two abbreviations: one for
# standard time and one for daylight savings time
# + dayOfWeek - day of the week(SUNDAY, MONDAY, TUESDAY, ... SATURDAY)
public type Civil record {
    // the date time in that region
    *DateFields;
    *TimeOfDayFields;
    ZoneOffset utcOffset?;
    string timeAbbrev?;
    ZERO_OR_ONE which?;
    DayOfWeek dayOfWeek?;
};

# Defualt zone value represation in different formats.
public type UtcZoneHandling "0"|"GMT"|"UT"|"Z";

# Indicate how to handle both `zoneOffset` and `timeAbbrev`.
public enum HeaderZoneHandling {
    PREFER_TIME_ABBREV,
    PREFER_ZONE_OFFSET,
    ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT
}
