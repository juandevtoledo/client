package com.lulobank.clients.services.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;

public class MoneyUtilTest {

    @Test
    public void testRoundTwoDigitsUp() {
        BigDecimal roundedValue = BigDecimal.valueOf(12.15);
        BigDecimal nonRoundedValue = BigDecimal.valueOf(12.148);
        BigDecimal resultRoundedValue = MoneyUtil.roundTwoDigitsUp(nonRoundedValue);

        assertTrue(resultRoundedValue.compareTo(roundedValue) == 0);
    }

    @Test
    public void testRoundTwoDigitsDown() {
        BigDecimal roundedValue = BigDecimal.valueOf(12.15);
        BigDecimal nonRoundedValue = BigDecimal.valueOf(12.152);
        BigDecimal resultRoundedValue = MoneyUtil.roundTwoDigitsDown(nonRoundedValue);

        assertTrue(resultRoundedValue.compareTo(roundedValue) == 0);
    }

}