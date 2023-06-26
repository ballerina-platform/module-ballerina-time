Ballerina Time Library
===================

  [![Build](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/build-timestamped-master.yml)
  [![codecov](https://codecov.io/gh/ballerina-platform/module-ballerina-time/branch/master/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerina-time)
  [![Trivy](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/trivy-scan.yml)
  [![GraalVM Check](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/build-with-bal-test-graalvm.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-time/actions/workflows/build-with-bal-test-graalvm.yml)
  [![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerina-time.svg)](https://github.com/ballerina-platform/module-ballerina-time/commits/master)
  [![Github issues](https://img.shields.io/github/issues/ballerina-platform/ballerina-standard-library/module/time.svg?label=Open%20Issues)](https://github.com/ballerina-platform/ballerina-standard-library/labels/module%2Ftime)

This library provides a set of APIs that have the capabilities to generate and manipulate UTC and localized time.

In cloud computing, the most essential type of time is UTC. Coordinated Universal Time (UTC) is the primary time standard on which the world agreed.
UTC is independent of daylight saving time and provides a unique time value for the entire world.
The definition of UTC started from the epoc `1970-01-01T00:00:00Z`. Initially, humans divided a day into 86400 seconds.
However, the Earth rotation does not adhere to this time duration as the Earth is slowing down and the day is getting longer.
As a result, a solar day in 2012 is longer than 86400 SI seconds.
To correct this incompatibility, additional seconds have been added to the UTC scale, which is known as leap-seconds.

The focus of this library is to give the most precise UTC with nanoseconds precision and also handle some complex use cases such as leap seconds and daylight time-saving.

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
Parallel to the aforementioned time representations, this library includes a set of APIs to facilitate time conversions
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

## Issues and projects 

Issues and Project tabs are disabled for this repository as this is part of the Ballerina Standard Library. To report bugs, request new features, start new discussions, view project boards, etc. please visit Ballerina Standard Library [parent repository](https://github.com/ballerina-platform/ballerina-standard-library). 

This repository only contains the source code for the package.

## Build from the source

### Set up the prerequisites

1. Download and install Java SE Development Kit (JDK) version 17 (from one of the following locations).
   * [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

   * [OpenJDK](https://adoptium.net/)

        > **Note:** Set the JAVA_HOME environment variable to the path name of the directory into which you installed JDK.
     
### Build the source

Execute the commands below to build from source.

1. To build the library:
   ```    
   ./gradlew clean build
   ```

1. To run the integration tests:
   ```
   ./gradlew clean test
   ```
1. To build the module without the tests:
   ```
   ./gradlew clean build -x test
   ```
1. To debug module implementation:
   ```
   ./gradlew clean build -Pdebug=<port>
   ./gradlew clean test -Pdebug=<port>
   ```
1. To debug the module with Ballerina language:
   ```
   ./gradlew clean build -PbalJavaDebug=<port>
   ./gradlew clean test -PbalJavaDebug=<port>
   ```
1. Publish ZIP artifact to the local `.m2` repository:
   ```
   ./gradlew clean build publishToMavenLocal
   ```
1. Publish the generated artifacts to the local Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToLocalCentral=true
   ```
1. Publish the generated artifacts to the Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToCentral=true
   ```      

## Contribute to Ballerina

As an open source project, Ballerina welcomes contributions from the community.

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

* Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
* Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
* For more information go to [the Time Package](https://ballerina.io/learn/api-docs/ballerina/time/).
* For example demonstrations of the usage, go to [Ballerina By Examples](https://ballerina.io/learn/by-example/).
