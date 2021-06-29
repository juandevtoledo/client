package com.lulobank.clients.services.utils;

import org.junit.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InterestUtilTest {

    @Test
    public void getMonthlyNominalRate() {
        Float interestRate = 16.5f;
        Float monthlyNominalRateExpect = 1.281f;
        Float monthlyNominalResult = InterestUtil.getMonthlyNominalRate(interestRate);
        assertThat(monthlyNominalResult, is(monthlyNominalRateExpect));
    }

    @Test
    public void getMonthlyNominalRateZero() {
        Float interestRate = 0f;
        BigDecimal monthlyNominalRateExpect = new BigDecimal(0).setScale(2, ROUND_HALF_UP);
        Float monthlyNominalResult = InterestUtil.getMonthlyNominalRate(interestRate);
        assertThat(monthlyNominalResult, is(monthlyNominalRateExpect.floatValue()));
    }

    @Test
    public void getMonthlyNominalRateNull() {
        Float interestRate = null;
        BigDecimal monthlyNominalRateExpect = new BigDecimal(0);
        Float monthlyNominalResult = InterestUtil.getMonthlyNominalRate(interestRate);
        assertThat(monthlyNominalResult, is(monthlyNominalRateExpect.floatValue()));
    }
}