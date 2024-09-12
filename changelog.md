# Change Log
This file contains all the notable changes done to the Ballerina time package through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [unreleased]

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
