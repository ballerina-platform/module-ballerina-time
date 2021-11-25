/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.time.util.Errors;
import io.ballerina.stdlib.time.util.Utils;

import java.time.DateTimeException;
import java.time.ZonedDateTime;

/**
 * Contains the extern APIs related to Ballerina TimeZone type generations and operations.
 *
 * @since 2.0.2
 */
public class TimeZoneExternMethods {

    private static final String ZONE_ID_ENTRY = "zoneId";

    public static Object externTimeZoneInitWithSystemZone(BObject timeZoneObj) {

        try {
            Zone zone = new Zone();
            timeZoneObj.addNativeData(ZONE_ID_ENTRY, zone);
            return null;
        } catch (DateTimeException e) {
            return Utils.createError(Errors.FormatError, e.getMessage());
        }
    }

    public static void externTimeZoneInitWithId(BObject timeZoneObj, BString zoneId) {

        Zone zone = new Zone(zoneId.getValue());
        timeZoneObj.addNativeData(ZONE_ID_ENTRY, zone);
    }

    public static Object externTimeZoneFixedOffset(BObject timeZoneObj) {

        Zone zone = (Zone) timeZoneObj.getNativeData(ZONE_ID_ENTRY);
        return zone.isFixedOffset();
    }

    public static Object externTimeZoneUtcFromCivil(BObject timeZoneObj, long year, long month, long day, long hour,
                                                    long minute, BDecimal second, BString zoneAbbr,
                                                    BString zoneHandling) {
        Zone zone = (Zone) timeZoneObj.getNativeData(ZONE_ID_ENTRY);
        ZonedDateTime zonedDateTime = Utils.createZoneDateTimeFromCivilValues(year, month, day, hour, minute,
                second, 0, 0, BDecimal.valueOf(0), zoneAbbr, zoneHandling.getValue());
        return zone.utcFromCivil(new Civil(zonedDateTime)).build();
    }

    public static BMap externTimeZoneUtcToCivil(BObject timeZoneObj, BArray utc) {

        Zone zone = (Zone) timeZoneObj.getNativeData(ZONE_ID_ENTRY);
        return zone.utcToCivil(new Utc(utc)).build();
    }

}
