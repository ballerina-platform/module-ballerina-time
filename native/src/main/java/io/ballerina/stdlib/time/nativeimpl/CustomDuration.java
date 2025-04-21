package io.ballerina.stdlib.time.nativeimpl;

import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.stdlib.time.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
