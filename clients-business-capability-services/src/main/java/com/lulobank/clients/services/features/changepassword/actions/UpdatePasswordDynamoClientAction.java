package com.lulobank.clients.services.features.changepassword.actions;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;

import com.lulobank.clients.services.features.changepassword.model.NewPasswordRequest;
import com.lulobank.clients.services.features.changepassword.model.Password;
import com.lulobank.clients.services.outboundadapters.model.ClientEntity;
import com.lulobank.clients.services.outboundadapters.repository.ClientsRepository;
import com.lulobank.clients.services.utils.ClientErrorResultsEnum;
import com.lulobank.clients.services.utils.ClientHelper;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class UpdatePasswordDynamoClientAction
    implements Action<Response<Password>, NewPasswordRequest> {
  private ClientsRepository clientsRepository;

  public UpdatePasswordDynamoClientAction(ClientsRepository clientsRepository) {
    this.clientsRepository = clientsRepository;
  }

  @Override
  public void run(Response<Password> passwordResponse, NewPasswordRequest command) {
    Optional<ClientEntity> clientEntity = clientsRepository.findByIdClient(command.getIdClient());
    if (clientEntity.isPresent()) {
      ClientEntity clientEntityRequest = clientEntity.get();
      clientEntityRequest.setQualityCode(ClientHelper.getHashString(command.getNewPassword()));
      clientsRepository.save(clientEntityRequest);
      passwordResponse.getContent().setIdClient(clientEntityRequest.getIdClient());
      passwordResponse.getContent().setEmailAddress(clientEntityRequest.getEmailAddress());
      passwordResponse
          .getContent()
          .setPhonePrefix(String.valueOf(clientEntityRequest.getPhonePrefix()));
      passwordResponse.getContent().setPhoneNumber(clientEntityRequest.getPhoneNumber());
    } else {
      passwordResponse
          .getErrors()
          .add(
              getListValidations(
                      ClientErrorResultsEnum.CLIENT_NOT_FOUND_IN_DB.name(),
                      String.valueOf(HttpStatus.NOT_FOUND.value()))
                  .get(0));
    }
  }
}
