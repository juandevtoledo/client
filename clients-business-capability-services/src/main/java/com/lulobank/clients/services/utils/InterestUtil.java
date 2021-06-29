package com.lulobank.clients.services.utils;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.valueOf;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InterestUtil {

    private static final Float MONTH_OF_YEAR = 12f;
    private static final double PERCENT = 100d;
    private static final int SCALE = 3;

    public static Float getMonthlyNominalRate(Float interestRate) {
        return Option.of(interestRate)
                .map(interest -> (interest / PERCENT))
                .map(InterestUtil::getMonthlyInterest)
                .map(InterestUtil::scale)
                .map(BigDecimal::floatValue)
                .getOrElse(0f);
    }

    private static BigDecimal scale(Double monthly) {
        return valueOf(monthly).setScale(SCALE, ROUND_HALF_UP);
    }

    private static double getMonthlyInterest(Double rate) {
        return (Math.pow((1 + rate), 1 / MONTH_OF_YEAR) * PERCENT) - (1d * PERCENT);
    }

}
