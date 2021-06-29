package com.lulobank.clients.starter.v3.adapters.in.validator;

import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import io.vavr.control.Option;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.nonNull;

public class FatcaInformationValidator implements ConstraintValidator<FatcaInformation, ClientFatcaRequest> {

    @Override
    public boolean isValid(ClientFatcaRequest value, ConstraintValidatorContext context) {
        return Option.of(value.getFatcaResponsibility())
                .map(resp -> !resp || nonNull(value.getCountryCode()) && atLeastOneTinField(value))
                .getOrElse(false);
    }

    private boolean atLeastOneTinField(ClientFatcaRequest value) {
        return nonNull(value.getTin()) || nonNull(value.getTinObservation());
    }
}
