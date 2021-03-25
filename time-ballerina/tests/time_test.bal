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
isolated function testUtcNow() {
    Utc|Error oldUtc = utcFromString("2007-12-03T10:15:30.00Z");
    Utc currentUtc = utcNow();
    if (oldUtc is Utc) {
        test:assertTrue(currentUtc[0] > oldUtc[0]);
    } else {
        test:assertFail(msg = oldUtc.message());
    }
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
isolated function testUtcFromString() {
    Utc|Error utc = utcFromString("2007-12-03T10:15:30.00Z");
    if (utc is Utc) {
        test:assertEquals(utc[0], 1196676930);
        test:assertEquals(utc[1], <decimal>0.0);
    } else {
        test:assertFail(msg = utc.message());
    }
}

@test:Config {}
isolated function testUtcFromStringWithInvalidFormat() {
    Utc|Error utc = utcFromString("2007-12-0310:15:30.00Z");
    if (utc is Utc) {
        test:assertFail("Expected time:Error not found");
    } else {
        test:assertEquals(utc.message(), 
        "Provided '2007-12-0310:15:30.00Z' is not adhere to the expected format '2007-12-03T10:15:30.00Z'");
    }
}

@test:Config {}
isolated function testUtcToString() {
    Utc|Error utc = utcFromString("1985-04-12T23:20:50.520Z");
    int expectedSecondsFromEpoch = 482196050;
    decimal expectedSecondFraction = 0.52;
    if (utc is Utc) {
        test:assertEquals(utc[0], expectedSecondsFromEpoch);
        test:assertEquals(utc[1], expectedSecondFraction);
        string utcString = utcToString(utc);
        test:assertEquals(utcString, "1985-04-12T23:20:50.520Z");
    } else {
        test:assertFail(msg = utc.message());
    }
}

@test:Config {}
isolated function testUtcAddSeconds() {
    Utc|Error utc1 = utcFromString("2021-04-12T23:20:50.520Z");
    if (utc1 is Utc) {
        Utc utc2 = utcAddSeconds(utc1, 20.900);
        string utcString = utcToString(utc2);
        test:assertEquals(utcString, "2021-04-12T23:21:11.420Z");
    } else {
        test:assertFail(msg = utc1.message());
    }
}

@test:Config {}
isolated function testUtcDiffSeconds() {
    Utc|Error utc1 = utcFromString("2021-04-12T23:20:50.520Z");
    Utc|Error utc2 = utcFromString("2021-04-11T23:20:50.520Z");
    decimal expectedSeconds1 = 86400;
    if (utc1 is Utc && utc2 is Utc) {
        test:assertEquals(utcDiffSeconds(utc1, utc2), expectedSeconds1);
    } else if (utc1 is Error) {
        test:assertFail(msg = utc1.message());
    } else if (utc2 is Error) {
        test:assertFail(msg = utc2.message());
    } else {
        test:assertFail("Unknown error");
    }

    Utc|Error utc3 = utcFromString("2021-04-12T23:20:50.520Z");
    Utc|Error utc4 = utcFromString("2021-04-11T23:20:55.640Z");
    decimal expectedSeconds2 = 86394.88;
    if (utc3 is Utc && utc4 is Utc) {
        test:assertEquals(utcDiffSeconds(utc3, utc4), expectedSeconds2);
    } else if (utc3 is Error) {
        test:assertFail(msg = utc3.message());
    } else if (utc4 is Error) {
        test:assertFail(msg = utc4.message());
    } else {
        test:assertFail("Unknown error");
    }

    Utc|Error utc5 = utcFromString("2021-04-12T23:20:50.520Z");
    Utc|Error utc6 = utcFromString("2021-04-11T23:20:55.640Z");
    decimal expectedSecond3 = -86394.88;
    if (utc5 is Utc && utc6 is Utc) {
        test:assertEquals(utcDiffSeconds(utc6, utc5), expectedSecond3);
    } else if (utc5 is Error) {
        test:assertFail(msg = utc5.message());
    } else if (utc6 is Error) {
        test:assertFail(msg = utc6.message());
    } else {
        test:assertFail("Unknown error");
    }
}

@test:Config {}
isolated function testDateValidateUsingValidDate() {
    Date date = {year: 1994, month: 11, day: 7};
    Error? err = dateValidate(date);
    if (err is Error) {
        test:assertFail(msg = err.message());
    }
}

@test:Config {}
isolated function testDateValidateUsingInvalidDate() {
    // Invalid number of days for a leap year
    Date date1 = {year: 1994, month: 2, day: 29};
    Error? err1 = dateValidate(date1);
    if (err1 is Error) {
        test:assertEquals(err1.message(), "Invalid date 'February 29' as '1994' is not a leap year");
    } else {
        test:assertFail("Expected error not found");
    }

    // Out of range month
    Date date2 = {year: 1994, month: 50, day: 10};
    Error? err2 = dateValidate(date2);
    if (err2 is Error) {
        test:assertEquals(err2.message(), "Invalid value for MonthOfYear (valid values 1 - 12): 50");
    } else {
        test:assertFail("Expected error not found");
    }

    // Out of range day
    Date date3 = {year: 1994, month: 4, day: 60};
    Error? err3 = dateValidate(date3);
    if (err3 is Error) {
        test:assertEquals(err3.message(), "Invalid value for DayOfMonth (valid values 1 - 28/31): 60");
    } else {
        test:assertFail("Expected error not found");
    }
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
    if (err is Error) {
        test:assertEquals(err.message(), "Invalid date 'February 29' as '1994' is not a leap year");
    } else {
        test:assertFail("Expected panic did not occur");
    }
}

@test:Config {}
isolated function testUtcToCivil() {
    Utc|Error utc = utcFromString("2021-04-12T23:20:50.520Z");
    if (utc is Utc) {
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
    } else {
        test:assertFail(msg = utc.message());
    }
}

// Indication that local time zones are formatted correctly
@test:Config {}
isolated function testUtcFromCivil() {
    Utc|Error expectedUtc = utcFromString("2021-04-12T23:20:50.520Z");
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
    Utc|Error utc = utcFromCivil(civil);
    if (expectedUtc is Utc && utc is Utc) {
        test:assertEquals(utc, expectedUtc);
    } else if (expectedUtc is Error) {
        test:assertFail(msg = expectedUtc.message());
    } else if (utc is Error) {
        test:assertFail(msg = utc.message());
    } else {
        test:assertFail("Unknown error");
    }
}

@test:Config {}
isolated function testUtcFromCivilWithoutSecond() {
    Utc|Error expectedUtc = utcFromString("2021-04-12T23:20:00Z");
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
    Utc|Error utc = utcFromCivil(civil);
    if (expectedUtc is Utc && utc is Utc) {
        test:assertEquals(utc, expectedUtc);
    } else if (expectedUtc is Error) {
        test:assertFail(msg = expectedUtc.message());
    } else if (utc is Error) {
        test:assertFail(msg = utc.message());
    } else {
        test:assertFail("Unknown error");
    }
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
    Utc|Error utc = utcFromCivil(civil);
    if (utc is Error) {
        test:assertEquals(utc.message(), "Invalid value for MinuteOfHour (valid values 0 - 59): 70");
    } else {
        test:assertFail("Expected `time:Error` not found");
    }
}

@test:Config {}
isolated function testCivilFromString() {
    string dateString = "2021-04-12T23:20:50.520Z";
    Civil|Error civil = civilFromString(dateString);
    if (civil is Civil) {
        Utc|Error utc = utcFromString(dateString);
        if (utc is Utc) {
            Civil|Error expectedCivil = utcToCivil(utc);
            if (expectedCivil is Civil) {
                test:assertEquals(civil, expectedCivil);
            } else {
                test:assertFail(expectedCivil.message());
            }
        } else {
            test:assertFail(msg = utc.message());
        }
    } else {
        test:assertFail(msg = civil.message());
    }
}

@test:Config {}
isolated function testCivilFromStringWithZone() {
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
    Civil|Error civil = civilFromString(dateString);
    if (civil is Civil) {
        test:assertEquals(civil, expectedCivil);
    } else {
        test:assertFail(msg = civil.message());
    }
}

@test:Config {}
isolated function testCivilFromStringWithoutZone() {
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
    Civil|Error civil = civilFromString(dateString);
    if (civil is Civil) {
        test:assertEquals(civil, expectedCivil);
    } else {
        test:assertFail(msg = civil.message());
    }
}

@test:Config {}
isolated function testCivilFromStringWithoutSecond() {
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
    Civil|Error civil = civilFromString(dateString);
    if (civil is Civil) {
        test:assertEquals(civil, expectedCivil);
    } else {
        test:assertFail(msg = civil.message());
    }
}

@test:Config {}
isolated function testCivilToString() {
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
    string|Error civilStr = civilToString(civil);
    string expectedStr = "2021-03-04T19:03:28.839564Z";
    if (civilStr is string) {
        test:assertEquals(civilStr, expectedStr);
    } else {
        test:assertFail(msg = civilStr.message());
    }
}

@test:Config {}
isolated function testUtcToEmailString() {
    Utc|Error utc = utcFromString("2007-12-03T10:15:30.00Z");
    if (utc is Utc) {
        test:assertEquals(utcToEmailString(utc, "GMT"), "Mon, 3 Dec 2007 10:15:30 GMT");
    } else {
        test:assertFail(msg = utc.message());
    }
}

@test:Config {}
isolated function testUtcToEmailStringWithZ() {
    Utc|Error utc = utcFromString("2007-12-03T10:15:30.00Z");
    if (utc is Utc) {
        test:assertEquals(utcToEmailString(utc, "Z"), "Mon, 3 Dec 2007 10:15:30 Z");
    } else {
        test:assertFail(msg = utc.message());
    }
}

@test:Config {}
isolated function testCivilFromEmailString() {
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
    Civil|Error civil = civilFromEmailString(dateString);
    if (civil is Civil) {
        test:assertEquals(civil, expectedCivil);
    } else {
        test:assertFail(msg = civil.message());
    }
}

@test:Config {}
isolated function testCivilToEmailString() {
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
    string|Error emailString = civilToEmailString(civil, ZONE_OFFSET_WITH_TIME_ABBREV_COMMENT);
    if (emailString is string) {
        test:assertEquals(emailString, expectedString);
    } else {
        test:assertFail(msg = emailString.message());
    }
}

@test:Config {}
isolated function testCivilToEmailStringWithZonePreference() {
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
    string|Error emailString = civilToEmailString(civil, PREFER_ZONE_OFFSET);
    if (emailString is string) {
        test:assertEquals(emailString, expectedString);
    } else {
        test:assertFail(msg = emailString.message());
    }
}
