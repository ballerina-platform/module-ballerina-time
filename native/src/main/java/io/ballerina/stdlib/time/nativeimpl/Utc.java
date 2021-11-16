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

import io.ballerina.runtime.api.PredefinedTypes;
import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.TupleType;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.stdlib.time.util.Constants;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;

import static io.ballerina.stdlib.time.util.Constants.ANALOG_GIGA;

/**
 * Contains the APIs related to Ballerina Utc type generation.
 *
 * @since 2.0.2
 */
public class Utc {

    private long secondsFromEpoc = 0;
    private BigDecimal lastSecondFraction = new BigDecimal(0);

    public Utc(Instant instant) {

        secondsFromEpoc = instant.getEpochSecond();
        lastSecondFraction = new BigDecimal(instant.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128);
    }

    public Utc(Instant instant, int precision) {

        secondsFromEpoc = instant.getEpochSecond();
        lastSecondFraction = new BigDecimal(instant.getNano()).divide(ANALOG_GIGA, MathContext.DECIMAL128)
                .setScale(precision, RoundingMode.HALF_UP);
    }

    public Utc (BArray utc) {
        if (utc.getLength() == 2) {
            secondsFromEpoc = utc.getInt(0);
            lastSecondFraction = new BigDecimal(utc.getValues()[1].toString()).multiply(Constants.ANALOG_GIGA);
        } else if (utc.getLength() == 1) {
            secondsFromEpoc = utc.getInt(0);
        }
    }

    public Utc (Date date) {
        // seconds = milliSeconds/1000;
        secondsFromEpoc = date.getTime() / 1000;
        // nanoSecondsAsFraction = (milliSeconds%1000)*(10^6)/(10^9);
        lastSecondFraction = new BigDecimal(date.getTime() % 1000)
                .divide(new BigDecimal(1000), MathContext.DECIMAL128)
                .setScale(Constants.UTC_MAX_PRECISION, RoundingMode.HALF_UP);
    }

    public Utc (long millis) {
        // seconds = milliSeconds/1000;
        secondsFromEpoc = millis / 1000;
        // nanoSecondsAsFraction = (milliSeconds%1000)*(10^6)/(10^9);
        lastSecondFraction = new BigDecimal(millis % 1000)
                .divide(new BigDecimal(1000), MathContext.DECIMAL128)
                .setScale(Constants.UTC_MAX_PRECISION, RoundingMode.HALF_UP);
    }

    public BArray build() {

        TupleType utcTupleType = TypeCreator.createTupleType(
                Arrays.asList(PredefinedTypes.TYPE_INT, PredefinedTypes.TYPE_DECIMAL));
        BArray utcTuple = ValueCreator.createTupleValue(utcTupleType);
        utcTuple.add(0, secondsFromEpoc);
        utcTuple.add(1, ValueCreator.createDecimalValue(lastSecondFraction));
        utcTuple.freezeDirect();
        return utcTuple;
    }

    public Instant generateInstant() {
        return Instant.ofEpochSecond(secondsFromEpoc, lastSecondFraction.intValue());
    }

    public ZonedDateTime generateZonedDateAtZ() {
        return Instant.ofEpochSecond(secondsFromEpoc, lastSecondFraction.intValue()).atZone(ZoneId.of("Z"));
    }

}
