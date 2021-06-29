package com.lulobank.clients.services.utils;

import java.util.function.Predicate;

public enum ProductTypeEnum {
  CREDIT_ACCOUNT,
  SAVING_ACCOUNT_PLUS,
  SAVING_ACCOUNT,
  ;

  public static final Predicate<ProductTypeEnum> isProductCredit =
      productTypeEnum -> ProductTypeEnum.CREDIT_ACCOUNT.equals(productTypeEnum);

  public static final Predicate<ProductTypeEnum> isProductSavings =
      productTypeEnum -> ProductTypeEnum.SAVING_ACCOUNT.equals(productTypeEnum);
}
