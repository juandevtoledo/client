package com.lulobank.clients.services.inboundadapters;

import com.lulobank.clients.services.features.onboardingvalidations.ValidateExistenceRequest;
import com.lulobank.clients.services.features.onboardingvalidations.ValidateExistenceResult;
import com.lulobank.clients.services.features.onboardingvalidations.ValidateIdCardExistanceHandler;
import com.lulobank.clients.services.features.onboardingvalidations.ValidatePhoneNumberExistance;
import com.lulobank.clients.services.features.onboardingvalidations.ValidatePhoneNumberExistanceHandler;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("validations")
@CrossOrigin(origins = "*")
public class ValidationsAdapter {

  private ClientsRepository repository;

  @Autowired
  public ValidationsAdapter(ClientsRepository repository) {
    this.repository = repository;
  }

  @GetMapping(value = "/phonenumber", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> validatePhone(
      @RequestHeader final HttpHeaders headers,
      @RequestParam("country") final int country,
      @RequestParam("number") final String number) {

    ValidateExistenceResult result =
        new ValidatePhoneNumberExistanceHandler(repository)
            .handle(new ValidatePhoneNumberExistance(country, number))
            .getContent();

    if (Boolean.TRUE.equals(result.getExists()))
      return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/idcard/{idcard}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> validateIdCard(
      @RequestHeader final HttpHeaders headers, @PathVariable("idcard") final String idCard) {
    ValidateExistenceResult result =
        new ValidateIdCardExistanceHandler(repository)
            .handle(new ValidateExistenceRequest(idCard))
            .getContent();

    if (Boolean.TRUE.equals(result.getExists()))
      return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
