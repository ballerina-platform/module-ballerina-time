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

# Date-Time formatters used in time:format() and time:parse()
#
# + BASIC_ISO_DATE - Sample format - yyyyMMddZZZ (eg: 20210228+0530)
# + ISO_DATE - Sample format - yyyy-MM-ddXXX (eg: 2021-02-28+05:30)
# + ISO_TIME - Sample format - HH:mm:ss.SSZZZZZ (eg: 23:10:15.02+05:30)
# + ISO_DATE_TIME - Sample format - yyyy-MM-dd'T'HH-mm-ss.SSXXX'['VV']' (eg: 2021-02-28T23:10:15.02+05:30[Asia/Colombo])
# + ISO_LOCAL_DATE_TIME - Sample format - yyyy-MM-dd'T'HH-mm-ss.SS (eg: 2021-02-28T23:10:15.02)
# + ISO_OFFSET_DATE_TIME - Sample format - yyyy-MM-dd'T'HH-mm-ss.SSXXX (eg: 2021-02-28T23:10:15.02+05:30)
# + ISO_ZONED_DATE_TIME - Similar to ISO_DATE_TIME but offset is compulsory
# + RFC_1123_DATE_TIME - Sample format - E, dd LLL yyyy HH:mm:ss ZZZ (eg: Sun, 28 Feb 2021 23:10:15 +0530)
public enum DateTimeFormat {
	BASIC_ISO_DATE,
	ISO_DATE,
	ISO_TIME,
	ISO_DATE_TIME,
	ISO_LOCAL_DATE_TIME,
	ISO_OFFSET_DATE_TIME,
	ISO_ZONED_DATE_TIME,
	RFC_1123_DATE_TIME
}

# Values returned by time:getWeekday()
public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
