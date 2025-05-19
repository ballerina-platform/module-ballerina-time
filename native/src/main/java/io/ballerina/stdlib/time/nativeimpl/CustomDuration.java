/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.stdlib.time.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This {@link CustomDuration} record represents a custom duration with various time components.
 * @param years        - The number of years.
 * @param months       - The number of months.
 * @param days         - The number of days.
 * @param hours        - The number of hours.
 * @param minutes      - The number of minutes.
 * @param seconds      - The number of seconds.
 * @param nanoSeconds  - The number of nanoseconds.
 *
 * @since 2.8.0
 */
public record CustomDuration(int years, int months, int days, int hours, int minutes, int seconds, int nanoSeconds) {

    public CustomDuration(int years, int months, int days, int hours, int minutes, BDecimal seconds) {
        this(
            years,
            months,
            days,
            hours,
            minutes,
            getSeconds(seconds),
            getNanoSeconds(seconds)
        );
    }

    private static int getSeconds(BDecimal seconds) {
        BigDecimal decimal = seconds.decimalValue();
        return decimal.setScale(0, RoundingMode.FLOOR).intValue();
    }

    private static int getNanoSeconds(BDecimal seconds) {
        BigDecimal decimal = seconds.decimalValue();
        BigDecimal fractional = decimal.subtract(new BigDecimal(decimal.setScale(0, RoundingMode.FLOOR).intValue()));
        return fractional
                .multiply(Constants.ANALOG_GIGA)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
