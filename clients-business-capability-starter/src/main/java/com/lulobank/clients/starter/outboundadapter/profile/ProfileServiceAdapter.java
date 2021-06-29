package com.lulobank.clients.starter.outboundadapter.profile;

import co.com.lulobank.tracing.restTemplate.RestTemplateClient;
import com.lulobank.clients.services.exception.ValidateRequestException;
import com.lulobank.clients.services.ports.out.ProfileService;
import com.lulobank.clients.starter.outboundadapter.profile.dto.UpdatePhoneRequest;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ProfileServiceAdapter implements ProfileService {

    private static final String SAVE_PHONE_NUMBER_BY_CLIENT = "profile/api/v1/internal/clients/%s/phone-numbers/email-addresses";

    private final RestTemplateClient restTemplateClient;

    public ProfileServiceAdapter(RestTemplateClient restTemplateClient) {
        this.restTemplateClient = restTemplateClient;
    }

    @Override
    public Try<Boolean> savePhoneNumberAndEmail(Map<String, String> headers,
                                                String clientId,
                                                String email,
                                                String phoneNumber,
                                                Integer countryCode) {
        String context = String.format(SAVE_PHONE_NUMBER_BY_CLIENT, clientId);
        UpdatePhoneRequest updatePhoneRequest = new UpdatePhoneRequest();
        updatePhoneRequest.setNewPhoneNumber(phoneNumber);
        updatePhoneRequest.setCountryCode(countryCode);
        updatePhoneRequest.setEmail(email);
        log.info(phoneNumber,countryCode);
        return restTemplateClient.put(context, updatePhoneRequest, headers, String.class)
                .map(r -> true)
                .toTry(ValidateRequestException::new);
    }

}
