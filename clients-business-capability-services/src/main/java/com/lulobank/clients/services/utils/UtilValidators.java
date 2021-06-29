package com.lulobank.clients.services.utils;

import com.lulobank.core.Command;
import com.lulobank.core.validations.ValidationResult;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.stream.Stream;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.Validate;

public class UtilValidators {
  private static final int FIRST_RECORD = 0;
  private static final String REGEX_EMAIL =
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}"
          + "~-]+)*|\""
          + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
          + "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]"
          + "?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

  private UtilValidators() {}

  public static ValidationResult getNotNullValidations(Command validateClass) {
    Class classRequest = validateClass.getClass();
    final ValidationResult[] validationResult = {null};
    Method[] methods = classRequest.getMethods();
    Stream.of(methods)
        .filter(
            method ->
                method.getName().startsWith("get")
                    && classRequest.equals(method.getDeclaringClass()))
        .forEach(
            method -> {
              try {
                if (method.invoke(validateClass, null) == null) {
                  validationResult[FIRST_RECORD] =
                      new ValidationResult("is null", method.getName().substring(3));
                }
              } catch (IllegalAccessException | InvocationTargetException e) {
                validationResult[FIRST_RECORD] =
                    new ValidationResult("has an error", method.getName().substring(3));
              }
            });
    return validationResult[FIRST_RECORD];
  }

  public static ValidationResult getValidationResult(String errorMessage, String errorCode) {
    return new ValidationResult(errorMessage, errorCode);
  }

  public static void validatePhoneNumber(String phoneNumber) {
    Validate.notNull(phoneNumber);
    Validate.notEmpty(phoneNumber);
    Validate.isTrue(phoneNumber.matches("((3)[0-9]{9})"), "phone number wrong format");
  }

  public static void validatePrefix(String prefix) {
    Validate.notNull(prefix);
    Validate.notEmpty(prefix);
    Validate.isTrue(prefix.matches("([0-9]{2,3})"), "phone prefix wrong format");
  }

  public static BigDecimal doubleToBd(Double value) {
    BigDecimal bdValue = new BigDecimal(value.toString());
    return new BigDecimal(bdValue.toPlainString());
  }

  public static Boolean validateEmail(String email) {
    return email.toLowerCase(LocaleUtils.toLocale("es_CO")).matches(REGEX_EMAIL);
  }
}
