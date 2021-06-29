package com.lulobank.clients.services.utils;

import io.vavr.collection.List;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
public enum BiometricResultCodes {
  PENDING(1, "Pendiente"),
  SUCCESSFUL(2, "Proceso satisfactorio"),
  INCONSISTENCY(3, "Proceso con inconsistencias"),
  INCONSISTENCY_WITH_FACIAL_COMPARE(4, "Documento auténtico sin cotejo facial"),
  FAILED_CAPTURE(5, "Captura errada"),
  INVALID_DOCUMENT(6, "Documento invalido"),
  CHECK_PROCESS(7, "Proceso de verificación"),
  CHANGE_DOCUMENT(8, "Documento alterado"),
  FAKE_DOCUMENT(9, "Documento falso"),
  FACE_NOT_APPLY(10, "Rostro no corresponde"),
  DOCUMENT_EXISTS(14, "Persona registrada previamente"),
  ;
  private Integer code;
  private String message;

  BiometricResultCodes(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  private static final EnumSet<BiometricResultCodes> FAILED_BIOMETRIC_FRAUD =
      EnumSet.of(
          INCONSISTENCY_WITH_FACIAL_COMPARE,
          INVALID_DOCUMENT,
          CHECK_PROCESS,
          CHANGE_DOCUMENT,
          FAKE_DOCUMENT,
          FACE_NOT_APPLY);

  private static final EnumSet<BiometricResultCodes> BIOMETRIC_RESULT_SUCCESS =
          EnumSet.of(
                  SUCCESSFUL,
                  DOCUMENT_EXISTS);

  public static final boolean isSuccessfulStatus(Integer status) {
    return List.ofAll(BIOMETRIC_RESULT_SUCCESS.stream())
            .filter(e -> e.code.equals(status))
            .map(Objects::nonNull)
            .getOrElse(false);

  };


  public static final Predicate<Integer> isCodeFailedBiometricFraud =
      biometricResultCode -> {
        return FAILED_BIOMETRIC_FRAUD.stream()
            .filter(e -> e.code.equals(biometricResultCode))
            .findFirst()
            .map(Objects::nonNull)
            .orElse(false);
      };
}
