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

import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.time.util.Constants;
import io.ballerina.stdlib.time.util.ModuleUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static io.ballerina.stdlib.time.util.Constants.ANALOG_GIGA;

/**
 * Contains the APIs related to Ballerina Civil type generation.
 *
 * @since 2.0.2
 */
public class Civil {

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();
    private BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
            Constants.CIVIL_RECORD);

    public BMap<BString, Object> buildFromZonedDateTime(ZonedDateTime zonedDateTime) {

        this.zonedDateTime = zonedDateTime;
        setCommonCivilFields();
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128));
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_SECOND),
                ValueCreator.createDecimalValue(second));

        return civilMap;

    }

    public BMap<BString, Object> buildFromZonedDateTimeString(String zonedDateTimeString) {

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedDateTimeString);
        this.zonedDateTime = zonedDateTime;
        setCommonCivilFields();
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128));

        if (isSecondExists(zonedDateTimeString)) {
            civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_SECOND),
                    ValueCreator.createDecimalValue(second));
        }
        if (isLocalTimeZoneExists(zonedDateTimeString)) {
            civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_UTC_OFFSET),
                    createZoneOffsetFromZonedDateTime(zonedDateTime));
        }

        return civilMap;

    }

    public BMap<BString, Object> buildFromEmailString(String zonedDateTimeString) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.EMAIL_DATE_TIME_FORMAT);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(zonedDateTimeString, dateTimeFormatter);
        this.zonedDateTime = zonedDateTime;
        setCommonCivilFields();
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128));
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_SECOND),
                ValueCreator.createDecimalValue(second));
        civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_UTC_OFFSET),
                createZoneOffsetFromZonedDateTime(zonedDateTime));

        return civilMap;

    }

    private void setCommonCivilFields() {

        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_YEAR), zonedDateTime.getYear());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_MONTH), zonedDateTime.getMonthValue());
        civilMap.put(StringUtils.fromString(Constants.DATE_RECORD_DAY), zonedDateTime.getDayOfMonth());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_HOUR), zonedDateTime.getHour());
        civilMap.put(StringUtils.fromString(Constants.TIME_OF_DAY_RECORD_MINUTE), zonedDateTime.getMinute());
        civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_TIME_ABBREV),
                StringUtils.fromString(zonedDateTime.getZone().toString()));
        civilMap.put(StringUtils.fromString(Constants.CIVIL_RECORD_DAY_OF_WEEK),
                (zonedDateTime.getDayOfWeek().getValue() % 7));
    }

    private boolean isLocalTimeZoneExists(String time) {

        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2}\\.\\d+)?(Z$)");
        return !pattern.matcher(time).find();
    }

    private boolean isSecondExists(String time) {

        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?");
        return pattern.matcher(time).find();
    }

    public BMap<BString, Object> createZoneOffsetFromZonedDateTime(ZonedDateTime zonedDateTime) {

        BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
                Constants.READABLE_ZONE_OFFSET_RECORD);
        Map<String, Integer> zoneInfo = zoneOffsetMapFromString(zonedDateTime.getOffset().toString());
        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR) != null) {
            civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_HOUR),
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_HOUR).longValue());
        } else {
            civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_HOUR), 0);
        }

        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE) != null) {
            civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_MINUTE),
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_MINUTE).longValue());
        } else {
            civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_MINUTE), 0);
        }

        if (zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND) != null) {
            civilMap.put(StringUtils.fromString(Constants.ZONE_OFFSET_RECORD_SECOND),
                    zoneInfo.get(Constants.ZONE_OFFSET_RECORD_SECOND).longValue());
        }
        civilMap.freezeDirect();
        return civilMap;
    }

    public Map<String, Integer> zoneOffsetMapFromString(String dateTime) {

        Map<String, Integer> zone = new HashMap<>();
        if (dateTime.strip().startsWith("+")) {
            dateTime = dateTime.replaceFirst("\\+", "");
            String[] zoneInfo = dateTime.split(":");
            if (zoneInfo.length > 0 && zoneInfo[0] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_HOUR, Integer.parseInt(zoneInfo[0]));
            }
            if (zoneInfo.length > 1 && zoneInfo[1] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_MINUTE, Integer.parseInt(zoneInfo[1]));
            }
            if (zoneInfo.length > 2 && zoneInfo[2] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_SECOND, Integer.parseInt(zoneInfo[2]));
            }
        } else if (dateTime.strip().startsWith("-")) {
            dateTime = dateTime.replaceFirst("\\-", "");
            String[] zoneInfo = dateTime.split(":");
            if (zoneInfo.length > 0 && zoneInfo[0] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_HOUR, Integer.parseInt(zoneInfo[0]) * -1);
            }
            if (zoneInfo.length > 1 && zoneInfo[1] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_MINUTE, Integer.parseInt(zoneInfo[1]) * -1);
            }
            if (zoneInfo.length > 2 && zoneInfo[2] != null) {
                zone.put(Constants.ZONE_OFFSET_RECORD_SECOND, Integer.parseInt(zoneInfo[2]) * -1);
            }
        }
        return zone;
    }

}
