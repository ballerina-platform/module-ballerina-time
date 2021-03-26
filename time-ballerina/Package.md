## Package Overview

This package provides implementations related to the UTC and localized time. In the world of cloud computing, the most essential type of time is UTC. 
Therefore, the more focus on this module is to give the most precise UTC and also handle some complex use cases such as leap seconds and daylight time-saving.

### UTC Time
The `time:Utc` is the tuple representation of the UTC. The UTC represents the number of seconds from a
specified epoch. Here, the epoch is the UNIX epoch of 1970-01-01T00:00:00Z.

Use the following API to get the current epoch time:
```ballerina
time:Utc utc = time:utcNow();
```

### Monotonic Time
The monotonic time represents the number of seconds from an unspecified epoch.

Use the following API to get the monotonic time from an unspecified topic:
```ballerina
decimal seconds = time:monotonicNow();
```

### Civil Time
The localized time represents using `time:Civil` record. It includes the following details:
- date
- time
- timezone information
- daylight time-saving information

### APIs
Parallel to the aforementioned time representations, this package includes a set of APIs to facilitate time conversions
and manipulations using a set of high-level APIs. Those conversion APIs can be listed as follows.

### The String Representations of UTC
```ballerina
// Converts from RFC 3339 timestamp to UTC
time:Utc utc = check time:utcFromString("2007-12-0310:15:30.00Z");

// Converts a given time:Utc time to a RFC 3339 timestamp
string utcString = time:utcToString(utc);
```

### The String Representations of Civil
```ballerina
// Converts from RFC 3339 timestamp to a civil record
time:Civil civil2 = check time:civilFromString("2007-12-03T10:15:30.00Z");

// Converts a given time:Civil time to a RFC 3339 timestamp
string civilString = check time:civilToString(civil);
```

### UTC Value Manipulation
```ballerina
// Returns the UTC time that occurs seconds after the given UTC
time:Utc utc = time:utcAddSeconds(time:utcNow(), 20.900);

// Returns difference in seconds between two given UTC time values
time:Utc utc1 = time:utcNow();
time:Utc utc2 = check time:utcFromString("2021-04-12T23:20:50.520Z");
time:Seconds seconds = time:utcDiffSeconds(utc1, utc2);
```

### UTC vs Civil
```ballerina
// Converts a given UTC to a Civil
time:Civil civil = time:utcToCivil(utc);

// Converts a given Civil to an UTC
time:Utc utc = time:utcFromCivil(civil);
```

To learn more about these APIs, see the [time example](https://ballerina.io/learn/by-example/time.html).

