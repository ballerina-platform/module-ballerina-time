# Ballerina Time Library

## Overview

This module provides APIs to generate and manipulate UTC and localized
time, with nanosecond precision and support for complex cases such as
leap seconds and daylight saving time.

## Key Features

- UTC time generation and manipulation with nanosecond precision
- Monotonic time for measuring elapsed durations from an unspecified epoch
- Civil (localized) time with date, time, timezone, and daylight saving information
- Time zone lookup by zone ID or system default
- Conversions between UTC, Civil, and RFC 3339 string representations

## Module Details

### UTC time
The `time:Utc` is the tuple representation of the UTC. The UTC represents the number of seconds from a
specified epoch. Here, the epoch is the UNIX epoch of 1970-01-01T00:00:00Z.

Use the following API to get the current epoch time:
```ballerina
time:Utc utc = time:utcNow();
```

### Monotonic time
The monotonic time represents the number of seconds from an unspecified epoch.

Use the following API to get the monotonic time from an unspecified topic:
```ballerina
decimal seconds = time:monotonicNow();
```

### Civil time
The localized time represents using the `time:Civil` record. It includes the following details:
- date
- time
- timezone information
- daylight time-saving information

### Time zone
The time zone can be obtained using a given zone ID or load the system time zone.
```ballerina
// Obtain the time zone corresponding to a given time zone ID.
time:Zone? zone = time:getZone("Asia/Colombo");

// Obtain the system time zone.
time:Zone zone = check time:loadSystemZone();
```

### APIs
Parallel to the aforementioned time representations, this module includes a set of APIs to facilitate time conversions
and manipulations using a set of high-level APIs. Those conversion APIs can be listed as follows.

#### The string representations of UTC
```ballerina
// Converts from RFC 3339 timestamp to UTC.
time:Utc utc = check time:utcFromString("2007-12-03T10:15:30.00Z");

// Converts a given `time:Utc` time to a RFC 3339 timestamp.
string utcString = time:utcToString(utc);
```

#### The string representations of civil
```ballerina
// Converts from RFC 3339 timestamp to a civil record.
time:Civil civil2 = check time:civilFromString("2007-12-03T10:15:30.00Z");

// Converts a given `time:Civil` time to a RFC 3339 timestamp.
string civilString = check time:civilToString(civil);
```

#### UTC value manipulation
```ballerina
// Returns the UTC time that occurs seconds after the given UTC.
time:Utc utc = time:utcAddSeconds(time:utcNow(), 20.900);

// Returns the difference in seconds between two given UTC time values.
time:Utc utc1 = time:utcNow();
time:Utc utc2 = check time:utcFromString("2021-04-12T23:20:50.520Z");
time:Seconds seconds = time:utcDiffSeconds(utc1, utc2);
```

#### UTC vs civil
```ballerina
// Converts a given UTC to a Civil.
time:Civil civil = time:utcToCivil(utc);

// Converts a given Civil to a UTC.
time:Utc utc = time:utcFromCivil(civil);
```
