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
import io.ballerina.stdlib.time.util.Utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

import static io.ballerina.stdlib.time.util.Constants.ANALOG_GIGA;

/**
 * Contains the APIs related to Ballerina Civil type generation.
 *
 * @since 2.0.2
 */
public class Civil {

    private final ZonedDateTime zonedDateTime;
    private boolean isSecondExists = false;
    private boolean isLocalTimeZoneExists = false;
    private final BMap<BString, Object> civilMap = ValueCreator.createRecordValue(ModuleUtils.getModule(),
            Constants.CIVIL_RECORD);

    public Civil(ZonedDateTime zonedDateTime) {

        this.zonedDateTime = zonedDateTime;
    }

    public Civil(String zonedDateTimeString, Constants.CivilInputStringTypes inputStringTypes) {

        if (Constants.CivilInputStringTypes.EMAIL_STRING.toString().equals(inputStringTypes.toString())) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.EMAIL_DATE_TIME_FORMAT);
            this.zonedDateTime = ZonedDateTime.parse(zonedDateTimeString, dateTimeFormatter);
            this.isSecondExists = true;
            this.isLocalTimeZoneExists = true;
        } else {
            this.zonedDateTime = ZonedDateTime.parse(zonedDateTimeString);
            this.isSecondExists = isSecondExists(zonedDateTimeString);
            this.isLocalTimeZoneExists = isLocalTimeZoneExists(zonedDateTimeString);
        }
    }

    public ZonedDateTime getZonedDateTime() {

        return zonedDateTime;
    }

    public BMap<BString, Object> build() {

        setCommonCivilFields();
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128));
        civilMap.put(Constants.TIME_OF_DAY_RECORD_SECOND_BSTRING, ValueCreator.createDecimalValue(second));

        return ValueCreator.createRecordValue(ModuleUtils.getModule(), Constants.CIVIL_RECORD, civilMap);
    }

    public BMap<BString, Object> buildWithZone() {

        setCommonCivilFields();
        BigDecimal second = new BigDecimal(zonedDateTime.getSecond());
        second = second.add(new BigDecimal(zonedDateTime.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128));

        if (this.isSecondExists) {
            civilMap.put(Constants.TIME_OF_DAY_RECORD_SECOND_BSTRING, ValueCreator.createDecimalValue(second));
        }
        if (this.isLocalTimeZoneExists) {
            civilMap.put(Constants.CIVIL_RECORD_UTC_OFFSET_BSTRING,
                    createZoneOffsetFromZonedDateTime(zonedDateTime));
        }

        return ValueCreator.createRecordValue(ModuleUtils.getModule(), Constants.CIVIL_RECORD, civilMap);
    }

    private void setCommonCivilFields() {

        civilMap.put(Constants.DATE_RECORD_YEAR_BSTRING, zonedDateTime.getYear());
        civilMap.put(Constants.DATE_RECORD_MONTH_BSTRING, zonedDateTime.getMonthValue());
        civilMap.put(Constants.DATE_RECORD_DAY_BSTRING, zonedDateTime.getDayOfMonth());
        civilMap.put(Constants.TIME_OF_DAY_RECORD_HOUR_BSTRING, zonedDateTime.getHour());
        civilMap.put(Constants.TIME_OF_DAY_RECORD_MINUTE_BSTRING, zonedDateTime.getMinute());
        civilMap.put(Constants.CIVIL_RECORD_TIME_ABBREV_BSTRING,
                StringUtils.fromString(zonedDateTime.getZone().toString()));
        civilMap.put(Constants.CIVIL_RECORD_DAY_OF_WEEK_BSTRING, (zonedDateTime.getDayOfWeek().getValue() % 7));
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

        Map<String, Integer> zoneInfo = Utils.zoneOffsetMapFromString(zonedDateTime.getOffset().toString());
        return Utils.createZoneOffsetFromZoneInfoMap(zoneInfo);
    }

}
