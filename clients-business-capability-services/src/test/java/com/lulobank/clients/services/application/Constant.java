package com.lulobank.clients.services.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Constant {
    public static final String ID_CLIENT = UUID.randomUUID().toString();
    public static final String ID_CARD = "112233445566";
    public static final String MAIL = "mail@mail.com";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ID_SAVINGS_ACCOUNT = "01234567890";
    public static final String CREATION_DATE_SAVING = "2020-06-11T14:25:45";
    public static final String CREATION_DATE_CARD = "2020-08-10";
    public static final String PHONE_NUMBER = "3101234567";
    public static final String ID_PRODUCT_OFFER = UUID.randomUUID().toString();
    public static final String ID_CBS = "784526";
    public static final String PAYMENT_STATUS = "SUCCESS";
    public static final BigDecimal VALUE_PAID = BigDecimal.valueOf(10000d);
    public static final String ID_TRANSACTION_BIOMETRIC = "1";
    public static final LocalDateTime REPORT_DATE_BLACKLIST = LocalDateTime.parse("2020-06-11T14:25:45");
    public static final LocalDateTime WHITELIST_EXPIRATION = LocalDateTime.parse("2020-12-11T14:25:45");
    public static final String EXPEDITION_DATE = "1960-06-11T14:25:45";
}