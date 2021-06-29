package com.lulobank.clients.starter.adapter.out;

import com.lulobank.clients.sdk.operations.exception.ClientsServiceException;
import com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocument;
import com.lulobank.clients.services.application.port.out.reporting.model.StoreDigitalEvidenceRequest;
import com.lulobank.clients.starter.adapter.BaseUnitTest;
import com.lulobank.clients.starter.adapter.out.reporting.ReportingAdapter;
import com.lulobank.clients.starter.adapter.out.reporting.ReportingClient;
import com.lulobank.clients.starter.outboundadapter.digitalevidence.mapper.DigitalEvidenceServiceMapper;
import com.lulobank.clients.v3.adapters.port.out.dynamo.dto.ClientsV3Entity;
import io.vavr.collection.Array;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.clients.services.application.port.out.reporting.model.EvidenceDocumentType.*;
import static com.lulobank.clients.starter.adapter.Constant.DOCUMENT_TYPE;
import static com.lulobank.clients.starter.adapter.Constant.ID_CARD;
import static com.lulobank.clients.starter.adapter.Constant.ID_CLIENT;
import static com.lulobank.clients.starter.adapter.Constant.MAIL;
import static com.lulobank.clients.starter.adapter.Constant.PHONE_NUMBER;
import static com.lulobank.clients.starter.adapter.out.reporting.ReportingClient.BASE_PATH;
import static com.lulobank.clients.starter.adapter.out.reporting.ReportingClient.BLACKLISTED_DIGITAL_EVIDENCE;
import static com.lulobank.clients.starter.adapter.out.reporting.ReportingClient.ID_CLIENT_PLACEHOLDER;

public class ReportingAdapterTest extends BaseUnitTest {

    private ReportingAdapter reportingAdapter;

    private StoreDigitalEvidenceRequest storeDigitalEvidenceRequest;

    @Before
    public void init() {
        reportingAdapter = new ReportingAdapter(new ReportingClient("http://localhost:8082"));

        ClientsV3Entity clientEntity = new ClientsV3Entity();
        clientEntity.setIdClient(ID_CLIENT);
        clientEntity.setIdCard(ID_CARD);
        clientEntity.setEmailAddress(MAIL);
        clientEntity.setPhoneNumber(PHONE_NUMBER);
        clientEntity.setTypeDocument(DOCUMENT_TYPE);
        storeDigitalEvidenceRequest = DigitalEvidenceServiceMapper.INSTANCE.toStoreDigitalEvidenceRequest(clientEntity);
        List<EvidenceDocument> evidenceDocumentList = Array.of(SAVINGS_ACCOUNT_CONTRACT, PRIVACY_POLICY, TERMS_AND_CONDITIONS)
                .map(EvidenceDocument::new).asJava();
        storeDigitalEvidenceRequest.setDocumentsToStore(evidenceDocumentList);
    }

    @Test
    public void shouldSaveDigitalEvidenceSuccessfully() {
        wireMockRule.stubFor(
                post(urlEqualTo("/" + BASE_PATH + BLACKLISTED_DIGITAL_EVIDENCE.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.OK.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")
                        )
        );

        Try<Boolean> digitalEvidence = reportingAdapter.createDigitalEvidence(new HashMap<>(), ID_CLIENT, storeDigitalEvidenceRequest);

        Assert.assertTrue(digitalEvidence.isSuccess());
    }

    @Test(expected = ClientsServiceException.class)
    public void saveDigitalEvidenceReturnedInternalServer() {
        wireMockRule.stubFor(
                post(urlEqualTo("/" + BASE_PATH + BLACKLISTED_DIGITAL_EVIDENCE.replace(ID_CLIENT_PLACEHOLDER, ID_CLIENT)))
                        .willReturn(
                                aResponse()
                                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .withBody("")
                                        .withHeader("Content-Type", "application/json")
                        )
        );

        Try<Boolean> digitalEvidence = reportingAdapter.createDigitalEvidence(new HashMap<>(), ID_CLIENT, storeDigitalEvidenceRequest);

        Assert.assertTrue(digitalEvidence.isFailure());
        digitalEvidence.get();
    }

}
