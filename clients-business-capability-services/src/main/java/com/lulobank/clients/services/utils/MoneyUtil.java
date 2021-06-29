package com.lulobank.clients.services.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyUtil {
    public static final BigDecimal GMF = valueOf(0.004);
    private static final int DEFAULT_SCALE = 2;

    public static BigDecimal roundTwoDigitsUp(BigDecimal amount) {
        return amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundTwoDigitsDown(BigDecimal amount) {
        return amount.setScale(DEFAULT_SCALE, RoundingMode.FLOOR);
    }
}
