package com.lulobank.clients.starter.v3.adapters.in.validator;

import com.lulobank.clients.starter.v3.adapters.in.dto.ClientFatcaRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FatcaInformationValidatorTest {

    @Mock
    private ConstraintValidatorContext context;
    private FatcaInformationValidator fatcaInformationValidator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fatcaInformationValidator = new FatcaInformationValidator();
    }

    @Test
    public void shouldValidWhenFatcaResponsibilityIsTrue() {
        boolean valid = fatcaInformationValidator.isValid(buildRequest(true), context);

        assertThat(valid, is(true));
    }

    @Test
    public void shouldValidWhenFatcaResponsibilityIsFalse() {
        boolean valid = fatcaInformationValidator.isValid(buildRequest(false), context);

        assertThat(valid, is(true));
    }

    @Test
    public void shouldNotValidWhenFatcaResponsibilityIsTrueAndCountryCodeIsNull() {
        ClientFatcaRequest request = buildRequest(true);
        request.setCountryCode(null);
        boolean valid = fatcaInformationValidator.isValid(request, context);

        assertThat(valid, is(false));
    }

    @Test
    public void shouldNotValidWhenFatcaResponsibilityIsTrueAndThereIsNotTinField() {
        ClientFatcaRequest request = buildRequest(true);
        request.setTin(null);
        request.setTinObservation(null);
        boolean valid = fatcaInformationValidator.isValid(request, context);

        assertThat(valid, is(false));
    }

    @Test
    public void shouldValidWhenFatcaResponsibilityIsTrueAndTinObservationIsNull() {
        ClientFatcaRequest request = buildRequest(true);
        request.setTinObservation(null);
        boolean valid = fatcaInformationValidator.isValid(request, context);

        assertThat(valid, is(true));
    }

    @Test
    public void shouldValidWhenFatcaResponsibilityIsTrueAndTinIsNull() {
        ClientFatcaRequest request = buildRequest(true);
        request.setTin(null);
        boolean valid = fatcaInformationValidator.isValid(request, context);

        assertThat(valid, is(true));
    }

    private ClientFatcaRequest buildRequest(boolean fatcaResponsibility) {
        ClientFatcaRequest request = new ClientFatcaRequest();
        request.setFatcaResponsibility(fatcaResponsibility);
        request.setIdClient(ID_CLIENT);
        request.setCountryCode("57");
        request.setTin("TIN_NUMBER_TEST");
        request.setTinObservation("TIN_OBSERVATION_TEST");
        return request;
    }

}