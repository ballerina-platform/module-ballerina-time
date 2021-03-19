## Package Overview

This package provides implementations related to the UTC and localized time. In the world of cloud computing, the most essential type of time is UTC. 
Therefore, the more focus on this module is to give the most precise UTC and also handle some complex use cases such as leap seconds and daylight time-saving.

### UTC Time
The `time:Utc` is the tuple representation of the UTC. The UTC represents the number of seconds from a
specified epoch. Here, the epoch is the UNIX epoch of 1970-01-01T00:00:00Z.

### Monotonic Time
The monotonic time represents the number of seconds from an unspecified epoch.

### Civil Time
The localized time represents using `time:Civil` record. It includes the following details:
- date
- time
- timezone information
- daylight time-saving information

### APIs
Parallel to the aforementioned time representations, this package includes a set of APIs to facilitate time conversions
and manipulations using a set of high-level APIs.

To learn more about these APIs, see the [time example](https://ballerina.io/learn/by-example/time.html).

