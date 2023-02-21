// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import ballerina/test;

@test:Config {}
isolated function testUtcNow() returns Error? {
    Utc oldUtc = check utcFromString("2007-12-03T10:15:30.00Z");
    Utc currentUtc = utcNow();
    test:assertTrue(currentUtc[0] > oldUtc[0]);
}

@test:Config {}
isolated function testUtcNowWithPrecision() {
    Utc currentUtc1 = utcNow();
    // length(str(0.123456789)) => 11
    test:assertEquals(currentUtc1[1].toString().length(), 11);

    Utc currentUtc2 = utcNow(6);
    // length(str(0.123456)) => 8
    test:assertEquals(currentUtc2[1].toString().length(), 8);

    Utc currentUtc3 = utcNow(3);
    // length(str(0.123)) => 5
    test:assertEquals(currentUtc3[1].toString().length(), 5);
}

@test:Config {}
isolated function testMonotonicNow() {
    Seconds time1 = monotonicNow();
    Seconds time2 = monotonicNow();
    test:assertTrue(time2 >= time1);
}

@test:Config {}
isolated function testUtcFromString() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    test:assertEquals(utc[0], 1196676930);
    test:assertEquals(utc[1], <decimal>0.0);
}

@test:Config {}
isolated function testUtcFromStringWithInvalidFormat() {
    Utc|Error err = utcFromString("2007-12-0310:15:30.00Z");
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(),
        "The provided string '2007-12-0310:15:30.00Z' does not adhere to the expected RFC 3339 format 'YYYY-MM-DDTHH:MM:SS.SSZ'. ");
}

@test:Config {}
isolated function testUtcToString() returns Error? {
    Utc utc = check utcFromString("1985-04-12T23:20:50.520Z");
    int expectedSecondsFromEpoch = 482196050;
    decimal expectedSecondFraction = 0.52;
    test:assertEquals(utc[0], expectedSecondsFromEpoch);
    test:assertEquals(utc[1], expectedSecondFraction);
    string utcString = utcToString(utc);
    test:assertEquals(utcString, "1985-04-12T23:20:50.520Z");
}

@test:Config {}
isolated function testUtcToStringWithoutFraction() {
    string utcString = utcToString([482196050]);
    test:assertEquals(utcString, "1985-04-12T23:20:50Z");
}

@test:Config {}
isolated function testUtcAddSeconds() returns Error? {
    Utc utc1 = check utcFromString("2021-04-12T23:20:50.520Z");
    Utc utc2 = utcAddSeconds(utc1, 20.900);
    string utcString = utcToString(utc2);
    test:assertEquals(utcString, "2021-04-12T23:21:11.420Z");
}

@test:Config {}
isolated function testUtcDiffSeconds() returns Error? {
    Utc utc1 = check utcFromString("2021-04-12T23:20:50.520Z");
    Utc utc2 = check utcFromString("2021-04-11T23:20:50.520Z");
    decimal expectedSeconds1 = 86400;
    test:assertEquals(utcDiffSeconds(utc1, utc2), expectedSeconds1);

    Utc utc3 = check utcFromString("2021-04-12T23:20:50.520Z");
    Utc utc4 = check utcFromString("2021-04-11T23:20:55.640Z");
    decimal expectedSeconds2 = 86394.88;
    test:assertEquals(utcDiffSeconds(utc3, utc4), expectedSeconds2);

    Utc utc5 = check utcFromString("2021-04-12T23:20:50.520Z");
    Utc utc6 = check utcFromString("2021-04-11T23:20:55.640Z");
    decimal expectedSecond3 = -86394.88;
    test:assertEquals(utcDiffSeconds(utc6, utc5), expectedSecond3);
}

@test:Config {}
isolated function testDateValidateUsingValidDate() {
    Date date = {year: 1994, month: 11, day: 7};
    Error? err = dateValidate(date);
    test:assertFalse(err is Error);
}

@test:Config {}
isolated function testDateValidateUsingInvalidDate() {
    // Invalid number of days for a leap year
    Date date1 = {year: 1994, month: 2, day: 29};
    Error? err1 = dateValidate(date1);
    test:assertTrue(err1 is Error);
    test:assertEquals((<Error>err1).message(), "Invalid date 'February 29' as '1994' is not a leap year");

    // Out of range month
    Date date2 = {year: 1994, month: 50, day: 10};
    Error? err2 = dateValidate(date2);
    test:assertTrue(err2 is Error);
    test:assertEquals((<Error>err2).message(), "Invalid value for MonthOfYear (valid values 1 - 12): 50");

    // Out of range day
    Date date3 = {year: 1994, month: 4, day: 60};
    Error? err3 = dateValidate(date3);
    test:assertTrue(err3 is Error);
    test:assertEquals((<Error>err3).message(), "Invalid value for DayOfMonth (valid values 1 - 28/31): 60");
}

@test:Config {}
isolated function testDayOfWeekUsingValidDate() {
    Date date = {year: 1994, month: 11, day: 7};
    test:assertEquals(dayOfWeek(date), MONDAY);
}

@test:Config {}
isolated function testDayOfWeekUsingInvalidDate() {
    Date date = {year: 1994, month: 2, day: 29};
    DayOfWeek|error err = trap dayOfWeek(date);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Invalid date 'February 29' as '1994' is not a leap year");
}

@test:Config {}
isolated function testUtcToCivil() returns Error? {
    Utc utc = check utcFromString("2021-04-12T23:20:50.520Z");
    Civil civil = utcToCivil(utc);
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        second: 50.52,
        timeAbbrev: "Z",
        dayOfWeek: MONDAY
    };
    test:assertEquals(civil, expectedCivil);
}

// Indication that local time zones are formatted correctly
@test:Config {}
isolated function testUtcFromCivil() returns Error? {
    Utc expectedUtc = check utcFromString("2021-04-12T23:20:50.520Z");
    ZoneOffset zoneOffset = {
        hours: 5,
        minutes: 30,
        seconds: <decimal>0.0
    };
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 50,
        second: 50.52,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    Utc utc = check utcFromCivil(civil);
    test:assertEquals(utc, expectedUtc);
}

@test:Config {}
isolated function testUtcFromCivilWithoutSecond() returns Error? {
    Utc expectedUtc = check utcFromString("2021-04-12T23:20:00Z");
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 50,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    Utc utc = check utcFromCivil(civil);
    test:assertEquals(utc, expectedUtc);
}

@test:Config {}
isolated function testUtcFromCivilWithInvalidValue() {
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 70,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    Utc|Error err = utcFromCivil(civil);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Invalid value for MinuteOfHour (valid values 0 - 59): 70");
}

@test:Config {}
isolated function testUtcFromCivilWithoutOffset() {
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 70,
        timeAbbrev: "Asia/Colombo"
    };
    Utc|Error err = utcFromCivil(civil);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "civilTime.utcOffset must not be null");
}

@test:Config {}
isolated function testCivilFromString() returns Error? {
    string dateString = "2021-04-12T23:20:50.520Z";
    Civil civil = check civilFromString(dateString);
    Utc utc = check utcFromString(dateString);
    Civil expectedCivil = utcToCivil(utc);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithZone() returns Error? {
    string dateString = "2021-04-12T23:20:50.520+05:30[Asia/Colombo]";
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        second: 50.52,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset,
        dayOfWeek: MONDAY
    };
    Civil civil = check civilFromString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithoutZone() returns Error? {
    string dateString = "2021-04-12T23:20:50.520Z";
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        second: 50.52,
        timeAbbrev: "Z",
        dayOfWeek: MONDAY
    };
    Civil civil = check civilFromString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithoutSecond() returns Error? {
    string dateString = "2021-04-12T23:20+05:30[Asia/Colombo]";
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset,
        dayOfWeek: MONDAY
    };
    Civil civil = check civilFromString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithoutZoneMinutes() returns Error? {
    string dateString = "2021-04-12T23:20:50.520+05";
    ZoneOffset zoneOffset = {hours: 5, minutes: 0};
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        second: 50.52,
        timeAbbrev: "+05:00",
        utcOffset: zoneOffset,
        dayOfWeek: MONDAY
    };
    Civil civil = check civilFromString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithZoneSeconds() returns Error? {
    string dateString = "2021-04-12T23:20:50.520+05:30:45";
    ZoneOffset zoneOffset = {hours: 5, minutes: 30, seconds: 45d};
    Civil expectedCivil = {
        year: 2021,
        month: 4,
        day: 12,
        hour: 23,
        minute: 20,
        second: 50.52,
        timeAbbrev: "+05:30:45",
        utcOffset: zoneOffset,
        dayOfWeek: MONDAY
    };
    Civil civil = check civilFromString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromStringWithInvalidInput() {
    string dateString = "2021-04-12T23:20:50.520.05:30:45";
    Civil|Error err = civilFromString(dateString);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Text '2021-04-12T23:20:50.520.05:30:45' could not be parsed at index 23");
}

@test:Config {}
isolated function testCivilToString() returns Error? {
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil civil = {
        year: 2021,
        month: 3,
        day: 5,
        hour: 0,
        minute: 33,
        second: 28.839564,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    string civilStr = check civilToString(civil);
    string expectedStr = "2021-03-04T19:03:28.839564Z";
    test:assertEquals(civilStr, expectedStr);
}

@test:Config {}
isolated function testCivilToStringWithTimeOfDay() returns Error? {
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    TimeOfDay timeOfDay = {
        year: 2021,
        month: 3,
        day: 5,
        hour: 0,
        minute: 33,
        second: 28.839564,
        utcOffset: zoneOffset
    };
    Civil civil = {
        year: timeOfDay?.year ?: 0,
        month: timeOfDay?.month ?: 0,
        day: timeOfDay?.day ?: 0,
        hour: timeOfDay.hour,
        minute: timeOfDay.minute,
        second: timeOfDay?.second ?: 0,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    string civilStr = check civilToString(civil);
    string expectedStr = "2021-03-04T19:03:28.839564Z";
    test:assertEquals(civilStr, expectedStr);
}

@test:Config {}
isolated function testCivilToStringWithoutOffset() {
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 70,
        timeAbbrev: "Asia/Colombo"
    };
    string|Error err = civilToString(civil);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "civil.utcOffset must not be null");
}

@test:Config {}
isolated function testCivilToStringWithInvalidInput() {
    ZoneOffset zoneOffset = {hours: 5, minutes: 30};
    Civil civil = {
        year: 2021,
        month: 3,
        day: 5,
        hour: 45,
        minute: 33,
        second: 28.839564,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    string|Error err = civilToString(civil);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Invalid value for HourOfDay (valid values 0 - 23): 45");
}

@test:Config {}
isolated function testUtcToEmailString() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    test:assertEquals(utcToEmailString(utc, "GMT"), "Mon, 3 Dec 2007 10:15:30 GMT");
}

@test:Config {}
isolated function testUtcToEmailStringWithZ() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    test:assertEquals(utcToEmailString(utc, "Z"), "Mon, 3 Dec 2007 10:15:30 Z");
}

@test:Config {}
isolated function testCivilFromEmailString() returns Error? {
    string dateString = "Wed, 10 Mar 2021 19:51:55 -0800 (PST)";
    ZoneOffset zoneOffset = {hours: -8, minutes: 0};
    Civil expectedCivil = {
        year: 2021,
        month: 3,
        day: 10,
        hour: 19,
        minute: 51,
        second: 55,
        timeAbbrev: "America/Los_Angeles",
        utcOffset: zoneOffset,
        dayOfWeek: WEDNESDAY
    };
    Civil civil = check civilFromEmailString(dateString);
    test:assertEquals(civil, expectedCivil);
}

@test:Config {}
isolated function testCivilFromEmailStringWithInvalidInput() {
    string dateString = "Wed, 10 2021 19:51:55 -0800 (PST)";
    Civil|Error err = civilFromEmailString(dateString);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Text 'Wed, 10 2021 19:51:55 -0800 (PST)' could not be parsed at index 8");
}

@test:Config {}
isolated function testCivilToEmailString() returns Error? {
    string expectedString = "Wed, 10 Mar 2021 19:51:55 -0800 (PST)";
    ZoneOffset zoneOffset = {hours: 8, minutes: 0};
    Civil civil = {
        year: 2021,
        month: 3,
        day: 10,
        hour: 19,
        minute: 51,
        second: 55,
        timeAbbrev: "America/Los_Angeles",
        utcOffset: zoneOffset
    };
    string emailString = check civilToEmailString(civil, ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT);
    test:assertEquals(emailString, expectedString);
}

@test:Config {}
isolated function testCivilToEmailStringWithZonePreference() returns Error? {
    string expectedString = "Wed, 10 Mar 2021 19:51:55 -0820";
    ZoneOffset zoneOffset = {hours: -8, minutes: -20};
    Civil civil = {
        year: 2021,
        month: 3,
        day: 10,
        hour: 19,
        minute: 51,
        second: 55,
        timeAbbrev: "America/Los_Angeles",
        utcOffset: zoneOffset
    };
    string emailString = check civilToEmailString(civil, PREFER_ZONE_OFFSET);
    test:assertEquals(emailString, expectedString);
}

@test:Config {}
isolated function testCivilToEmailStringWithIncorrectInput() {
    ZoneOffset zoneOffset = {hours: -8, minutes: -20};
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 70,
        timeAbbrev: "Asia/Colombo",
        utcOffset: zoneOffset
    };
    string|Error err = civilToEmailString(civil, PREFER_ZONE_OFFSET);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Invalid value for MinuteOfHour (valid values 0 - 59): 70");
}

@test:Config {}
isolated function testCivilToEmailStringWithoutZoneOffset() {
    Civil civil = {
        year: 2021,
        month: 4,
        day: 13,
        hour: 4,
        minute: 70,
        timeAbbrev: "Asia/Colombo"
    };
    string|Error err = civilToEmailString(civil, PREFER_ZONE_OFFSET);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "civilTime.utcOffset must not be null with time:PREFER_ZONE_OFFSET");
}

@test:Config {}
isolated function testCivilToEmailStringWithInvalidInput() {
    ZoneOffset zoneOffset = {hours: 8, minutes: 0};
    Civil civil = {
        year: 2021,
        month: 30, // Invalid month
        day: 10,
        hour: 19,
        minute: 51,
        second: 55,
        timeAbbrev: "America/Los_Angeles",
        utcOffset: zoneOffset
    };
    string|Error err = civilToEmailString(civil, ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Invalid value for MonthOfYear (valid values 1 - 12): 30");
}

@test:Config {}
isolated function testLoadSystemZone() returns Error? {
    _ = check loadSystemZone();
    //final Zone systemZone = check loadSystemZone();
    //test:assertTrue(systemZone.fixedOffset() is ()); // Cannot test this extensively since this may change in different environments (or docker images).
}

@test:Config {}
isolated function testGetZone() returns Error? {
    Zone? systemZone1 = getZone("Asia/Colombo");
    test:assertTrue(systemZone1 is Zone);
    test:assertTrue((<Zone>systemZone1).fixedOffset() is ());

    Zone? systemZone2 = getZone("Greenwich");
    test:assertTrue(systemZone2 is Zone);
    ZoneOffset? zoneOffset1 = (<Zone>systemZone2).fixedOffset();
    test:assertTrue(zoneOffset1 is ZoneOffset);
    test:assertEquals(<ZoneOffset>zoneOffset1, {hours: 0, minutes: 0});

    Zone? systemZone3 = getZone("Etc/GMT-9");
    test:assertTrue(systemZone3 is Zone);
    ZoneOffset? zoneOffset2 = (<Zone>systemZone3).fixedOffset();
    test:assertTrue(zoneOffset2 is ZoneOffset);
    test:assertEquals(<ZoneOffset>zoneOffset2, {hours: 9, minutes: 0});
}

@test:Config {}
isolated function testZoneUtcFromCivil() returns Error? {
    Civil civil = {
        year: 2021,
        month: 3,
        day: 10,
        hour: 19,
        minute: 51,
        second: 55,
        timeAbbrev: "America/Los_Angeles"
    };
    Zone? zone = getZone("Etc/GMT-9");
    test:assertTrue(zone is Zone);
    test:assertEquals(check (<Zone>zone).utcFromCivil(civil), <Utc>[1615434715, 0]);
}

@test:Config {}
isolated function testZoneUtcFromCivilWithoutTimeAbbrev() returns Error? {
    Civil civil = {
        year: 2021,
        month: 3,
        day: 10,
        hour: 19,
        minute: 51,
        second: 55
    };
    Zone? zone = getZone("Etc/GMT-9");
    test:assertTrue(zone is Zone);
    Utc|Error err = (<Zone>zone).utcFromCivil(civil);
    test:assertTrue(err is Error);
    test:assertEquals((<Error>err).message(), "Abbreviation for the local time is required for the conversion");
}

@test:Config {}
isolated function testZoneUtcToCivil1() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    Civil civil = {
        year: 2007,
        month: 12,
        day: 3,
        hour: 19,
        minute: 15,
        second: 30,
        timeAbbrev: "Etc/GMT-9",
        dayOfWeek: 1
    };
    Zone? zone = getZone("Etc/GMT-9");
    test:assertTrue(zone is Zone);
    test:assertEquals((<Zone>zone).utcToCivil(utc), civil);
}

@test:Config {}
isolated function testZoneUtcToCivil2() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    Civil civil = {
        year: 2007,
        month: 12,
        day: 3,
        hour: 15,
        minute: 45,
        second: 30,
        timeAbbrev: "Asia/Colombo",
        dayOfWeek: 1
    };
    Zone? zone = getZone("Asia/Colombo");
    test:assertTrue(zone is Zone);
    test:assertEquals((<Zone>zone).utcToCivil(utc), civil);
}

@test:Config {}
isolated function testZoneToEmailStringConversion() returns Error? {
    Zone? systemZone = getZone("Asia/Colombo");
    test:assertTrue(systemZone is Zone);
    Civil civil = (<Zone>systemZone).utcToCivil(check utcFromString("2007-12-03T10:15:30.00Z"));
    test:assertEquals(civilToEmailString(civil, PREFER_TIME_ABBREV), "Mon, 3 Dec 2007 15:45:30 +0530 (IST)");
}

@test:Config {}
isolated function testZoneToEmailStringConversionWithIncorrectArgument() returns Error? {
    Zone? systemZone = getZone("Asia/Colombo");
    test:assertTrue(systemZone is Zone);
    Civil civil = (<Zone>systemZone).utcToCivil(check utcFromString("2007-12-03T10:15:30.00Z"));

    string|Error err1 = civilToEmailString(civil, PREFER_ZONE_OFFSET);
    test:assertTrue(err1 is Error);
    test:assertEquals((<Error>err1).message(), "civilTime.utcOffset must not be null with time:PREFER_ZONE_OFFSET");

    string|Error err2 = civilToEmailString(civil, ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT);
    test:assertTrue(err2 is Error);
    test:assertEquals((<Error>err2).message(), "civilTime.utcOffset must not be null with time:ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT");
}

@test:Config {}
isolated function testGmtToEmailStringConversion() returns Error? {
    Utc utc = check utcFromString("2007-12-03T10:15:30.00Z");
    Utc utc2 = check utcFromString("2007-12-03T10:15:30.00+05:30");
    Civil civil = check civilFromString("2007-12-03T10:15:30.00+00:00");
    
    test:assertEquals(utcToEmailString(utc, "Z"), "Mon, 3 Dec 2007 10:15:30 Z");
    test:assertEquals(utcToEmailString(utc2, "0"), "Mon, 3 Dec 2007 04:45:30 +0000");
    test:assertEquals(utcToEmailString(utc), "Mon, 3 Dec 2007 10:15:30 +0000");
    test:assertEquals(civilToEmailString(civil, PREFER_TIME_ABBREV), "Mon, 3 Dec 2007 10:15:30 +0000 (Z)");
    test:assertEquals(civilToEmailString(utcToCivil(utc), PREFER_TIME_ABBREV), "Mon, 3 Dec 2007 10:15:30 +0000 (Z)");
}
