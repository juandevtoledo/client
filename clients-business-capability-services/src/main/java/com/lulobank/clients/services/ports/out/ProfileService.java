package com.lulobank.clients.services.ports.out;

import io.vavr.control.Try;

import java.util.Map;

public interface ProfileService {

    Try<Boolean> savePhoneNumberAndEmail(Map<String, String> headers,
                                         String clientId,
                                         String email,
                                         String phoneNumber,
                                         Integer countryCode);

}
