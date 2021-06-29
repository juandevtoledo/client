package com.lulobank.clients.services;

import java.util.Random;
import java.util.UUID;

public class Constants {
  public static final String ID_CLIENT = "aa9504b5-0485-4fc8-aacc-8d9f64092664";
  public static final String ID_CARD = "1167813466";
  public static final double AMOUNT = 10000d;
  public static final int INSTALLMENTS = 12;
  public static final double MAX_AMOUNT_INSTALLMENT = 5000d;
  public static final float INTEREST_RATE = 16f;
  public static final String ACESS_TOKEN = "wqerwedq21314wesd";
  public static final String PURPOSE = "Travel";
  public static final String NAME = "Name";
  public static final String LAST_NAME = "LastName";
  public static final String GENDER ="M" ;
  public static final String ID_TRANSACTION = UUID.randomUUID().toString();
  public static final String DATE_ISSUE = "1988-01-22T00:00:00";
  public static final String DATE_ISSUE_ENTITY = "1988-01-22";
  public static final Integer PREFIX=57;
  public static final String PHONE = "777-99-01";
  public static final String BIRTH_DATE = "1989-02-23T00:00:00";
  public static final String ID_CBS = String.valueOf(new Random().nextInt());
  public static final String ACCOUNT_ID = String.valueOf(new Random().nextInt());
  public static final String EMAIL="test@test.com";
  public static final String BIRTH_DATE_ENTITY = "1989-02-23";
  public static final String ADDRESS = "STREET ELM";
  public static final String ADDRESS_PREFIX = "ELM";
  public static final String ADDRESS_COMPLEMENT = "APTO ELM";
  public static final String CITY = "BOGOTA";
  public static final String CITY_ID = "10101";
  public static final String DEPARTMENT = "CUNDINAMARCA";
  public static final String DEPARTMENT_ID = "1111";
  public static final String TYPE_DOCUMENT = "CC";
  public static final String ID_BIOMETRIC = "4444";
}
