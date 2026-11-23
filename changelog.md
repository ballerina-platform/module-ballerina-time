# Change Log
This file contains all the notable changes done to the Ballerina time package through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- [[#9873] Civil values whose local time falls in a DST fall-back overlap now carry `which` (0 for the earlier, 1 for the later occurrence); outside an overlap the record is unchanged](https://github.com/ballerina-platform/ballerina-library/issues/9873)
- [[#9873] When a civil value has both a region `timeAbbrev` and a `utcOffset` that is valid for its local time, the offset selects the occurrence in a DST overlap (`which` is used only without such an offset)](https://github.com/ballerina-platform/ballerina-library/issues/9873)

### Fixed

- [[#9873] Fix `time:Zone.utcToCivil` results resolving to the wrong instant during a DST fall-back overlap. `time:utcFromCivil` and `time:civilToEmailString` with `PREFER_ZONE_OFFSET` still require `utcOffset`, which `Zone.utcToCivil` results do not carry](https://github.com/ballerina-platform/ballerina-library/issues/9873)

## [2.8.4] - 2026-10-16

### Changed

- [[#9301] Regroup standard library packages under new Area categories](https://github.com/ballerina-platform/ballerina-library/issues/9301)

## [2.8.2] - 2026-10-02
### Changed

[[#9111] Updated Keywords and Reformat README for Connector Store Discoverability](https://github.com/ballerina-platform/ballerina-library/issues/9111)

## [2.7.0] - 2025-03-12
### Added

- [Add support for custom time duration calculations](https://github.com/ballerina-platform/ballerina-library/issues/6840)

## [2.5.0] - 2024-09-12
### Fixed
- [When converting a `time:Civil` with time-zone information to a string using `time:civilToString` API error is thrown](https://github.com/ballerina-platform/ballerina-library/issues/6986)

## [2.3.0] - 2023-06-30
### changed
- [Mark Standard Libraries as GraalVM Compatible](https://github.com/ballerina-platform/ballerina-standard-library/issues/4568)
- [Add support for inferring utc offset for zulu time(Z) in utcFromCivil and civilToString APIs](https://github.com/ballerina-platform/module-ballerina-time/pull/459) 

## [2.2.4]
### changed
- [Update error message for unsupported input strings in UtcFromString](https://github.com/ballerina-platform/ballerina-standard-library/issues/3808) 

## [2.2.3]
### Changed
- [API docs updated](https://github.com/ballerina-platform/ballerina-standard-library/issues/3463)

## [2.2.1] - 2022-03-01
### Fixed
- [Remove unnecessary condition when Civil to Email string conversion with time:PREFER_TIME_ABBREV](https://github.com/ballerina-platform/ballerina-standard-library/issues/2626)

## [2.1.0] - 2021-04-02 
### Added 
- [Add time zone handling APIs](https://github.com/ballerina-platform/ballerina-standard-library/issues/1059)

## [2.0.0-alpha6] - 2021-04-02
### Added
- [Enable APIs to use email typed date time strings](https://github.com/ballerina-platform/ballerina-standard-library/issues/1117)
    - convert a given UTC to an email string
    - convert a given Civil to an email string
    - convert a given email string to Civil
