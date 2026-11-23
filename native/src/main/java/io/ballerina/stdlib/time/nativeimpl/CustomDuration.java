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
import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZonedDateTime;

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
public record CustomDuration(long years, long months, long days, long hours, long minutes, long seconds,
                             long nanoSeconds) {

    public CustomDuration(long years, long months, long days, long hours, long minutes, BDecimal seconds) {
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

    /**
     * Adds the years and months, then the days on the local time-line (as {@link java.time.Period} does), and
     * finally the hours, minutes and seconds on the instant time-line.
     */
    public ZonedDateTime addTo(ZonedDateTime zonedDateTime) {
        try {
            return zonedDateTime
                    .plusMonths(Math.addExact(Math.multiplyExact(years, 12), months))
                    .plusDays(days)
                    .plus(Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).plusNanos(nanoSeconds));
        } catch (ArithmeticException e) {
            throw new DateTimeException("Duration is out of range", e);
        }
    }

    private static long getSeconds(BDecimal seconds) {
        try {
            return seconds.decimalValue().setScale(0, RoundingMode.FLOOR).longValueExact();
        } catch (ArithmeticException e) {
            throw new DateTimeException("Duration seconds is out of range: " +
                    seconds.decimalValue().toPlainString(), e);
        }
    }

    private static long getNanoSeconds(BDecimal seconds) {
        BigDecimal decimal = seconds.decimalValue();
        BigDecimal fractional = decimal.subtract(decimal.setScale(0, RoundingMode.FLOOR));
        return fractional
                .multiply(Constants.ANALOG_GIGA)
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();
    }
}
