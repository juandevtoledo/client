package com.lulobank.clients.starter.inboundadapter;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.annotation.MFAType;
import com.lulobank.clients.sdk.operations.dto.ClientResult;
import com.lulobank.clients.services.features.profilev2.UpdateClientEmailHandler;
import com.lulobank.clients.services.features.profilev2.model.UpdateClientEmailRequest;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

import static com.lulobank.clients.services.utils.TransactionTypeMFA.UPDATE_EMAIL;

@RestController
@RequestMapping("/V2")
@CrossOrigin(origins = "*")
public class ClientsAdapter {

  public static final Locale LOCALE_CO = new Locale("es", "CO");
  private final UpdateClientEmailHandler updateClientEmailHandler;

  @Autowired
  public ClientsAdapter(
      UpdateClientEmailHandler updateClientEmailHandler) {
    this.updateClientEmailHandler = updateClientEmailHandler;
  }

  @PutMapping(value = "/{idClient}/profile/email", produces = MediaType.APPLICATION_JSON_VALUE)
  @MFA(transaction = UPDATE_EMAIL,requestBodyClass = UpdateClientEmailRequest.class,type = MFAType.DYNAMIC)
  public ResponseEntity<ClientResult> updateClientEmail(
      @RequestHeader final HttpHeaders headers,
      @PathVariable String idClient,
      @Valid @RequestBody final UpdateClientEmailRequest updateClientEmailRequest) {
    updateClientEmailRequest.setIdClient(idClient);
    updateClientEmailRequest.setHttpHeaders(headers.toSingleValueMap());

    return Try.of(() -> updateClientEmailRequest)
        .andThenTry(request -> request.setNewEmail(request.getNewEmail().toLowerCase(LOCALE_CO)))
        .map(updateClientEmailHandler::handle)
        .get();
  }

}
