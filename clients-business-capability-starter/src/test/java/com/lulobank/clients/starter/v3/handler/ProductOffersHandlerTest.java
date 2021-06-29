package com.lulobank.clients.starter.v3.handler;

import com.lulobank.clients.services.application.port.in.UpdateProductOffersPort;
import com.lulobank.clients.v3.error.ClientsDataError;
import com.lulobank.clients.services.domain.productoffers.UpdateProductOffersRequest;
import com.lulobank.clients.services.domain.productoffers.UpdateStatus;
import com.lulobank.clients.starter.adapter.in.dto.ErrorResponse;
import com.lulobank.clients.starter.adapter.in.dto.GenericResponse;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProductOffersHandlerTest {

    @Mock
    private UpdateProductOffersPort updateProductOffersPort;

    private ProductOffersHandler productOffersHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productOffersHandler = new ProductOffersHandler(updateProductOffersPort);
    }
    
    @Test
    public void shouldProcessUpdateOfferOk() {
        when(updateProductOffersPort.execute(any(UpdateProductOffersRequest.class)))
                .thenReturn(Either.right(new UpdateStatus(true)));

        ResponseEntity<GenericResponse> response = productOffersHandler
                .updateClientProductOffers(new HttpHeaders(), ID_CLIENT, new UpdateProductOffersRequest());

        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.ACCEPTED));
    }

    @Test
    public void shouldNotProcessUpdateOfferWhenErrorRetrievingOffer() {
        when(updateProductOffersPort.execute(any(UpdateProductOffersRequest.class)))
                .thenReturn(Either.left(ClientsDataError.clientNotFound()));

        ResponseEntity<GenericResponse> response = productOffersHandler
                .updateClientProductOffers(new HttpHeaders(), ID_CLIENT, new UpdateProductOffersRequest());

        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));

        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getFailure(), is("404"));
        assertThat(body.getDetail(), is("D"));
        assertThat(body.getCode(), is("CLI_101"));
    }

    @Test
    public void shouldNotProcessUpdateOfferWhenUpdateResultIsFalse() {
        when(updateProductOffersPort.execute(any(UpdateProductOffersRequest.class)))
                .thenReturn(Either.right(new UpdateStatus(false)));

        ResponseEntity<GenericResponse> response = productOffersHandler
                .updateClientProductOffers(new HttpHeaders(), ID_CLIENT, new UpdateProductOffersRequest());

        assertThat(response, notNullValue());
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));

        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body, notNullValue());
        assertThat(body.getFailure(), is("502"));
        assertThat(body.getDetail(), is("D"));
        assertThat(body.getCode(), is("CLI_100"));
    }
}