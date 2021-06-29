package com.lulobank.clients.starter.v3.adapters.in;

import com.google.common.collect.ImmutableList;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import com.lulobank.clients.starter.v3.adapters.in.dto.ApprovedOffersResponse;
import com.lulobank.clients.starter.v3.handler.ProductOfferHandler;
import com.lulobank.clients.starter.v3.handler.ProductOffersHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ProductOffersAdapterTest {

    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private ProductOffersHandler productOffersHandler;
    @Mock
    private ProductOfferHandler productOfferHandler;

    private ProductOffersAdapter productOffersAdapter;

    private HttpHeaders headers;
    private UpdateProductOffersRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productOffersAdapter = new ProductOffersAdapter(productOffersHandler, productOfferHandler);

        headers = new HttpHeaders();
        request = new UpdateProductOffersRequest();
    }

    @Test
    public void shouldProcessGetOfferClientOk() {
        ApprovedOffersResponse getOfferResponse = buildApprovedOffersResponse();
        when(productOfferHandler.getClientProductOffers(headers, ID_CLIENT)).thenReturn(ResponseEntity.ok(getOfferResponse));

        ResponseEntity<GenericResponse> response = productOffersAdapter.getApprovedOffers(headers, ID_CLIENT);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(getOfferResponse));
    }

	@Test
    public void shouldProcessUpdateOfferClientOk() {
        when(productOffersHandler.updateClientProductOffers(headers, ID_CLIENT, request)).thenReturn(ResponseEntity.accepted().build());

        ResponseEntity<GenericResponse> response = productOffersAdapter.updateOffer(headers, ID_CLIENT, request);

        assertThat(response.getStatusCode(), is(HttpStatus.ACCEPTED));
        assertThat(response.getBody(), nullValue());
    }

    @Test
    public void shouldReturnErrorWhenBindingResultNotEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(ImmutableList.of(new ObjectError("status", "status is null or empty")));

        ErrorResponse response = productOffersAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("400"));
        assertThat(response.getDetail(), is("V"));
        assertThat(response.getCode(), is("CLI_102"));
    }

    @Test
    public void shouldReturnErrorWhenBindingResultErrorsEmpty() {
        when(bindingResult.getAllErrors()).thenReturn(emptyList());

        ErrorResponse response = productOffersAdapter
                .handleValidationExceptions(new MethodArgumentNotValidException(methodParameter, bindingResult));

        assertThat(response, notNullValue());
        assertThat(response.getFailure(), is("500"));
        assertThat(response.getDetail(), is("D"));
        assertThat(response.getCode(), is("CLI_100"));
    }
    
    private ApprovedOffersResponse buildApprovedOffersResponse() {
		return ApprovedOffersResponse.builder().build();
	}
}